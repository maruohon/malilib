package malilib.config.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.multiplayer.ServerData;

import malilib.MaLiLib;
import malilib.MaLiLibReference;
import malilib.action.NamedAction;
import malilib.config.ConfigManagerImpl;
import malilib.config.ModConfig;
import malilib.config.category.ConfigOptionCategory;
import malilib.config.option.BaseGenericConfig;
import malilib.config.option.ConfigOption;
import malilib.config.serialization.JsonConfigSerializerRegistry.ConfigFromJsonOverrider;
import malilib.input.Hotkey;
import malilib.input.HotkeyManagerImpl;
import malilib.overlay.message.MessageDispatcher;
import malilib.registry.Registry;
import malilib.util.FileNameUtils;
import malilib.util.FileUtils;
import malilib.util.HttpUtils;
import malilib.util.data.ModInfo;
import malilib.util.data.json.JsonUtils;
import malilib.util.game.wrap.GameUtils;

public class ConfigOverrideHandler
{
    protected final Map<Pair<ConfigOptionCategory, BaseGenericConfig<?>>, Pair<JsonElement, String>> configOverrides = new HashMap<>();
    protected final Map<ModInfo, Map<ConfigOptionCategory, List<BaseGenericConfig<?>>>> overridableConfigs = new HashMap<>();
    protected final Map<ModInfo, List<NamedAction>> allBaseActions = new HashMap<>();
    protected final Map<ModInfo, List<Hotkey>> allHotkeys = new HashMap<>();
    protected final Map<NamedAction, String> lockedActions = new HashMap<>();
    protected final Map<Hotkey, String> lockedHotkeys = new HashMap<>();

    public void readAndApplyConfigOverrides()
    {
        this.initOverride();

        // Overrides that come later will override previous definitions.
        // The server overrides are handled last so that they have priority over other types.
        try
        {
            this.readOverridesFromLocalCommonConfig();
            this.readOverridesFromLocalPerWorldConfig();

            if (GameUtils.isSinglePlayer())
            {
                this.readOverridesFromSinglePlayerWorldConfig();
            }
            else
            {
                this.readOverridesFromServer();
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.error("Exception while trying to apply config overrides", e);
        }

        this.applyOverridesAndPrintMessage();
    }

    public void applyOverridesFromServer(JsonObject obj)
    {
        this.initOverride();
        this.readOverridesFromJson(obj);
        this.applyOverridesAndPrintMessage();
    }

    public void clearConfigOverrides()
    {
        this.fetchAllOverridableConfigs();
        this.overridableConfigs.values().forEach(map -> map.values().forEach(list -> list.forEach(BaseGenericConfig::disableOverride)));

        Registry.ACTION_REGISTRY.clearActionLocks();
        ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).clearHotkeyLocks();
    }

    protected void initOverride()
    {
        this.configOverrides.clear();
        this.lockedActions.clear();
        this.lockedHotkeys.clear();

        this.fetchAllOverridableConfigs();
        this.fetchAllActions();
        this.fetchAllHotkeys();

        int configCount = this.overridableConfigs.values().stream().mapToInt(m -> m.values().stream().mapToInt(List::size).sum()).sum();
        MaLiLib.debugLog("  > There are {} overridable configs in the game", configCount);
    }

    protected void fetchAllOverridableConfigs()
    {
        this.overridableConfigs.clear();

        for (ModConfig modConfig : ((ConfigManagerImpl) Registry.CONFIG_MANAGER).getAllModConfigs())
        {
            Map<ConfigOptionCategory, List<BaseGenericConfig<?>>> map = new HashMap<>();

            for (ConfigOptionCategory category : modConfig.getConfigOptionCategories())
            {
                List<BaseGenericConfig<?>> list = new ArrayList<>();

                for (ConfigOption<?> config : category.getConfigOptions())
                {
                    ConfigFromJsonOverrider<?> overrider = Registry.JSON_CONFIG_SERIALIZER.getOverrider(config);

                    if (overrider != null && config instanceof BaseGenericConfig)
                    {
                        list.add((BaseGenericConfig<?>) config);
                    }
                }

                if (list.isEmpty() == false)
                {
                    map.put(category, list);
                }
            }

            if (map.isEmpty() == false)
            {
                this.overridableConfigs.put(modConfig.getModInfo(), map);
            }
        }
    }

    protected void fetchAllActions()
    {
        this.allBaseActions.clear();

        for (NamedAction action : Registry.ACTION_REGISTRY.getBaseActions())
        {
            this.allBaseActions.computeIfAbsent(action.getModInfo(), k -> new ArrayList<>()).add(action);
        }
    }

    protected void fetchAllHotkeys()
    {
        this.allHotkeys.clear();

        List<Hotkey> list = new ArrayList<>();
        Registry.HOTKEY_MANAGER.getHotkeyCategories().forEach(c -> list.addAll(c.getHotkeys()));

        for (Hotkey hotkey : list)
        {
            this.allHotkeys.computeIfAbsent(hotkey.getKeyBind().getModInfo(), k -> new ArrayList<>()).add(hotkey);
        }
    }

    protected void applyOverridesAndPrintMessage()
    {
        try
        {
            MaLiLib.debugLog("  > Applying {} config overrides...", this.configOverrides.size());
            int count = this.applyConfigOverrides();

            MaLiLib.debugLog("  > Applied {} config overrides", count);

            if (count > 0)
            {
                MessageDispatcher.warning(8000).translate("malilib.message.info.config_overrides.config_overrides_applied", count);
            }

            count = this.applyActionLocks();

            MaLiLib.debugLog("  > Applied {} action locks", count);

            if (count > 0)
            {
                MessageDispatcher.warning(8000).translate("malilib.message.info.config_overrides.action_locks_applied", count);
            }

            count = this.applyHotkeyLocks();

            MaLiLib.debugLog("  > Applied {} hotkey locks", count);

            if (count > 0)
            {
                MessageDispatcher.warning(8000).translate("malilib.message.info.config_overrides.hotkey_locks_applied", count);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.error("Exception while trying to apply config overrides", e);
        }
    }

    protected void readOverridesFromSinglePlayerWorldConfig()
    {
        Path worldDir = GameUtils.getCurrentSinglePlayerWorldDirectory();
        String fileName = "malilib_config_overrides.json";
        Path file = worldDir.resolve(fileName);

        this.readOverridesFromFile(file);
    }

    protected void readOverridesFromLocalPerWorldConfig()
    {
        Path configDir = ConfigUtils.getActiveConfigDirectory();
        String worldName = malilib.util.StringUtils.getWorldOrServerNameOrDefault("fallback");
        worldName = FileNameUtils.generateSimpleSafeFileName(worldName);
        String fileName = "config_overrides_world_" + worldName + ".json";
        Path file = configDir.resolve(MaLiLibReference.MOD_ID).resolve(fileName);

        this.readOverridesFromFile(file);
    }

    protected void readOverridesFromLocalCommonConfig()
    {
        Path configDir = ConfigUtils.getActiveConfigDirectory();
        Path file = configDir.resolve(MaLiLibReference.MOD_ID).resolve("config_overrides_common.json");

        this.readOverridesFromFile(file);
    }

    protected void readOverridesFromServer()
    {
        ServerData serverData = GameUtils.getClient().getCurrentServerData();

        if (serverData != null)
        {
            String motd = serverData.serverMOTD;
            String[] lines = motd.split("\\n");

            for (int index = 3; index < lines.length; ++index)
            {
                String str = lines[index];

                if (this.readOverridesFromString(str))
                {
                    return;
                }

                if (this.readOverridesFromURL(str))
                {
                    return;
                }
            }
        }
    }

    protected void readOverridesFromFile(Path file)
    {
        if (Files.isRegularFile(file))
        {
            String str = FileUtils.readFileAsString(file, -1);

            if (str != null)
            {
                this.readOverridesFromString(str);
            }
        }
    }

    protected boolean readOverridesFromURL(String str)
    {
        str = str.trim();

        if (str.startsWith("http://") || str.startsWith("https://"))
        {
            if (str.endsWith("§r"))
            {
                // Strip away the ending '§r'
                str = str.substring(0, str.length() - 2);
            }

            String pageContent = HttpUtils.tryFetchPage(str, 2000);

            if (pageContent != null)
            {
                return this.readOverridesFromString(pageContent);
            }
        }

        return false;
    }

    protected boolean readOverridesFromString(String str)
    {
        str = str.trim();

        if (StringUtils.isBlank(str) || str.charAt(0) != '{')
        {
            return false;
        }

        int lastBrace = str.lastIndexOf('}');

        if (lastBrace <= 1)
        {
            return false;
        }

        // Strip away any junk from the end, like the ending '§r'
        if (lastBrace < str.length() - 1)
        {
            str = str.substring(0, lastBrace + 1);
        }

        JsonElement el = JsonUtils.parseJsonFromString(str);

        MaLiLib.debugLog("Cleaned up overrides JSON string: '{}'", str);
        MaLiLib.debugLog("Parsed override JSON: '{}'", el);

        if (el != null && el.isJsonObject())
        {
            return this.readOverridesFromJson(el.getAsJsonObject());
        }

        return false;
    }

    protected boolean readOverridesFromJson(JsonObject root)
    {
        boolean foundOverrides = false;

        foundOverrides |= this.overrideReader(root, "malilib_config_overrides", this::readConfigOverridesFrom);
        foundOverrides |= this.overrideReader(root, "malilib_action_locks", this::readActionLocksFrom);
        foundOverrides |= this.overrideReader(root, "malilib_hotkey_locks", this::readHotkeyLocksFrom);

        return foundOverrides;
    }

    protected boolean overrideReader(JsonObject root, String arrayName, BiFunction<JsonObject, OverrideTestsAndMessage, OverrideTestsAndMessage> overrideHandler)
    {
        if (JsonUtils.hasArray(root, arrayName))
        {
            JsonArray overridesArray = root.get(arrayName).getAsJsonArray();
            MaLiLib.debugLog("  > Found {} top level override definitions", overridesArray.size());
            OverrideTestsAndMessage data = new OverrideTestsAndMessage(s -> true, s -> true, s -> true, null);
            JsonUtils.getArrayElementsAsObjects(overridesArray, o -> this.readOverridesFromArrayElement(o, data, overrideHandler));

            return true;
        }

        return false;
    }

    protected void readOverridesFromArrayElement(JsonObject obj,
                                                 OverrideTestsAndMessage data,
                                                 BiFunction<JsonObject, OverrideTestsAndMessage, OverrideTestsAndMessage> overrideHandler)
    {
        OverrideTestsAndMessage data2 = overrideHandler.apply(obj, data);

        if (JsonUtils.hasArray(obj, "overrides"))
        {
            JsonArray overrideArr = obj.get("overrides").getAsJsonArray();
            MaLiLib.debugLog("    > Found {} policy overrides", overrideArr.size());
            JsonUtils.getArrayElementsAsObjects(overrideArr, o -> this.readOverridesFromArrayElement(o, data2, overrideHandler));
        }
    }

    protected OverrideTestsAndMessage readConfigOverridesFrom(JsonObject obj, OverrideTestsAndMessage data)
    {
        Predicate<String> modTest = this.getModTestOrDefault(obj, data.modTest);
        Predicate<String> categoryTest = this.getCategoryTestOrDefault(obj, data.categoryTest);
        Predicate<String> nameTest = this.getNameTestOrDefault(obj, data.nameTest);
        String message = JsonUtils.getStringOrDefault(obj, "message", data.message);

        data = new OverrideTestsAndMessage(modTest, categoryTest, nameTest, message);

        JsonElement overrideValue = obj.get("override_value");
        Pair<JsonElement, String> overrideData = Pair.of(overrideValue, message);
        String policy = JsonUtils.getStringOrDefault(obj, "policy", "");
        boolean enableOverride = "override".equalsIgnoreCase(policy);
        int overrideCount = 0;

        if (overrideValue == null && enableOverride)
        {
            MaLiLib.debugLog("      > Error: no 'override_value' found in override definition {}", obj);
            return data;
        }

        MaLiLib.debugLog("      > Reading override rule, policy = {}", policy);

        for (ModInfo modInfo : this.overridableConfigs.keySet())
        {
            String modId = modInfo.getModId();

            if (modTest.test(modId) == false)
            {
                continue;
            }

            for (Map<ConfigOptionCategory, List<BaseGenericConfig<?>>> map : this.overridableConfigs.values())
            {
                for (Map.Entry<ConfigOptionCategory, List<BaseGenericConfig<?>>> entry : map.entrySet())
                {
                    if (categoryTest.test(entry.getKey().getName()) == false)
                    {
                        continue;
                    }

                    for (BaseGenericConfig<?> cfg : entry.getValue())
                    {
                        if (nameTest.test(cfg.getName()) == false)
                        {
                            continue;
                        }

                        Pair<ConfigOptionCategory, BaseGenericConfig<?>> cac = Pair.of(entry.getKey(), cfg);

                        if (enableOverride)
                        {
                            this.configOverrides.put(cac, overrideData);
                        }
                        else
                        {
                            this.configOverrides.remove(cac);
                        }

                        ++overrideCount;
                    }
                }
            }
        }

        if (enableOverride)
        {
            MaLiLib.debugLog("      > Found {} override rules", overrideCount);
        }
        else
        {
            MaLiLib.debugLog("      > Found {} override removal rules", overrideCount);
        }

        return data;
    }

    protected OverrideTestsAndMessage readActionLocksFrom(JsonObject obj, OverrideTestsAndMessage data)
    {
        Predicate<String> modTest = this.getModTestOrDefault(obj, data.modTest);
        Predicate<String> nameTest = this.getNameTestOrDefault(obj, data.nameTest);
        String message = JsonUtils.getStringOrDefault(obj, "message", data.message);

        data = new OverrideTestsAndMessage(modTest, null, nameTest, message);

        String policy = JsonUtils.getStringOrDefault(obj, "policy", "");
        boolean shouldDisable = "disable".equalsIgnoreCase(policy);
        int ruleCount = 0;

        MaLiLib.debugLog("      > Reading action lock rule, policy = {}", policy);

        for (ModInfo modInfo : this.allBaseActions.keySet())
        {
            String modId = modInfo.getModId();

            if (modTest.test(modId) == false)
            {
                continue;
            }

            for (NamedAction action : this.allBaseActions.get(modInfo))
            {
                if (nameTest.test(action.getName()) == false)
                {
                    continue;
                }

                if (shouldDisable)
                {
                    this.lockedActions.put(action, message);
                }
                else
                {
                    this.lockedActions.remove(action);
                }

                ++ruleCount;
            }
        }

        if (shouldDisable)
        {
            MaLiLib.debugLog("      > Found {} action lock rules", ruleCount);
        }
        else
        {
            MaLiLib.debugLog("      > Found {} action lock removal rules", ruleCount);
        }

        return data;
    }

    protected OverrideTestsAndMessage readHotkeyLocksFrom(JsonObject obj, OverrideTestsAndMessage data)
    {
        Predicate<String> modTest = this.getModTestOrDefault(obj, data.modTest);
        Predicate<String> nameTest = this.getNameTestOrDefault(obj, data.nameTest);
        String message = JsonUtils.getStringOrDefault(obj, "message", data.message);

        data = new OverrideTestsAndMessage(modTest, null, nameTest, message);

        String policy = JsonUtils.getStringOrDefault(obj, "policy", "");
        boolean shouldDisable = "disable".equalsIgnoreCase(policy);
        int ruleCount = 0;

        MaLiLib.debugLog("      > Reading hotkey lock rule, policy = {}", policy);

        for (ModInfo modInfo : this.allHotkeys.keySet())
        {
            String modId = modInfo.getModId();

            if (modTest.test(modId) == false)
            {
                continue;
            }

            for (Hotkey hotkey : this.allHotkeys.get(modInfo))
            {
                if (nameTest.test(hotkey.getName()) == false)
                {
                    continue;
                }

                if (shouldDisable)
                {
                    this.lockedHotkeys.put(hotkey, message);
                }
                else
                {
                    this.lockedHotkeys.remove(hotkey);
                }

                ++ruleCount;
            }
        }

        if (shouldDisable)
        {
            MaLiLib.debugLog("      > Found {} hotkey lock rules", ruleCount);
        }
        else
        {
            MaLiLib.debugLog("      > Found {} hotkey lock removal rules", ruleCount);
        }

        return data;
    }

    @SuppressWarnings("unchecked")
    protected <T, C extends BaseGenericConfig<T>> int applyConfigOverrides()
    {
        int count = 0;

        for (Map.Entry<Pair<ConfigOptionCategory, BaseGenericConfig<?>>, Pair<JsonElement, String>> entry : this.configOverrides.entrySet())
        {
            Pair<ConfigOptionCategory, BaseGenericConfig<?>> pair = entry.getKey();
            Pair<JsonElement, String> overrideData = entry.getValue();
            JsonElement overrideValue = overrideData.getLeft();
            String name = "";

            if (overrideValue == null)
            {
                MaLiLib.LOGGER.warn("ConfigOverrideHandler#applyConfigOverrides(): Missing override value for '{}'", pair.getRight().getName());
                continue;
            }

            try
            {
                C cfg = (C) pair.getRight();
                name = cfg.getName();
                ConfigFromJsonOverrider<C> overrider = Registry.JSON_CONFIG_SERIALIZER.getOverrider(cfg);

                if (overrider != null)
                {
                    String msg = entry.getValue().getRight();

                    if (cfg instanceof BaseGenericConfig)
                    {
                        Object oldValue = ((BaseGenericConfig<?>) cfg).getValue();
                        overrider.overrideConfigValue(cfg, overrideValue);
                        cfg.setOverrideMessage(msg);

                        Object newValue = ((BaseGenericConfig<?>) cfg).getValue();
                        MaLiLib.debugLog("    Overrode '{}.{}' from '{}' to '{}' with message '{}'",
                                         pair.getLeft().getName(), name, oldValue, newValue, msg);
                    }
                    else
                    {
                        overrider.overrideConfigValue(cfg, overrideValue);
                        cfg.setOverrideMessage(msg);

                        MaLiLib.debugLog("    Overrode '{}.{}' (unknown values) with message '{}'",
                                         pair.getLeft().getName(), name, msg);
                    }

                    ++count;
                }

            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.warn("applyOverrides(): Exception while trying to override the value of '{}'", name);
            }
        }

        return count;
    }

    protected int applyActionLocks()
    {
        Registry.ACTION_REGISTRY.setLockedActions(this.lockedActions);
        return this.lockedActions.size();
    }

    protected int applyHotkeyLocks()
    {
        ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).setLockedHotkeys(this.lockedHotkeys);
        return this.lockedHotkeys.size();
    }

    @Nullable
    protected Predicate<String> getStringTest(JsonObject obj, String keyName, String errorMsgName)
    {
        Predicate<String> test = null;

        if (JsonUtils.hasString(obj, keyName))
        {
            String str = JsonUtils.getString(obj, keyName);

            try
            {
                Pattern pattern = Pattern.compile(str);
                test = (name) -> pattern.matcher(name).matches();
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.error("Failed to compile {} regex '{}'", errorMsgName, str);
                test = (name) -> false;
            }
        }

        return test;
    }

    @Nullable
    protected Predicate<String> getNameTest(JsonObject obj)
    {
        Predicate<String> test = this.getStringTest(obj, "name_regex", "mod name");

        if (test == null && JsonUtils.hasArray(obj, "names"))
        {
            final Set<String> names = new HashSet<>();
            final String prefix = JsonUtils.getStringOrDefault(obj, "name_prefix", "");
            final String suffix = JsonUtils.getStringOrDefault(obj, "name_suffix", "");
            JsonArray arr = obj.get("names").getAsJsonArray();
            final int size = arr.size();

            for (int i = 0; i < size; ++i)
            {
                names.add(prefix + arr.get(i).getAsString() + suffix);
            }

            test = names::contains;
        }

        return test;
    }

    protected Predicate<String> getModTestOrDefault(JsonObject obj, Predicate<String> defaultFilter)
    {
        Predicate<String> filter = this.getStringTest(obj, "mod", "mod name filter");
        return filter != null ? filter : defaultFilter;
    }

    protected Predicate<String> getCategoryTestOrDefault(JsonObject obj, Predicate<String> defaultFilter)
    {
        Predicate<String> filter = this.getStringTest(obj, "category", "category name filter");
        return filter != null ? filter : defaultFilter;
    }

    protected Predicate<String> getNameTestOrDefault(JsonObject obj, Predicate<String> defaultFilter)
    {
        Predicate<String> filter = this.getNameTest(obj);
        return filter != null ? filter : defaultFilter;
    }

    protected static class OverrideTestsAndMessage
    {
        @Nullable public final Predicate<String> modTest;
        @Nullable public final Predicate<String> categoryTest;
        @Nullable public final Predicate<String> nameTest;
        @Nullable public final String message;

        public OverrideTestsAndMessage(@Nullable Predicate<String> modTest,
                                       @Nullable Predicate<String> categoryTest,
                                       @Nullable Predicate<String> nameTest,
                                       @Nullable String message)
        {
            this.modTest = modTest;
            this.categoryTest = categoryTest;
            this.nameTest = nameTest;
            this.message = message;
        }
    }
}

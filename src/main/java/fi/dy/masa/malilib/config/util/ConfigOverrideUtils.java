package fi.dy.masa.malilib.config.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import com.google.common.collect.ArrayListMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.action.ActionContext;
import fi.dy.masa.malilib.config.ConfigManagerImpl;
import fi.dy.masa.malilib.config.ModConfig;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.ConfigSearchInfo;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.GameUtils;
import fi.dy.masa.malilib.util.JsonUtils;

public class ConfigOverrideUtils
{
    public static ActionResult resetConfigOverrides()
    {
        return resetConfigOverrides(ActionContext.COMMON);
    }

    public static ActionResult resetConfigOverrides(ActionContext ctx)
    {
        getAllToggleConfigs().values().forEach(BooleanConfig::disableOverride);
        return ActionResult.SUCCESS;
    }

    public static void applyConfigOverrides()
    {
        if (GameUtils.isSinglePlayer() == false)
        {
            Minecraft mc = GameUtils.getClient();
            ServerData serverData = mc.getCurrentServerData();

            if (serverData != null)
            {
                String motd = serverData.serverMOTD;
                String[] lines = motd.split("\\n");

                if (lines.length >= 4 && lines[3].isEmpty() == false && lines[3].charAt(0) == '{')
                {
                    String jsonStr = lines[3];
                    int lastBrace = jsonStr.lastIndexOf('}');

                    // Strip away the ending '§r'
                    if (lastBrace > 0)
                    {
                        jsonStr = jsonStr.substring(0, lastBrace + 1);
                    }

                    JsonElement el = JsonUtils.parseJsonFromString(jsonStr);

                    if (MaLiLibConfigs.Debug.DEBUG_MESSAGES.getBooleanValue())
                    {
                        MaLiLib.LOGGER.info("Cleaned up config override JSON string: '{}'", jsonStr);
                        MaLiLib.LOGGER.info("Parsed override JSON: '{}'", el);
                    }

                    if (el != null && el.isJsonObject())
                    {
                        applyConfigOverrides(el.getAsJsonObject());
                    }
                }
            }
            else
            {
                getAllToggleConfigs().values().forEach((cfg) -> {
                    cfg.setOverrideMessage("malilib.hover.config_override.error_getting_server_info_applying_fallback");
                    cfg.enableOverrideWithValue(false);
                });
            }
        }
    }

    protected static ArrayListMultimap<String, BooleanConfig> getAllToggleConfigs()
    {
        ArrayListMultimap<String, BooleanConfig> configs = ArrayListMultimap.create();
        List<ModConfig> modConfigs = ((ConfigManagerImpl) Registry.CONFIG_MANAGER).getAllModConfigs();

        for (ModConfig modConfig : modConfigs)
        {
            for (ConfigOptionCategory category : modConfig.getConfigOptionCategories())
            {
                for (ConfigInfo config : category.getConfigOptions())
                {
                    ConfigSearchInfo<ConfigInfo> info = Registry.CONFIG_WIDGET.getSearchInfo(config);

                    if (info != null)
                    {
                        BooleanConfig booleanConfig = info.getBooleanConfig(config);

                        if (booleanConfig != null)
                        {
                            configs.put(booleanConfig.getModInfo().getModId(), booleanConfig);
                        }
                    }
                }
            }
        }

        return configs;
    }

    public static void applyConfigOverrides(JsonObject root)
    {
        if (JsonUtils.hasArray(root, "mod_features") == false)
        {
            return;
        }

        ArrayListMultimap<String, BooleanConfig> configs = getAllToggleConfigs();

        JsonArray arrModFeatures = root.get("mod_features").getAsJsonArray();
        final int size = arrModFeatures.size();

        for (int i = 0; i < size; ++i)
        {
            JsonElement el = arrModFeatures.get(i);

            if (el.isJsonObject() == false)
            {
                continue;
            }

            JsonObject obj = el.getAsJsonObject();
            Predicate<String> modFilter = getModFilterOrDefault(obj, (name) -> true);
            Predicate<String> featureFilter = getFeatureFilterOrDefault(obj, (name) -> true);
            String message = JsonUtils.getStringOrDefault(obj, "message", null);

            applyOverridesFrom(obj, configs, modFilter, featureFilter, message);

            if (JsonUtils.hasArray(obj, "overrides"))
            {
                JsonArray overrideArr = obj.get("overrides").getAsJsonArray();
                final int overridesSize = overrideArr.size();

                if (MaLiLibConfigs.Debug.DEBUG_MESSAGES.getBooleanValue())
                {
                    MaLiLib.LOGGER.info("Found {} override definitions", overridesSize);
                }

                for (int overrideIndex = 0; overrideIndex < overridesSize; ++overrideIndex)
                {
                    JsonElement overrideEl = overrideArr.get(overrideIndex);

                    if (overrideEl.isJsonObject() == false)
                    {
                        continue;
                    }

                    JsonObject overrideObj = overrideEl.getAsJsonObject();
                    Predicate<String> overrideModFilter = getModFilterOrDefault(overrideObj, modFilter);
                    Predicate<String> overrideFeatureFilter = getFeatureFilterOrDefault(overrideObj, featureFilter);
                    String overrideMessage = JsonUtils.getStringOrDefault(overrideObj, "message", message);

                    applyOverridesFrom(overrideObj, configs, overrideModFilter, overrideFeatureFilter, overrideMessage);
                }
            }
        }

        int overrideCount = 0;

        for (BooleanConfig config : configs.values())
        {
            if (config.hasOverride())
            {
                ++overrideCount;
            }
        }

        if (overrideCount > 0)
        {
            if (MaLiLibConfigs.Debug.DEBUG_MESSAGES.getBooleanValue())
            {
                MaLiLib.LOGGER.info("Applied {} feature overrides", overrideCount);
            }

            MessageDispatcher.warning().time(8000).translate("malilib.message.info.feature_overrides_applied", overrideCount);
        }
    }

    protected static void applyOverridesFrom(JsonObject obj,
                                             ArrayListMultimap<String, BooleanConfig> configs,
                                             Predicate<String> modFilter,
                                             Predicate<String> configFilter,
                                             @Nullable String message)
    {
        String policy = JsonUtils.getStringOrDefault(obj, "policy", null);
        boolean enableOverride = "deny".equals(policy);
        boolean overrideValue = JsonUtils.getBooleanOrDefault(obj, "inverse", false);

        for (String modId : configs.keySet())
        {
            if (modFilter.test(modId) == false)
            {
                continue;
            }

            for (BooleanConfig cfg : configs.get(modId))
            {
                if (configFilter.test(cfg.getName()) == false)
                {
                    continue;
                }

                if (message != null)
                {
                    cfg.setOverrideMessage(message);
                }

                if (enableOverride)
                {
                    if (MaLiLibConfigs.Debug.DEBUG_MESSAGES.getBooleanValue())
                    {
                        MaLiLib.LOGGER.info("Overriding status of '{}' to '{}'", cfg.getName(), overrideValue);
                    }

                    cfg.enableOverrideWithValue(overrideValue);
                }
                else
                {
                    if (MaLiLibConfigs.Debug.DEBUG_MESSAGES.getBooleanValue())
                    {
                        MaLiLib.LOGGER.info("Disabling override for '{}'", cfg.getName());
                    }

                    cfg.disableOverride();
                }
            }
        }
    }

    protected static Predicate<String> getModFilterOrDefault(JsonObject obj, Predicate<String> defaultFilter)
    {
        Predicate<String> filter = getModFilter(obj);
        return filter != null ? filter : defaultFilter;
    }

    @Nullable
    protected static Predicate<String> getModFilter(JsonObject obj)
    {
        Predicate<String> modFilter = null;

        if (JsonUtils.hasString(obj, "mod"))
        {
            String filterStr = JsonUtils.getString(obj, "mod");

            try
            {
                Pattern pattern = Pattern.compile(filterStr);
                modFilter = (name) -> pattern.matcher(name).matches();
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.error("Failed to compile mod name filter regex '{}'", filterStr);
                modFilter = (name) -> false;
            }
        }

        return modFilter;
    }

    protected static Predicate<String> getFeatureFilterOrDefault(JsonObject obj, Predicate<String> defaultFilter)
    {
        Predicate<String> filter = getFeatureFilter(obj);
        return filter != null ? filter : defaultFilter;
    }

    @Nullable
    protected static Predicate<String> getFeatureFilter(JsonObject obj)
    {
        Predicate<String> featureFilter = null;

        if (JsonUtils.hasString(obj, "feature_filter"))
        {
            String filterStr = JsonUtils.getString(obj, "feature_filter");

            try
            {
                Pattern pattern = Pattern.compile(filterStr);
                featureFilter = (name) -> pattern.matcher(name).matches();
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.error("Failed to compile mod feature filter regex '{}'", filterStr);
                featureFilter = (name) -> false;
            }
        }
        else if (JsonUtils.hasArray(obj, "features"))
        {
            final String prefix = JsonUtils.getStringOrDefault(obj, "name_prefix", "");
            final String suffix = JsonUtils.getStringOrDefault(obj, "name_suffix", "");
            final Set<String> names = new HashSet<>();
            JsonArray arr = obj.get("features").getAsJsonArray();
            final int size = arr.size();
            for (int i = 0; i < size; ++i) { names.add(prefix + arr.get(i).getAsString() + suffix); }

            featureFilter = names::contains;
        }

        return featureFilter;
    }
}

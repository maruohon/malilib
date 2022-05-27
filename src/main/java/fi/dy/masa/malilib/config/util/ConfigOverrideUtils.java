package fi.dy.masa.malilib.config.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import com.google.common.collect.ArrayListMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.client.multiplayer.ServerData;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.action.ActionContext;
import fi.dy.masa.malilib.config.ConfigManagerImpl;
import fi.dy.masa.malilib.config.ModConfig;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.config.option.BooleanAndDoubleConfig;
import fi.dy.masa.malilib.config.option.BooleanAndFileConfig;
import fi.dy.masa.malilib.config.option.BooleanAndIntConfig;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.OverridableConfig;
import fi.dy.masa.malilib.gui.config.ConfigSearchInfo;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.data.BooleanStorageWithDefault;
import fi.dy.masa.malilib.util.data.json.JsonUtils;
import fi.dy.masa.malilib.util.game.wrap.GameUtils;

public class ConfigOverrideUtils
{
    public static ActionResult resetConfigOverrides()
    {
        return resetConfigOverrides(ActionContext.COMMON);
    }

    public static ActionResult resetConfigOverrides(ActionContext ctx)
    {
        getAllOverridableConfigs().values().forEach(p -> p.getRight().disableOverride());
        return ActionResult.SUCCESS;
    }

    public static void applyConfigOverrides()
    {
        try
        {
            if (GameUtils.isSinglePlayer())
            {
                tryApplyOverridesFromLocalConfig();
            }
            else
            {
                tryApplyOverridesFromServer();
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.error("Exception while trying to apply config overrides", e);
        }
    }

    protected static boolean tryApplyOverridesFromString(String str)
    {
        if (StringUtils.isBlank(str) || str.charAt(0) != '{')
        {
            return false;
        }

        int lastBrace = str.lastIndexOf('}');

        if (lastBrace <= 1)
        {
            return false;
        }

        // Strip away the ending '§r'
        str = str.substring(0, lastBrace + 1);

        JsonElement el = JsonUtils.parseJsonFromString(str);

        MaLiLib.debugLog("Cleaned up config override JSON string: '{}'", str);
        MaLiLib.debugLog("Parsed override JSON: '{}'", el);

        if (el != null && el.isJsonObject())
        {
            return applyConfigOverrides(el.getAsJsonObject());
        }

        return false;
    }

    protected static boolean tryApplyOverridesFromURL(String str)
    {
        if (str.startsWith("http://") || str.startsWith("https://"))
        {
            // Strip away the ending '§r'
            str = str.substring(0, str.length() - 2);

            String pageContent = tryFetchPage(str, 2000);

            if (pageContent != null)
            {
                return tryApplyOverridesFromString(pageContent.trim());
            }
        }

        return false;
    }

    protected static void tryApplyOverridesFromLocalConfig()
    {
        Path configDir = ConfigUtils.getActiveConfigDirectoryPath();
        File file = configDir.resolve(MaLiLibReference.MOD_ID).resolve("config_overrides.json").toFile();
        String str = FileUtils.readFileAsString(file, -1);

        if (str != null)
        {
            tryApplyOverridesFromString(str);
        }
    }

    protected static void tryApplyOverridesFromServer()
    {
        ServerData serverData = GameUtils.getClient().getCurrentServerData();

        if (serverData != null)
        {
            String motd = serverData.serverMOTD;
            String[] lines = motd.split("\\n");

            for (int index = 3; index < lines.length; ++index)
            {
                String str = lines[index];

                if (tryApplyOverridesFromString(str))
                {
                    return;
                }

                if (tryApplyOverridesFromURL(str))
                {
                    return;
                }
            }
        }
    }

    protected static <C extends ConfigInfo & OverridableConfig<?>>
    ArrayListMultimap<String, Pair<ConfigOptionCategory, C>> getAllOverridableConfigs()
    {
        List<ModConfig> modConfigs = ((ConfigManagerImpl) Registry.CONFIG_MANAGER).getAllModConfigs();
        ArrayListMultimap<String, Pair<ConfigOptionCategory, C>> configs = ArrayListMultimap.create();

        for (ModConfig modConfig : modConfigs)
        {
            for (ConfigOptionCategory category : modConfig.getConfigOptionCategories())
            {
                for (ConfigInfo config : category.getConfigOptions())
                {
                    ConfigSearchInfo<ConfigInfo> info = Registry.CONFIG_WIDGET.getSearchInfo(config);

                    if (info != null && info.hasToggle)
                    {
                        BooleanStorageWithDefault storage = info.getBooleanStorage(config);

                        if (storage instanceof OverridableConfig<?>)
                        {
                            @SuppressWarnings("unchecked")
                            C cfg = (C) config;
                            configs.put(config.getModInfo().getModId(), Pair.of(category, cfg));
                        }
                    }
                }
            }
        }

        return configs;
    }

    public static <C extends ConfigInfo & OverridableConfig<?>>
    boolean applyConfigOverrides(JsonObject root)
    {
        if (JsonUtils.getStringOrDefault(root, "type", "?").equals("malilib_config_overrides") == false ||
            JsonUtils.hasArray(root, "config_overrides") == false)
        {
            return false;
        }

        ArrayListMultimap<String, Pair<ConfigOptionCategory, C>> categoriesAndConfigs = getAllOverridableConfigs();
        HashMap<C, Pair<JsonElement, String>> activeOverrides = new HashMap<>();

        JsonArray arrModFeatures = root.get("config_overrides").getAsJsonArray();
        final int size = arrModFeatures.size();

        for (int i = 0; i < size; ++i)
        {
            JsonElement el = arrModFeatures.get(i);

            if (el.isJsonObject() == false)
            {
                continue;
            }

            JsonObject obj = el.getAsJsonObject();
            Predicate<String> modFilter = getModTestOrDefault(obj, (name) -> true);
            Predicate<String> categoryFilter = getCategoryTestOrDefault(obj, (name) -> true);
            Predicate<String> featureFilter = getFeatureTestOrDefault(obj, (name) -> true);
            String message = JsonUtils.getStringOrDefault(obj, "message", null);

            readOverridesFrom(obj, categoriesAndConfigs, activeOverrides, modFilter,
                              categoryFilter, featureFilter, message);

            if (JsonUtils.hasArray(obj, "overrides"))
            {
                JsonArray overrideArr = obj.get("overrides").getAsJsonArray();
                final int overridesSize = overrideArr.size();

                MaLiLib.debugLog("Found {} override definitions", overridesSize);

                for (int overrideIndex = 0; overrideIndex < overridesSize; ++overrideIndex)
                {
                    JsonElement overrideEl = overrideArr.get(overrideIndex);

                    if (overrideEl.isJsonObject() == false)
                    {
                        continue;
                    }

                    JsonObject overrideObj = overrideEl.getAsJsonObject();
                    Predicate<String> overrideModFilter = getModTestOrDefault(overrideObj, modFilter);
                    Predicate<String> overrideCategoryFilter = getCategoryTestOrDefault(overrideObj, categoryFilter);
                    Predicate<String> overrideFeatureFilter = getFeatureTestOrDefault(overrideObj, featureFilter);
                    String overrideMessage = JsonUtils.getStringOrDefault(overrideObj, "message", message);

                    readOverridesFrom(overrideObj, categoriesAndConfigs, activeOverrides, overrideModFilter,
                                      overrideCategoryFilter, overrideFeatureFilter, overrideMessage);
                }
            }
        }

        int overrideCount = applyOverrides(activeOverrides);

        /*
        for (Pair<ConfigOptionCategory, C> categoryAndConfig : categoriesAndConfigs.values())
        {
            if (categoryAndConfig.getRight().hasOverride())
            {
                ++overrideCount;
            }
        }
        */

        if (overrideCount > 0)
        {
            MaLiLib.debugLog("Applied {} feature overrides", overrideCount);
            MessageDispatcher.warning(8000).translate("malilib.message.info.config_overrides_applied", overrideCount);
        }

        return overrideCount > 0;
    }

    protected static <C extends ConfigInfo & OverridableConfig<?>>
    void readOverridesFrom(JsonObject obj,
                           ArrayListMultimap<String, Pair<ConfigOptionCategory, C>> categoriesAndConfigs,
                           Map<C, Pair<JsonElement, String>> activeOverrides,
                           Predicate<String> modFilter,
                           Predicate<String> categoryFilter,
                           Predicate<String> configFilter,
                           @Nullable String message)
    {
        String policy = JsonUtils.getStringOrDefault(obj, "policy", "");
        JsonElement overrideValue = obj.get("override_value");
        Pair<JsonElement, String> pair = Pair.of(overrideValue, message);
        boolean enableOverride = "override".equalsIgnoreCase(policy);

        for (String modId : categoriesAndConfigs.keySet())
        {
            if (modFilter.test(modId) == false)
            {
                continue;
            }

            for (Pair<ConfigOptionCategory, C> cac : categoriesAndConfigs.get(modId))
            {
                C cfg = cac.getRight();
                String name = cfg.getName();

                if (categoryFilter.test(cac.getLeft().getName()) == false ||
                    configFilter.test(name) == false)
                {
                    continue;
                }

                if (enableOverride)
                {
                    activeOverrides.put(cfg, pair);
                }
                else
                {
                    activeOverrides.remove(cfg);
                }
            }
        }
    }

    protected static <C extends ConfigInfo & OverridableConfig<?>>
    int applyOverrides(Map<C, Pair<JsonElement, String>> activeOverrides)
    {
        int count = 0;

        for (Map.Entry<C, Pair<JsonElement, String>> entry : activeOverrides.entrySet())
        {
            C cfg = entry.getKey();
            JsonElement overrideValue = entry.getValue().getLeft();
            @Nullable String message = entry.getValue().getRight();

            if (overrideValue == null)
            {
                MaLiLib.LOGGER.warn("applyOverrides(): Missing override value for '{}'", cfg.getName());
                continue;
            }

            // TODO FIXME this needs a better/proper way to load the value from the JSON de-serializer,
            // and then set that value as the override value.
            try
            {
                ConfigSearchInfo<ConfigInfo> info = Registry.CONFIG_WIDGET.getSearchInfo(cfg);

                if (info != null && info.hasToggle && overrideValue.isJsonPrimitive())
                {
                    BooleanStorageWithDefault storage = info.getBooleanStorage(cfg);
                    boolean booleanValue = overrideValue.getAsBoolean();

                    if (storage instanceof BooleanConfig)
                    {
                        MaLiLib.debugLog("Overriding value of '{}' to '{}'", cfg.getName(), booleanValue);
                        ((BooleanConfig) cfg).enableOverrideWithValue(booleanValue);
                        cfg.setOverrideMessage(message);
                        ++count;
                    }
                    else if (storage instanceof BooleanAndIntConfig)
                    {
                        MaLiLib.debugLog("Overriding value of '{}' to '{}'", cfg.getName(), booleanValue);
                        BooleanAndIntConfig config = (BooleanAndIntConfig) cfg;
                        BooleanAndIntConfig.BooleanAndInt currentValue = config.getValue();
                        config.enableOverrideWithValue(new BooleanAndIntConfig.BooleanAndInt(booleanValue, currentValue.intValue));
                        cfg.setOverrideMessage(message);
                        ++count;
                    }
                    else if (storage instanceof BooleanAndDoubleConfig)
                    {
                        MaLiLib.debugLog("Overriding value of '{}' to '{}'", cfg.getName(), booleanValue);
                        BooleanAndDoubleConfig config = (BooleanAndDoubleConfig) cfg;
                        BooleanAndDoubleConfig.BooleanAndDouble currentValue = config.getValue();
                        config.enableOverrideWithValue(new BooleanAndDoubleConfig.BooleanAndDouble(booleanValue, currentValue.doubleValue));
                        cfg.setOverrideMessage(message);
                        ++count;
                    }
                    else if (storage instanceof BooleanAndFileConfig)
                    {
                        MaLiLib.debugLog("Overriding value of '{}' to '{}'", cfg.getName(), booleanValue);
                        BooleanAndFileConfig config = (BooleanAndFileConfig) cfg;
                        BooleanAndFileConfig.BooleanAndFile currentValue = config.getValue();
                        config.enableOverrideWithValue(new BooleanAndFileConfig.BooleanAndFile(booleanValue, currentValue.fileValue));
                        cfg.setOverrideMessage(message);
                        ++count;
                    }
                }
            }
            catch (Exception ignore) {}
        }

        return count;
    }

    protected static Predicate<String> getModTestOrDefault(JsonObject obj, Predicate<String> defaultFilter)
    {
        Predicate<String> filter = getStringTest(obj, "mod", "mod name filter");
        return filter != null ? filter : defaultFilter;
    }

    protected static Predicate<String> getCategoryTestOrDefault(JsonObject obj, Predicate<String> defaultFilter)
    {
        Predicate<String> filter = getStringTest(obj, "category", "category name filter");
        return filter != null ? filter : defaultFilter;
    }

    protected static Predicate<String> getFeatureTestOrDefault(JsonObject obj, Predicate<String> defaultFilter)
    {
        Predicate<String> filter = getFeatureTest(obj);
        return filter != null ? filter : defaultFilter;
    }

    @Nullable
    protected static Predicate<String> getStringTest(JsonObject obj, String keyName, String errorMsgName)
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
    protected static Predicate<String> getFeatureTest(JsonObject obj)
    {
        Predicate<String> test = getStringTest(obj, "name_regex", "mod name");

        if (test == null && JsonUtils.hasArray(obj, "features"))
        {
            final String prefix = JsonUtils.getStringOrDefault(obj, "name_prefix", "");
            final String suffix = JsonUtils.getStringOrDefault(obj, "name_suffix", "");
            final Set<String> names = new HashSet<>();
            JsonArray arr = obj.get("features").getAsJsonArray();
            final int size = arr.size();

            for (int i = 0; i < size; ++i)
            {
                names.add(prefix + arr.get(i).getAsString() + suffix);
            }

            test = names::contains;
        }

        return test;
    }

    protected static HttpURLConnection createUrlConnection(URL url, int timeout) throws IOException
    {
        MaLiLib.debugLog("Opening connection to {}", url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);

        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        connection.setUseCaches(false);

        return connection;
    }

    @Nullable
    protected static String performGetRequest(URL url, int timeout) throws IOException
    {
        MaLiLib.debugLog("Reading data from: " + url);
        HttpURLConnection connection = createUrlConnection(url, timeout);
        InputStream inputStream = null;

        try
        {
            inputStream = connection.getInputStream();
            String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            MaLiLib.debugLog("Successful read, server response was: " + connection.getResponseCode());
            MaLiLib.debugLog("Result: " + result);
            return result;
        }
        catch (IOException e)
        {
            IOUtils.closeQuietly(inputStream);
            inputStream = connection.getErrorStream();

            if (inputStream != null)
            {
                MaLiLib.debugLog("Reading error page from: " + url);
                final String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                MaLiLib.debugLog("Successful read, server response was: " + connection.getResponseCode());
                MaLiLib.debugLog("Result: " + result);
                return result;
            }
            else
            {
                MaLiLib.debugLog("Request failed", e);
            }
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }

        return null;
    }

    @Nullable
    public static String tryFetchPage(String pageURL, int timeout)
    {
        try
        {
            return performGetRequest(new URL(pageURL), timeout);
        }
        catch (Exception e)
        {
            MaLiLib.debugLog("Page fetch failed", e);
        }

        return null;
    }
}

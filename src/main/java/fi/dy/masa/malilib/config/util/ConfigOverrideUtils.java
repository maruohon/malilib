package fi.dy.masa.malilib.config.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.client.multiplayer.ServerData;
import fi.dy.masa.malilib.MaLiLib;
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
import fi.dy.masa.malilib.util.FileUtils;
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
        getAllToggleConfigs().values().forEach(p -> p.getRight().disableOverride());
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
        File file = new File(ConfigUtils.getActiveConfigDirectory(), "malilib_config_overrides.json");
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

    protected static ArrayListMultimap<String, Pair<ConfigOptionCategory, BooleanConfig>> getAllToggleConfigs()
    {
        ArrayListMultimap<String, Pair<ConfigOptionCategory, BooleanConfig>> configs = ArrayListMultimap.create();
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
                            configs.put(booleanConfig.getModInfo().getModId(), Pair.of(category, booleanConfig));
                        }
                    }
                }
            }
        }

        return configs;
    }

    public static boolean applyConfigOverrides(JsonObject root)
    {
        if (JsonUtils.getStringOrDefault(root, "type", "?").equals("malilib_config_overrides") == false ||
            JsonUtils.hasArray(root, "config_overrides") == false)
        {
            return false;
        }

        ArrayListMultimap<String, Pair<ConfigOptionCategory, BooleanConfig>> categoriesAndConfigs = getAllToggleConfigs();
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
            Predicate<String> modFilter = getModFilterOrDefault(obj, (name) -> true);
            Predicate<String> categoryFilter = getCategoryFilterOrDefault(obj, (name) -> true);
            Predicate<String> featureFilter = getFeatureFilterOrDefault(obj, (name) -> true);
            String message = JsonUtils.getStringOrDefault(obj, "message", null);

            applyOverridesFrom(obj, categoriesAndConfigs, modFilter, categoryFilter, featureFilter, message);

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
                    Predicate<String> overrideModFilter = getModFilterOrDefault(overrideObj, modFilter);
                    Predicate<String> overrideCategoryFilter = getCategoryFilterOrDefault(overrideObj, categoryFilter);
                    Predicate<String> overrideFeatureFilter = getFeatureFilterOrDefault(overrideObj, featureFilter);
                    String overrideMessage = JsonUtils.getStringOrDefault(overrideObj, "message", message);

                    applyOverridesFrom(overrideObj, categoriesAndConfigs,
                                       overrideModFilter, overrideCategoryFilter,
                                       overrideFeatureFilter, overrideMessage);
                }
            }
        }

        int overrideCount = 0;

        for (Pair<ConfigOptionCategory, BooleanConfig> categoryAndConfig : categoriesAndConfigs.values())
        {
            if (categoryAndConfig.getRight().hasOverride())
            {
                ++overrideCount;
            }
        }

        if (overrideCount > 0)
        {
            MaLiLib.debugLog("Applied {} feature overrides", overrideCount);
            MessageDispatcher.warning(8000).translate("malilib.message.info.config_overrides_applied", overrideCount);
        }

        return overrideCount > 0;
    }

    protected static void applyOverridesFrom(JsonObject obj,
                                             ArrayListMultimap<String, Pair<ConfigOptionCategory, BooleanConfig>> categoriesAndConfigs,
                                             Predicate<String> modFilter,
                                             Predicate<String> categoryFilter,
                                             Predicate<String> configFilter,
                                             @Nullable String message)
    {
        String policy = JsonUtils.getStringOrDefault(obj, "policy", null);
        boolean enableOverride = "deny".equalsIgnoreCase(policy);
        boolean overrideValue = JsonUtils.getBooleanOrDefault(obj, "inverse", false);

        for (String modId : categoriesAndConfigs.keySet())
        {
            if (modFilter.test(modId) == false)
            {
                continue;
            }

            for (Pair<ConfigOptionCategory, BooleanConfig> cac : categoriesAndConfigs.get(modId))
            {
                BooleanConfig cfg = cac.getRight();

                if (categoryFilter.test(cac.getLeft().getName()) == false ||
                    configFilter.test(cfg.getName()) == false)
                {
                    continue;
                }

                if (message != null)
                {
                    cfg.setOverrideMessage(message);
                }

                if (enableOverride)
                {
                    MaLiLib.debugLog("Overriding status of '{}' to '{}'", cfg.getName(), overrideValue);
                    cfg.enableOverrideWithValue(overrideValue);
                }
                else
                {
                    MaLiLib.debugLog("Disabling override for '{}'", cfg.getName());
                    cfg.disableOverride();
                }
            }
        }
    }

    protected static Predicate<String> getModFilterOrDefault(JsonObject obj, Predicate<String> defaultFilter)
    {
        Predicate<String> filter = getStringTest(obj, "mod", "mod name filter");
        return filter != null ? filter : defaultFilter;
    }

    protected static Predicate<String> getCategoryFilterOrDefault(JsonObject obj, Predicate<String> defaultFilter)
    {
        Predicate<String> filter = getStringTest(obj, "category", "category name filter");
        return filter != null ? filter : defaultFilter;
    }

    protected static Predicate<String> getFeatureFilterOrDefault(JsonObject obj, Predicate<String> defaultFilter)
    {
        Predicate<String> filter = getFeatureFilter(obj);
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
    protected static Predicate<String> getFeatureFilter(JsonObject obj)
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

package malilib.config.util;

import com.google.gson.JsonObject;

import malilib.action.ActionContext;
import malilib.input.ActionResult;

public class ConfigOverrideUtils
{
    public static ActionResult resetConfigOverrides()
    {
        return resetConfigOverrides(ActionContext.COMMON);
    }

    public static ActionResult resetConfigOverrides(ActionContext ctx)
    {
        ConfigOverrideHandler handler = new ConfigOverrideHandler();
        handler.clearConfigOverrides();
        return ActionResult.SUCCESS;
    }

    public static void applyConfigOverrides()
    {
        ConfigOverrideHandler handler = new ConfigOverrideHandler();
        handler.readAndApplyConfigOverrides();
    }

    public static void applyConfigOverridesFromServer(JsonObject obj)
    {
        ConfigOverrideHandler handler = new ConfigOverrideHandler();
        handler.applyOverridesFromServer(obj);
    }
}

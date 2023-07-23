package malilib.config.util;

import com.google.gson.JsonObject;

import malilib.action.ActionContext;
import malilib.input.ActionResult;

public class ConfigLockUtils
{
    public static ActionResult resetConfigLocks()
    {
        return resetConfigLocks(ActionContext.COMMON);
    }

    public static ActionResult resetConfigLocks(ActionContext ctx)
    {
        ConfigLockHandler handler = new ConfigLockHandler();
        handler.clearLocks();
        return ActionResult.SUCCESS;
    }

    public static void applyConfigLocks()
    {
        ConfigLockHandler handler = new ConfigLockHandler();
        handler.readAndApplyLocks();
    }

    public static void applyConfigLocksFromServer(JsonObject obj)
    {
        ConfigLockHandler handler = new ConfigLockHandler();
        handler.applyLocksFromServer(obj);
    }
}

package fi.dy.masa.malilib.action;

import fi.dy.masa.malilib.input.ActionResult;

public interface ParameterizedAction
{
    ActionResult executeWithArgument(ActionContext ctx, String arg);
}

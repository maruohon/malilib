package fi.dy.masa.malilib.action;

import fi.dy.masa.malilib.input.ActionResult;

public interface ParameterizedAction
{
    /**
     * Runs the action/task code
     * @param ctx the common context data for the action
     * @param arg the string argument for the action.
     *            This may contain multiple different arguments
     *            that the action will further parse and handle.
     * @return the result of running the action
     */
    ActionResult executeWithArgument(ActionContext ctx, String arg);
}

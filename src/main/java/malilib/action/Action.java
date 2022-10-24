package malilib.action;

import malilib.input.ActionResult;

public interface Action
{
    /**
     * Runs the action/task code
     * @param ctx the common context data for the action
     * @return the result of running the action
     */
    ActionResult execute(ActionContext ctx);
}

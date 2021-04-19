package fi.dy.masa.malilib.action;

import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.TextInputScreen;
import fi.dy.masa.malilib.input.ActionResult;

public class BaseParameterizedAction implements Action
{
    protected final ParameterizedAction action;
    @Nullable protected final String argument;

    public BaseParameterizedAction(ParameterizedAction action, @Nullable String argument)
    {
        this.action = action;
        this.argument = argument;
    }

    @Nullable
    public String getArgument()
    {
        return this.argument;
    }

    public BaseParameterizedAction parameterize(@Nullable String argument)
    {
        return of(this.action, argument);
    }

    protected boolean runWithArgument(String argument)
    {
        if (StringUtils.isBlank(argument) == false)
        {
            this.action.executeWithArgument(new ActionContext(), argument);
            return true;
        }

        return false;
    }

    @Override
    public ActionResult execute(ActionContext ctx)
    {
        if (this.argument == null)
        {
            TextInputScreen screen = new TextInputScreen("malilib.gui.title.provide_arguments_for_action",
                                                         "", null, this::runWithArgument);
            BaseScreen.openScreen(screen);
            return ActionResult.SUCCESS;
        }

        return this.action.executeWithArgument(new ActionContext(), this.argument);
    }

    public static BaseParameterizedAction of(ParameterizedAction action)
    {
        return new BaseParameterizedAction(action, null);
    }

    public static BaseParameterizedAction of(ParameterizedAction action, @Nullable String argument)
    {
        return new BaseParameterizedAction(action, argument);
    }
}

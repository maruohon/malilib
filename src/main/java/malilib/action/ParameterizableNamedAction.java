package malilib.action;

import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import malilib.action.util.ActionUtils;
import malilib.gui.BaseScreen;
import malilib.gui.TextInputScreen;
import malilib.input.ActionResult;
import malilib.util.data.ModInfo;
import malilib.util.data.json.JsonUtils;

public class ParameterizableNamedAction extends NamedAction
{
    protected final ParameterizedAction action;

    public ParameterizableNamedAction(String name,
                                      String translationKey,
                                      ModInfo mod,
                                      ParameterizedAction action)
    {
        super(ActionType.PARAMETERIZABLE, name, translationKey, mod);

        this.action = action;
        this.coloredDisplayNameTranslationKey = "malilib.label.actions.parameterizable_entry_widget_name";
    }

    @Override
    public boolean isUserAdded()
    {
        return false;
    }

    @Override
    public ActionResult execute(ActionContext ctx)
    {
        if (this.checkIsAllowedOrPrintMessage() == false)
        {
            return ActionResult.FAIL;
        }

        TextInputScreen screen = new TextInputScreen("malilib.title.screen.provide_arguments_for_action",
                                                     str -> this.executeWithArgument(ctx, str));
        BaseScreen.openPopupScreen(screen);

        return ActionResult.SUCCESS;
    }

    public boolean executeWithArgument(ActionContext ctx, String argument)
    {
        if (StringUtils.isBlank(argument) == false)
        {
            if (this.checkIsAllowedOrPrintMessage())
            {
                this.action.executeWithArgument(ctx, argument);
            }

            return true;
        }

        return false;
    }

    public ParameterizedNamedAction parameterize(String name, String argument)
    {
        return ParameterizedNamedAction.of(name, this, argument);
    }

    public static ParameterizableNamedAction of(ModInfo mod, String name, ParameterizedAction action)
    {
        String translationKey = ActionUtils.createTranslationKeyFor(mod, name);
        return new ParameterizableNamedAction(name, translationKey, mod, action);
    }

    @Override
    public NamedAction loadFromJson(JsonObject obj)
    {
        if (JsonUtils.hasString(obj, "name") &&
            JsonUtils.hasString(obj, "arg"))
        {
            String name = JsonUtils.getString(obj, "name");
            String arg = JsonUtils.getString(obj, "arg");
            return this.parameterize(name, arg);
        }

        return this;
    }

    @Nullable
    public static ParameterizableNamedAction parameterizableActionFromJson(JsonObject obj)
    {
        NamedAction action = NamedAction.baseActionFromJson(obj);

        if (action instanceof ParameterizableNamedAction)
        {
            return (ParameterizableNamedAction) action;
        }

        return null;
    }
}

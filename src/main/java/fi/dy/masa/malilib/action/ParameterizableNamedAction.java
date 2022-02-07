package fi.dy.masa.malilib.action;

import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.TextInputScreen;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

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
    }

    @Override
    public ActionResult execute(ActionContext ctx)
    {
        TextInputScreen screen = new TextInputScreen("malilib.gui.title.provide_arguments_for_action",
                                                     this::executeWithArgument);
        BaseScreen.openScreen(screen);

        return ActionResult.SUCCESS;
    }

    public boolean executeWithArgument(String argument)
    {
        if (StringUtils.isBlank(argument) == false)
        {
            this.action.executeWithArgument(ActionContext.COMMON, argument);
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

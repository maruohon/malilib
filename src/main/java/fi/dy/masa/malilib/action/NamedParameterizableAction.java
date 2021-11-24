package fi.dy.masa.malilib.action;

import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.TextInputScreen;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.util.data.ModInfo;

public class NamedParameterizableAction extends NamedAction
{
    protected final ParameterizedAction action;

    public NamedParameterizableAction(ParameterizedAction action, ModInfo mod, String name,
                                      String registryName, String translationKey)
    {
        super(mod, name, registryName, translationKey);

        this.action = action;
    }

    @Override
    public boolean needsArgument()
    {
        return true;
    }

    public NamedParameterizedAction parameterize(@Nullable String argument)
    {
        return NamedParameterizedAction.of(this, argument);
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

    @Override
    public ActionResult execute(ActionContext ctx)
    {
        TextInputScreen screen = new TextInputScreen("malilib.gui.title.provide_arguments_for_action",
                                                     "", null, this::executeWithArgument);
        BaseScreen.openScreen(screen);

        return ActionResult.SUCCESS;
    }

    public static NamedParameterizableAction of(ModInfo mod, String name, ParameterizedAction action)
    {
        return new NamedParameterizableAction(action, mod, name,
                                              ActionUtils.createRegistryNameFor(mod, name),
                                              ActionUtils.createTranslationKeyFor(mod, name));
    }
}

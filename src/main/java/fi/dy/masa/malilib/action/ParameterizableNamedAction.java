package fi.dy.masa.malilib.action;

import org.apache.commons.lang3.StringUtils;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.TextInputScreen;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.util.data.ModInfo;

public class ParameterizableNamedAction extends NamedAction
{
    protected final ParameterizedAction action;

    public ParameterizableNamedAction(String name,
                                      String registryName,
                                      String translationKey,
                                      ModInfo mod,
                                      ParameterizedAction action)
    {
        super(name, registryName, translationKey, mod);

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
        return new ParameterizableNamedAction(name,
                                              ActionUtils.createRegistryNameFor(mod, name),
                                              ActionUtils.createTranslationKeyFor(mod, name),
                                              mod, action);
    }
}

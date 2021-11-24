package fi.dy.masa.malilib.action;

import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.data.ModInfo;

public class SimpleNamedAction extends NamedAction
{
    protected Action action;

    public SimpleNamedAction(Action action, ModInfo mod, String name,
                             String registryName, String translationKey)
    {
        super(mod, name, registryName, translationKey);

        this.action = action;
    }

    @Override
    public ActionResult execute(ActionContext ctx)
    {
        return this.action.execute(ctx);
    }

    public static SimpleNamedAction of(ModInfo mod, String name, Action action)
    {
        return new SimpleNamedAction(action, mod, name,
                                     ActionUtils.createRegistryNameFor(mod, name),
                                     ActionUtils.createTranslationKeyFor(mod, name));
    }

    public static SimpleNamedAction of(ModInfo mod, String name, EventListener listener)
    {
        return new SimpleNamedAction(EventAction.of(listener), mod, name,
                                     ActionUtils.createRegistryNameFor(mod, name),
                                     ActionUtils.createTranslationKeyFor(mod, name));
    }

}

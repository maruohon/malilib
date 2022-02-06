package fi.dy.masa.malilib.action;

import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.data.ModInfo;

public class SimpleNamedAction extends NamedAction
{
    protected Action action;

    public SimpleNamedAction(String name,
                             String registryName,
                             String translationKey,
                             ModInfo mod,
                             Action action)
    {
        super(name, registryName, translationKey, mod);

        this.action = action;
    }

    @Override
    public ActionResult execute(ActionContext ctx)
    {
        return this.action.execute(ctx);
    }

    public static SimpleNamedAction of(ModInfo mod, String name, Action action)
    {
        return new SimpleNamedAction(name,
                                     ActionUtils.createRegistryNameFor(mod, name),
                                     ActionUtils.createTranslationKeyFor(mod, name),
                                     mod, action);
    }

    public static SimpleNamedAction of(ModInfo mod, String name, EventListener listener)
    {
        return new SimpleNamedAction(name,
                                     ActionUtils.createRegistryNameFor(mod, name),
                                     ActionUtils.createTranslationKeyFor(mod, name),
                                     mod, EventAction.of(listener));
    }

}

package fi.dy.masa.malilib.action;

import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.data.ModInfo;

public class SimpleNamedAction extends NamedAction
{
    protected Action action;

    public SimpleNamedAction(String name,
                             String translationKey,
                             ModInfo mod,
                             Action action)
    {
        super(ActionType.SIMPLE, name, translationKey, mod);

        this.action = action;
    }

    @Override
    public boolean isUserAdded()
    {
        return false;
    }

    @Override
    public ActionResult execute(ActionContext ctx)
    {
        return this.action.execute(ctx);
    }

    public static SimpleNamedAction of(ModInfo mod, String name, Action action)
    {
        String translationKey = ActionUtils.createTranslationKeyFor(mod, name);
        return new SimpleNamedAction(name, translationKey, mod, action);
    }

    public static SimpleNamedAction of(ModInfo mod, String name, EventListener listener)
    {
        String translationKey = ActionUtils.createTranslationKeyFor(mod, name);
        return new SimpleNamedAction(name, translationKey, mod, EventAction.of(listener));
    }
}

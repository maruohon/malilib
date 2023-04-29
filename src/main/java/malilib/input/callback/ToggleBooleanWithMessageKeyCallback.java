package malilib.input.callback;

import javax.annotation.Nullable;

import malilib.action.Action;
import malilib.action.ActionContext;
import malilib.action.builtin.BooleanToggleAction;
import malilib.config.option.BooleanContainingConfig;
import malilib.input.ActionResult;
import malilib.input.KeyAction;
import malilib.input.KeyBind;
import malilib.overlay.message.MessageHelpers.BooleanConfigMessageFactory;
import malilib.overlay.message.MessageOutput;

public class ToggleBooleanWithMessageKeyCallback implements HotkeyCallback
{
    protected final BooleanContainingConfig<?> config;
    @Nullable protected final BooleanConfigMessageFactory messageFactory;

    public ToggleBooleanWithMessageKeyCallback(BooleanContainingConfig<?> config)
    {
        this(config, null);
    }

    public ToggleBooleanWithMessageKeyCallback(BooleanContainingConfig<?> config,
                                               @Nullable BooleanConfigMessageFactory messageFactory)
    {
        this.config = config;
        this.messageFactory = messageFactory;
    }

    @Override
    public ActionResult onKeyAction(KeyAction action, KeyBind key)
    {
        MessageOutput messageOutput = key.getSettings().getMessageType();
        // The action needs to be created here to capture the MessageOutput from the KeyBind
        Action toggleAction = BooleanToggleAction.of(this.config, this.messageFactory, () -> messageOutput);

        return toggleAction.execute(ActionContext.COMMON);
    }
}

package fi.dy.masa.malilib.input.callback;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.BooleanContainingConfig;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.overlay.message.MessageHelpers.BooleanConfigMessageFactory;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import fi.dy.masa.malilib.overlay.message.MessageUtils;

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
        this.config.toggleBooleanValue();
        MessageOutput messageOutput = key.getSettings().getMessageType();

        if (messageOutput != MessageOutput.NONE)
        {
            MessageUtils.printBooleanConfigToggleMessage(messageOutput, this.config, this.messageFactory);
        }

        return ActionResult.SUCCESS;
    }
}

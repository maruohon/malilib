package fi.dy.masa.malilib.input.callback;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.BaseGenericConfig;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.overlay.message.MessageHelpers.BooleanConfigMessageFactory;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import fi.dy.masa.malilib.overlay.message.MessageUtils;
import fi.dy.masa.malilib.util.data.BooleanStorage;

public class ToggleBooleanWithMessageKeyCallback<CFG extends BaseGenericConfig<?> & BooleanStorage> implements HotkeyCallback
{
    protected final CFG config;
    @Nullable protected final BooleanConfigMessageFactory messageFactory;

    public ToggleBooleanWithMessageKeyCallback(CFG config)
    {
        this(config, null);
    }

    public ToggleBooleanWithMessageKeyCallback(CFG config,
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

package fi.dy.masa.malilib.input.callback;

import java.util.function.Function;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import fi.dy.masa.malilib.overlay.message.MessageUtils;

public class ToggleBooleanWithMessageKeyCallback implements HotkeyCallback
{
    protected final BooleanConfig config;
    @Nullable protected final Function<BooleanConfig, String> messageFactory;

    public ToggleBooleanWithMessageKeyCallback(BooleanConfig config)
    {
        this(config, null);
    }

    public ToggleBooleanWithMessageKeyCallback(BooleanConfig config,
                                               @Nullable Function<BooleanConfig, String> messageFactory)
    {
        this.config = config;
        this.messageFactory = messageFactory;
    }

    @Override
    public ActionResult onKeyAction(KeyAction action, KeyBind key)
    {
        this.config.toggleBooleanValue();
        MessageOutput messageOutput = key.getSettings().getMessageType();
        MessageUtils.printBooleanConfigToggleMessage(messageOutput, this.config, this.messageFactory);
        return ActionResult.SUCCESS;
    }
}

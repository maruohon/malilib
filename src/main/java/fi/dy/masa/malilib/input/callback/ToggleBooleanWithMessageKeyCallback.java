package fi.dy.masa.malilib.input.callback;

import java.util.function.Function;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.overlay.message.MessageUtils;

public class ToggleBooleanWithMessageKeyCallback extends ToggleBooleanKeyCallback
{
    @Nullable protected final Function<BooleanConfig, String> messageFactory;

    public ToggleBooleanWithMessageKeyCallback(BooleanConfig config)
    {
        this(config, null);
    }

    public ToggleBooleanWithMessageKeyCallback(BooleanConfig config, @Nullable Function<BooleanConfig, String> messageFactory)
    {
        super(config);

        this.messageFactory = messageFactory;
    }

    @Override
    public ActionResult onKeyAction(KeyAction action, KeyBind key)
    {
        super.onKeyAction(action, key);
        MessageUtils.printBooleanConfigToggleMessage(this.config, this.messageFactory);
        return ActionResult.SUCCESS;
    }
}

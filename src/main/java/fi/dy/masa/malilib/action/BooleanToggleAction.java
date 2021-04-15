package fi.dy.masa.malilib.action;

import java.util.function.Function;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.overlay.message.MessageUtils;

public class BooleanToggleAction implements Action
{
    protected final BooleanConfig config;
    @Nullable Function<BooleanConfig, String> messageFactory;

    public BooleanToggleAction(BooleanConfig config, @Nullable Function<BooleanConfig, String> messageFactory)
    {
        this.config = config;
        this.messageFactory = messageFactory;
    }

    @Override
    public ActionResult execute(ActionContext ctx)
    {
        this.config.toggleBooleanValue();
        MessageUtils.printBooleanConfigToggleMessage(this.config, this.messageFactory);
        return ActionResult.SUCCESS;
    }

    public static BooleanToggleAction of(BooleanConfig config, @Nullable Function<BooleanConfig, String> messageFactory)
    {
        return new BooleanToggleAction(config, messageFactory);
    }
}

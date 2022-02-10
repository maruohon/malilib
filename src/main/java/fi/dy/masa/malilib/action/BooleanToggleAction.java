package fi.dy.masa.malilib.action;

import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import fi.dy.masa.malilib.overlay.message.MessageUtils;

public class BooleanToggleAction implements Action
{
    protected final BooleanConfig config;
    @Nullable protected final Function<BooleanConfig, String> messageFactory;
    @Nullable protected final Supplier<MessageOutput> messageTypeSupplier;

    public BooleanToggleAction(BooleanConfig config,
                               @Nullable Function<BooleanConfig, String> messageFactory)
    {
        this(config, messageFactory, null);
    }

    public BooleanToggleAction(BooleanConfig config,
                               @Nullable Function<BooleanConfig, String> messageFactory,
                               @Nullable Supplier<MessageOutput> messageTypeSupplier)
    {
        this.config = config;
        this.messageFactory = messageFactory;
        this.messageTypeSupplier = messageTypeSupplier;
    }

    @Override
    public ActionResult execute(ActionContext ctx)
    {
        this.config.toggleBooleanValue();
        MessageOutput messageOutput = this.messageTypeSupplier != null ? this.messageTypeSupplier.get() : MessageOutput.CUSTOM_HOTBAR;
        MessageUtils.printBooleanConfigToggleMessage(messageOutput, this.config, this.messageFactory);
        return ActionResult.SUCCESS;
    }

    public static BooleanToggleAction of(BooleanConfig config,
                                         @Nullable Function<BooleanConfig, String> messageFactory)
    {
        return new BooleanToggleAction(config, messageFactory);
    }

    public static BooleanToggleAction of(BooleanConfig config,
                                         @Nullable Function<BooleanConfig, String> messageFactory,
                                         @Nullable Supplier<MessageOutput> messageTypeSupplier)
    {
        return new BooleanToggleAction(config, messageFactory, messageTypeSupplier);
    }
}

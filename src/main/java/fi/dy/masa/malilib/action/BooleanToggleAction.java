package fi.dy.masa.malilib.action;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.BooleanContainingConfig;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.overlay.message.MessageHelpers.BooleanConfigMessageFactory;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import fi.dy.masa.malilib.overlay.message.MessageUtils;

public class BooleanToggleAction implements Action
{
    protected final BooleanContainingConfig<?> config;
    @Nullable protected final BooleanConfigMessageFactory messageFactory;
    @Nullable protected final Supplier<MessageOutput> messageTypeSupplier;

    protected BooleanToggleAction(BooleanContainingConfig<?> config,
                                  @Nullable BooleanConfigMessageFactory messageFactory,
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
        MessageOutput messageOutput = this.messageTypeSupplier != null ? this.messageTypeSupplier.get() : MessageOutput.DEFAULT_TOGGLE;

        if (messageOutput != MessageOutput.NONE)
        {
            MessageUtils.printBooleanConfigToggleMessage(messageOutput, this.config, this.messageFactory);
        }

        return ActionResult.SUCCESS;
    }

    public static BooleanToggleAction of(BooleanContainingConfig<?> config,
                                         @Nullable BooleanConfigMessageFactory messageFactory,
                                         @Nullable Supplier<MessageOutput> messageTypeSupplier)
    {
        return new BooleanToggleAction(config, messageFactory, messageTypeSupplier);
    }
}

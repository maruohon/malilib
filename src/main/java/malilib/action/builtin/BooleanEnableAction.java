package malilib.action.builtin;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import malilib.action.Action;
import malilib.action.ActionContext;
import malilib.config.option.BooleanContainingConfig;
import malilib.input.ActionResult;
import malilib.overlay.message.MessageHelpers.BooleanConfigMessageFactory;
import malilib.overlay.message.MessageOutput;
import malilib.overlay.message.MessageUtils;

public class BooleanEnableAction implements Action
{
    protected final BooleanContainingConfig<?> config;
    @Nullable protected final BooleanConfigMessageFactory messageFactory;
    @Nullable protected final Supplier<MessageOutput> messageTypeSupplier;

    protected BooleanEnableAction(BooleanContainingConfig<?> config,
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
        this.config.setBooleanValue(true);
        MessageOutput messageOutput = this.messageTypeSupplier != null ? this.messageTypeSupplier.get() : MessageOutput.DEFAULT_TOGGLE;

        if (messageOutput != MessageOutput.NONE)
        {
            MessageUtils.printBooleanConfigToggleMessage(messageOutput, this.config, this.messageFactory);
        }

        return ActionResult.SUCCESS;
    }

    public static BooleanEnableAction of(BooleanContainingConfig<?> config,
                                         @Nullable BooleanConfigMessageFactory messageFactory,
                                         @Nullable Supplier<MessageOutput> messageTypeSupplier)
    {
        return new BooleanEnableAction(config, messageFactory, messageTypeSupplier);
    }
}

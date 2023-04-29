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
import malilib.util.data.ToBooleanFunction;

public class BaseBooleanConfigAction implements Action
{
    protected final BooleanContainingConfig<?> config;
    protected final ToBooleanFunction<BooleanContainingConfig<?>> configFunction;
    @Nullable protected final BooleanConfigMessageFactory messageFactory;
    @Nullable protected final Supplier<MessageOutput> messageTypeSupplier;

    protected BaseBooleanConfigAction(BooleanContainingConfig<?> config,
                                      ToBooleanFunction<BooleanContainingConfig<?>> configFunction,
                                      @Nullable BooleanConfigMessageFactory messageFactory,
                                      @Nullable Supplier<MessageOutput> messageTypeSupplier)
    {
        this.config = config;
        this.configFunction = configFunction;
        this.messageFactory = messageFactory;
        this.messageTypeSupplier = messageTypeSupplier;
    }

    @Override
    public ActionResult execute(ActionContext ctx)
    {
        boolean changed = this.configFunction.applyAsBoolean(this.config);

        MessageOutput messageOutput = this.messageTypeSupplier != null ? this.messageTypeSupplier.get() : MessageOutput.DEFAULT_TOGGLE;

        if (messageOutput != MessageOutput.NONE)
        {
            if (changed || this.config.hasOverride() || this.config.isLocked())
            {
                MessageUtils.printBooleanConfigToggleMessage(messageOutput, this.config, this.messageFactory);
            }
            else
            {
                MessageUtils.printBooleanConfigAlreadyAtValueMessage(messageOutput, this.config);
            }
        }

        return ActionResult.SUCCESS;
    }
}

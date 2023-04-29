package malilib.action.builtin;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import malilib.config.option.BooleanContainingConfig;
import malilib.overlay.message.MessageHelpers.BooleanConfigMessageFactory;
import malilib.overlay.message.MessageOutput;

public class BooleanDisableAction extends BaseBooleanConfigAction
{
    protected BooleanDisableAction(BooleanContainingConfig<?> config,
                                   @Nullable BooleanConfigMessageFactory messageFactory,
                                   @Nullable Supplier<MessageOutput> messageTypeSupplier)
    {
        super(config, cfg -> cfg.setBooleanValue(false), messageFactory, messageTypeSupplier);
    }

    public static BooleanDisableAction of(BooleanContainingConfig<?> config,
                                          @Nullable BooleanConfigMessageFactory messageFactory,
                                          @Nullable Supplier<MessageOutput> messageTypeSupplier)
    {
        return new BooleanDisableAction(config, messageFactory, messageTypeSupplier);
    }
}
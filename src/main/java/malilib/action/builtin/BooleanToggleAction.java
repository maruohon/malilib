package malilib.action.builtin;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import malilib.config.option.BooleanContainingConfig;
import malilib.overlay.message.MessageHelpers.BooleanConfigMessageFactory;
import malilib.overlay.message.MessageOutput;

public class BooleanToggleAction extends BaseBooleanConfigAction
{
    protected BooleanToggleAction(BooleanContainingConfig<?> config,
                                  @Nullable BooleanConfigMessageFactory messageFactory,
                                  @Nullable Supplier<MessageOutput> messageTypeSupplier)
    {
        super(config, BooleanContainingConfig::toggleBooleanValue, messageFactory, messageTypeSupplier);
    }

    public static BooleanToggleAction of(BooleanContainingConfig<?> config,
                                         @Nullable BooleanConfigMessageFactory messageFactory,
                                         @Nullable Supplier<MessageOutput> messageTypeSupplier)
    {
        return new BooleanToggleAction(config, messageFactory, messageTypeSupplier);
    }
}
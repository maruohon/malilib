package fi.dy.masa.malilib.action;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.BaseGenericConfig;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.overlay.message.MessageHelpers.BooleanConfigMessageFactory;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import fi.dy.masa.malilib.overlay.message.MessageUtils;
import fi.dy.masa.malilib.util.data.BooleanStorage;

public class BooleanEnableAction<CFG extends BaseGenericConfig<?> & BooleanStorage> implements Action
{
    protected final CFG config;
    @Nullable protected final BooleanConfigMessageFactory messageFactory;
    @Nullable protected final Supplier<MessageOutput> messageTypeSupplier;

    protected BooleanEnableAction(CFG config,
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

    public static <CFG extends BaseGenericConfig<?> & BooleanStorage>
    BooleanEnableAction<?> of(CFG config,
                              @Nullable BooleanConfigMessageFactory messageFactory,
                              @Nullable Supplier<MessageOutput> messageTypeSupplier)
    {
        return new BooleanEnableAction<>(config, messageFactory, messageTypeSupplier);
    }
}

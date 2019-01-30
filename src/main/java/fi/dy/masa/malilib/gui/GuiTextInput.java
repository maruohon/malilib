package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.interfaces.IStringConsumer;
import fi.dy.masa.malilib.interfaces.IStringConsumerFeedback;

public class GuiTextInput extends GuiTextInputBase
{
    protected final IStringConsumer consumer;
    protected final IStringConsumerFeedback consumerFeedback;

    public GuiTextInput(int maxTextLength, String titleKey, String defaultText, @Nullable GuiBase parent, IStringConsumer consumer)
    {
        super(maxTextLength, titleKey, defaultText, parent);

        this.consumer = consumer;
        this.consumerFeedback = null;
    }

    public GuiTextInput(int maxTextLength, String titleKey, String defaultText, @Nullable GuiBase parent, IStringConsumerFeedback consumer)
    {
        super(maxTextLength, titleKey, defaultText, parent);

        this.consumer = null;
        this.consumerFeedback = consumer;
    }

    @Override
    protected boolean applyValue(String string)
    {
        if (this.consumerFeedback != null)
        {
            return this.consumerFeedback.setString(string);
        }

        this.consumer.setString(string);
        return true;
    }
}

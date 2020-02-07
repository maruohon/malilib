package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.interfaces.IStringConsumerFeedback;

public class GuiTextInputFeedback extends GuiTextInputBase
{
    protected final IStringConsumerFeedback consumer;

    public GuiTextInputFeedback(String titleKey, String defaultText, @Nullable GuiScreen parent, IStringConsumerFeedback consumer)
    {
        super(titleKey, defaultText, parent);

        this.consumer = consumer;
    }

    @Override
    protected boolean applyValue(String string)
    {
        return this.consumer.setString(this.textField.getText());
    }
}

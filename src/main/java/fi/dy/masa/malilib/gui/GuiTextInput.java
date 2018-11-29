package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.interfaces.IStringConsumer;

public class GuiTextInput extends GuiTextInputBase
{
    protected final IStringConsumer consumer;

    public GuiTextInput(int maxTextLength, String titleKey, String defaultText, @Nullable GuiBase parent, IStringConsumer consumer)
    {
        super(maxTextLength, titleKey, defaultText, parent);

        this.consumer = consumer;
    }

    @Override
    protected boolean applyValue(String string)
    {
        this.consumer.setString(this.textField.getText());
        return true;
    }
}

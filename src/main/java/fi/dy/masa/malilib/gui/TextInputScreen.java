package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.util.consumer.StringConsumer;

public class TextInputScreen extends BaseTextInputScreen
{
    protected final StringConsumer stringConsumer;

    public TextInputScreen(String titleKey, StringConsumer stringConsumer)
    {
        this(titleKey, "", stringConsumer);
    }

    public TextInputScreen(String titleKey, String defaultText, StringConsumer stringConsumer)
    {
        super(titleKey, defaultText);

        this.stringConsumer = stringConsumer;
    }

    public TextInputScreen(String titleKey, String defaultText,
                           StringConsumer stringConsumer, @Nullable GuiScreen parent)
    {
        this(titleKey, defaultText, stringConsumer);

        this.setParent(parent);
    }

    @Override
    protected boolean applyValue()
    {
        return this.stringConsumer.consumeString(this.textField.getText());
    }
}

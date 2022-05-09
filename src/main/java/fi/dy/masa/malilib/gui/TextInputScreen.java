package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import fi.dy.masa.malilib.util.data.ResultingStringConsumer;

public class TextInputScreen extends BaseTextInputScreen
{
    protected final ResultingStringConsumer stringConsumer;

    public TextInputScreen(String titleKey, ResultingStringConsumer stringConsumer)
    {
        this(titleKey, "", stringConsumer);
    }

    public TextInputScreen(String titleKey, String defaultText, ResultingStringConsumer stringConsumer)
    {
        super(titleKey, defaultText);

        this.stringConsumer = stringConsumer;
    }

    public TextInputScreen(String titleKey, String defaultText,
                           ResultingStringConsumer stringConsumer, @Nullable Screen parent)
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

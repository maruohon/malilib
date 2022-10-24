package malilib.gui;

import malilib.util.data.ResultingStringConsumer;

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

    @Override
    protected boolean applyValue()
    {
        return this.stringConsumer.consumeString(this.textField.getText());
    }
}

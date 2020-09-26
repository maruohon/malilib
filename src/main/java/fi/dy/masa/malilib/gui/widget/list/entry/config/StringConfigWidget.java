package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;

public class StringConfigWidget extends BaseConfigOptionWidget<StringConfig>
{
    protected final StringConfig config;
    protected final String initialValue;
    protected final BaseTextFieldWidget textField;

    public StringConfigWidget(int x, int y, int width, int height, int listIndex,
                              int originalListIndex, StringConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.config = config;
        this.initialValue = this.config.getStringValue();

        this.textField = new BaseTextFieldWidget(x, y, 20, 16, this.config.getStringValue());
        this.textField.setListener((str) -> {
            this.config.setValueFromString(str);
            this.resetButton.setEnabled(this.config.isModified());
        });

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.textField.setText(this.config.getStringValue());
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getX() + this.getMaxLabelWidth() + 10;
        int y = this.getY() + 1;
        int elementWidth = this.getElementWidth();

        this.textField.setPosition(x, y + 2);
        this.textField.setWidth(elementWidth);
        this.textField.setText(this.config.getStringValue());

        // Set the cursor to the start at first, so that the beginning
        // of the string is shown by default. Otherwise, depending on the string length,
        // an arbitrary number of characters from the end would show at first,
        // even just one, depending on the alignment/length of the string.
        this.textField.setCursorToStart();

        this.updateResetButton(x + elementWidth + 4, y, this.config);

        this.addWidget(this.textField);
        this.addWidget(this.resetButton);
    }

    @Override
    public void onAboutToDestroy()
    {
        String text = this.textField.getText();

        if (text.equals(this.initialValue) == false)
        {
            this.config.setValueFromString(text);
        }
    }
    @Override
    public boolean wasModified()
    {
        return this.config.getStringValue().equals(this.initialValue) == false;
    }
}

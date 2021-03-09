package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;

public class StringConfigWidget extends BaseConfigOptionWidget<String, StringConfig>
{
    protected final BaseTextFieldWidget textField;

    public StringConfigWidget(int x, int y, int width, int height, int listIndex,
                              int originalListIndex, StringConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.textField = new BaseTextFieldWidget(x, y, 20, 16, this.config.getStringValue());
        this.textField.setHoverStringProvider("locked", this.config::getLockAndOverrideMessages);

        this.textField.setListener((str) -> {
            this.config.setValueFromString(str);
            this.updateResetButtonState();
        });

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.textField.setText(this.config.getStringValue());
            this.updateResetButtonState();
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getElementsStartPosition();
        int y = this.getY() + 1;
        int elementWidth = this.getElementWidth();

        this.textField.setPosition(x, y + 2);
        this.textField.setWidth(elementWidth);
        this.textField.setText(this.config.getStringValue());
        this.textField.setEnabled(this.config.isLocked() == false);
        this.textField.updateHoverStrings();

        // Set the cursor to the start at first, so that the beginning
        // of the string is shown by default. Otherwise, depending on the string length,
        // an arbitrary number of characters from the end would show at first,
        // even just one, depending on the alignment/length of the string.
        this.textField.setCursorToStart();

        this.updateResetButton(x + elementWidth + 4, y);

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
}

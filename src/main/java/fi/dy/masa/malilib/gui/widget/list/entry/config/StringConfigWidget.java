package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class StringConfigWidget extends BaseConfigOptionWidget<String, StringConfig>
{
    protected final BaseTextFieldWidget textField;

    public StringConfigWidget(StringConfig config,
                              DataListEntryWidgetData constructData,
                              ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.textField = new BaseTextFieldWidget(20, 16, this.config.getValue());
        this.textField.setHoverStringProvider("locked", this.config::getLockAndOverrideMessages);
        this.textField.translateAndAddHoverString("malilib.hover.config.string.default_value", config.getDefaultValue());
        this.textField.setShowCursorPosition(true);

        this.textField.setListener((str) -> {
            this.config.setValue(str);
            this.updateWidgetState();
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        // Set the cursor to the start at first, so that the beginning
        // of the string is shown by default. Otherwise, depending on the string length,
        // an arbitrary number of characters from the end would show at first,
        // even just one, depending on the alignment/length of the string.
        this.textField.setCursorToStart();

        this.addWidget(this.textField);
        this.addWidget(this.resetButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getElementsStartPosition();
        int y = this.getY() + 1;
        int elementWidth = this.getElementWidth();

        this.textField.setPosition(x, y + 2);
        this.textField.setWidth(elementWidth);
        this.textField.setEnabled(this.config.isLocked() == false);

        this.resetButton.setPosition(this.textField.getRight() + 4, y);
    }

    @Override
    public void updateWidgetState()
    {
        super.updateWidgetState();

        this.textField.setText(this.config.getValue());
        this.textField.updateHoverStrings();
    }

    @Override
    public void onAboutToDestroy()
    {
        String text = this.textField.getText();

        if (text.equals(this.initialValue) == false)
        {
            this.config.setValue(text);
        }
    }
}

package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.ColorConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.ColorIndicatorWidget;
import fi.dy.masa.malilib.util.data.Color4f;

public class ColorConfigWidget extends BaseConfigOptionWidget<Integer, ColorConfig>
{
    protected final ColorIndicatorWidget colorIndicatorWidget;
    protected final BaseTextFieldWidget textField;
    protected final String initialStringValue;

    public ColorConfigWidget(int x, int y, int width, int height, int listIndex,
                             int originalListIndex, ColorConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.initialStringValue = Color4f.getHexColorString(this.initialValue);

        this.colorIndicatorWidget = new ColorIndicatorWidget(18, 18, this.config, (newValue) -> {
            this.config.setValue(newValue);
            this.reAddSubWidgets();
        });
        this.colorIndicatorWidget.getHoverInfoFactory()
                .setStringListProvider("locked", this.config::getLockAndOverrideMessages, 110);

        this.textField = new BaseTextFieldWidget(70, 16, this.config.getStringValue());
        this.textField.setHoverStringProvider("locked", this.config::getLockAndOverrideMessages);

        this.textField.setListener((str) -> {
            this.config.setValueFromString(str);
            this.updateResetButtonState();
        });

        this.resetButton.setActionListener(() -> {
            this.config.resetToDefault();
            this.reAddSubWidgets();
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getElementsStartPosition();
        int y = this.getY();
        int elementWidth = this.getElementWidth();

        this.colorIndicatorWidget.setPosition(x, y + 2);
        this.colorIndicatorWidget.updateHoverStrings();

        this.textField.setPosition(x + this.colorIndicatorWidget.getWidth() + 4, y + 3);
        this.textField.setText(this.config.getStringValue());
        this.textField.setEnabled(this.config.isLocked() == false);
        this.textField.updateHoverStrings();

        this.updateResetButton(x + elementWidth + 4, y + 1);

        this.addWidget(this.colorIndicatorWidget);
        this.addWidget(this.textField);
        this.addWidget(this.resetButton);
    }

    @Override
    public void onAboutToDestroy()
    {
        String text = this.textField.getText();

        if (text.equals(this.initialStringValue) == false)
        {
            this.config.setValueFromString(text);
        }
    }
}

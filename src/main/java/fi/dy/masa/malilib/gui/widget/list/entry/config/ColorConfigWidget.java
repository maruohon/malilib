package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.ColorConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.ColorIndicatorWidget;
import fi.dy.masa.malilib.util.data.Color4f;

public class ColorConfigWidget extends BaseConfigOptionWidget<ColorConfig>
{
    protected final ColorConfig config;
    protected final ColorIndicatorWidget colorIndicatorWidget;
    protected final BaseTextFieldWidget textField;
    protected final int initialValue;
    protected final String initialStringValue;

    public ColorConfigWidget(int x, int y, int width, int height, int listIndex,
                             int originalListIndex, ColorConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, 22, listIndex, originalListIndex, config, ctx);

        this.config = config;
        this.initialValue = this.config.getIntegerValue();
        this.initialStringValue = Color4f.getHexColorString(this.initialValue);

        this.colorIndicatorWidget = new ColorIndicatorWidget(x, y, 18, 18, this.config, (newValue) -> {
            this.config.setIntegerValue(newValue);
            this.reAddSubWidgets();
        });

        this.textField = new BaseTextFieldWidget(x, y, 70, 16, this.config.getStringValue());
        this.textField.setListener((str) -> {
            this.config.setValueFromString(str);
            this.resetButton.setEnabled(this.config.isModified());
        });

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.reAddSubWidgets();
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getX() + this.getMaxLabelWidth() + 10;
        int y = this.getY();
        int elementWidth = this.getElementWidth();

        this.colorIndicatorWidget.setPosition(x, y + 2);
        this.textField.setPosition(x + this.colorIndicatorWidget.getWidth() + 4, y + 3);
        this.updateResetButton(x + elementWidth + 4, y + 1, this.config);

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

    @Override
    public boolean wasModified()
    {
        return this.config.getIntegerValue() != this.initialValue;
    }
}

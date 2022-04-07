package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.ColorConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.ColorIndicatorWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.util.data.Color4f;

public class ColorConfigWidget extends BaseGenericConfigWidget<Color4f, ColorConfig>
{
    protected final ColorIndicatorWidget colorIndicatorWidget;
    protected final BaseTextFieldWidget textField;
    protected final String initialStringValue;

    public ColorConfigWidget(ColorConfig config,
                             DataListEntryWidgetData constructData,
                             ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.initialStringValue = this.initialValue.toString();

        this.colorIndicatorWidget = new ColorIndicatorWidget(18, 18, this.config, (newValue) -> {
            this.config.setValueFromInt(newValue);
            this.updateWidgetState();
        });
        this.colorIndicatorWidget.getHoverInfoFactory()
                .setStringListProvider("locked", this.config::getLockAndOverrideMessages, 110);

        this.textField = new BaseTextFieldWidget(70, 16, this.config.getStringValue());
        this.textField.setHoverStringProvider("locked", this.config::getLockAndOverrideMessages);

        this.textField.setListener((str) -> {
            this.config.setValueFromString(str);
            this.updateWidgetState();
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.colorIndicatorWidget);
        this.addWidget(this.textField);
        this.addWidget(this.resetButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getElementsStartPosition();
        int y = this.getY();

        this.colorIndicatorWidget.setPosition(x, y + 2);

        this.textField.setWidth(this.getElementWidth() - 22);
        this.textField.setPosition(this.colorIndicatorWidget.getRight() + 4, y + 3);
        this.textField.setEnabled(this.config.isLocked() == false);

        this.resetButton.setPosition(this.textField.getRight() + 4, y + 1);
    }

    @Override
    public void updateWidgetState()
    {
        super.updateWidgetState();

        this.textField.setText(this.config.getStringValue());
        this.textField.updateHoverStrings();
        this.colorIndicatorWidget.updateHoverStrings();
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

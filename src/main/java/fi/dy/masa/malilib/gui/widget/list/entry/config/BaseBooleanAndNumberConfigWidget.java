package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.util.function.BiConsumer;
import java.util.function.Function;
import fi.dy.masa.malilib.config.option.BaseConfigOption;
import fi.dy.masa.malilib.config.option.SliderConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.BooleanConfigButton;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.util.data.BooleanStorage;

public abstract class BaseBooleanAndNumberConfigWidget<TYPE, CFG extends BaseConfigOption<TYPE> & SliderConfig & BooleanStorage> extends NumericConfigWidget<TYPE, CFG>
{
    protected final BooleanConfigButton booleanButton;

    public BaseBooleanAndNumberConfigWidget(CFG config,
                                            DataListEntryWidgetData constructData,
                                            ConfigWidgetContext ctx,
                                            BiConsumer<CFG, String> fromStringSetter,
                                            Function<CFG, String> toStringConverter)
    {
        super(config, constructData, ctx, fromStringSetter, toStringConverter);

        this.booleanButton = new BooleanConfigButton(-1, 20, this.config);
        this.booleanButton.setHoverStringProvider("locked", this.config::getLockAndOverrideMessages);

        this.booleanButton.setActionListener(() -> {
            this.config.toggleBooleanValue();
            this.updateWidgetState();
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();
        this.addWidget(this.booleanButton);
    }

    @Override
    protected void updateNumberWidgetPositions()
    {
        int x = this.getElementsStartPosition();
        int y = this.getY();
        int elementWidth = this.getElementWidth();
        int numWidth = elementWidth - this.booleanButton.getWidth() - 38;

        this.booleanButton.setPosition(x, y + 1);

        x = this.booleanButton.getRight() + 2;
        this.sliderWidget.setPosition(x, y + 1);
        this.sliderWidget.setWidth(numWidth);

        this.textField.setPosition(x, y + 3);
        this.textField.setWidth(numWidth);
    }

    @Override
    public void updateWidgetState()
    {
        super.updateWidgetState();

        this.booleanButton.setEnabled(this.config.isLocked() == false);
        this.booleanButton.updateButtonState();
    }
}

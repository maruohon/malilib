package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.SliderConfig;
import fi.dy.masa.malilib.config.option.BaseConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.SliderWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;

public abstract class NumericConfigWidget<TYPE, CFG extends BaseConfig<TYPE> & SliderConfig> extends BaseConfigOptionWidget<TYPE, CFG>
{
    protected final BaseTextFieldWidget textField;
    protected final GenericButton sliderToggleButton;
    protected SliderWidget sliderWidget;

    public NumericConfigWidget(int x, int y, int width, int height, int listIndex,
                               int originalListIndex, CFG config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.textField = new BaseTextFieldWidget(x, y, 60, 16);
        this.textField.setHoverStringProvider("lock", config::getLockAndOverrideMessages);

        this.sliderWidget = new SliderWidget(x, y, 60, 20, config.getSliderCallback(this::updateResetButtonState));
        this.sliderWidget.setHoverStringProvider("lock", config::getLockAndOverrideMessages);

        this.sliderToggleButton = new GenericButton(x, y, () -> this.config.isSliderActive() ? DefaultIcons.BTN_TXTFIELD : DefaultIcons.BTN_SLIDER);
        this.sliderToggleButton.getHoverInfoFactory().setStringListProvider("slider", this::getSliderMessages);

        this.sliderToggleButton.setActionListener((btn, mbtn) -> {
            this.config.toggleSliderActive();
            this.reAddSubWidgets();
        });

        this.resetButton.setActionListener((btn, mbtn) -> {
            this.config.resetToDefault();
            this.updateResetButtonState();
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
        boolean locked = this.config.isLocked();

        if (this.config.isSliderActive())
        {
            this.sliderWidget.setLocked(locked);
            this.sliderWidget.setPosition(x, y + 1);
            this.sliderWidget.setWidth(elementWidth - 18);
            x += this.sliderWidget.getWidth() + 2;

            this.addWidget(this.sliderWidget);
        }
        else
        {
            this.textField.setEnabled(locked == false);
            this.textField.updateHoverStrings();
            this.textField.setPosition(x, y + 3);
            this.textField.setWidth(elementWidth - 18);
            this.textField.setText(this.getCurrentValueAsString());
            x += this.textField.getWidth() + 2;

            this.addWidget(this.textField);
        }

        this.sliderToggleButton.setPosition(x, y + 3);
        this.sliderToggleButton.setEnabled(this.config.allowSlider());
        this.sliderToggleButton.updateHoverStrings();

        this.updateResetButton(x + 20, y + 1);

        this.addWidget(this.sliderToggleButton);
        this.addWidget(this.resetButton);
    }

    protected List<String> getSliderMessages()
    {
        if (this.config.allowSlider() == false)
        {
            return ImmutableList.of("malilib.gui.button.hover.text_field_slider_toggle",
                                    "malilib.gui.button.hover.text_field_slider_toggle.not_allowed");
        }

        return ImmutableList.of("malilib.gui.button.hover.text_field_slider_toggle");
    }

    protected abstract String getCurrentValueAsString();
}

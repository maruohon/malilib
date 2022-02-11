package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.BaseConfigOption;
import fi.dy.masa.malilib.config.option.SliderConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.SliderWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class NumericConfigWidget<TYPE, CFG extends BaseConfigOption<TYPE> & SliderConfig> extends BaseConfigOptionWidget<TYPE, CFG>
{
    protected final BaseTextFieldWidget textField;
    protected final GenericButton sliderToggleButton;
    protected final String initialStringValue;
    protected final BiConsumer<CFG, String> fromStringSetter;
    protected final Function<CFG, String> toStringConverter;
    protected SliderWidget sliderWidget;

    protected NumericConfigWidget(int x, int y, int width, int height, int listIndex,
                                  int originalListIndex, CFG config,
                                  ConfigWidgetContext ctx,
                                  BiConsumer<CFG, String> fromStringSetter,
                                  Function<CFG, String> toStringConverter)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.fromStringSetter = fromStringSetter;
        this.toStringConverter = toStringConverter;
        this.initialStringValue = String.valueOf(this.initialValue);

        this.textField = new BaseTextFieldWidget(60, 16);
        this.textField.setHoverStringProvider("lock", config::getLockAndOverrideMessages);
        this.textField.setListener((str) -> {
            this.fromStringSetter.accept(this.config, str);
            this.updateResetButtonState();
        });

        this.sliderWidget = new SliderWidget(60, 20, config.getSliderCallback(this::updateResetButtonState));
        this.sliderWidget.setHoverStringProvider("lock", config::getLockAndOverrideMessages);

        this.sliderToggleButton = new GenericButton(() -> this.config.isSliderActive() ? DefaultIcons.BTN_TXTFIELD : DefaultIcons.BTN_SLIDER);
        this.sliderToggleButton.setHoverStringProvider("slider", this::getSliderMessages);

        this.sliderToggleButton.setActionListener(() -> {
            this.config.toggleSliderActive();
            this.reAddSubWidgets();
        });

        this.resetButton.setActionListener(() -> {
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

    @Override
    public void onAboutToDestroy()
    {
        String text = this.textField.getText();

        if (this.config.isSliderActive() == false && text.equals(this.initialStringValue) == false)
        {
            this.fromStringSetter.accept(this.config, text);
        }
    }

    protected List<String> getSliderMessages()
    {
        if (this.config.allowSlider() == false)
        {
            return ImmutableList.of(StringUtils.translate("malilib.gui.button.hover.text_field_slider_toggle"),
                                    StringUtils.translate("malilib.gui.button.hover.text_field_slider_toggle.not_allowed"));
        }

        return ImmutableList.of(StringUtils.translate("malilib.gui.button.hover.text_field_slider_toggle"));
    }

    protected String getCurrentValueAsString()
    {
        return this.toStringConverter.apply(this.config);
    }
}

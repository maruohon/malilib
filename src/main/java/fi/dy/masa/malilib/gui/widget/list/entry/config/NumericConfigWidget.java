package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.BaseConfigOption;
import fi.dy.masa.malilib.config.option.SliderConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
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

        this.sliderToggleButton = GenericButton.create(this::getSliderToggleButtonIcon, this::toggleSlider);
        this.sliderToggleButton.setHoverStringProvider("slider", this::getSliderMessages);

        this.resetButton.setActionListener(this::reset);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        if (this.config.isSliderActive())
        {
            this.addWidget(this.sliderWidget);
        }
        else
        {
            this.addWidget(this.textField);
        }

        this.addWidget(this.sliderToggleButton);
        this.addWidget(this.resetButton);
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int x = this.getElementsStartPosition();
        int y = this.getY();
        int elementWidth = this.getElementWidth();
        boolean locked = this.config.isLocked();

        this.sliderWidget.setLocked(locked);
        this.sliderWidget.setPosition(x, y + 1);
        this.sliderWidget.setWidth(elementWidth - 18);

        this.textField.setEnabled(locked == false);
        this.textField.updateHoverStrings();
        this.textField.setPosition(x, y + 3);
        this.textField.setWidth(elementWidth - 18);
        this.textField.setText(this.getCurrentValueAsString());

        x += elementWidth - 16;
        this.sliderToggleButton.setPosition(x, y + 3);
        this.sliderToggleButton.setEnabled(this.config.allowSlider());
        this.sliderToggleButton.updateHoverStrings();

        this.updateResetButton(x + 20, y + 1);
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

    protected void toggleSlider()
    {
        this.config.toggleSliderActive();
        this.reAddSubWidgets();
    }

    protected void reset()
    {
        this.config.resetToDefault();
        this.updateResetButtonState();
        this.reAddSubWidgets();
    }

    protected MultiIcon getSliderToggleButtonIcon()
    {
        return this.config.isSliderActive() ? DefaultIcons.BTN_TXTFIELD : DefaultIcons.BTN_SLIDER;
    }

    protected List<String> getSliderMessages()
    {
        if (this.config.allowSlider() == false)
        {
            return ImmutableList.of(StringUtils.translate("malilib.hover.button.config.text_field.slider_toggle"),
                                    StringUtils.translate("malilib.hover.button.config.text_field.slider_toggle.not_allowed"));
        }

        return ImmutableList.of(StringUtils.translate("malilib.hover.button.config.text_field.slider_toggle"));
    }

    protected String getCurrentValueAsString()
    {
        return this.toStringConverter.apply(this.config);
    }
}

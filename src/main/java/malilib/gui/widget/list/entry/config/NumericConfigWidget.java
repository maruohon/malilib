package malilib.gui.widget.list.entry.config;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import com.google.common.collect.ImmutableList;

import malilib.config.option.BaseGenericConfig;
import malilib.config.option.SliderConfig;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.icon.DefaultIcons;
import malilib.gui.icon.MultiIcon;
import malilib.gui.widget.BaseTextFieldWidget;
import malilib.gui.widget.SliderWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.util.StringUtils;

public abstract class NumericConfigWidget<TYPE, CFG extends BaseGenericConfig<TYPE> & SliderConfig> extends BaseGenericConfigWidget<TYPE, CFG>
{
    protected final BaseTextFieldWidget textField;
    protected final GenericButton valueAdjustButton;
    protected final GenericButton sliderToggleButton;
    protected final String initialStringValue;
    protected final BiConsumer<CFG, String> fromStringSetter;
    protected final Function<CFG, String> toStringConverter;
    protected SliderWidget sliderWidget;

    protected NumericConfigWidget(CFG config,
                                  DataListEntryWidgetData constructData,
                                  ConfigWidgetContext ctx,
                                  BiConsumer<CFG, String> fromStringSetter,
                                  Function<CFG, String> toStringConverter)
    {
        super(config, constructData, ctx);

        this.fromStringSetter = fromStringSetter;
        this.toStringConverter = toStringConverter;
        this.initialStringValue = toStringConverter.apply(config);

        this.textField = new BaseTextFieldWidget(60, 16);
        this.textField.setHoverStringProvider("lock", config::getLockAndOverrideMessages);
        this.textField.setListener((str) -> {
            this.fromStringSetter.accept(this.config, str);
            this.updateWidgetState();
        });

        this.sliderWidget = new SliderWidget(60, 20, config.getSliderCallback(this::updateWidgetState));
        this.sliderWidget.setHoverStringProvider("lock", config::getLockAndOverrideMessages);

        this.valueAdjustButton = GenericButton.create(DefaultIcons.BTN_PLUSMINUS_16);
        this.valueAdjustButton.setActionListener(this::onValueAdjustButtonClick);
        this.valueAdjustButton.setCanScrollToClick(true);
        this.valueAdjustButton.translateAndAddHoverString("malilib.hover.button.plus_minus_tip");

        this.sliderToggleButton = GenericButton.create(this::getSliderToggleButtonIcon, this::toggleSlider);
        this.sliderToggleButton.setHoverStringProvider("slider", this::getSliderMessages);
        this.sliderToggleButton.updateHoverStrings();
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

        this.addWidget(this.valueAdjustButton);
        this.addWidget(this.sliderToggleButton);
        this.addWidget(this.resetButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int y = this.getY();
        boolean enabled = this.config.isLocked() == false;

        this.sliderWidget.setEnabled(enabled);
        this.textField.setEnabled(enabled);
        this.valueAdjustButton.setEnabled(enabled);

        this.updateNumberWidgetPositions();

        this.valueAdjustButton.setPosition(this.textField.getRight() + 2, y + 3);

        this.sliderToggleButton.setPosition(this.valueAdjustButton.getRight() + 2, y + 3);
        this.sliderToggleButton.setEnabled(this.config.allowSlider());

        this.resetButton.setPosition(this.sliderToggleButton.getRight() + 4, y + 1);
    }

    protected void updateNumberWidgetPositions()
    {
        int x = this.getElementsStartPosition();
        int y = this.getY();
        int elementWidth = this.getElementWidth();

        this.sliderWidget.setPosition(x, y + 1);
        this.sliderWidget.setWidth(elementWidth - 36);

        this.textField.setPosition(x, y + 3);
        this.textField.setWidth(elementWidth - 36);
    }

    @Override
    public void updateWidgetState()
    {
        super.updateWidgetState();

        this.textField.setText(this.getCurrentValueAsString());
        this.textField.updateHoverStrings();
        this.sliderWidget.updateWidgetState();
    }

    @Override
    public void onAboutToDestroy()
    {
        String text = this.textField.getText();

        if (this.config.isSliderActive() == false &&
            this.config.isLocked() == false &&
            text.equals(this.initialStringValue) == false)
        {
            this.fromStringSetter.accept(this.config, text);
        }
    }

    protected abstract boolean onValueAdjustButtonClick(int mouseButton);

    protected void toggleSlider()
    {
        this.config.toggleSliderActive();
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

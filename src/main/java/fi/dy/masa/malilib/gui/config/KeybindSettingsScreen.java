package fi.dy.masa.malilib.gui.config;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.option.BaseConfigOption;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.liteloader.DialogHandler;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.IntegerTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.SliderWidget;
import fi.dy.masa.malilib.gui.widget.button.BooleanConfigButton;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.button.OptionListConfigButton;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindSettingsConfigs;

public class KeybindSettingsScreen extends BaseScreen
{
    protected final KeyBind keybind;
    protected final String keybindName;
    protected final KeyBindSettingsConfigs configs;
    protected final List<BaseConfigOption<?>> configList;
    protected int labelWidth;
    protected int configWidth;

    public KeybindSettingsScreen(KeyBind keybind, String name, @Nullable DialogHandler dialogHandler, GuiScreen parent)
    {
        this.keybind = keybind;
        this.keybindName = name;
        this.dialogHandler = dialogHandler;

        // When we have a dialog handler, then we are inside the Liteloader config menu.
        // In there we don't want to use the normal "GUI replacement and render parent first" trick.
        // The "dialog handler" stuff is used within the Liteloader config menus,
        // because there we can't change the mc.currentScreen reference to this GUI,
        // because otherwise Liteloader will freak out.
        // So instead we are using a weird wrapper "sub panel" thingy in there, and thus
        // we can NOT try to render the parent GUI here in that case, otherwise it will
        // lead to an infinite recursion loop and a StackOverflowError.
        if (this.dialogHandler == null)
        {
            this.setParent(parent);
        }

        this.backgroundColor = 0xFF000000;
        this.renderBorder = true;
        this.useTitleHierarchy = false;
        this.setTitle("malilib.gui.title.keybind_settings.advanced", this.keybindName);

        this.configs = new KeyBindSettingsConfigs(keybind, this::initScreen);
        this.configList = this.configs.getConfigList();
        this.labelWidth = this.getMaxDisplayNameLength(this.configList);
        this.configWidth = 100;

        int totalWidth = this.labelWidth + this.configWidth + 30;
        totalWidth = Math.max(totalWidth, this.titleText.renderWidth + 20);
        int totalHeight = this.configList.size() * 18 + 30;

        this.setScreenWidthAndHeight(totalWidth, totalHeight);
        this.centerOnScreen();
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        // TODO fix this stuff when there is a proper element placer/helper available
        this.clearWidgets();
        this.addWidget(this.closeButton);

        int x = this.x + 10;
        int y = this.y + 24;

        for (BaseConfigOption<?> config : this.configList)
        {
            this.createLabelAndConfigWidget(x, y, this.labelWidth, this.configWidth, config);
            y += 18;
        }
    }

    public int getMaxDisplayNameLength(List<BaseConfigOption<?>> configs)
    {
        int width = 0;

        for (BaseConfigOption<?> config : configs)
        {
            width = Math.max(width, this.getStringWidth(config.getPrettyName()));
        }

        return width;
    }

    protected void createLabelAndConfigWidget(int x, int y, int labelWidth, int configWidth, BaseConfigOption<?> config)
    {
        int color = config.isModified() ? 0xFFFFFF55 : 0xFFAAAAAA;
        LabelWidget label = new LabelWidget(color, config.getPrettyName());
        label.setSize(labelWidth + 4, 16);
        label.setPosition(x, y);
        label.addHoverStrings(config.getComment());
        label.getPadding().setTop(3);
        this.addWidget(label);
        x += labelWidth + 10;

        if (config instanceof BooleanConfig)
        {
            BooleanConfigButton btn = new BooleanConfigButton(-1, 16, (BooleanConfig) config);
            btn.setPosition(x, y);
            this.addWidget(btn);
        }
        else if (config instanceof OptionListConfig)
        {
            OptionListConfigButton btn = new OptionListConfigButton(configWidth, 16, (OptionListConfig<?>) config);
            btn.setPosition(x, y);
            this.addWidget(btn);
        }
        else if (config instanceof IntegerConfig)
        {
            IntegerConfig intConfig = (IntegerConfig) config;

            if (intConfig.isSliderActive())
            {
                SliderWidget widget = new SliderWidget(82, 16, intConfig.getSliderCallback(null));
                widget.setPosition(x, y);
                this.addWidget(widget);
            }
            else
            {
                BaseTextFieldWidget textField = new BaseTextFieldWidget(82, 16);
                textField.setPosition(x, y);
                textField.setText(intConfig.getStringValue());
                int min = intConfig.getMinIntegerValue();
                int max = intConfig.getMaxIntegerValue();
                textField.setTextValidator(new IntegerTextFieldWidget.IntValidator(min, max));
                textField.setListener(intConfig::setValueFromString);
                this.addWidget(textField);
            }

            Supplier<MultiIcon> iconSupplier = () -> intConfig.isSliderActive() ? DefaultIcons.BTN_TXTFIELD : DefaultIcons.BTN_SLIDER;
            GenericButton sliderToggleButton = new GenericButton(iconSupplier);
            sliderToggleButton.setPosition(x + 84, y);

            sliderToggleButton.setActionListener(() -> {
                intConfig.toggleSliderActive();
                this.initScreen();
            });

            this.addWidget(sliderToggleButton);
        }
    }

    @Override
    public void onGuiClosed()
    {
        this.keybind.setSettings(this.configs.getCurrentSettings());
        super.onGuiClosed();
    }
}

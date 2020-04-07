package fi.dy.masa.malilib.gui.widgets;

import java.io.File;
import java.util.ArrayList;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.gui.SliderCallbackDouble;
import fi.dy.masa.malilib.config.gui.SliderCallbackInteger;
import fi.dy.masa.malilib.config.options.ConfigFile;
import fi.dy.masa.malilib.config.options.IConfigBase;
import fi.dy.masa.malilib.config.options.IConfigBoolean;
import fi.dy.masa.malilib.config.options.IConfigDouble;
import fi.dy.masa.malilib.config.options.IConfigInteger;
import fi.dy.masa.malilib.config.options.IConfigOptionList;
import fi.dy.masa.malilib.config.options.IConfigResettable;
import fi.dy.masa.malilib.config.options.IConfigSlider;
import fi.dy.masa.malilib.config.options.IConfigStringList;
import fi.dy.masa.malilib.config.options.IConfigValue;
import fi.dy.masa.malilib.config.options.IStringRepresentable;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase.ConfigOptionWrapper;
import fi.dy.masa.malilib.gui.GuiDirectorySelector;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonBoolean;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.button.ConfigButtonOptionList;
import fi.dy.masa.malilib.gui.button.ConfigButtonStringList;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IConfigInfoProvider;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.interfaces.ISliderCallback;
import fi.dy.masa.malilib.gui.listener.ConfigOptionChangeListenerKeybind;
import fi.dy.masa.malilib.gui.listener.ConfigOptionListenerResetConfig;
import fi.dy.masa.malilib.gui.listener.ConfigOptionListenerResetConfig.ConfigResetterButton;
import fi.dy.masa.malilib.gui.listener.ConfigOptionListenerResetConfig.ConfigResetterTextField;
import fi.dy.masa.malilib.gui.util.GuiIconBase;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetConfigOption extends WidgetConfigOptionBase<ConfigOptionWrapper>
{
    protected final ConfigOptionWrapper wrapper;
    protected final IKeybindConfigGui host;
    @Nullable protected final KeybindSettings initialKeybindSettings;
    @Nullable protected ImmutableList<String> initialStringList;
    protected int colorDisplayPosX;

    public WidgetConfigOption(int x, int y, int width, int height, int labelWidth, int configWidth,
            ConfigOptionWrapper wrapper, int listIndex, IKeybindConfigGui host, WidgetListConfigOptionsBase<?, ?> parent)
    {
        super(x, y, width, height, parent, wrapper, listIndex);

        this.host = host;
        this.wrapper = wrapper;

        if (wrapper.getType() == ConfigOptionWrapper.Type.CONFIG)
        {
            IConfigBase config = wrapper.getConfig();

            if (config instanceof IStringRepresentable)
            {
                IStringRepresentable configStr = (IStringRepresentable) config;
                this.initialStringValue = configStr.getStringValue();
                this.lastAppliedValue = configStr.getStringValue();
                this.initialKeybindSettings = config.getType() == ConfigType.HOTKEY ? ((IHotkey) config).getKeybind().getSettings() : null;
            }
            else
            {
                this.initialStringValue = null;
                this.lastAppliedValue = null;
                this.initialKeybindSettings = null;

                if (config instanceof IConfigStringList)
                {
                    this.initialStringList = ImmutableList.copyOf(((IConfigStringList) config).getStrings());
                }
            }

            this.addConfigOption(x, y, this.getZLevel(), labelWidth, configWidth, wrapper);
        }
        else
        {
            this.initialStringValue = null;
            this.lastAppliedValue = null;
            this.initialKeybindSettings = null;

            this.addLabel(x, y + 7, labelWidth, -1, 0xFFFFFFFF, wrapper.getLabel());
        }
    }

    protected void addConfigOption(int x, int y, int zLevel, int labelWidth, int configWidth, ConfigOptionWrapper wrapper)
    {
        IConfigBase config = wrapper.getConfig();
        ConfigType type = config.getType();

        y += 1;
        int configHeight = 20;

        WidgetLabel label = this.addLabel(x, y, labelWidth, 20, 0xFFFFFFFF, config.getConfigGuiDisplayName());
        label.setPaddingY(5);

        String comment = null;
        IConfigInfoProvider infoProvider = this.host.getHoverInfoProvider();

        if (infoProvider != null)
        {
            comment = infoProvider.getHoverInfo(wrapper);
        }
        else
        {
            comment = config.getComment();
        }

        if (comment != null)
        {
            label.addHoverStrings(comment);
        }

        x += labelWidth + 10;

        if (type == ConfigType.BOOLEAN)
        {
            ConfigButtonBoolean optionButton = new ConfigButtonBoolean(x, y, configWidth, configHeight, (IConfigBoolean) config);
            this.addConfigButtonEntry(x + configWidth + 4, y, (IConfigResettable) config, optionButton);
        }
        else if (type == ConfigType.OPTION_LIST)
        {
            ConfigButtonOptionList optionButton = new ConfigButtonOptionList(x, y, configWidth, configHeight, (IConfigOptionList<?>) config);
            this.addConfigButtonEntry(x + configWidth + 4, y, (IConfigResettable) config, optionButton);
        }
        else if (type == ConfigType.STRING_LIST)
        {
            ConfigButtonStringList optionButton = new ConfigButtonStringList(x, y, configWidth, configHeight, (IConfigStringList) config, this.host, this.host.getDialogHandler());
            this.addConfigButtonEntry(x + configWidth + 4, y, (IConfigResettable) config, optionButton);
        }
        else if (type == ConfigType.HOTKEY)
        {
            configWidth -= 25; // adjust the width to match other configs due to the settings widget
            IKeybind keybind = ((IHotkey) config).getKeybind();
            ConfigButtonKeybind keybindButton = new ConfigButtonKeybind(x, y, configWidth, configHeight, keybind, this.host);
            x += configWidth + 4;

            this.addWidget(new WidgetKeybindSettings(x, y, 20, 20, keybind, config.getName(), this.parent, this.host.getDialogHandler()));
            x += 25;

            this.addButton(keybindButton, this.host.getButtonPressListener());
            this.addKeybindResetButton(x, y, keybind, keybindButton);
        }
        else if (type == ConfigType.STRING ||
                 type == ConfigType.COLOR ||
                 type == ConfigType.INTEGER ||
                 type == ConfigType.DOUBLE)
        {
            int resetX = x + configWidth + 4;

            if (type == ConfigType.COLOR)
            {
                configWidth -= 24; // adjust the width to match other configs due to the color display
                this.colorDisplayPosX = x + configWidth + 4;
                this.addWidget(new WidgetColorIndicator(this.colorDisplayPosX, y, 19, 19, (IConfigInteger) config));
            }
            else if (type == ConfigType.INTEGER || type == ConfigType.DOUBLE)
            {
                configWidth -= 18;
                this.colorDisplayPosX = x + configWidth + 2;
            }

            if ((type == ConfigType.INTEGER || type == ConfigType.DOUBLE) &&
                 config instanceof IConfigSlider && ((IConfigSlider) config).shouldUseSlider())
            {
                this.addConfigSliderEntry(x, y, resetX, configWidth, configHeight, (IConfigSlider) config);
            }
            else
            {
                this.addConfigTextFieldEntry(x, y, resetX, configWidth, configHeight, (IConfigValue) config);
            }

            if (type != ConfigType.COLOR && (config instanceof IConfigSlider))
            {
                IGuiIcon icon = ((IConfigSlider) config).shouldUseSlider() ? GuiIconBase.BTN_TXTFIELD : GuiIconBase.BTN_SLIDER;
                ButtonGeneric toggleBtn = new ButtonGeneric(this.colorDisplayPosX, y + 2, icon);
                this.addButton(toggleBtn, new ListenerSliderToggle((IConfigSlider) config));
            }
        }
        else if (type == ConfigType.DIRECTORY)
        {
            final ConfigFile cfg = (ConfigFile) config;
            final File dir = ((ConfigFile) config).getFile();
            final String path = dir.getAbsolutePath();

            ArrayList<String> lines = new ArrayList<>();
            StringUtils.splitTextToLines(lines, StringUtils.translate("malilib.gui.button.hover.select_directory_value", path), 320);

            ButtonGeneric button = new ButtonGeneric(x, y, configWidth, configHeight, "malilib.gui.button.select_directory");
            button.addHoverStrings(lines);

            ButtonGeneric resetButton = this.createResetButton(x + configWidth + 4, y, cfg);
            ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig(cfg, new ConfigResetterButton(button), resetButton, null);

            this.addButton(button, (btn, mbtn) -> {
                    final File rootDirectory = new File("/");
                    final GuiDirectorySelector gui = new GuiDirectorySelector(dir, rootDirectory, (d) -> cfg.setValueFromString(d.getAbsolutePath()));
                    gui.setParent(GuiUtils.getCurrentScreen());
                    GuiBase.openGui(gui);
            });
            this.addButton(resetButton, listenerReset);
        }
    }

    @Override
    public boolean wasConfigModified()
    {
        if (this.wrapper.getType() == ConfigOptionWrapper.Type.CONFIG)
        {
            IConfigBase config = this.wrapper.getConfig();
            boolean modified = false;

            if (this.wrapper.getConfig() instanceof IStringRepresentable)
            {
                if (this.textField != null)
                {
                    modified |= this.initialStringValue.equals(this.textField.getText()) == false;
                }

                if (this.initialKeybindSettings != null && this.initialKeybindSettings.equals(((IHotkey) config).getKeybind().getSettings()) == false)
                {
                    modified = true;
                }

                return modified || this.initialStringValue.equals(((IStringRepresentable) config).getStringValue()) == false;
            }
            else if (this.initialStringList != null && this.wrapper.getConfig() instanceof IConfigStringList)
            {
                return this.initialStringList.equals(((IConfigStringList) this.wrapper.getConfig()).getStrings()) == false;
            }
        }

        return false;
    }

    public void applyNewValueToConfig()
    {
        if (this.wrapper.getType() == ConfigOptionWrapper.Type.CONFIG && this.wrapper.getConfig() instanceof IStringRepresentable)
        {
            IStringRepresentable config = (IStringRepresentable) this.wrapper.getConfig();

            if (this.textField != null && this.hasPendingModifications())
            {
                config.setValueFromString(this.textField.getText());
            }

            this.lastAppliedValue = config.getStringValue();
        }
    }

    protected void addConfigButtonEntry(int xReset, int yReset, IConfigResettable config, ButtonBase optionButton)
    {
        ButtonGeneric resetButton = this.createResetButton(xReset, yReset, config);
        ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig(config, new ConfigResetterButton(optionButton), resetButton, null);

        this.addButton(optionButton, (btn, mbtn) -> resetButton.setEnabled(config.isModified()));
        this.addButton(resetButton, listenerReset);
    }

    protected void addConfigTextFieldEntry(int x, int y, int resetX, int configWidth, int configHeight, IConfigValue config)
    {
        WidgetTextFieldBase textField = new WidgetTextFieldBase(x + 2, y + 1, configWidth - 4, configHeight - 3, config.getStringValue());

        if (config.getType() == ConfigType.COLOR)
        {
            textField.setTextValidator(WidgetTextFieldBase.VALIDATOR_HEX_COLOR_8);
        }
        else if (config.getType() == ConfigType.INTEGER)
        {
            textField.setTextValidator(WidgetTextFieldBase.VALIDATOR_INTEGER);
        }
        else if (config.getType() == ConfigType.DOUBLE)
        {
            textField.setTextValidator(WidgetTextFieldBase.VALIDATOR_DOUBLE);
        }

        ButtonGeneric resetButton = this.createResetButton(resetX, y, config);
        ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig(config, new ConfigResetterTextField(config, textField), resetButton, null);

        this.textField = this.addTextField(textField, (newText) -> {
            resetButton.setEnabled(config.isModified(newText));
            config.setValueFromString(newText);
        });
        this.addButton(resetButton, listenerReset);
    }

    protected void addConfigSliderEntry(int x, int y, int resetX, int configWidth, int configHeight, IConfigSlider config)
    {
        ButtonGeneric resetButton = this.createResetButton(resetX, y, config);
        ISliderCallback callback;

        if (config instanceof IConfigDouble)
        {
            callback = new SliderCallbackDouble((IConfigDouble) config, resetButton);
        }
        else if (config instanceof IConfigInteger)
        {
            callback = new SliderCallbackInteger((IConfigInteger) config, resetButton);
        }
        else
        {
            return;
        }

        WidgetSlider slider = new WidgetSlider(x, y, configWidth, configHeight, callback);
        ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig(config, null, resetButton, null);

        this.addWidget(slider);
        this.addButton(resetButton, listenerReset);
    }

    protected void addKeybindResetButton(int x, int y, IKeybind keybind, ConfigButtonKeybind buttonHotkey)
    {
        ButtonGeneric button = this.createResetButton(x, y, keybind);

        ConfigOptionChangeListenerKeybind listener = new ConfigOptionChangeListenerKeybind(keybind, buttonHotkey, button, this.host);
        this.host.addKeybindChangeListener(listener);
        this.addButton(button, listener);
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        this.drawSubWidgets(mouseX, mouseY, isActiveGui, hoveredWidgetId);

        if (this.wrapper.getType() == ConfigOptionWrapper.Type.CONFIG)
        {
            this.drawTextFields(mouseX, mouseY, isActiveGui, hoveredWidgetId);
            super.render(mouseX, mouseY, isActiveGui, hoveredWidgetId);
        }

        RenderUtils.color(1f, 1f, 1f, 1f);
    }

    public static class ListenerSliderToggle implements IButtonActionListener
    {
        protected final IConfigSlider config;

        public ListenerSliderToggle(IConfigSlider config)
        {
            this.config = config;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            this.config.toggleUseSlider();

            GuiScreen gui = GuiUtils.getCurrentScreen();

            if (gui != null)
            {
                gui.initGui();
            }
        }
    }
}

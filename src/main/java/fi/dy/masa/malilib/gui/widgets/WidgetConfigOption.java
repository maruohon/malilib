package fi.dy.masa.malilib.gui.widgets;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.IConfigOptionList;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.config.IConfigStringList;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.IStringRepresentable;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerButton;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerKeybind;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerTextField;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig.ConfigResetterButton;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig.ConfigResetterTextField;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.gui.GuiConfigsBase.ConfigOptionWrapper;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonBoolean;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.button.ConfigButtonOptionList;
import fi.dy.masa.malilib.gui.button.ConfigButtonStringList;
import fi.dy.masa.malilib.gui.interfaces.IConfigInfoProvider;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class WidgetConfigOption extends WidgetConfigOptionBase
{
    protected final ConfigOptionWrapper wrapper;
    protected final IKeybindConfigGui host;
    @Nullable protected final KeybindSettings initialKeybindSettings;
    @Nullable protected ImmutableList<String> initialStringList;
    protected int colorDisplayPosX;

    public WidgetConfigOption(int x, int y, int width, int height, float zLevel, int labelWidth, int configWidth,
            ConfigOptionWrapper wrapper, IKeybindConfigGui host, MinecraftClient mc, WidgetListConfigOptionsBase<?, ?> parent)
    {
        super(x, y, width, height, zLevel, mc, parent);

        this.host = host;
        this.wrapper = wrapper;

        if (wrapper.getType() == ConfigOptionWrapper.Type.CONFIG)
        {
            IConfigBase config = wrapper.getConfig();

            if (wrapper.getConfig() instanceof IStringRepresentable)
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

                if (wrapper.getConfig() instanceof IConfigStringList)
                {
                    this.initialStringList = ImmutableList.copyOf(((IConfigStringList) wrapper.getConfig()).getStrings());
                }
            }

            this.addConfigOption(x, y, zLevel, labelWidth, configWidth, config);
        }
        else
        {
            this.initialStringValue = null;
            this.lastAppliedValue = null;
            this.initialKeybindSettings = null;

            this.addLabel(x, y + 7, labelWidth, 8, 0xFFFFFFFF, wrapper.getLabel());
        }
    }

    protected void addConfigOption(int x, int y, float zLevel, int labelWidth, int configWidth, IConfigBase config)
    {
        ConfigType type = config.getType();

        int id = 0;

        y += 1;
        int configHeight = 20;

        this.addLabel(x, y + 7, labelWidth, 8, 0xFFFFFFFF, config.getName());

        String comment = null;
        IConfigInfoProvider infoProvider = this.host.getHoverInfoProvider();

        if (infoProvider != null)
        {
            comment = infoProvider.getHoverInfo(config);
        }
        else
        {
            comment = config.getComment();
        }

        if (comment != null)
        {
            this.addConfigComment(x, y + 5, labelWidth, 12, comment);
        }

        x += labelWidth + 10;

        if (type == ConfigType.BOOLEAN)
        {
            ConfigButtonBoolean optionButton = new ConfigButtonBoolean(id++, x, y, configWidth, configHeight, (IConfigBoolean) config);
            this.addConfigButtonEntry(id++, x + configWidth + 4, y, (IConfigResettable) config, optionButton);
        }
        else if (type == ConfigType.OPTION_LIST)
        {
            ConfigButtonOptionList optionButton = new ConfigButtonOptionList(id++, x, y, configWidth, configHeight, (IConfigOptionList) config);
            this.addConfigButtonEntry(id++, x + configWidth + 4, y, (IConfigResettable) config, optionButton);
        }
        else if (type == ConfigType.STRING_LIST)
        {
            ConfigButtonStringList optionButton = new ConfigButtonStringList(id++, x, y, configWidth, configHeight, (IConfigStringList) config, this.host, this.host.getDialogHandler());
            this.addConfigButtonEntry(id++, x + configWidth + 4, y, (IConfigResettable) config, optionButton);
        }
        else if (type == ConfigType.HOTKEY)
        {
            configWidth -= 25; // adjust the width to match other configs due to the settings widget
            IKeybind keybind = ((IHotkey) config).getKeybind();
            ConfigButtonKeybind keybindButton = new ConfigButtonKeybind(id++, x, y, configWidth, configHeight, keybind, this.host);
            x += configWidth + 4;

            this.addWidget(new WidgetKeybindSettings(x, y, 20, 20, zLevel, keybind, config.getName(), this.parent, this.host.getDialogHandler()));
            x += 25;

            this.addButton(keybindButton, this.host.getButtonPressListener());
            this.addKeybindResetButton(id++, x, y, keybind, keybindButton);
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
            }

            this.addConfigTextFieldEntry(id++, x, y, resetX, configWidth, configHeight, (IConfigValue) config);
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
                    modified |= this.initialStringValue.equals(this.textField.getTextField().getText()) == false;
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
                config.setValueFromString(this.textField.getTextField().getText());
            }

            this.lastAppliedValue = config.getStringValue();
        }
    }

    protected void addConfigComment(int x, int y, int width, int height, String comment)
    {
        this.addWidget(new WidgetHoverInfo(x, y, width, height, comment));
    }

    protected void addConfigButtonEntry(int id, int xReset, int yReset, IConfigResettable config, ButtonBase optionButton)
    {
        ButtonGeneric resetButton = this.createResetButton(id, xReset, yReset, config);
        ConfigOptionChangeListenerButton<ButtonBase> listenerChange = new ConfigOptionChangeListenerButton<>(config, resetButton, null);
        ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig(config, new ConfigResetterButton(optionButton), resetButton, null);

        this.addButton(optionButton, listenerChange);
        this.addButton(resetButton, listenerReset);
    }

    protected void addConfigTextFieldEntry(int id, int x, int y, int resetX, int configWidth, int configHeight, IConfigValue config)
    {
        TextFieldWidget field = this.createTextField(id++, x, y + 1, configWidth - 4, configHeight - 3);
        field.setMaxLength(this.maxTextfieldTextLength);
        field.setText(config.getStringValue());

        ButtonGeneric resetButton = this.createResetButton(id, resetX, y, config);
        ConfigOptionChangeListenerTextField listenerChange = new ConfigOptionChangeListenerTextField(config, field, resetButton);
        ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig(config, new ConfigResetterTextField(config, field), resetButton, null);

        this.addTextField(field, listenerChange);
        this.addButton(resetButton, listenerReset);
    }

    protected void addKeybindResetButton(int id, int x, int y, IKeybind keybind, ConfigButtonKeybind buttonHotkey)
    {
        ButtonGeneric button = this.createResetButton(id, x, y, keybind);

        ConfigOptionChangeListenerKeybind listener = new ConfigOptionChangeListenerKeybind(keybind, buttonHotkey, button, this.host);
        this.host.addKeybindChangeListener(listener);
        this.addButton(button, listener);
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        GlStateManager.color4f(1, 1, 1, 1);

        this.drawSubWidgets(mouseX, mouseY);

        if (this.wrapper.getType() == ConfigOptionWrapper.Type.CONFIG)
        {
            IConfigBase config = this.wrapper.getConfig();
            this.drawTextFields(mouseX, mouseY);
            this.drawButtons(mouseX, mouseY, 0f);

            if (config.getType() == ConfigType.COLOR)
            {
                int y = this.y + 1;
                DrawableHelper.fill(this.colorDisplayPosX    , y + 0, this.colorDisplayPosX + 19, y + 19, 0xFFFFFFFF);
                DrawableHelper.fill(this.colorDisplayPosX + 1, y + 1, this.colorDisplayPosX + 18, y + 18, 0xFF000000);
                DrawableHelper.fill(this.colorDisplayPosX + 2, y + 2, this.colorDisplayPosX + 17, y + 17, 0xFF000000 | ((ConfigColor) config).getIntegerValue());
            }
        }
    }
}

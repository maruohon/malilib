package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.IConfigOptionList;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerButton;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerKeybind;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerTextField;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig.ConfigResetterButton;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig.ConfigResetterTextField;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase.ConfigOptionWrapper;
import fi.dy.masa.malilib.gui.GuiTextFieldWrapper;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonBoolean;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.button.ConfigButtonOptionList;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IConfigInfoProvider;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.wrappers.ButtonWrapper;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class WidgetConfigOption extends WidgetBase
{
    protected final List<WidgetBase> widgets = new ArrayList<>();
    protected final List<ButtonWrapper<? extends ButtonBase>> buttons = new ArrayList<>();
    protected final ConfigOptionWrapper wrapper;
    protected final Minecraft mc;
    protected final IKeybindConfigGui host;
    protected final WidgetListConfigOptions parent;
    protected final String initialValue;
    @Nullable
    protected final KeybindSettings initialKeybindSettings;
    protected GuiTextFieldWrapper textField = null;
    /**
     * The last applied value for any textfield-based configs.
     * Button based (boolean, option-list) values get applied immediately upon clicking the button.
     */
    protected String lastAppliedValue;
    protected int maxTextfieldTextLength = 256;
    protected int colorDisplayPosX;

    public WidgetConfigOption(int x, int y, int width, int height, float zLevel, int labelWidth, int configWidth,
            ConfigOptionWrapper wrapper, IKeybindConfigGui host, Minecraft mc, WidgetListConfigOptions parent)
    {
        super(x, y, width, height, zLevel);

        this.host = host;
        this.mc = mc;
        this.parent = parent;
        this.wrapper = wrapper;

        if (wrapper.getType() == ConfigOptionWrapper.Type.CONFIG)
        {
            IConfigValue config = wrapper.getConfig();
            this.initialValue = config.getStringValue();
            this.lastAppliedValue = config.getStringValue();
            this.initialKeybindSettings = config.getType() == ConfigType.HOTKEY ? ((IHotkey) config).getKeybind().getSettings() : null;

            this.addConfigOption(x, y, zLevel, labelWidth, configWidth, config);
        }
        else
        {
            this.initialValue = null;
            this.lastAppliedValue = null;
            this.initialKeybindSettings = null;

            this.addLabel(x, y + 7, labelWidth, 8, 0xFFFFFFFF, wrapper.getLabel());
        }
    }

    protected void addConfigOption(int x, int y, float zLevel, int labelWidth, int configWidth, IConfigValue config)
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

        if (type == ConfigType.BOOLEAN && (config instanceof IConfigBoolean))
        {
            ConfigButtonBoolean optionButton = new ConfigButtonBoolean(id++, x, y, configWidth, configHeight, (IConfigBoolean) config);
            this.addConfigButtonEntry(id++, x + configWidth + 4, y, config, optionButton);
        }
        else if (type == ConfigType.OPTION_LIST && (config instanceof IConfigOptionList))
        {
            ConfigButtonOptionList optionButton = new ConfigButtonOptionList(id++, x, y, configWidth, configHeight, (IConfigOptionList) config);
            this.addConfigButtonEntry(id++, x + configWidth + 4, y, config, optionButton);
        }
        else if (type == ConfigType.HOTKEY && (config instanceof IHotkey))
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

            this.addConfigTextFieldEntry(id++, x, y, resetX, configWidth, configHeight, config);
        }
    }

    public boolean wasConfigModified()
    {
        if (this.wrapper.getType() == ConfigOptionWrapper.Type.CONFIG)
        {
            IConfigValue config = this.wrapper.getConfig();
            boolean modified = false;

            if (this.textField != null)
            {
                modified |= this.initialValue.equals(this.textField.getTextField().getText()) == false;
            }

            if (this.initialKeybindSettings != null && this.initialKeybindSettings.equals(((IHotkey) config).getKeybind().getSettings()) == false)
            {
                modified = true;
            }

            return modified || this.initialValue.equals(config.getStringValue()) == false;
        }

        return false;
    }

    public boolean hasPendingModifications()
    {
        if (this.textField != null)
        {
            return this.lastAppliedValue.equals(this.textField.getTextField().getText()) == false;
        }

        return false;
    }

    public void applyNewValueToConfig()
    {
        if (this.wrapper.getType() == ConfigOptionWrapper.Type.CONFIG)
        {
            IConfigValue config = this.wrapper.getConfig();

            if (this.textField != null && this.hasPendingModifications())
            {
                config.setValueFromString(this.textField.getTextField().getText());
            }

            this.lastAppliedValue = config.getStringValue();
        }
    }

    protected <T extends ButtonBase> ButtonWrapper<T> addButton(T button, IButtonActionListener<T> listener)
    {
        ButtonWrapper<T> entry = new ButtonWrapper<>(button, listener);
        this.buttons.add(entry);
        return entry;
    }

    protected void addConfigComment(int x, int y, int width, int height, String comment)
    {
        this.addWidget(new WidgetHoverInfo(x, y, width, height, comment));
    }

    protected GuiTextField createTextField(int id, int x, int y, int width, int height)
    {
        return new GuiTextField(id, this.mc.fontRenderer, x + 2, y, width, height);
    }

    protected void addTextField(GuiTextField field, ConfigOptionChangeListenerTextField listener)
    {
        GuiTextFieldWrapper wrapper = new GuiTextFieldWrapper(field, listener);
        this.textField = wrapper;
        this.parent.addTextField(wrapper);
    }

    protected void addConfigButtonEntry(int id, int xReset, int yReset, IConfigValue config, ButtonBase optionButton)
    {
        ButtonGeneric resetButton = this.createResetButton(id, xReset, yReset, config);

        ConfigOptionChangeListenerButton<ButtonBase> listenerChange = new ConfigOptionChangeListenerButton<>(config, resetButton, null);
        ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig(config, new ConfigResetterButton(optionButton), resetButton, null);

        this.addButton(optionButton, listenerChange);
        this.addButton(resetButton, listenerReset);
    }

    protected void addConfigTextFieldEntry(int id, int x, int y, int resetX, int configWidth, int configHeight, IConfigValue config)
    {
        GuiTextField field = this.createTextField(id++, x, y + 1, configWidth - 4, configHeight - 3);
        field.setMaxStringLength(this.maxTextfieldTextLength);
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

    protected ButtonGeneric createResetButton(int id, int x, int y, IConfigResettable config)
    {
        String labelReset = I18n.format("malilib.gui.button.reset.caps");
        int w = this.mc.fontRenderer.getStringWidth(labelReset) + 10;

        ButtonGeneric resetButton = new ButtonGeneric(id, x, y, w, 20, labelReset);
        resetButton.enabled = config.isModified();

        return resetButton;
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        for (ButtonWrapper<?> entry : this.buttons)
        {
            if (entry.mousePressed(this.mc, mouseX, mouseY, mouseButton))
            {
                // Don't call super if the button press got handled
                return true;
            }
        }

        boolean ret = false;

        if (this.textField != null)
        {
            ret |= this.textField.getTextField().mouseClicked(mouseX, mouseY, mouseButton);
        }

        if (this.subWidgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.subWidgets)
            {
                ret |= widget.isMouseOver(mouseX, mouseY) && widget.onMouseClicked(mouseX, mouseY, mouseButton);
            }
        }

        return ret;
    }

    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        if (this.textField != null)
        {
            if (keyCode == Keyboard.KEY_RETURN)
            {
                this.applyNewValueToConfig();
            }
            else
            {
                return this.textField.keyTyped(typedChar, keyCode);
            }
        }

        return false;
    }

    @Override
    public boolean canSelectAt(int mouseX, int mouseY, int mouseButton)
    {
        return false;
    }

    protected void drawButtons(int mouseX, int mouseY, float partialTicks)
    {
        for (ButtonWrapper<?> entry : this.buttons)
        {
            entry.draw(this.mc, mouseX, mouseY, partialTicks);
        }
    }

    protected void drawTextFields(int mouseX, int mouseY)
    {
        if (this.textField != null)
        {
            this.textField.getTextField().drawTextBox();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        GlStateManager.color(1, 1, 1, 1);

        this.drawSubWidgets(mouseX, mouseY);

        if (this.wrapper.getType() == ConfigOptionWrapper.Type.CONFIG)
        {
            IConfigValue config = this.wrapper.getConfig();
            this.drawTextFields(mouseX, mouseY);
            this.drawButtons(mouseX, mouseY, 0f);

            if (config.getType() == ConfigType.COLOR)
            {
                int y = this.y + 1;
                GuiBase.drawRect(this.colorDisplayPosX    , y + 0, this.colorDisplayPosX + 19, y + 19, 0xFFFFFFFF);
                GuiBase.drawRect(this.colorDisplayPosX + 1, y + 1, this.colorDisplayPosX + 18, y + 18, 0xFF000000);
                GuiBase.drawRect(this.colorDisplayPosX + 2, y + 2, this.colorDisplayPosX + 17, y + 17, 0xFF000000 | ((ConfigColor) config).getIntegerValue());
            }
        }
    }
}

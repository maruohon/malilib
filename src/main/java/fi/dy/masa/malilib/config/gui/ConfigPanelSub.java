package fi.dy.masa.malilib.config.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import com.google.common.collect.ImmutableList;
import com.mumfrey.liteloader.modconfig.AbstractConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.IConfigOptionList;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.IStringRepresentable;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig.ConfigResetterButton;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig.ConfigResetterTextField;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.gui.HoverInfo;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonBoolean;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.button.ConfigButtonOptionList;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.wrappers.ButtonWrapper;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import net.minecraft.client.resources.I18n;

public class ConfigPanelSub extends AbstractConfigPanel implements IKeybindConfigGui
{
    private final String modId;
    private final ConfigPanelBase parent;
    private final List<ButtonWrapper<? extends ButtonBase>> buttons = new ArrayList<>();
    private final Map<IConfigValue, ConfigTextFieldWrapper> textFields = new HashMap<>();
    private final ButtonPressDirtyListenerSimple<ButtonBase> dirtyListener = new ButtonPressDirtyListenerSimple<>();
    protected final List<ConfigOptionChangeListenerKeybind> hotkeyResetListeners = new ArrayList<>();
    private final List<HoverInfo> configComments = new ArrayList<>();
    private final String title;
    protected ConfigButtonKeybind activeKeybindButton;
    protected List<IConfigValue> configs = ImmutableList.of();
    protected int elementWidth = 204;
    protected int maxTextfieldTextLength = 256;

    public ConfigPanelSub(String modId, String title, ConfigPanelBase parent)
    {
        this.modId = modId;
        this.title = title;
        this.parent = parent;
    }

    public ConfigPanelSub(String modId, String title, IConfigValue[] configs, ConfigPanelBase parent)
    {
        this(modId, title, parent);

        this.configs = ImmutableList.copyOf(this.configs);
    }

    @Override
    public List<? extends IConfigValue> getConfigs()
    {
        return this.configs;
    }

    protected void onSettingsChanged()
    {
        ConfigManager.getInstance().onConfigsChanged(this.modId);

        if (this.hotkeyResetListeners.size() > 0)
        {
            InputEventHandler.getInstance().updateUsedKeys();
        }
    }

    public ConfigPanelSub setElementWidth(int elementWidth)
    {
        this.elementWidth = elementWidth;
        return this;
    }

    @Override
    public String getPanelTitle()
    {
        return this.title;
    }

    @Override
    public void onPanelHidden()
    {
        boolean dirty = false;

        if (this.getButtonPressListener().isDirty())
        {
            dirty = true;
            this.getButtonPressListener().resetDirty();
        }

        dirty |= this.handleTextFields();

        if (dirty)
        {
            this.onSettingsChanged();
        }
    }

    @Override
    public void mousePressed(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton)
    {
        // Don't call super if the button got handled
        if (this.mousePressed(mouseX, mouseY, mouseButton) == false)
        {
            super.mousePressed(host, mouseX, mouseY, mouseButton);
        }
    }

    /**
     * Handle a mouse button click. Returns true if the click was handled
     */
    protected boolean mousePressed(int mouseX, int mouseY, int mouseButton)
    {
        for (ButtonWrapper<? extends ButtonBase> entry : this.buttons)
        {
            if (entry.mousePressed(this.mc, mouseX, mouseY, mouseButton))
            {
                // Don't call super if the button press got handled
                return true;
            }
        }

        // When clicking on not-a-button, clear the selection
        if (this.activeKeybindButton != null)
        {
            this.activeKeybindButton.onClearSelection();
            this.setActiveKeybindButton(null);
            return true;
        }

        return false;
    }

    protected <T extends ButtonBase> ButtonWrapper<T> addButton(T button, IButtonActionListener<T> listener)
    {
        ButtonWrapper<T> entry = new ButtonWrapper<>(button, listener);
        this.buttons.add(entry);
        return entry;
    }

    @Override
    public void addKeybindChangeListener(ConfigOptionChangeListenerKeybind listener)
    {
        this.hotkeyResetListeners.add(listener);
    }

    @Override
    public void setActiveKeybindButton(@Nullable ConfigButtonKeybind button)
    {
        if (this.activeKeybindButton != null)
        {
            this.activeKeybindButton.onClearSelection();
            this.updateKeybindButtons();
        }

        this.activeKeybindButton = button;

        if (this.activeKeybindButton != null)
        {
            this.activeKeybindButton.onSelected();
        }
    }

    @Override
    public ButtonPressDirtyListenerSimple<ButtonBase> getButtonPressListener()
    {
        return this.dirtyListener;
    }

    protected void updateKeybindButtons()
    {
        for (ConfigOptionChangeListenerKeybind listener : this.hotkeyResetListeners)
        {
            listener.updateButtons();
        }
    }

    protected boolean handleTextFields()
    {
        boolean dirty = false;

        for (IConfigValue config : this.getConfigs())
        {
            ConfigType type = config.getType();

            if (type == ConfigType.STRING ||
                type == ConfigType.COLOR ||
                type == ConfigType.INTEGER ||
                type == ConfigType.DOUBLE)
            {
                ConfigTextField field = this.getTextFieldFor(config);

                if (field != null)
                {
                    String newValue = field.getText();

                    if (newValue.equals(config.getStringValue()) == false)
                    {
                        config.setValueFromString(newValue);
                        dirty = true;
                    }
                }
            }
        }

        return dirty;
    }

    @Override
    public void addOptions(ConfigPanelHost host)
    {
        this.clearOptions();

        final int xStart = 10;
        int x = xStart;
        int y = 10;
        int configWidth = this.elementWidth;
        int configHeight = 20;
        int labelWidth = this.getMaxLabelWidth(this.getConfigs());
        int id = 0;

        for (IConfigValue config : this.getConfigs())
        {
            this.addLabel(id++, x, y + 7, labelWidth, 8, 0xFFFFFFFF, config.getName());

            String comment = config.getComment();
            ConfigType type = config.getType();

            if (comment != null)
            {
                this.addConfigComment(x, y + 2, labelWidth, 10, comment);
            }

            x += labelWidth + 10;

            if (type == ConfigType.BOOLEAN && (config instanceof IConfigBoolean))
            {
                ConfigButtonBoolean optionButton = new ConfigButtonBoolean(id++, x, y, configWidth, configHeight, (IConfigBoolean) config);
                this.addConfigButtonEntry(id++, x + configWidth + 10, y, config, optionButton);
            }
            else if (type == ConfigType.OPTION_LIST && (config instanceof IConfigOptionList))
            {
                ConfigButtonOptionList optionButton = new ConfigButtonOptionList(id++, x, y, configWidth, configHeight, (IConfigOptionList) config);
                this.addConfigButtonEntry(id++, x + configWidth + 10, y, config, optionButton);
            }
            else if (type == ConfigType.HOTKEY && (config instanceof IHotkey))
            {
                IKeybind keybind = ((IHotkey) config).getKeybind();
                ConfigButtonKeybind keybindButton = new ConfigButtonKeybind(id++, x, y, configWidth, configHeight, keybind, this);

                this.addButton(keybindButton, this.getButtonPressListener());
                this.addKeybindResetButton(id++, x + configWidth + 10, y, keybind, keybindButton);
            }
            else if (type == ConfigType.STRING ||
                     type == ConfigType.COLOR ||
                     type == ConfigType.INTEGER ||
                     type == ConfigType.DOUBLE)
            {
                this.addConfigTextFieldEntry(id++, x, y, configWidth, configHeight, config);
            }

            x = xStart;
            y += configHeight + 1;
        }
    }

    protected void addConfigButtonEntry(int id, int xReset, int yReset, IConfigValue config, ButtonBase optionButton)
    {
        ButtonGeneric resetButton = this.createResetButton(id, xReset, yReset, config);

        ConfigOptionChangeListenerButton<ButtonBase> listenerChange = new ConfigOptionChangeListenerButton<>(config, resetButton, this.getButtonPressListener());
        ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig(config, new ConfigResetterButton(optionButton), resetButton, this.getButtonPressListener());

        this.addButton(optionButton, listenerChange);
        this.addButton(resetButton, listenerReset);
    }

    protected void addConfigTextFieldEntry(int id, int x, int y, int configWidth, int configHeight, IConfigValue config)
    {
        ConfigTextField field = this.addTextField(id++, x, y + 1, configWidth - 4, configHeight - 3);
        field.getNativeTextField().setMaxStringLength(this.maxTextfieldTextLength);
        field.setText(config.getStringValue());

        ButtonGeneric resetButton = this.createResetButton(id, x + configWidth + 10, y, config);
        ConfigOptionChangeListenerTextField listenerChange = new ConfigOptionChangeListenerTextField(config, field.getNativeTextField(), resetButton);
        ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig(config, new ConfigResetterTextField(config, field.getNativeTextField()), resetButton, this.getButtonPressListener());

        this.addTextField(config, field, listenerChange);
        this.addButton(resetButton, listenerReset);
    }

    protected ButtonGeneric createResetButton(int id, int x, int y, IStringRepresentable config)
    {
        String labelReset = I18n.format("malilib.gui.button.reset.caps");
        int w = this.mc.fontRenderer.getStringWidth(labelReset) + 10;

        ButtonGeneric resetButton = new ButtonGeneric(id, x, y, w, 20, labelReset);
        resetButton.enabled = config.isModified();

        return resetButton;
    }

    protected void addKeybindResetButton(int id, int x, int y, IKeybind keybind, ConfigButtonKeybind buttonHotkey)
    {
        ButtonGeneric button = this.createResetButton(id, x, y, keybind);

        ConfigOptionChangeListenerKeybind listener = new ConfigOptionChangeListenerKeybind(keybind, buttonHotkey, button, this);
        this.addKeybindChangeListener(listener);
        this.addButton(button, listener);
    }

    @Override
    public void clearOptions()
    {
        super.clearOptions();

        this.buttons.clear();
        this.textFields.clear();
        this.hotkeyResetListeners.clear();
    }

    @Override
    public void drawPanel(ConfigPanelHost host, int mouseX, int mouseY, float partialTicks)
    {
        super.drawPanel(host, mouseX, mouseY, partialTicks);

        this.drawButtons(mouseX, mouseY, partialTicks);

        for (HoverInfo label : this.configComments)
        {
            if (label.isMouseOver(mouseX, mouseY))
            {
                this.drawHoveringText(label.getLines(), label.getX(), label.getY() + 30);
                break;
            }
        }
    }

    protected void drawButtons(int mouseX, int mouseY, float partialTicks)
    {
        for (ButtonWrapper<?> entry : this.buttons)
        {
            entry.draw(this.mc, mouseX, mouseY, partialTicks);
        }
    }

    protected void addTextField(IConfigValue config, ConfigTextField field, ConfigOptionChangeListenerTextField listener)
    {
        this.textFields.put(config, new ConfigTextFieldWrapper(field, listener));
    }

    @Nullable
    protected ConfigTextField getTextFieldFor(IConfigValue config)
    {
        ConfigTextFieldWrapper wrapper = this.textFields.get(config);
        return wrapper != null ? wrapper.getConfigTextField() : null;
    }

    protected void addConfigComment(int x, int y, int width, int height, String comment)
    {
        HoverInfo info = new HoverInfo(x, y, width, height);
        info.addLines(comment);
        this.configComments.add(info);
    }

    protected int getMaxLabelWidth(List<? extends IConfigBase> entries)
    {
        int maxWidth = 0;

        for (IConfigBase entry : entries)
        {
            maxWidth = Math.max(maxWidth, this.mc.fontRenderer.getStringWidth(entry.getName()));
        }

        return maxWidth;
    }

    @Override
    public void keyPressed(ConfigPanelHost host, char keyChar, int keyCode)
    {
        if (this.activeKeybindButton != null)
        {
            this.activeKeybindButton.onKeyPressed(keyCode);
        }
        else
        {
            if (keyCode == Keyboard.KEY_ESCAPE)
            {
                this.parent.setSelectedSubPanel(-1);
                return;
            }

            super.keyPressed(host, keyChar, keyCode);

            for (ConfigTextFieldWrapper wrapper : this.textFields.values())
            {
                if (wrapper.keyTyped(keyChar, keyCode))
                {
                    break;
                }
            }
        }
    }

    /**
     * Returns true if some of the options in this panel were modified
     * @return
     */
    public boolean hasModifications()
    {
        return false;
    }
}

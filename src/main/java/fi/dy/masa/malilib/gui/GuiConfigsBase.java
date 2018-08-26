package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerButton;
import fi.dy.masa.malilib.config.gui.ConfigOptionDirtyListener;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetKeybind;
import fi.dy.masa.malilib.config.gui.IKeybindConfigGui;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig.ConfigResetterBase;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig.ConfigResetterButton;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig.ConfigResetterTextField;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ButtonWrapper;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.reference.Reference;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public abstract class GuiConfigsBase extends GuiScreen implements IKeybindConfigGui
{
    private final String modId;
    @Nullable private final GuiScreen parent;
    private final List<GuiLabel> labels = new ArrayList<>();
    private final List<HoverInfo> configComments = new ArrayList<>();
    private final List<ButtonWrapper<? extends ButtonBase>> buttons = new ArrayList<>();
    private final List<GuiTextFieldWrapper> textFields = new ArrayList<>();
    private final Map<IConfigValue, GuiTextFieldWrapper> textFieldsForConfigs = new HashMap<>();
    private final List<ConfigOptionListenerResetConfig> configResetListeners = new ArrayList<>();
    protected final List<ConfigOptionListenerResetKeybind> hotkeyResetListeners = new ArrayList<>();
    private final ConfigOptionDirtyListener<ButtonBase> dirtyListener = new ConfigOptionDirtyListener<>();
    protected ConfigButtonKeybind activeKeybindButton;
    protected int elementWidth = 204;
    protected int maxTextfieldTextLength = 256;

    public GuiConfigsBase(@Nullable GuiScreen parent)
    {
        this.modId = Reference.MOD_ID;
        this.parent = parent;
    }

    protected abstract Collection<IConfigValue> getConfigs();

    protected void onSettingsChanged()
    {
        ConfigManager.getInstance().onConfigsChanged(this.modId);

        if (this.hotkeyResetListeners.size() > 0)
        {
            InputEventHandler.getInstance().updateUsedKeys();
        }
    }

    public GuiConfigsBase setElementWidth(int elementWidth)
    {
        this.elementWidth = elementWidth;
        return this;
    }

    @Override
    public void onGuiClosed()
    {
        boolean dirty = false;

        if (this.getConfigListener().isDirty())
        {
            dirty = true;
            this.getConfigListener().resetDirty();
        }

        dirty |= this.handleTextFields();

        if (dirty)
        {
            this.onSettingsChanged();
        }
    }

    public boolean keyPressed(char keyChar, int keyCode)
    {
        if (this.activeKeybindButton != null)
        {
            this.activeKeybindButton.onKeyPressed(keyCode);
            return true;
        }
        else
        {
            if (keyCode == Keyboard.KEY_ESCAPE)
            {
                this.mc.displayGuiScreen(this.parent);
                return true;
            }

            for (GuiTextFieldWrapper wrapper : this.textFields)
            {
                if (wrapper.keyTyped(keyChar, keyCode))
                {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Handle a mouse button click. Returns true if the click was handled
     */
    public boolean mousePressed(int mouseX, int mouseY, int mouseButton)
    {
        for (ButtonWrapper<? extends ButtonBase> entry : this.buttons)
        {
            if (entry.mousePressed(this.mc, mouseX, mouseY, mouseButton))
            {
                // Don't call super if the button press got handled
                return true;
            }
        }

        boolean ret = false;

        for (GuiTextFieldWrapper wrapper : this.textFields)
        {
            ret |= wrapper.getTextField().mouseClicked(mouseX, mouseY, mouseButton);
        }

        if (ret)
        {
            return true;
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

    protected void addLabel(int id, int x, int y, int width, int height, int colour, String... lines)
    {
        if (lines == null || lines.length < 1)
        {
            return;
        }

        GuiLabel label = new GuiLabel(this.mc.fontRenderer, id, x, y, width, height, colour);

        for (String line : lines)
        {
            label.addLine(line);
        }

        this.labels.add(label);
    }

    protected void addConfigComment(int x, int y, int width, int height, String comment)
    {
        HoverInfo info = new HoverInfo(x, y, width, height);
        info.addLines(comment);
        this.configComments.add(info);
    }

    protected GuiTextField createTextField(int id, int x, int y, int width, int height)
    {
        return new GuiTextField(id, this.mc.fontRenderer, x + 2, y, width, height);
    }

    protected void addTextField(IConfigValue config, GuiTextField field, ConfigOptionTextFieldChangeListener listener)
    {
        GuiTextFieldWrapper wrapper = new GuiTextFieldWrapper(field, listener);
        this.textFields.add(wrapper);
        this.textFieldsForConfigs.put(config, wrapper);
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
    public ConfigOptionDirtyListener<ButtonBase> getConfigListener()
    {
        return this.dirtyListener;
    }

    protected void updateKeybindButtons()
    {
        for (ConfigOptionListenerResetKeybind listener : this.hotkeyResetListeners)
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
                GuiTextField field = this.getTextFieldFor(config);

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

    protected void addConfigButtonEntry(int id, int xReset, int yReset, IConfigValue config, ButtonBase optionButton)
    {
        ButtonGeneric resetButton = this.createResetButton(id, xReset, yReset, config);

        ConfigOptionChangeListenerButton<ButtonBase> listenerChange = new ConfigOptionChangeListenerButton<>(config, this.getConfigListener(), resetButton);
        ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig(config, new ConfigResetterButton(optionButton), resetButton, this.getConfigListener());

        this.addButton(optionButton, listenerChange);
        this.addButton(resetButton, listenerReset);
    }

    protected void addConfigTextFieldEntry(int id, int x, int y, int configWidth, int configHeight, IConfigValue config)
    {
        GuiTextField field = this.createTextField(id++, x, y + 1, configWidth - 4, configHeight - 3);
        field.setMaxStringLength(this.maxTextfieldTextLength);
        field.setText(config.getStringValue());

        ButtonGeneric resetButton = this.createResetButton(id, x + configWidth + 10, y, config);
        ConfigOptionTextFieldChangeListener listenerChange = new ConfigOptionTextFieldChangeListener(config, field, resetButton);
        ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig(config, new ConfigResetterTextField(config, field), resetButton, this.getConfigListener());

        this.addTextField(config, field, listenerChange);
        this.addButton(resetButton, listenerReset);
    }

    protected ButtonWrapper<ButtonGeneric> createConfigResetButton(int id, int x, int y, IConfigValue config, ConfigResetterBase reset)
    {
        String label = I18n.format("malilib.gui.button.reset.caps");
        int w = this.mc.fontRenderer.getStringWidth(label) + 10;
        ButtonGeneric buttonReset = new ButtonGeneric(id, x, y, w, 20, label);
        buttonReset.enabled = config.isModified();

        ConfigOptionListenerResetConfig listener = new ConfigOptionListenerResetConfig(config, reset, buttonReset, this.getConfigListener());
        this.configResetListeners.add(listener);
        return this.addButton(buttonReset, listener);
    }

    protected ButtonGeneric createResetButton(int id, int x, int y, IConfigValue config)
    {
        String labelReset = I18n.format("malilib.gui.button.reset.caps");
        int w = this.mc.fontRenderer.getStringWidth(labelReset) + 10;

        ButtonGeneric resetButton = new ButtonGeneric(id, x, y, w, 20, labelReset);
        resetButton.enabled = config.isModified();

        return resetButton;
    }

    protected void addKeybindResetButton(int id, int x, int y, IKeybind keybind, ConfigButtonKeybind buttonHotkey)
    {
        String label = I18n.format("malilib.gui.button.reset.caps");
        int w = this.mc.fontRenderer.getStringWidth(label) + 10;
        ButtonGeneric button = new ButtonGeneric(id, x, y, w, 20, label);
        button.enabled = keybind.isModified();

        ConfigOptionListenerResetKeybind listener = new ConfigOptionListenerResetKeybind(keybind, buttonHotkey, button, this);
        this.hotkeyResetListeners.add(listener);
        this.addButton(button, listener);
    }

    public void clearOptions()
    {
        this.labels.clear();
        this.configComments.clear();
        this.buttons.clear();
        this.textFields.clear();
        this.textFieldsForConfigs.clear();
        this.configResetListeners.clear();
        this.hotkeyResetListeners.clear();
    }

    protected void drawGui(int mouseX, int mouseY, float partialTicks)
    {
        this.drawLabels(mouseX, mouseY, partialTicks);
        this.drawButtons(mouseX, mouseY, partialTicks);
        this.drawTextFields(mouseX, mouseY, partialTicks);

        for (HoverInfo label : this.configComments)
        {
            if (label.isMouseOver(mouseX, mouseY))
            {
                this.drawHoveringText(label.getLines(), label.getX(), label.getY() + 30);
                break;
            }
        }
    }

    protected void drawLabels(int mouseX, int mouseY, float partialTicks)
    {
        for (GuiLabel label : this.labels)
        {
            label.drawLabel(this.mc, mouseX, mouseY);
        }
    }

    protected void drawButtons(int mouseX, int mouseY, float partialTicks)
    {
        for (ButtonWrapper<?> entry : this.buttons)
        {
            entry.draw(this.mc, mouseX, mouseY, partialTicks);
        }
    }

    protected void drawTextFields(int mouseX, int mouseY, float partialTicks)
    {
        for (GuiTextFieldWrapper entry : this.textFields)
        {
            entry.getTextField().drawTextBox();
        }
    }

    @Nullable
    protected GuiTextField getTextFieldFor(IConfigValue config)
    {
        GuiTextFieldWrapper wrapper = this.textFieldsForConfigs.get(config);
        return wrapper != null ? wrapper.getTextField() : null;
    }

    protected int getMaxLabelWidth(Collection<IConfigValue> entries)
    {
        int maxWidth = 0;

        for (IConfigBase entry : entries)
        {
            maxWidth = Math.max(maxWidth, this.mc.fontRenderer.getStringWidth(entry.getName()));
        }

        return maxWidth;
    }
}

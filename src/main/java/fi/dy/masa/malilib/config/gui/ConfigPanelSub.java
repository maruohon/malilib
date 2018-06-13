package fi.dy.masa.malilib.config.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import com.mumfrey.liteloader.modconfig.AbstractConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.IConfigOptionList;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig.ConfigResetterBase;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig.ConfigResetterButton;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig.ConfigResetterTextField;
import fi.dy.masa.malilib.gui.HoverInfo;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ButtonWrapper;
import fi.dy.masa.malilib.gui.button.ConfigButtonBoolean;
import fi.dy.masa.malilib.gui.button.ConfigButtonOptionList;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import net.minecraft.client.resources.I18n;

public abstract class ConfigPanelSub extends AbstractConfigPanel
{
    private final ConfigPanelBase parent;
    private final List<ButtonWrapper<? extends ButtonBase>> buttons = new ArrayList<>();
    private final Map<IConfigValue, TextFieldWrapper> textFields = new HashMap<>();
    private final ConfigOptionDirtyListener<ButtonBase> dirtyListener = new ConfigOptionDirtyListener<>();
    private final List<ConfigOptionListenerResetConfig> configResetListeners = new ArrayList<>();
    private final List<HoverInfo> configComments = new ArrayList<>();
    private final String title;
    protected IConfigValue[] configs = new IConfigValue[0];
    protected int elementWidth = 204;
    protected int maxTextfieldTextLength = 256;

    public ConfigPanelSub(String title, ConfigPanelBase parent)
    {
        this.title = title;
        this.parent = parent;
    }

    protected IConfigValue[] getConfigs()
    {
        return this.configs;
    }

    protected void onSettingsChanged()
    {
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

        if (this.dirtyListener.isDirty())
        {
            dirty = true;
            this.dirtyListener.resetDirty();
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

        return false;
    }

    protected <T extends ButtonBase> ButtonWrapper<T> addButton(T button, IButtonActionListener<T> listener)
    {
        ButtonWrapper<T> entry = new ButtonWrapper<>(button, listener);
        this.buttons.add(entry);
        return entry;
    }

    protected boolean handleTextFields()
    {
        boolean dirty = false;

        for (IConfigValue config : this.getConfigs())
        {
            ConfigType type = config.getType();

            if (type == ConfigType.STRING ||
                type == ConfigType.HEX_STRING ||
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

    protected ConfigOptionDirtyListener<ButtonBase> getConfigListener()
    {
        return this.dirtyListener;
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
            else if (type == ConfigType.STRING ||
                     type == ConfigType.HEX_STRING ||
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

        ConfigOptionChangeListenerButton<ButtonBase> listenerChange = new ConfigOptionChangeListenerButton<>(config, this.dirtyListener, resetButton);
        ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig(new ConfigResetterButton(optionButton), config, resetButton);

        this.addButton(optionButton, listenerChange);
        this.addButton(resetButton, listenerReset);
    }

    protected void addConfigTextFieldEntry(int id, int x, int y, int configWidth, int configHeight, IConfigValue config)
    {
        ConfigTextField field = this.addTextField(id++, x, y + 1, configWidth - 4, configHeight - 3);
        field.setText(config.getStringValue());
        field.getNativeTextField().setMaxStringLength(this.maxTextfieldTextLength);

        ButtonGeneric resetButton = this.createResetButton(id, x + configWidth + 10, y, config);
        ConfigOptionChangeListenerTextField listenerChange = new ConfigOptionChangeListenerTextField(config, field, resetButton);
        ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig(new ConfigResetterTextField(config, field), config, resetButton);

        this.addTextField(config, field, listenerChange);
        this.addButton(resetButton, listenerReset);
    }

    protected ButtonWrapper<ButtonGeneric> createConfigResetButton(int id, int x, int y, IConfigValue config, ConfigResetterBase reset)
    {
        String label = I18n.format("malilib.gui.button.reset.caps");
        int w = this.mc.fontRenderer.getStringWidth(label) + 10;
        ButtonGeneric buttonReset = new ButtonGeneric(id, x, y, w, 20, label);
        buttonReset.enabled = config.isModified();

        ConfigOptionListenerResetConfig listener = new ConfigOptionListenerResetConfig(reset, config, buttonReset);
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

    @Override
    public void clearOptions()
    {
        super.clearOptions();

        this.buttons.clear();
        this.textFields.clear();
        this.configResetListeners.clear();
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
        this.textFields.put(config, new TextFieldWrapper(field, listener));
    }

    @Nullable
    protected ConfigTextField getTextFieldFor(IConfigValue config)
    {
        TextFieldWrapper wrapper = this.textFields.get(config);
        return wrapper != null ? wrapper.getTextField() : null;
    }

    protected void addConfigComment(int x, int y, int width, int height, String comment)
    {
        HoverInfo info = new HoverInfo(x, y, width, height);
        info.addLines(comment);
        this.configComments.add(info);
    }

    protected int getMaxLabelWidth(IConfigBase[] entries)
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
        if (keyCode == Keyboard.KEY_ESCAPE)
        {
            this.parent.setSelectedSubPanel(-1);
            return;
        }

        super.keyPressed(host, keyChar, keyCode);

        for (TextFieldWrapper wrapper : this.textFields.values())
        {
            if (wrapper.keyTyped(keyChar, keyCode))
            {
                break;
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

package fi.dy.masa.malilib.config.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lwjgl.input.Keyboard;
import com.mumfrey.liteloader.modconfig.AbstractConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.IConfigOptionList;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonEntry;
import fi.dy.masa.malilib.gui.button.ConfigButtonBoolean;
import fi.dy.masa.malilib.gui.button.ConfigButtonOptionList;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import net.minecraft.client.resources.I18n;

public abstract class ConfigPanelSub extends AbstractConfigPanel
{
    private final ConfigPanelBase parent;
    private final Map<IConfigValue, ConfigTextField> textFields = new HashMap<>();
    private final ConfigOptionListenerGeneric<ButtonBase> listener = new ConfigOptionListenerGeneric<>();
    private final List<ButtonEntry<?>> buttons = new ArrayList<>();
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

        if (this.listener.isDirty())
        {
            dirty = true;
            this.listener.resetDirty();
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
        for (ButtonEntry<?> entry : this.buttons)
        {
            if (entry.mousePressed(this.mc, mouseX, mouseY, mouseButton))
            {
                // Don't call super if the button press got handled
                return true;
            }
        }

        return false;
    }

    protected <T extends ButtonBase> ButtonEntry<T> addButton(T button, IButtonActionListener<T> listener)
    {
        ButtonEntry<T> entry = new ButtonEntry<>(button, listener);
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

    protected ConfigOptionListenerGeneric<ButtonBase> getConfigListener()
    {
        return this.listener;
    }

    @Override
    public void addOptions(ConfigPanelHost host)
    {
        this.clearOptions();

        int x = 10;
        int y = 10;
        int configWidth = this.elementWidth;
        int configHeight = 20;
        int labelWidth = this.getMaxLabelWidth(this.getConfigs()) + 10;

        for (IConfigValue config : this.getConfigs())
        {
            this.addLabel(0, x, y + 7, labelWidth, 8, 0xFFFFFFFF, config.getName());

            String comment = config.getComment();
            ConfigType type = config.getType();

            if (comment != null)
            {
                this.addConfigComment(x, y + 2, labelWidth, 10, comment);
            }

            if (type == ConfigType.BOOLEAN)
            {
                this.addButton(new ConfigButtonBoolean(0, x + labelWidth, y, configWidth, configHeight, (IConfigBoolean) config), this.listener);
            }
            else if (type == ConfigType.OPTION_LIST)
            {
                this.addButton(new ConfigButtonOptionList(0, x + labelWidth, y, configWidth, configHeight, (IConfigOptionList) config), this.listener);
            }
            else if (type == ConfigType.STRING ||
                     type == ConfigType.HEX_STRING ||
                     type == ConfigType.INTEGER ||
                     type == ConfigType.DOUBLE)
            {
                ConfigTextField field = this.addTextField(0, x + labelWidth, y + 1, configWidth - 4, configHeight - 3);
                field.setText(config.getStringValue());
                field.getNativeTextField().setMaxStringLength(this.maxTextfieldTextLength);
                this.addTextField(config, field);
            }

            y += configHeight + 1;
        }
    }

    @Override
    public void clearOptions()
    {
        super.clearOptions();
        this.buttons.clear();
        this.textFields.clear();
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
                this.drawHoveringText(label.getLines(), label.x, label.y + 30);
                break;
            }
        }
    }

    protected void drawButtons(int mouseX, int mouseY, float partialTicks)
    {
        for (ButtonEntry<?> entry : this.buttons)
        {
            entry.draw(this.mc, mouseX, mouseY, partialTicks);
        }
    }

    protected void addTextField(IConfigValue config, ConfigTextField field)
    {
        this.textFields.put(config, field);
    }

    protected ConfigTextField getTextFieldFor(IConfigValue config)
    {
        return this.textFields.get(config);
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
    }

    /**
     * Returns true if some of the options in this panel were modified
     * @return
     */
    public boolean hasModifications()
    {
        return false;
    }

    public static class HoverInfo
    {
        protected final List<String> lines;
        protected int x;
        protected int y;
        protected int width;
        protected int height;

        public HoverInfo(int x, int y, int width, int height)
        {
            this.lines = new ArrayList<>();
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void addLines(String... lines)
        {
            for (String line : lines)
            {
                line = I18n.format(line);
                String[] split = line.split("\n");

                for (String str : split)
                {
                    this.lines.add(str);
                }
            }
        }

        public List<String> getLines()
        {
            return this.lines;
        }

        public boolean isMouseOver(int mouseX, int mouseY)
        {
            return mouseX >= this.x && mouseX <= (this.x + this.width) && mouseY >= this.y && mouseY <= (this.y + this.height);
        }
    }
}

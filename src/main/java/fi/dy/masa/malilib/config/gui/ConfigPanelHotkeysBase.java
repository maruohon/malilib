package fi.dy.masa.malilib.config.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonHotkey;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.KeybindEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class ConfigPanelHotkeysBase extends ConfigPanelSub
{
    private ConfigButtonHotkey activeButton;
    protected final IHotkey[] hotkeys;
    protected final List<ConfigOptionListenerResetKeybind> hotkeyResetListeners = new ArrayList<>();
    protected int hotkeyButtonWidth = 200;

    public ConfigPanelHotkeysBase(String title, IHotkey[] hotkeys, ConfigPanelBase parent)
    {
        super(title, parent);

        this.hotkeys = hotkeys;
    }

    @Override
    protected void onSettingsChanged()
    {
        KeybindEventHandler.getInstance().updateUsedKeys();
    }

    protected IHotkey[] getHotkeys()
    {
        return this.hotkeys;
    }

    protected String getHotkeyComment(IHotkey hotkey)
    {
        return hotkey.getComment();
    }

    @Override
    public void clearOptions()
    {
        super.clearOptions();

        this.hotkeyResetListeners.clear();
    }

    @Override
    public void addOptions(ConfigPanelHost host)
    {
        this.clearOptions();

        int xStart = 10;
        int x = xStart;
        int y = 10;
        int i = 0;
        IHotkey[] hotkeys = this.getHotkeys();
        int labelWidth = this.getMaxLabelWidth(hotkeys);

        for (IHotkey hotkey : hotkeys)
        {
            this.addLabel(i, x, y + 7, labelWidth, 8, 0xFFFFFFFF, hotkey.getName());
            this.addConfigComment(x, y + 7, labelWidth, 8, this.getHotkeyComment(hotkey));

            x += labelWidth + 10;
            ConfigButtonHotkey buttonHotkey = new ConfigButtonHotkey(i + 1, x, y, this.hotkeyButtonWidth, 20, hotkey, this);

            x += this.hotkeyButtonWidth + 10;
            this.addButton(buttonHotkey, this.getConfigListener());
            this.addHotkeyResetButton(i + 2, x, y, hotkey, buttonHotkey);

            i += 3;
            y += 21;
            x = xStart;
        }
    }

    protected void addHotkeyResetButton(int id, int x, int y, IHotkey hotkey, ConfigButtonHotkey buttonHotkey)
    {
        String label = I18n.format("malilib.gui.button.reset.caps");
        int w = this.mc.fontRenderer.getStringWidth(label) + 10;
        ButtonGeneric button = new ButtonGeneric(id, x, y, w, 20, label);
        button.enabled = hotkey.getKeybind().isModified();

        ConfigOptionListenerResetKeybind listener = new ConfigOptionListenerResetKeybind(hotkey, buttonHotkey, button);
        this.hotkeyResetListeners.add(listener);
        this.addButton(button, listener);
    }

    public void setActiveButton(@Nullable ConfigButtonHotkey button)
    {
        if (this.activeButton != null)
        {
            this.activeButton.onClearSelection();
            this.updateButtons();
        }

        this.activeButton = button;

        if (this.activeButton != null)
        {
            this.activeButton.onSelected();
        }
    }

    protected void updateButtons()
    {
        for (ConfigOptionListenerResetKeybind listener : this.hotkeyResetListeners)
        {
            listener.updateButtons();
        }
    }

    @Override
    protected boolean mousePressed(int mouseX, int mouseY, int mouseButton)
    {
        boolean handled = super.mousePressed(mouseX, mouseY, mouseButton);

        // When clicking on not-a-button, clear the selection
        if (handled == false && this.activeButton != null)
        {
            this.activeButton.onClearSelection();
            this.setActiveButton(null);
            return true;
        }

        return handled;
    }

    @Override
    public void keyPressed(ConfigPanelHost host, char keyChar, int keyCode)
    {
        if (this.activeButton != null)
        {
            this.activeButton.onKeyPressed(keyCode);
        }
        else
        {
            super.keyPressed(host, keyChar, keyCode);
        }
    }

    public class ConfigOptionListenerResetKeybind implements IButtonActionListener<ButtonGeneric>
    {
        private final ConfigButtonHotkey buttonHotkey;
        private final ButtonGeneric button;
        private final IHotkey hotkey;

        public ConfigOptionListenerResetKeybind(IHotkey hotkey, ConfigButtonHotkey buttonHotkey, ButtonGeneric button)
        {
            this.buttonHotkey = buttonHotkey;
            this.button = button;
            this.hotkey = hotkey;
        }

        @Override
        public void actionPerformed(ButtonGeneric control)
        {
            this.hotkey.getKeybind().resetToDefault();
            this.buttonHotkey.playPressSound(Minecraft.getMinecraft().getSoundHandler());
            this.updateButtons();
        }

        @Override
        public void actionPerformedWithButton(ButtonGeneric control, int mouseButton)
        {
            this.actionPerformed(control);
        }

        public void updateButtons()
        {
            this.button.enabled = this.hotkey.getKeybind().isModified();
            this.buttonHotkey.updateDisplayString();
        }
    }
}

package fi.dy.masa.malilib.config.gui;

import java.util.List;
import com.google.common.collect.ImmutableList;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.hotkeys.IHotkey;

public class ConfigPanelHotkeysBase extends ConfigPanelSub
{
    protected final List<IHotkey> hotkeys;
    protected int hotkeyButtonWidth = 200;

    public ConfigPanelHotkeysBase(String modId, String title, IHotkey[] hotkeys, ConfigPanelBase parent)
    {
        super(modId, title, parent);

        this.hotkeys = ImmutableList.copyOf(hotkeys);
    }

    protected List<IHotkey> getHotkeys()
    {
        return this.hotkeys;
    }

    protected String getHotkeyComment(IHotkey hotkey)
    {
        return hotkey.getComment();
    }

    @Override
    public void addOptions(ConfigPanelHost host)
    {
        this.clearOptions();

        int xStart = 10;
        int x = xStart;
        int y = 10;
        int i = 0;
        List<IHotkey> hotkeys = this.getHotkeys();
        int labelWidth = this.getMaxLabelWidth(hotkeys);

        for (IHotkey hotkey : hotkeys)
        {
            this.addLabel(i, x, y + 7, labelWidth, 8, 0xFFFFFFFF, hotkey.getName());
            this.addConfigComment(x, y + 7, labelWidth, 8, this.getHotkeyComment(hotkey));

            x += labelWidth + 10;
            ConfigButtonKeybind buttonHotkey = new ConfigButtonKeybind(i + 1, x, y, this.hotkeyButtonWidth, 20, hotkey.getKeybind(), this);

            x += this.hotkeyButtonWidth + 10;
            this.addButton(buttonHotkey, this.getButtonPressListener());
            this.addKeybindResetButton(i + 2, x, y, hotkey.getKeybind(), buttonHotkey);

            i += 3;
            y += 21;
            x = xStart;
        }
    }
}

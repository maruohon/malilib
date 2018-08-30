package fi.dy.masa.malilib.config.gui;

import java.util.List;
import com.google.common.collect.ImmutableList;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.InputEventHandler.KeybindCategory;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.reference.Reference;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class ConfigPanelAllHotkeys extends ConfigPanelHotkeysBase
{
    public ConfigPanelAllHotkeys(MaLiLibConfigPanel parent)
    {
        super(Reference.MOD_ID, I18n.format("malilib.gui.title.all_hotkeys"), ImmutableList.of(), parent);
    }

    @Override
    protected void onSettingsChanged()
    {
        ConfigManager.getInstance().saveAllConfigs();
        InputEventHandler.getInstance().updateUsedKeys();
    }

    @Override
    public void addOptions(ConfigPanelHost host)
    {
        this.clearOptions();

        int xStart = 10;
        int x = xStart;
        int y = 10;
        int i = 0;

        int maxLabelWidth = 0;
        List<KeybindCategory> categories = InputEventHandler.getInstance().getKeybindCategories();

        for (KeybindCategory category : categories)
        {
            maxLabelWidth = Math.max(maxLabelWidth, this.getMaxLabelWidth(category.getHotkeys()));
        }

        for (KeybindCategory category : categories)
        {
            // Category header
            String header = category.getModName() + " - " + category.getCategory();
            int labelWidth = this.mc.fontRenderer.getStringWidth(header);
            this.addLabel(i++, x, y, labelWidth, 8, 0xFFFFFFFF, header);
            y += 12;
            this.addLabel(i++, x, y, labelWidth, 8, 0xFFFFFFFF, "-------------------------------------------------------------------");
            y += 12;

            // Draw a horizontal bar
            Gui.drawRect(xStart, y, xStart + 300, y + 1, 0xFFFFFFFF);
            y += 6;

            for (IHotkey hotkey : category.getHotkeys())
            {
                this.addLabel(i++, x, y + 7, maxLabelWidth, 8, 0xFFFFFFFF, hotkey.getName());
                this.addConfigComment(x, y + 7, maxLabelWidth, 8, this.getHotkeyComment(hotkey));

                x += maxLabelWidth + 10;
                ConfigButtonKeybind buttonHotkey = new ConfigButtonKeybind(i++, x, y, this.hotkeyButtonWidth, 20, hotkey.getKeybind(), this);

                x += this.hotkeyButtonWidth + 10;
                this.addButton(buttonHotkey, this.getButtonPressListener());
                this.addKeybindResetButton(i++, x, y, hotkey.getKeybind(), buttonHotkey);

                y += 21;
                x = xStart;
            }

            y += 30;
        }
    }
}

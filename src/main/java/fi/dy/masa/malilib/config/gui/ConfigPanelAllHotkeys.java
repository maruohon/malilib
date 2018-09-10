package fi.dy.masa.malilib.config.gui;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.InputEventHandler.KeybindCategory;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.reference.MaLiLibReference;
import net.minecraft.client.resources.I18n;

public class ConfigPanelAllHotkeys extends GuiModConfigs
{
    public ConfigPanelAllHotkeys()
    {
        super(MaLiLibReference.MOD_ID, I18n.format("malilib.gui.title.all_hotkeys"), createWrappers(), false);
    }

    protected static List<ConfigOptionWrapper> createWrappers()
    {
        List<KeybindCategory> categories = InputEventHandler.getInstance().getKeybindCategories();
        ImmutableList.Builder<ConfigOptionWrapper> builder = ImmutableList.builder();
        boolean first = true;

        for (KeybindCategory category : categories)
        {
            // Category header
            String header = category.getModName() + " - " + category.getCategory();

            if (first == false)
            {
                builder.add(new ConfigOptionWrapper(""));
            }

            builder.add(new ConfigOptionWrapper(header));
            builder.add(new ConfigOptionWrapper("-------------------------------------------------------------------"));
            first = false;

            for (IHotkey hotkey : category.getHotkeys())
            {
                builder.add(new ConfigOptionWrapper(hotkey));
            }
        }

        return builder.build();
    }

    @Override
    protected void onSettingsChanged()
    {
        ConfigManager.getInstance().saveAllConfigs();
        InputEventHandler.getInstance().updateUsedKeys();
    }
}

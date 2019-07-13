package fi.dy.masa.malilib;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.gui.GuiModConfigs;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.interfaces.IConfigInfoProvider;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.KeybindCategory;

public class ConfigPanelAllHotkeys extends GuiModConfigs
{
    public ConfigPanelAllHotkeys()
    {
        super(MaLiLibReference.MOD_ID, createWrappers(), false, "malilib.gui.title.all_hotkeys");

        this.setHoverInfoProvider(new HoverInfoProvider(this));
    }

    public static List<ConfigOptionWrapper> createWrappers()
    {
        List<KeybindCategory> categories = InputEventHandler.getKeybindManager().getKeybindCategories();
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
                String prefix = GuiBase.TXT_YELLOW + category.getModName() + " -> " + category.getCategory() + " -> " + hotkey.getName() + "\n";
                builder.add(new ConfigOptionWrapper(prefix, hotkey));
            }
        }

        return builder.build();
    }

    @Override
    protected void onSettingsChanged()
    {
        ((ConfigManager) ConfigManager.getInstance()).saveAllConfigs();
        InputEventHandler.getKeybindManager().updateUsedKeys();
    }

    @Override
    protected boolean useKeybindSearch()
    {
        return true;
    }

    public static class HoverInfoProvider implements IConfigInfoProvider
    {
        protected final GuiListBase<?, ?, ?> gui;

        public HoverInfoProvider(GuiListBase<?, ?, ?> gui)
        {
            this.gui = gui;
        }

        @Override
        public String getHoverInfo(ConfigOptionWrapper wrapper)
        {
            String comment = wrapper.getConfig().getComment();

            if (this.gui.isSearchOpen())
            {
                String prefix = wrapper.getLabelPrefix();

                if (prefix != null)
                {
                    return prefix + comment;
                }
            }

            return comment;
        }
    }
}

package fi.dy.masa.malilib;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.gui.config.GuiModConfigs;
import fi.dy.masa.malilib.event.dispatch.InputEventDispatcher;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.interfaces.IConfigInfoProvider;
import fi.dy.masa.malilib.input.IHotkey;
import fi.dy.masa.malilib.input.KeyBindCategory;

public class ConfigPanelAllHotkeys extends GuiModConfigs
{
    public ConfigPanelAllHotkeys()
    {
        super(MaLiLibReference.MOD_ID, createWrappers(), false, "malilib.gui.title.all_hotkeys");

        this.setHoverInfoProvider(new HoverInfoProvider(this));
    }

    public static List<ConfigOptionWrapper> createWrappers()
    {
        List<KeyBindCategory> categories = InputEventDispatcher.getKeyBindManager().getKeyBindCategories();
        ImmutableList.Builder<ConfigOptionWrapper> builder = ImmutableList.builder();
        boolean first = true;

        for (KeyBindCategory category : categories)
        {
            // Category header
            String header = category.getModName() + " - " + category.getCategoryName();

            if (first == false)
            {
                builder.add(new ConfigOptionWrapper(""));
            }

            builder.add(new ConfigOptionWrapper(header));
            builder.add(new ConfigOptionWrapper("-------------------------------------------------------------------"));
            first = false;

            for (IHotkey hotkey : category.getHotkeys())
            {
                String prefix = GuiBase.TXT_YELLOW + category.getModName() + " -> " + category.getCategoryName() + " -> " + hotkey.getName() + "\n";
                builder.add(new ConfigOptionWrapper(prefix, hotkey));
            }
        }

        return builder.build();
    }

    @Override
    protected void onSettingsChanged()
    {
        ((ConfigManager) ConfigManager.INSTANCE).saveAllConfigs();
        InputEventDispatcher.getKeyBindManager().updateUsedKeys();
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

package fi.dy.masa.malilib;

import java.util.Collections;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.ConfigManagerImpl;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.event.dispatch.InputEventDispatcher;
import fi.dy.masa.malilib.gui.BaseListScreen;
import fi.dy.masa.malilib.gui.config.ConfigInfoProvider;
import fi.dy.masa.malilib.gui.config.ModConfigScreen;

public class ConfigPanelAllHotkeys extends ModConfigScreen
{
    public ConfigPanelAllHotkeys()
    {
        super(MaLiLibReference.MOD_ID, Collections.emptyList(), "malilib.gui.title.all_hotkeys");

        this.setHoverInfoProvider(new HoverInfoProvider(this));
    }

    /*
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
                String prefix = BaseScreen.TXT_YELLOW + category.getModName() + " -> " + category.getCategoryName() + " -> " + hotkey.getName() + "\n";
                builder.add(new ConfigOptionWrapper(prefix, hotkey));
            }
        }

        return builder.build();
    }
    */

    @Override
    protected void onSettingsChanged()
    {
        ((ConfigManagerImpl) ConfigManager.INSTANCE).saveAllConfigs();
        InputEventDispatcher.getKeyBindManager().updateUsedKeys();
    }

    @Override
    public boolean useKeyBindSearch()
    {
        return true;
    }

    public static class HoverInfoProvider implements ConfigInfoProvider
    {
        protected final BaseListScreen<?> gui;

        public HoverInfoProvider(BaseListScreen<?> gui)
        {
            this.gui = gui;
        }

        @Override
        public String getHoverInfo(ConfigInfo config)
        {
            String comment = config.getComment();

            // TODO config refactor
            /*
            if (this.gui.isSearchOpen())
            {
                String prefix = wrapper.getLabelPrefix();

                if (prefix != null)
                {
                    return prefix + comment;
                }
            }
            */

            return comment;
        }
    }
}

package fi.dy.masa.malilib;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import fi.dy.masa.malilib.util.data.ModInfo;

public class MaLiLibConfigScreen
{
    public static final ModInfo MOD_INFO = MaLiLibReference.MOD_INFO;

    public static final BaseConfigTab GENERIC = new BaseConfigTab(MOD_INFO, "generic", 120, MaLiLibConfigs.Generic.OPTIONS, MaLiLibConfigScreen::create);
    public static final BaseConfigTab INFO    = new BaseConfigTab(MOD_INFO, "info",     -1, MaLiLibConfigs.Info.OPTIONS,    MaLiLibConfigScreen::create);
    public static final BaseConfigTab DEBUG   = new BaseConfigTab(MOD_INFO, "debug",   120, MaLiLibConfigs.Debug.OPTIONS,   MaLiLibConfigScreen::create);

    private static final ImmutableList<ConfigTab> CONFIG_TABS = ImmutableList.of(
            GENERIC,
            INFO,
            DEBUG
    );

    public static BaseConfigScreen create(@Nullable GuiScreen currentScreen)
    {
        return new BaseConfigScreen(MOD_INFO, null, CONFIG_TABS, GENERIC, "malilib.gui.title.configs");
    }

    public static ImmutableList<ConfigTab> getConfigTabs()
    {
        return CONFIG_TABS;
    }
}

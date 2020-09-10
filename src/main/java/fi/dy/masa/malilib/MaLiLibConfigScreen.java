package fi.dy.masa.malilib;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.gui.config.ConfigTab;

public class MaLiLibConfigScreen
{
    private static final BaseConfigTab GENERIC = new BaseConfigTab("malilib.gui.title.generic", MaLiLibReference.MOD_NAME, 120, MaLiLibConfigs.Generic.OPTIONS);
    private static final BaseConfigTab INFO    = new BaseConfigTab("malilib.gui.title.info",    MaLiLibReference.MOD_NAME,  -1, MaLiLibConfigs.Info.OPTIONS);
    private static final BaseConfigTab DEBUG   = new BaseConfigTab("malilib.gui.title.debug",   MaLiLibReference.MOD_NAME, 120, MaLiLibConfigs.Debug.OPTIONS);

    private static final ImmutableList<ConfigTab> TABS = ImmutableList.of(
            GENERIC,
            INFO,
            DEBUG
    );

    public static BaseConfigScreen create()
    {
        return new BaseConfigScreen(MaLiLibReference.MOD_ID, null, TABS, GENERIC, "malilib.gui.title.configs");
    }

    public static ImmutableList<ConfigTab> getConfigTabs()
    {
        return TABS;
    }
}

package fi.dy.masa.malilib;

import java.util.Collections;
import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.gui.config.ConfigTab;

public class MaLiLibConfigGui extends BaseConfigScreen
{
    private static final BaseConfigTab GENERIC       = new BaseConfigTab("malilib.gui.title.generic", 204, true, MaLiLibConfigs.Generic.OPTIONS);
    private static final BaseConfigTab DEBUG         = new BaseConfigTab("malilib.gui.title.debug", 80, false, MaLiLibConfigs.Debug.OPTIONS);
    private static final BaseConfigTab ALL_HOTKEYS   = new BaseConfigTab("malilib.gui.title.all_hotkeys", 204, true, Collections.emptyList());

    private static final ImmutableList<ConfigTab> TABS = ImmutableList.of(
            GENERIC,
            DEBUG,
            ALL_HOTKEYS
    );

    private static ConfigTab tab = GENERIC;

    public MaLiLibConfigGui()
    {
        super(10, 50, MaLiLibReference.MOD_ID, null, TABS, "malilib.gui.title.configs");

        this.setHoverInfoProvider(new ConfigPanelAllHotkeys.HoverInfoProvider(this));
    }

    @Override
    public ConfigTab getCurrentTab()
    {
        return tab;
    }

    @Override
    public void setCurrentTab(ConfigTab tab)
    {
        MaLiLibConfigGui.tab = tab;
    }

    @Override
    public List<? extends ConfigInfo> getConfigs()
    {
        if (this.getCurrentTab() == ALL_HOTKEYS)
        {
            //return ConfigPanelAllHotkeys.createWrappers(); // TODO config refactor
        }

        return super.getConfigs();
    }
}

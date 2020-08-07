package fi.dy.masa.malilib;

import java.io.File;
import java.util.Collections;
import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ColorConfig;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.DirectoryConfig;
import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.config.option.FileConfig;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.config.option.StringListConfig;
import fi.dy.masa.malilib.config.value.InfoType;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.gui.config.ConfigTab;

public class MaLiLibConfigScreen extends BaseConfigScreen
{
    private static final BaseConfigTab GENERIC       = new BaseConfigTab("malilib.gui.title.generic", 100, true, MaLiLibConfigs.Generic.OPTIONS);
    private static final BaseConfigTab DEBUG         = new BaseConfigTab("malilib.gui.title.debug", 100, false, MaLiLibConfigs.Debug.OPTIONS);
    private static final BaseConfigTab ALL_HOTKEYS   = new BaseConfigTab("malilib.gui.title.all_hotkeys", 200, true, Collections.emptyList());

    private static final ImmutableList<ConfigTab> TABS = ImmutableList.of(
            GENERIC,
            DEBUG,
            ALL_HOTKEYS
    );

    private static final List<ConfigInfo> DEBUG_STUFF = ImmutableList.of(
            new BooleanConfig("Boolean 1", true),
            new ColorConfig("Color 1", "#3040C050"),
            new ColorConfig("Color 2", "#3040C050"),
            new DirectoryConfig("Directory 1", new File(".")),
            new DoubleConfig("Double 1", 123456.789),
            new FileConfig("File", new File("foo_bar.txt")),
            new HotkeyConfig("Hotkey", "G,H"),
            new HotkeyedBooleanConfig("Hotkeyed", false, "U,I"),
            new IntegerConfig("Integer 1", 12345),
            new OptionListConfig<>("Option List", InfoType.MESSAGE_OVERLAY),
            new StringListConfig("String List 1", ImmutableList.of("default 1", "default 2", "default 3")),
            new StringConfig("String 1", "foo bar"));

    private static ConfigTab tab = GENERIC;

    public MaLiLibConfigScreen()
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
        MaLiLibConfigScreen.tab = tab;
    }

    @Override
    public List<? extends ConfigInfo> getConfigs()
    {
        if (this.getCurrentTab() == ALL_HOTKEYS)
        {
            //return ConfigPanelAllHotkeys.createWrappers(); // TODO config refactor
            return DEBUG_STUFF;
        }

        return super.getConfigs();
    }
}

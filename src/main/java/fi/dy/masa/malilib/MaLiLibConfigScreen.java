package fi.dy.masa.malilib;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.BaseTabbedScreen;
import fi.dy.masa.malilib.gui.action.ActionListScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import fi.dy.masa.malilib.gui.edit.CustomHotkeysEditScreen;
import fi.dy.masa.malilib.gui.edit.CustomIconListScreen;
import fi.dy.masa.malilib.gui.edit.overlay.InfoRendererWidgetListScreen;
import fi.dy.masa.malilib.gui.tab.BaseScreenTab;
import fi.dy.masa.malilib.gui.tab.ScreenTab;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseInfoRendererWidgetEntryWidget;
import fi.dy.masa.malilib.overlay.widget.ConfigStatusIndicatorContainerWidget;
import fi.dy.masa.malilib.overlay.widget.InfoRendererWidget;
import fi.dy.masa.malilib.util.data.ModInfo;

public class MaLiLibConfigScreen
{
    public static final ModInfo MOD_INFO = MaLiLibReference.MOD_INFO;

    public static final BaseConfigTab GENERIC           = new BaseConfigTab(MOD_INFO, "generic", 120, MaLiLibConfigs.Generic.OPTIONS, MaLiLibConfigScreen::create);
    public static final BaseConfigTab HOTKEYS           = new BaseConfigTab(MOD_INFO, "hotkeys", 160, MaLiLibConfigs.Hotkeys.HOTKEYS, MaLiLibConfigScreen::create);
    public static final BaseConfigTab DEBUG             = new BaseConfigTab(MOD_INFO, "debug",   120, MaLiLibConfigs.Debug.OPTIONS,   MaLiLibConfigScreen::create);
    public static final BaseScreenTab ACTIONS           = new BaseScreenTab(MOD_INFO, "actions",                      (scr) -> scr instanceof ActionListScreen, ActionListScreen::createActionListScreen);
    public static final BaseScreenTab CSI               = new BaseScreenTab(MOD_INFO, "config_status_indicator.abbr", (scr) -> scr instanceof ConfigStatusIndicatorWidgetListScreen, MaLiLibConfigScreen::createConfigStatusIndicatorListScreen).setHoverText("malilib.gui.button.hover.config_status_indicator");
    public static final BaseScreenTab ICONS             = new BaseScreenTab(MOD_INFO, "custom_icons",                 (scr) -> scr instanceof CustomIconListScreen, CustomIconListScreen::openCustomIconListScreen).setHoverText("malilib.gui.button.hover.custom_icons_configuration");
    public static final BaseScreenTab CUSTOM_HOTKEYS    = new BaseScreenTab(MOD_INFO, "custom_hotkeys",               (scr) -> scr instanceof CustomHotkeysEditScreen, MaLiLibConfigScreen::createCustomHotkeysEditScreen).setHoverText("malilib.gui.button.hover.custom_hotkeys_configuration");
    public static final BaseScreenTab INFO_RENDERERS    = new BaseScreenTab(MOD_INFO, "info_renderers",               (scr) -> scr instanceof AllInfoWidgetsListScreen, MaLiLibConfigScreen::createInfoRendererWidgetsListScreen).setHoverText("malilib.gui.button.hover.info_renderers_configuration");

    private static final ImmutableList<ConfigTab> CONFIG_TABS = ImmutableList.of(
            GENERIC,
            HOTKEYS,
            DEBUG
    );

    public static final ImmutableList<ScreenTab> ALL_TABS = ImmutableList.of(
            GENERIC,
            HOTKEYS,
            DEBUG,
            ACTIONS,
            CSI,
            ICONS,
            CUSTOM_HOTKEYS,
            INFO_RENDERERS
    );

    public static void open()
    {
        BaseScreen.openScreen(create(null));
    }

    public static BaseConfigScreen create(@Nullable GuiScreen currentScreen)
    {
        // The parent screen should not be set here, to prevent infinite recursion via
        // the call to the parent's setWorldAndResolution -> initScreen -> switch tab -> etc.
        return new BaseConfigScreen(MOD_INFO, null, ALL_TABS, GENERIC, "malilib.gui.title.configs");
    }

    public static BaseTabbedScreen createConfigStatusIndicatorListScreen(@Nullable GuiScreen currentScreen)
    {
        return new ConfigStatusIndicatorWidgetListScreen();
    }

    public static BaseTabbedScreen createInfoRendererWidgetsListScreen(@Nullable GuiScreen currentScreen)
    {
        return new AllInfoWidgetsListScreen();
    }

    public static BaseTabbedScreen createCustomHotkeysEditScreen(@Nullable GuiScreen currentScreen)
    {
        return new CustomHotkeysEditScreen();
    }

    public static ImmutableList<ConfigTab> getConfigTabs()
    {
        return CONFIG_TABS;
    }

    public static class ConfigStatusIndicatorWidgetListScreen extends InfoRendererWidgetListScreen<ConfigStatusIndicatorContainerWidget>
    {
        public ConfigStatusIndicatorWidgetListScreen()
        {
            super(InfoRendererWidgetListScreen.createSupplierFromInfoManagerForExactType(ConfigStatusIndicatorContainerWidget.class),
                  ConfigStatusIndicatorContainerWidget::new,
                  BaseInfoRendererWidgetEntryWidget::new);

            this.setTitle("malilib.gui.title.config_status_indicator_configuration");
            this.canCreateNewWidgets = true;
        }
    }

    public static class AllInfoWidgetsListScreen extends InfoRendererWidgetListScreen<InfoRendererWidget>
    {
        public AllInfoWidgetsListScreen()
        {
            super(InfoRendererWidgetListScreen.createSupplierFromInfoManagerForSubtypes(InfoRendererWidget.class),
                  null,
                  BaseInfoRendererWidgetEntryWidget::new);

            this.setTitle("malilib.gui.title.info_renderer_widgets");
        }
    }
}

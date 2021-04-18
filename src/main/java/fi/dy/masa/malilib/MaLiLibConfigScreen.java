package fi.dy.masa.malilib;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.ActionListScreen;
import fi.dy.masa.malilib.gui.BaseScreenTab;
import fi.dy.masa.malilib.gui.BaseTabbedScreen;
import fi.dy.masa.malilib.gui.CustomHotkeysEditScreen;
import fi.dy.masa.malilib.gui.ScreenTab;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigTab;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import fi.dy.masa.malilib.gui.InfoRendererWidgetListScreen;
import fi.dy.masa.malilib.gui.widget.list.entry.ConfigStatusIndicatorContainerEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.ToastRendererWidgetEntryWidget;
import fi.dy.masa.malilib.overlay.widget.ConfigStatusIndicatorContainerWidget;
import fi.dy.masa.malilib.overlay.widget.ToastRendererWidget;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class MaLiLibConfigScreen
{
    public static final ModInfo MOD_INFO = MaLiLibReference.MOD_INFO;

    public static final BaseConfigTab GENERIC           = new BaseConfigTab(MOD_INFO, "generic", 120, MaLiLibConfigs.Generic.OPTIONS, MaLiLibConfigScreen::create);
    public static final BaseConfigTab INFO              = new BaseConfigTab(MOD_INFO, "info",     -1, MaLiLibConfigs.Info.OPTIONS,    MaLiLibConfigScreen::create);
    public static final BaseConfigTab HOTKEYS           = new BaseConfigTab(MOD_INFO, "hotkeys", 160, MaLiLibConfigs.Hotkeys.HOTKEYS, MaLiLibConfigScreen::create);
    public static final BaseConfigTab DEBUG             = new BaseConfigTab(MOD_INFO, "debug",   120, MaLiLibConfigs.Debug.OPTIONS,   MaLiLibConfigScreen::create);
    public static final BaseScreenTab ACTIONS           = new BaseScreenTab(MOD_INFO, "actions",                      (scr) -> scr instanceof ActionListScreen, ActionListScreen::createActionListScreen);
    public static final BaseScreenTab CUSTOM_HOTKEYS    = new BaseScreenTab(MOD_INFO, "custom_hotkeys",               (scr) -> scr instanceof CustomHotkeysEditScreen, MaLiLibConfigScreen::createCustomHotkeysEditScreen).setHoverText("malilib.gui.button.hover.custom_hotkeys_configuration");
    public static final BaseScreenTab CSI               = new BaseScreenTab(MOD_INFO, "config_status_indicator.abbr", (scr) -> scr instanceof ConfigStatusIndicatorWidgetListScreen, MaLiLibConfigScreen::createConfigStatusIndicatorListScreen).setHoverText("malilib.gui.button.hover.config_status_indicator");
    public static final BaseScreenTab TOAST             = new BaseScreenTab(MOD_INFO, "toast_renderer.abbr",          (scr) -> scr instanceof ToastRendererWidgetListScreen, MaLiLibConfigScreen::createToastRendererListScreen).setHoverText("malilib.gui.button.hover.toast_renderer_configuration");

    private static final ImmutableList<ConfigTab> CONFIG_TABS = ImmutableList.of(
            GENERIC,
            INFO,
            HOTKEYS,
            DEBUG
    );

    public static final ImmutableList<ScreenTab> ALL_TABS = ImmutableList.of(
            GENERIC,
            INFO,
            HOTKEYS,
            DEBUG_STUFF_TAB,
            DEBUG,
            ACTIONS,
            CUSTOM_HOTKEYS,
            CSI,
            TOAST
    );

    public static BaseConfigScreen create(@Nullable GuiScreen currentScreen)
    {
        return new BaseConfigScreen(MOD_INFO, null, CONFIG_TABS, GENERIC, "malilib.gui.title.configs");

    public static BaseTabbedScreen createConfigStatusIndicatorScreen(@Nullable GuiScreen currentScreen)
        return new BaseConfigScreen(MOD_INFO, currentScreen, ALL_TABS, GENERIC, "malilib.gui.title.configs");
    }

    public static BaseTabbedScreen createConfigStatusIndicatorListScreen(@Nullable GuiScreen currentScreen)
    {
        return new ConfigStatusIndicatorWidgetListScreen();
    }

    public static BaseTabbedScreen createToastRendererListScreen(@Nullable GuiScreen currentScreen)
    {
        return new ToastRendererWidgetListScreen();
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
            super(InfoRendererWidgetListScreen.createSupplierFromInfoManager(ConfigStatusIndicatorContainerWidget.class),
                  ConfigStatusIndicatorContainerWidget::new,
                  ConfigStatusIndicatorContainerEntryWidget::new);

            this.title = StringUtils.translate("malilib.gui.title.config_status_indicator_configuration");
            this.canCreateNewWidgets = true;
        }
    }

    public static class ToastRendererWidgetListScreen extends InfoRendererWidgetListScreen<ToastRendererWidget>
    {
        public ToastRendererWidgetListScreen()
        {
            super(InfoRendererWidgetListScreen.createSupplierFromInfoManager(ToastRendererWidget.class),
                  ToastRendererWidget::new,
                  ToastRendererWidgetEntryWidget::new);

            this.title = StringUtils.translate("malilib.gui.title.toast_renderer_configuration");
        }
    }
}

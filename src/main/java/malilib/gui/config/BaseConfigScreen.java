package malilib.gui.config;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

import malilib.MaLiLibConfigs;
import malilib.config.ConfigManagerImpl;
import malilib.gui.BaseListScreen;
import malilib.gui.tab.ScreenTab;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.button.KeyBindConfigButton;
import malilib.gui.widget.list.ConfigOptionListWidget;
import malilib.listener.EventListener;
import malilib.registry.Registry;
import malilib.util.ListUtils;
import malilib.util.data.ConfigOnTab;
import malilib.util.data.ModInfo;

public class BaseConfigScreen extends BaseListScreen<ConfigOptionListWidget> implements KeybindEditScreen
{
    protected final ModInfo modInfo;
    @Nullable protected EventListener configSaveListener;
    @Nullable protected KeyBindConfigButton activeKeyBindButton;
    protected int configElementsWidth = 120;

    public BaseConfigScreen(ModInfo modInfo,
                            List<? extends ScreenTab> configTabs,
                            @Nullable ConfigTab defaultTab,
                            String titleKey, Object... args)
    {
        super(10, 46, 20, 62, modInfo.getModId(), configTabs, defaultTab);

        this.modInfo = modInfo;
        this.shouldRestoreScrollbarPosition = MaLiLibConfigs.Generic.REMEMBER_CONFIG_TAB_SCROLL_POSITIONS.getBooleanValue();

        this.addPreScreenCloseListener(this::saveConfigsOnScreenClose);
        this.createSwitchModConfigScreenDropDown(modInfo);
        this.setTitle(titleKey, args);
    }

    protected void saveConfigsOnScreenClose()
    {
        if (((ConfigManagerImpl) Registry.CONFIG_MANAGER).saveIfDirty())
        {
            this.onSettingsChanged();
        }
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (this.activeKeyBindButton != null)
        {
            this.activeKeyBindButton.onKeyTyped(keyCode, scanCode, modifiers);
            return true;
        }

        return super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (super.onMouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        // When clicking on not-a-button, clear the selection
        if (this.activeKeyBindButton != null && mouseButton == 0)
        {
            this.setActiveKeyBindButton(null);
            return true;
        }

        return false;
    }

    public void setConfigSaveListener(@Nullable EventListener configSaveListener)
    {
        this.configSaveListener = configSaveListener;
    }

    public int getDefaultConfigElementWidth()
    {
        ScreenTab tab = this.getCurrentTab();

        if (tab instanceof ConfigTab)
        {
            return ((ConfigTab) tab).getConfigWidgetsWidth();
        }

        return this.configElementsWidth;
    }

    /**
     * Sets the requested config elements width for this screen.
     * Use -1 to indicate automatic/default width decided by the widgets.
     */
    public BaseConfigScreen setConfigElementsWidth(int configElementsWidth)
    {
        this.configElementsWidth = configElementsWidth;
        return this;
    }

    public ModInfo getModInfo()
    {
        return this.modInfo;
    }

    @Override
    public void switchToTab(ScreenTab tab)
    {
        this.saveScrollBarPositionForCurrentTab();

        super.switchToTab(tab);

        this.restoreScrollBarPositionForCurrentTab();
        this.reCreateConfigWidgets();
    }

    public void reCreateConfigWidgets()
    {
        for (GenericButton tabButton : this.tabButtons)
        {
            tabButton.updateButtonState();
        }

        ConfigOptionListWidget listWidget = this.getListWidget();

        if (listWidget != null)
        {
            listWidget.refreshEntries();
        }
    }

    protected void onSettingsChanged()
    {
        Registry.HOTKEY_MANAGER.updateUsedKeys();

        if (this.configSaveListener != null)
        {
            this.configSaveListener.onEvent();
        }
    }

    public List<ConfigOnTab> getConfigs()
    {
        ScreenTab tab = this.getCurrentTab();

        if (tab instanceof ConfigTab)
        {
            return ((ConfigTab) tab).getTabbedConfigs();
        }

        return Collections.emptyList();
    }

    @Override
    protected ConfigOptionListWidget createListWidget()
    {
        ConfigOptionListWidget listWidget = new ConfigOptionListWidget(this::getDefaultConfigElementWidth,
                                                                       this.modInfo, this::getConfigs, this);
        listWidget.addConfigSearchBarWidget(this);
        return listWidget;
    }

    @Override
    protected void clearElements()
    {
        super.clearElements();
        this.setActiveKeyBindButton(null);
    }

    @Override
    public void setActiveKeyBindButton(@Nullable KeyBindConfigButton button)
    {
        if (this.activeKeyBindButton != null)
        {
            this.activeKeyBindButton.onClearSelection();
        }

        this.activeKeyBindButton = button;

        if (this.activeKeyBindButton != null)
        {
            this.activeKeyBindButton.onSelected();
        }
    }

    public static BaseConfigScreen withExtensionModTabs(ModInfo modInfo,
                                                        List<? extends ScreenTab> configTabs,
                                                        @Nullable ConfigTab defaultTab,
                                                        String titleKey, Object... args)
    {
        List<? extends ScreenTab> extraTabs = Registry.CONFIG_TAB.getExtraConfigScreenTabsFor(modInfo);
        List<ScreenTab> allTabs = ListUtils.getAppendedList(configTabs, extraTabs);
        return new BaseConfigScreen(modInfo, allTabs, defaultTab, titleKey, args);
    }
}

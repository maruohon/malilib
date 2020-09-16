package fi.dy.masa.malilib.gui.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.event.dispatch.KeyBindManager;
import fi.dy.masa.malilib.gui.BaseListScreen;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.util.DialogHandler;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.CyclableContainerWidget;
import fi.dy.masa.malilib.gui.widget.button.BaseButton;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.button.KeyBindConfigButton;
import fi.dy.masa.malilib.gui.widget.list.ConfigOptionListWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;

public class BaseConfigScreen extends BaseListScreen<ConfigOptionListWidget<? extends ConfigInfo>> implements KeybindEditingScreen
{
    protected static final Map<String, ConfigScreenState> CURRENT_STATE = new HashMap<>();

    protected final List<ConfigTab> configTabs;
    protected final List<BaseButton> tabButtons = new ArrayList<>();
    protected final String modId;
    @Nullable protected EventListener configSaveListener;
    @Nullable protected CyclableContainerWidget tabButtonContainerWidget;
    @Nullable protected ConfigTab defaultTab;
    @Nullable protected KeyBindConfigButton activeKeyBindButton;
    @Nullable protected DialogHandler dialogHandler;
    protected int configElementsWidth = 120;

    public BaseConfigScreen(String modId, @Nullable GuiScreen parent,
                            List<ConfigTab> configTabs, @Nullable ConfigTab defaultTab, String titleKey, Object... args)
    {
        super(10, 46, 20, 62);

        this.modId = modId;
        this.defaultTab = defaultTab;
        this.title = StringUtils.translate(titleKey, args);
        this.configTabs = configTabs;

        this.setParent(parent);
    }

    public ConfigScreenState getTabState()
    {
        return getTabState(this.modId);
    }

    public static ConfigScreenState getTabState(String modId)
    {
        return CURRENT_STATE.computeIfAbsent(modId, (id) -> new ConfigScreenState(null));
    }

    public void setConfigSaveListener(@Nullable EventListener configSaveListener)
    {
        this.configSaveListener = configSaveListener;
    }

    @Nullable
    public ConfigTab getCurrentTab()
    {
        ConfigScreenState state = getTabState(this.modId);

        if (state.currentTab == null)
        {
            state.currentTab = this.defaultTab;
        }

        return state.currentTab;
    }

    @Nullable
    public static ConfigTab getCurrentTab(String modId)
    {
        return getTabState(modId).currentTab;
    }

    public void setCurrentTab(ConfigTab tab)
    {
        setCurrentTab(this.modId, tab);
    }

    public static void setCurrentTab(String modId, ConfigTab tab)
    {
        getTabState(modId).currentTab = tab;
    }

    public int getDefaultConfigElementWidth()
    {
        return this.getCurrentTab() != null ? this.getCurrentTab().getConfigWidth() : this.configElementsWidth;
    }

    /**
     * Sets the requested config elements width for this screen.
     * Use -1 to indicate automatic/default width decided by the widgets.
     * @param configElementsWidth
     * @return
     */
    public BaseConfigScreen setConfigElementsWidth(int configElementsWidth)
    {
        this.configElementsWidth = configElementsWidth;
        return this;
    }

    @Override
    public List<? extends ConfigInfo> getConfigs()
    {
        return this.getCurrentTab() != null ? this.getCurrentTab().getConfigsForDisplay() : Collections.emptyList();
    }

    @Override
    @Nullable
    public DialogHandler getDialogHandler()
    {
        return this.dialogHandler;
    }

    public void setDialogHandler(@Nullable DialogHandler handler)
    {
        this.dialogHandler = handler;
    }

    @Override
    public String getModId()
    {
        return this.modId;
    }

    @Override
    protected ConfigOptionListWidget<? extends ConfigInfo> createListWidget(int listX, int listY, int listWidth, int listHeight)
    {
        ConfigOptionListWidget<? extends ConfigInfo> widget =
                new ConfigOptionListWidget<>(listX, listY, listWidth, listHeight, this.modId, this::getConfigs,
                                             this::getDefaultConfigElementWidth,
                                             new ConfigWidgetContext(this::getListWidget, this, this::getDialogHandler));
        widget.addConfigSearchBarWidget(this);

        return widget;
    }

    public void reCreateConfigWidgets()
    {
        for (BaseButton tabButton : this.tabButtons)
        {
            tabButton.updateButtonState();
        }

        this.reCreateListWidget();
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.clearOptions();
        this.createTabButtonWidget();

        Keyboard.enableRepeatEvents(true);
    }

    protected void createTabButtonWidget()
    {
        // This stores the value when resizing the window
        if (this.tabButtonContainerWidget != null)
        {
            this.getTabState().currentTabStartIndex = this.tabButtonContainerWidget.getStartIndex();
        }

        this.tabButtonContainerWidget = new CyclableContainerWidget(10, 22, this.width - 20, 20, this.createTabButtons());
        this.tabButtonContainerWidget.setStartIndex(this.getTabState().currentTabStartIndex);
        this.addWidget(this.tabButtonContainerWidget);
    }

    protected List<BaseButton> createTabButtons()
    {
        this.tabButtons.clear();

        for (ConfigTab tab : this.configTabs)
        {
            this.tabButtons.add(this.createTabButton(tab));
        }

        return this.tabButtons;
    }

    protected GenericButton createTabButton(final ConfigTab tab)
    {
        GenericButton button = new GenericButton(0, 0, -1, 20, tab.getDisplayName());
        button.setEnabled(this.getCurrentTab() != tab);
        button.setEnabledStatusSupplier(() -> this.getCurrentTab() != tab);
        button.setActionListener(tab.getButtonActionListener(this));
        return button;
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        if (this.tabButtonContainerWidget != null)
        {
            this.getTabState().currentTabStartIndex = this.tabButtonContainerWidget.getStartIndex();
        }

        if (ConfigManager.INSTANCE.saveConfigsIfChanged(this.modId))
        {
            this.onSettingsChanged();
        }

        Keyboard.enableRepeatEvents(false);
    }

    protected void onSettingsChanged()
    {
        KeyBindManager.INSTANCE.updateUsedKeys();

        if (this.configSaveListener != null)
        {
            this.configSaveListener.onEvent();
        }
    }

    @Override
    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        if (this.activeKeyBindButton != null)
        {
            this.activeKeyBindButton.onKeyPressed(keyCode);
            return true;
        }
        else
        {
            if (this.getListWidget().onKeyTyped(typedChar, keyCode))
            {
                return true;
            }

            if (keyCode == Keyboard.KEY_ESCAPE && this.getParent() != GuiUtils.getCurrentScreen())
            {
                BaseScreen.openGui(this.getParent());
                return true;
            }

            return super.onKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (super.onMouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        // When clicking on not-a-button, clear the selection
        if (this.activeKeyBindButton != null)
        {
            this.setActiveKeyBindButton(null);
            return true;
        }

        return false;
    }

    @Override
    public void clearOptions()
    {
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
}

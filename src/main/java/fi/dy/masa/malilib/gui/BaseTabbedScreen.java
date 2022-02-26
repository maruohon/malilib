package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.tab.ScreenTab;
import fi.dy.masa.malilib.gui.tab.TabbedScreenState;
import fi.dy.masa.malilib.gui.widget.CyclableContainerWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public abstract class BaseTabbedScreen extends BaseScreen
{
    protected static final Map<String, TabbedScreenState> CURRENT_STATE = new HashMap<>();
    protected static final Object2IntOpenHashMap<ScreenTab> SCROLLBAR_POSITIONS = new Object2IntOpenHashMap<>();

    protected final List<? extends ScreenTab> screenTabs;
    protected final List<GenericButton> tabButtons = new ArrayList<>();
    protected final String screenId;
    @Nullable protected final ScreenTab defaultTab;
    @Nullable protected CyclableContainerWidget tabButtonContainerWidget;
    protected boolean shouldCreateTabButtons;
    protected boolean shouldRestoreScrollbarPosition;
    protected int tabButtonContainerWidgetX = 10;
    protected int tabButtonContainerWidgetY = 22;

    public BaseTabbedScreen(String screenId,
                            List<? extends ScreenTab> screenTabs,
                            @Nullable ScreenTab defaultTab)
    {
        this.screenId = screenId;
        this.defaultTab = defaultTab;
        this.screenTabs = screenTabs;
    }

    @Override
    protected void initScreen()
    {
        ScreenTab tab = this.getCurrentTab();

        if (tab != null && tab.canUseCurrentScreen(this) == false)
        {
            tab.createAndOpenScreen(this);
            return;
        }

        this.restoreScrollBarPositionForCurrentTab();

        super.initScreen();

        if (this.shouldCreateTabButtons())
        {
            this.createTabButtonContainerWidget();
        }
    }

    @Override
    protected void onScreenClosed()
    {
        if (this.tabButtonContainerWidget != null)
        {
            this.getTabState().visibleTabsStartIndex = this.tabButtonContainerWidget.getStartIndex();
        }

        this.saveScrollBarPositionForCurrentTab();

        super.onScreenClosed();
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + this.tabButtonContainerWidgetX;
        int y = this.y + this.tabButtonContainerWidgetY;

        if (this.tabButtonContainerWidget != null)
        {
            this.tabButtonContainerWidget.setPosition(x, y);
        }
    }

    public TabbedScreenState getTabState()
    {
        return getTabState(this.screenId);
    }

    @Nullable
    public ScreenTab getCurrentTab()
    {
        TabbedScreenState state = this.getTabState();

        if (state.currentTab == null)
        {
            state.currentTab = this.defaultTab;
        }

        return state.currentTab;
    }

    public void setCurrentTab(ScreenTab tab)
    {
        setCurrentTab(this.screenId, tab);
    }

    public void switchToTab(ScreenTab tab)
    {
        this.setCurrentTab(tab);
    }

    public void saveScrollBarPositionForCurrentTab()
    {
        if (this.shouldRestoreScrollBarPosition())
        {
            ScreenTab tab = this.getCurrentTab();
            int position = this.getCurrentScrollbarPosition();

            if (tab != null && position >= 0)
            {
                setScrollBarPosition(tab, position);
            }
        }
    }

    public void restoreScrollBarPositionForCurrentTab()
    {
        if (this.shouldRestoreScrollBarPosition())
        {
            ScreenTab tab = this.getCurrentTab();

            if (tab != null)
            {
                this.setCurrentScrollbarPosition(getScrollBarPosition(tab));
            }
        }
    }

    protected int getCurrentScrollbarPosition()
    {
        return 0;
    }

    protected void setCurrentScrollbarPosition(int position)
    {
    }

    protected boolean shouldRestoreScrollBarPosition()
    {
        return this.shouldRestoreScrollbarPosition;
    }

    protected boolean shouldCreateTabButtons()
    {
        return this.shouldCreateTabButtons;
    }

    protected List<? extends ScreenTab> getScreenTabs()
    {
        return this.screenTabs;
    }

    protected int getTabButtonContainerWidgetWidth()
    {
        return this.screenWidth - 20;
    }

    protected void createTabButtonContainerWidget()
    {
        // This stores the value when resizing the window
        if (this.tabButtonContainerWidget != null)
        {
            this.getTabState().visibleTabsStartIndex = this.tabButtonContainerWidget.getStartIndex();
        }

        int x = this.x + this.tabButtonContainerWidgetX;
        int y = this.y + this.tabButtonContainerWidgetY;
        int width = this.getTabButtonContainerWidgetWidth();

        this.tabButtonContainerWidget = new CyclableContainerWidget(width, 20, this.createTabButtons());
        this.tabButtonContainerWidget.setPosition(x, y);
        this.tabButtonContainerWidget.setStartIndex(this.getTabState().visibleTabsStartIndex);
        this.addWidget(this.tabButtonContainerWidget);
    }

    protected List<GenericButton> createTabButtons()
    {
        this.tabButtons.clear();

        for (ScreenTab tab : this.getScreenTabs())
        {
            this.tabButtons.add(this.createTabButton(tab));
        }

        return this.tabButtons;
    }

    protected GenericButton createTabButton(final ScreenTab tab)
    {
        // The CyclableContainerWidget will re-position all the fitting buttons
        GenericButton button = GenericButton.create(tab.getDisplayName());
        button.setActionListener(tab.getButtonActionListener(this));
        button.setEnabledStatusSupplier(() -> this.getCurrentTab() != tab);

        String hoverText = tab.getHoverText();

        if (hoverText != null)
        {
            button.translateAndAddHoverString(hoverText);
        }

        return button;
    }

    public static TabbedScreenState getTabState(String screenId)
    {
        return CURRENT_STATE.computeIfAbsent(screenId, (id) -> new TabbedScreenState(null));
    }

    @Nullable
    public static ScreenTab getCurrentTab(String screenId)
    {
        return getTabState(screenId).currentTab;
    }

    public static void setCurrentTab(String screenId, ScreenTab tab)
    {
        getTabState(screenId).currentTab = tab;
    }

    public static int getScrollBarPosition(ScreenTab tab)
    {
        return SCROLLBAR_POSITIONS.getInt(tab);
    }

    public static void setScrollBarPosition(ScreenTab tab, int position)
    {
        SCROLLBAR_POSITIONS.put(tab, position);
    }
}

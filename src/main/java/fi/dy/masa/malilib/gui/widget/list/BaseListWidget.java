package fi.dy.masa.malilib.gui.widget.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.position.EdgeInt;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.InteractableWidget;
import fi.dy.masa.malilib.gui.widget.ContainerWidget;
import fi.dy.masa.malilib.gui.widget.ScrollBarWidget;
import fi.dy.masa.malilib.gui.widget.SearchBarWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseListEntryWidget;
import fi.dy.masa.malilib.gui.widget.util.DefaultWidgetPositioner;
import fi.dy.masa.malilib.gui.widget.util.WidgetPositioner;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.RenderUtils;

public abstract class BaseListWidget extends ContainerWidget
{
    protected final ArrayList<BaseListEntryWidget> entryWidgets = new ArrayList<>();
    protected final EdgeInt listPosition = new EdgeInt(2, 2, 2, 2);
    protected final ScrollBarWidget scrollBar;

    protected WidgetPositioner searchBarPositioner = new DefaultWidgetPositioner();
    protected WidgetPositioner headerWidgetPositioner = new DefaultWidgetPositioner();

    @Nullable protected BaseScreen parentScreen;
    @Nullable protected SearchBarWidget searchBarWidget;
    @Nullable protected ContainerWidget headerWidget;
    @Nullable protected EventListener entryRefreshListener;

    protected int entryWidgetStartX;
    protected int entryWidgetStartY;
    protected int entryWidgetFixedHeight = 22;
    protected int entryWidgetWidth;
    protected int listHeight;
    protected int requestedScrollBarPosition = -1;
    protected int visibleListEntries;

    protected boolean allowKeyboardNavigation;
    protected boolean areEntriesFixedHeight = true;

    public BaseListWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);

        // The position gets updated in setSize()
        this.scrollBar = new ScrollBarWidget(0, 0, 8, height);
        this.scrollBar.setArrowTextures(DefaultIcons.SMALL_ARROW_UP, DefaultIcons.SMALL_ARROW_DOWN);
        this.scrollBar.setValueChangeListener(this::reCreateListEntryWidgets);
    }

    public abstract int getTotalListWidgetCount();

    @Nullable
    protected abstract BaseListEntryWidget createListEntryWidget(int x, int y, int listIndex);

    public List<BaseListEntryWidget> getListEntryWidgets()
    {
        return this.entryWidgets;
    }

    public void setRequestedScrollBarPosition(int position)
    {
        this.requestedScrollBarPosition = position;
    }

    /**
     * Creates a header widget, that will be displayed before the first entry of the list.
     */
    protected void createHeaderWidget()
    {
    }

    @Override
    protected void onPositionChanged(int oldX, int oldY)
    {
        this.updatePositioningAndElements();
    }

    @Override
    protected void onSizeChanged()
    {
        this.updatePositioningAndElements();
    }

    @Override
    protected void onPositionOrSizeChanged(int oldX, int oldY)
    {
        this.updatePositioningAndElements();
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.scrollBar);
        this.addWidgetIfNotNull(this.getSearchBarWidget());
        this.addWidgetIfNotNull(this.headerWidget);
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int bw = this.borderEnabled ? this.borderWidth : 0;
        int x = this.getX() + bw;
        int startY = this.getY() + bw;
        int listWidth = this.getListMaxWidthForTotalWidth(this.getWidth());

        SearchBarWidget searchBarWidget = this.getSearchBarWidget();

        if (searchBarWidget != null)
        {
            this.updateSearchBarPosition(x, startY, listWidth - bw * 2);
            startY = searchBarWidget.getY() + searchBarWidget.getHeight() + 2;
        }

        if (this.headerWidget != null)
        {
            this.updateHeaderWidgetPosition(x, startY, this.entryWidgetWidth);
            startY = this.headerWidget.getY() + this.headerWidget.getHeight() + 2;
        }

        int rightPadding = this.listPosition.getRight();
        int bottomPadding = this.listPosition.getBottom();

        this.entryWidgetStartX = x + this.listPosition.getLeft();
        this.entryWidgetStartY = startY + this.listPosition.getTop();
        this.entryWidgetWidth = x + listWidth - this.entryWidgetStartX - rightPadding - this.scrollBar.getWidth() - 1;
        this.listHeight = this.getHeight() - (this.entryWidgetStartY - this.getY()) - bottomPadding;

        int scrollBarX = x + listWidth - this.scrollBar.getWidth() - bw - 1;
        int scrollBarY = this.entryWidgetStartY;

        this.scrollBar.setPosition(scrollBarX, scrollBarY);
        this.scrollBar.setHeight(this.listHeight);
    }

    protected void updateSearchBarPosition(int defaultX, int defaultY, int defaultWidth)
    {
        SearchBarWidget searchBarWidget = this.getSearchBarWidget();

        if (searchBarWidget != null)
        {
            this.searchBarPositioner.positionWidget(searchBarWidget, defaultX, defaultY, defaultWidth);
        }
    }

    protected void updateHeaderWidgetPosition(int defaultX, int defaultY, int defaultWidth)
    {
        ContainerWidget widget = this.headerWidget;

        if (widget != null)
        {
            this.headerWidgetPositioner.positionWidget(widget, defaultX, defaultY, defaultWidth);
        }
    }

    public void initWidget()
    {
        this.clearWidgets();

        this.createHeaderWidget();
        this.reAddSubWidgets();
        this.updateSubWidgetsToGeometryChanges();
        this.refreshEntries();

        Keyboard.enableRepeatEvents(true);
    }

    protected void updatePositioningAndElements()
    {
        this.updateSubWidgetsToGeometryChanges();
        this.reCreateListEntryWidgets();
    }

    protected void updateScrollBarHeight()
    {
        final int count = this.getTotalListWidgetCount();
        int totalHeight = 0;

        if (this.visibleListEntries < count)
        {
            // There is no other way than to assume a fixed height here, since all the widgets don't exist at once
            totalHeight += count * this.entryWidgetFixedHeight;
        }
        else
        {
            for (int i = 0; i < count; ++i)
            {
                totalHeight += this.getHeightForExistingListEntryWidget(i);
            }
        }

        this.scrollBar.setTotalHeight(Math.max(totalHeight, this.scrollBar.getHeight()));
    }

    protected int getHeightForListEntryWidgetCreation(int listIndex)
    {
        return this.entryWidgetFixedHeight;
    }

    protected int getHeightForExistingListEntryWidget(int listIndex)
    {
        if (this.areEntriesFixedHeight || listIndex >= this.entryWidgets.size())
        {
            return this.entryWidgetFixedHeight;
        }
        else
        {
            return this.entryWidgets.get(listIndex).getHeight();
        }
    }

    protected int getListMaxWidthForTotalWidth(int width)
    {
        return width;
    }

    public boolean isSearchOpen()
    {
        return this.getSearchBarWidget() != null && this.getSearchBarWidget().isSearchOpen();
    }

    protected boolean hasFilter()
    {
        return this.getSearchBarWidget() != null && this.getSearchBarWidget().hasFilter();
    }

    protected String getFilterText()
    {
        return this.getSearchBarWidget() != null ? this.getSearchBarWidget().getFilter().toLowerCase(Locale.ROOT) : "";
    }

    @Nullable
    public BaseScreen getParentScreen()
    {
        return this.parentScreen;
    }

    public EdgeInt getListPosition()
    {
        return this.listPosition;
    }

    public ScrollBarWidget getScrollbar()
    {
        return this.scrollBar;
    }

    @Nullable
    public SearchBarWidget getSearchBarWidget()
    {
        return this.searchBarWidget;
    }

    public void setListEntryWidgetFixedHeight(int height)
    {
        this.entryWidgetFixedHeight = height;
    }

    public void setEntryRefreshListener(@Nullable EventListener entryRefreshListener)
    {
        this.entryRefreshListener = entryRefreshListener;
    }

    public void setSearchBarPositioner(WidgetPositioner positioner)
    {
        this.searchBarPositioner = positioner;
    }

    public void setHeaderWidgetPositioner(WidgetPositioner positioner)
    {
        this.headerWidgetPositioner = positioner;
    }

    public BaseListWidget setParentScreen(GuiScreen parent)
    {
        if (parent instanceof BaseScreen)
        {
            this.parentScreen = (BaseScreen) parent;
        }

        return this;
    }

    public void addSearchBar(SearchBarWidget widget)
    {
        this.searchBarWidget = widget;
    }

    public void addDefaultSearchBar()
    {
        this.searchBarWidget = new SearchBarWidget(this.getX() + 2, this.getY() + 3,
                                                   this.getWidth() - 14, 14, 0, DefaultIcons.SEARCH,
                                                   HorizontalAlignment.LEFT, this::onSearchBarChange);
    }

    public void onGuiClosed()
    {
        for (BaseListEntryWidget widget : this.entryWidgets)
        {
            widget.onAboutToDestroy();
        }
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.onMouseClickedSearchBar(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        if (this.headerWidget != null && this.headerWidget.tryMouseClick(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        BaseListEntryWidget hoveredWidget = this.getHoveredListWidget(mouseX, mouseY);

        if (hoveredWidget != null &&
            hoveredWidget.isMouseOver(mouseX, mouseY) &&
            this.onEntryWidgetClicked(hoveredWidget, mouseX, mouseY, mouseButton))
        {
            return true;
        }

        for (BaseListEntryWidget widget : this.entryWidgets)
        {
            if (widget.tryMouseClick(mouseX, mouseY, mouseButton))
            {
                return true;
            }
        }

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (this.headerWidget != null)
        {
            this.headerWidget.onMouseReleased(mouseX, mouseY, mouseButton);
        }

        for (BaseListEntryWidget widget : this.entryWidgets)
        {
            widget.onMouseReleased(mouseX, mouseY, mouseButton);
        }

        super.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    protected boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (this.getSearchBarWidget() != null &&
            this.getSearchBarWidget().tryMouseScroll(mouseX, mouseY, mouseWheelDelta))
        {
            return true;
        }

        if (this.headerWidget != null &&
            this.headerWidget.tryMouseScroll(mouseX, mouseY, mouseWheelDelta))
        {
            return true;
        }

        if (super.onMouseScrolled(mouseX, mouseY, mouseWheelDelta))
        {
            return true;
        }

        for (BaseListEntryWidget widget : this.entryWidgets)
        {
            if (widget.tryMouseScroll(mouseX, mouseY, mouseWheelDelta))
            {
                return true;
            }
        }

        if (GuiUtils.isMouseInRegion(mouseX, mouseY, this.getX(), this.entryWidgetStartY, this.getWidth(), this.listHeight))
        {
            int amount = MathHelper.clamp(3, 1, this.visibleListEntries);
            this.offsetSelectionOrScrollbar(mouseWheelDelta < 0 ? amount : -amount, false);
            return true;
        }

        return false;
    }

    @Override
    public boolean onMouseMoved(int mouseX, int mouseY)
    {
        if (this.getSearchBarWidget() != null &&
            this.getSearchBarWidget().onMouseMoved(mouseX, mouseY))
        {
            return true;
        }

        if (this.headerWidget != null &&
            this.headerWidget.onMouseMoved(mouseX, mouseY))
        {
            return true;
        }

        for (BaseListEntryWidget widget : this.entryWidgets)
        {
            if (widget.onMouseMoved(mouseX, mouseY))
            {
                return true;
            }
        }

        return super.onMouseMoved(mouseX, mouseY);
    }

    protected boolean onEntryWidgetClicked(BaseListEntryWidget widget, int mouseX, int mouseY, int mouseButton)
    {
        return widget.tryMouseClick(mouseX, mouseY, mouseButton);
    }

    protected boolean onMouseClickedSearchBar(int mouseX, int mouseY, int mouseButton)
    {
        SearchBarWidget widget = this.getSearchBarWidget();

        if (widget != null)
        {
            boolean searchOpenPre = widget.isSearchOpen();
            String filterPre = widget.getFilter();

            if (widget.tryMouseClick(mouseX, mouseY, mouseButton))
            {
                // Toggled the search bar on or off, or cleared the filter with a right click
                if (widget.isSearchOpen() != searchOpenPre || filterPre.equals(widget.getFilter()) == false)
                {
                    this.refreshFilteredEntries();
                    this.resetScrollBarPosition();
                }

                return true;
            }
        }

        return false;
    }

    public void onSearchBarChange(String text)
    {
        this.refreshFilteredEntries();
        this.resetScrollBarPosition();
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (this.headerWidget != null && this.headerWidget.onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        for (BaseListEntryWidget widget : this.entryWidgets)
        {
            if (widget.onKeyTyped(keyCode, scanCode, modifiers))
            {
                return true;
            }
        }

        if (super.onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        if (this.allowKeyboardNavigation)
        {
                 if (keyCode == Keyboard.KEY_UP)    this.offsetSelectionOrScrollbar(-1, true);
            else if (keyCode == Keyboard.KEY_DOWN)  this.offsetSelectionOrScrollbar( 1, true);
            else if (keyCode == Keyboard.KEY_PRIOR) this.offsetSelectionOrScrollbar(-this.visibleListEntries / 2, true);
            else if (keyCode == Keyboard.KEY_NEXT)  this.offsetSelectionOrScrollbar(this.visibleListEntries / 2, true);
            else if (keyCode == Keyboard.KEY_HOME)  this.offsetSelectionOrScrollbar(-this.getTotalListWidgetCount(), true);
            else if (keyCode == Keyboard.KEY_END)   this.offsetSelectionOrScrollbar(this.getTotalListWidgetCount(), true);
            else return false;

            return true;
        }

        return false;
    }

    @Override
    public boolean onCharTyped(char charIn, int modifiers)
    {
        if (this.headerWidget != null && this.headerWidget.onCharTyped(charIn, modifiers))
        {
            return true;
        }

        for (BaseListEntryWidget widget : this.entryWidgets)
        {
            if (widget.onCharTyped(charIn, modifiers))
            {
                return true;
            }
        }

        return super.onCharTyped(charIn, modifiers);
    }

    protected void offsetSelectionOrScrollbar(int amount, boolean changeSelection)
    {
        if (changeSelection == false)
        {
            int old = this.scrollBar.getValue();
            this.scrollBar.offsetValue(amount);

            if (old != this.scrollBar.getValue())
            {
                this.reCreateListEntryWidgets();
            }
        }
    }

    public void resetScrollBarPosition()
    {
        this.scrollBar.setValue(0);
    }

    @Nullable
    protected BaseListEntryWidget getHoveredListWidget(int mouseX, int mouseY)
    {
        final int relativeY = mouseY - this.entryWidgetStartY;

        if (relativeY >= 0 && mouseY <= this.entryWidgetStartY + this.listHeight &&
            mouseX >= this.entryWidgetStartX &&
            mouseX < this.entryWidgetStartX + this.entryWidgetWidth)
        {

            if (this.areEntriesFixedHeight)
            {
                int relIndex = relativeY / this.entryWidgetFixedHeight;
                return relIndex < this.entryWidgets.size() ? this.entryWidgets.get(relIndex) : null;
            }
            else
            {
                for (BaseListEntryWidget widget : this.entryWidgets)
                {
                    if (widget.isMouseOver(mouseX, mouseY))
                    {
                        return widget;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public InteractableWidget getTopHoveredWidget(int mouseX, int mouseY, InteractableWidget highestFoundWidget)
    {
        highestFoundWidget = super.getTopHoveredWidget(mouseX, mouseY, highestFoundWidget);
        highestFoundWidget = InteractableWidget.getTopHoveredWidgetFromList(this.entryWidgets, mouseX, mouseY, highestFoundWidget);
        return highestFoundWidget;
    }

    @Override
    public List<BaseTextFieldWidget> getAllTextFields()
    {
        List<BaseTextFieldWidget> textFields = new ArrayList<>(super.getAllTextFields());

        if (this.entryWidgets.isEmpty() == false)
        {
            for (InteractableWidget widget : this.entryWidgets)
            {
                textFields.addAll(widget.getAllTextFields());
            }
        }

        return textFields;
    }

    public void refreshEntries()
    {
        this.refreshFilteredEntries();
    }

    public void refreshFilteredEntries()
    {
        this.reCreateListEntryWidgets();
    }

    public void focusWidget(int listIndex)
    {
        for (BaseListEntryWidget widget : this.entryWidgets)
        {
            if (widget.getListIndex() == listIndex)
            {
                widget.focusWidget();
                break;
            }
        }
    }

    protected void onEntriesRefreshed()
    {
        if (this.entryRefreshListener != null)
        {
            this.entryRefreshListener.onEvent();
        }
    }

    public void reCreateListEntryWidgets()
    {
        for (BaseListEntryWidget widget : this.entryWidgets)
        {
            widget.onAboutToDestroy();
        }

        int usableHeight = this.listHeight;
        int usedHeight = 0;
        int x = this.entryWidgetStartX;
        int y = this.entryWidgetStartY;

        this.entryWidgets.clear();
        this.visibleListEntries = 0;

        int listIndex = this.getListStartIndex();
        this.onPreListEntryWidgetsCreation(listIndex);

        final int totalEntryCount = this.getTotalListWidgetCount();

        for ( ; listIndex < totalEntryCount; ++listIndex)
        {
            BaseListEntryWidget widget = this.createListEntryWidget(x, y, listIndex);

            if (widget == null)
            {
                break;
            }

            int widgetHeight = widget.getHeight();

            //System.out.printf("i: %d, usable: %d, used: %d, lh: %d, sy: %d\n", listIndex, usableHeight, usedHeight, this.listHeight, this.entryWidgetsStartY);
            if (usedHeight + widgetHeight > usableHeight)
            {
                break;
            }

            this.onSubWidgetAdded(widget);
            this.entryWidgets.add(widget);
            ++this.visibleListEntries;

            usedHeight += widgetHeight;
            y += widgetHeight;
        }

        this.onListEntryWidgetsCreated();
    }

    protected int getListStartIndex()
    {
        // This "request" workaround is needed because the ConfigScreenTabButtonListener
        // can't set the scroll bar value before re-creating the widgets, as the
        // maximum allowed value for the scroll bar isn't set yet to the correct value,
        // which only happens once the amount of visible widgets is known.
        if (this.requestedScrollBarPosition >= 0)
        {
            return this.requestedScrollBarPosition;
        }

        return this.scrollBar.getValue();
    }

    protected void onPreListEntryWidgetsCreation(int firstListIndex)
    {
    }

    /**
     * Called after the list entry widgets have been (re-)created
     */
    protected void onListEntryWidgetsCreated()
    {
        this.scrollBar.setMaxValue(this.getTotalListWidgetCount() - this.visibleListEntries);
        this.updateScrollBarHeight();

        if (this.requestedScrollBarPosition >= 0)
        {
            this.getScrollbar().setValue(this.requestedScrollBarPosition);
            this.requestedScrollBarPosition = -1;
        }
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        int diffX = x - this.getX();
        int diffY = y - this.getY();
        float diffZ = z - this.getZLevel();

        RenderUtils.color(1f, 1f, 1f, 1f);

        super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hoveredWidgetId);

        // Draw the currently visible widgets
        for (int i = 0; i < this.entryWidgets.size(); i++)
        {
            this.renderWidget(i, diffX, diffY, diffZ, mouseX, mouseY, isActiveGui, hoveredWidgetId);
        }

        GlStateManager.disableLighting();
    }

    protected void renderWidget(int widgetIndex, int diffX, int diffY, float diffZ, int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        BaseListEntryWidget widget =  this.entryWidgets.get(widgetIndex);
        int wx = widget.getX() + diffX;
        int wy = widget.getY() + diffY;
        float wz = widget.getZLevel() + diffZ;
        widget.renderAt(wx, wy, wz, mouseX, mouseY, isActiveGui, hoveredWidgetId, false);
    }

    @Override
    public void renderDebug(int mouseX, int mouseY, boolean hovered, boolean renderAll, boolean infoAlways)
    {
        super.renderDebug(mouseX, mouseY, hovered, renderAll, infoAlways);
        BaseScreen.renderWidgetDebug(this.entryWidgets, mouseX, mouseY, renderAll, infoAlways);
    }
}

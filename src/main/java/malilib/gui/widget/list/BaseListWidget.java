package malilib.gui.widget.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;

import net.minecraft.client.gui.screen.Screen;

import malilib.gui.BaseScreen;
import malilib.gui.icon.DefaultIcons;
import malilib.gui.util.GuiUtils;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.BaseTextFieldWidget;
import malilib.gui.widget.ContainerWidget;
import malilib.gui.widget.InteractableWidget;
import malilib.gui.widget.ScrollBarWidget;
import malilib.gui.widget.list.entry.BaseListEntryWidget;
import malilib.gui.widget.list.header.DataListHeaderWidget;
import malilib.gui.widget.list.search.SearchBarWidget;
import malilib.gui.widget.util.DefaultWidgetPositioner;
import malilib.gui.widget.util.WidgetPositioner;
import malilib.input.Keys;
import malilib.listener.EventListener;
import malilib.util.MathUtils;
import malilib.util.data.EdgeInt;

public abstract class BaseListWidget extends ContainerWidget implements ListEntryWidgetFactory
{
    protected final EdgeInt listPosition = new EdgeInt(2, 2, 2, 2);
    protected final ScrollBarWidget scrollBar;
    protected ListEntryWidgetFactory listEntryWidgetFactory;

    protected WidgetPositioner searchBarPositioner = new DefaultWidgetPositioner();
    protected WidgetPositioner headerWidgetPositioner = new DefaultWidgetPositioner();

    @Nullable protected BaseScreen parentScreen;
    @Nullable protected SearchBarWidget searchBarWidget;
    @Nullable protected DataListHeaderWidget<?> headerWidget;
    @Nullable protected EventListener entryRefreshListener;

    protected int entryWidgetStartX;
    protected int entryWidgetStartY;
    protected int entryWidgetFixedHeight = 22;
    protected int entryWidgetWidth;
    protected int keyboardNavigationIndex = -1;
    protected int listHeight;
    protected int requestedScrollBarPosition = -1;
    protected int visibleListEntries;

    protected boolean allowKeyboardNavigation;
    protected boolean areEntriesFixedHeight = true;

    public BaseListWidget(int width, int height)
    {
        super(width, height);

        this.canReceiveMouseClicks = true;
        this.canReceiveMouseScrolls = true;
        this.canReceiveMouseMoves = true;
        // Raise the z-level, so it's likely to be on top of all other widgets in the same screen
        this.zLevelIncrement = 10;
        this.listEntryWidgetFactory = this;

        // The position gets updated in setSize()
        this.scrollBar = new ScrollBarWidget(8, height);
        this.scrollBar.setValueChangeListener(this::reCreateListEntryWidgets);
    }

    @Nullable
    protected abstract BaseListEntryWidget createListEntryWidget(int x, int y, int listIndex);

    public abstract ArrayList<BaseListEntryWidget> getEntryWidgetList();

    protected abstract void addNewEntryWidget(BaseListEntryWidget widget);

    public void setAllowKeyboardNavigation(boolean allowKeyboardNavigation)
    {
        this.allowKeyboardNavigation = allowKeyboardNavigation;
    }

    public void setAreEntriesFixedHeight(boolean areEntriesFixedHeight)
    {
        this.areEntriesFixedHeight = areEntriesFixedHeight;
    }

    public void setRequestedScrollBarPosition(int position)
    {
        this.requestedScrollBarPosition = position;
    }

    public void setListEntryWidgetFactory(ListEntryWidgetFactory listEntryWidgetFactory)
    {
        this.listEntryWidgetFactory = listEntryWidgetFactory;
    }

    /**
     * Creates a header widget, that will be displayed before the first entry of the list.
     */
    protected void createAndSetHeaderWidget()
    {
    }

    /*
    @Override
    protected void onPositionChanged(int oldX, int oldY)
    {
        this.updateSubWidgetPositions();
        this.reCreateListEntryWidgets();
    }
    */

    @Override
    protected void onSizeChanged()
    {
        this.updateSubWidgetPositions();
        this.reCreateListEntryWidgets();
    }

    @Override
    protected void onPositionOrSizeChanged(int oldX, int oldY)
    {
        super.onPositionOrSizeChanged(oldX, oldY);

        this.reCreateListEntryWidgets();
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.scrollBar);
        this.addWidget(this.getSearchBarWidget());
        this.addWidget(this.headerWidget);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        final int bw = this.getBorderRenderer().getNormalSettings().getActiveBorderWidth();
        final int x = this.getX() + bw;
        final int listWidth = this.getListMaxWidthForTotalWidth(this.getWidth());
        final int rightPadding = this.listPosition.getRight();
        final int bottomPadding = this.listPosition.getBottom();
        int startY = this.getY() + bw;

        SearchBarWidget searchBarWidget = this.getSearchBarWidget();

        if (searchBarWidget != null)
        {
            this.updateSearchBarPosition(x, startY, listWidth - bw * 2);
            startY = searchBarWidget.getBottom() + searchBarWidget.getMargin().getBottom();
        }

        this.entryWidgetStartX = x + this.listPosition.getLeft();
        this.entryWidgetWidth = x + listWidth - this.entryWidgetStartX - rightPadding - this.scrollBar.getWidth() - 1;

        if (this.headerWidget != null)
        {
            this.updateHeaderWidgetPosition(x, startY, this.entryWidgetWidth);
            startY = this.headerWidget.getBottom() + this.headerWidget.getMargin().getBottom();
        }

        this.entryWidgetStartY = startY + this.listPosition.getTop();
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

    public void initListWidget()
    {
        this.createAndSetHeaderWidget();
        this.reAddSubWidgets();
        this.updateSubWidgetPositions();
    }

    protected void updateScrollBarHeight()
    {
        final int count = this.getFactoryTotalListWidgetCount();
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

    public int getEntryWidgetWidth()
    {
        return this.entryWidgetWidth;
    }

    public int getHeightForListEntryWidgetCreation(int listIndex)
    {
        return this.entryWidgetFixedHeight;
    }

    protected int getHeightForExistingListEntryWidget(int listIndex)
    {
        if (this.areEntriesFixedHeight || listIndex >= this.getEntryWidgetList().size())
        {
            return this.entryWidgetFixedHeight;
        }
        else
        {
            return this.getEntryWidgetList().get(listIndex).getHeight();
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

    public boolean hasFilter()
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

    public BaseListWidget setParentScreen(Screen parent)
    {
        if (parent instanceof BaseScreen)
        {
            this.parentScreen = (BaseScreen) parent;
        }

        return this;
    }

    public void setSearchBar(@Nullable SearchBarWidget widget)
    {
        this.searchBarWidget = widget;
    }

    public void addDefaultSearchBar()
    {
        this.searchBarWidget = new SearchBarWidget(this.getWidth() - 14, 14, this::onSearchBarTextChanged,
                                                   this::refreshFilteredEntries, DefaultIcons.SEARCH);
    }

    public void onScreenClosed()
    {
        for (BaseListEntryWidget widget : this.getEntryWidgetList())
        {
            widget.onAboutToDestroy();
        }
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
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

        for (InteractableWidget widget : this.getEntryWidgetList())
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

        for (InteractableWidget widget : this.getEntryWidgetList())
        {
            widget.onMouseReleased(mouseX, mouseY, mouseButton);
        }

        super.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    protected boolean onMouseScrolled(int mouseX, int mouseY, double verticalWheelDelta, double horizontalWheelDelta)
    {
        if (this.getSearchBarWidget() != null &&
            this.getSearchBarWidget().tryMouseScroll(mouseX, mouseY, verticalWheelDelta, horizontalWheelDelta))
        {
            return true;
        }

        if (this.headerWidget != null &&
            this.headerWidget.tryMouseScroll(mouseX, mouseY, verticalWheelDelta, horizontalWheelDelta))
        {
            return true;
        }

        if (super.onMouseScrolled(mouseX, mouseY, verticalWheelDelta, horizontalWheelDelta))
        {
            return true;
        }

        for (InteractableWidget widget : this.getEntryWidgetList())
        {
            if (widget.tryMouseScroll(mouseX, mouseY, verticalWheelDelta, horizontalWheelDelta))
            {
                return true;
            }
        }

        if (GuiUtils.isMouseInRegion(mouseX, mouseY, this.getX(), this.entryWidgetStartY, this.getWidth(), this.listHeight))
        {
            int amount = MathUtils.clamp(3, 1, this.visibleListEntries);
            this.offsetScrollBarPosition(verticalWheelDelta < 0.0 ? amount : -amount);
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

        for (InteractableWidget widget : this.getEntryWidgetList())
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

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (this.headerWidget != null && this.headerWidget.onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        for (InteractableWidget widget : this.getEntryWidgetList())
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
                 if (keyCode == Keys.KEY_UP)        this.keyboardNavigateByOne(false);
            else if (keyCode == Keys.KEY_DOWN)      this.keyboardNavigateByOne(true);
            else if (keyCode == Keys.KEY_PAGE_UP)   this.keyboardNavigateByOnePage(false);
            else if (keyCode == Keys.KEY_PAGE_DOWN) this.keyboardNavigateByOnePage(true);
            else if (keyCode == Keys.KEY_HOME)      this.keyboardNavigateToEnd(false);
            else if (keyCode == Keys.KEY_END)       this.keyboardNavigateToEnd(true);
            else if (keyCode == Keys.KEY_SPACE)     this.toggleKeyboardNavigationPositionSelection();
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

        for (InteractableWidget widget : this.getEntryWidgetList())
        {
            if (widget.onCharTyped(charIn, modifiers))
            {
                return true;
            }
        }

        return super.onCharTyped(charIn, modifiers);
    }

    public void setKeyboardNavigationIndex(int listIndex)
    {
        this.keyboardNavigationIndex = listIndex;
    }

    protected int getKeyboardNavigationIndex()
    {
        return this.keyboardNavigationIndex;
    }

    protected void offsetScrollBarPosition(int amount)
    {
        this.scrollBar.offsetValue(amount);
    }

    protected void keyboardNavigateByOne(boolean down)
    {
        if (this.visibleListEntries <= 0)
        {
            return;
        }

        int visibleMinusOne = this.visibleListEntries - 1;
        int offset = down ? 1 : -1;
        int oldKeyboardIndex = this.getKeyboardNavigationIndex();
        int scrollPosition = this.scrollBar.getValue();

        // 1: No keyboard navigation index set currently, or
        // 2: the keyboard index is currently off-screen:
        // move the index to the first row
        if (oldKeyboardIndex < 0 ||
            oldKeyboardIndex < scrollPosition ||
            oldKeyboardIndex > scrollPosition + visibleMinusOne)
        {
            int newKeyboardIndex = this.scrollBar.getValue();
            this.setKeyboardNavigationIndex(newKeyboardIndex);
        }
        else
        {
            int totalCount = this.getFactoryTotalListWidgetCount();
            int newKeyboardIndex = MathUtils.clamp(oldKeyboardIndex + offset, 0, totalCount - 1);
            this.setKeyboardNavigationIndex(newKeyboardIndex);

            // keyboard index went off-screen on the top
            if (newKeyboardIndex < scrollPosition)
            {
                // scroll up so the keyboard index is the second to last visible row
                int newScrollIndex = newKeyboardIndex - visibleMinusOne + 1;
                this.offsetScrollBarPosition(newScrollIndex - scrollPosition);
            }
            // keyboard index went off-screen on the bottom
            else if (newKeyboardIndex > scrollPosition + visibleMinusOne)
            {
                // scroll down so the keyboard index is the second visible row
                int newScrollIndex = newKeyboardIndex - 1;
                this.offsetScrollBarPosition(newScrollIndex - scrollPosition);
            }
        }
    }

    protected void keyboardNavigateByOnePage(boolean down)
    {
        int visibleMinusOne = this.visibleListEntries - 1;
        int scrollAmount = down ? visibleMinusOne : -visibleMinusOne;
        int oldKeyboardIndex = this.getKeyboardNavigationIndex();
        int scrollPosition = this.scrollBar.getValue();
        int newKeyboardIndex;


        // 1: No keyboard navigation index set currently, or
        // 2: the keyboard index is currently off-screen:
        // move the index to the first row or the last row, depending on the navigation direction
        if (oldKeyboardIndex >= 0 &&
            oldKeyboardIndex >= scrollPosition &&
            oldKeyboardIndex <= scrollPosition + visibleMinusOne)
        {
            this.offsetScrollBarPosition(scrollAmount);
        }

        newKeyboardIndex = this.scrollBar.getValue() + (down ? visibleMinusOne : 0);

        this.setKeyboardNavigationIndex(newKeyboardIndex);
    }

    protected void keyboardNavigateToEnd(boolean down)
    {
        int totalCount = this.getFactoryTotalListWidgetCount();
        int newIndex = down ? totalCount - 1 : 0;

        this.scrollBar.setValue(newIndex); // gets clamped
        this.setKeyboardNavigationIndex(newIndex);
    }

    public void toggleKeyboardNavigationPositionSelection()
    {
    }

    public void resetScrollBarPositionWithoutNotify()
    {
        this.scrollBar.setValueNoNotify(0);
    }

    protected boolean isMouseOverListArea(int mouseX, int mouseY)
    {
        final int relativeY = mouseY - this.entryWidgetStartY;

        return relativeY >= 0 && mouseY <= this.entryWidgetStartY + this.listHeight &&
               mouseX >= this.entryWidgetStartX &&
               mouseX < this.entryWidgetStartX + this.entryWidgetWidth;
    }

    @Nullable
    protected BaseListEntryWidget getHoveredListWidget(int mouseX, int mouseY)
    {
        final int relativeY = mouseY - this.entryWidgetStartY;

        if (relativeY >= 0 && mouseY <= this.entryWidgetStartY + this.listHeight &&
            mouseX >= this.entryWidgetStartX &&
            mouseX < this.entryWidgetStartX + this.entryWidgetWidth)
        {
            /*
            if (this.areEntriesFixedHeight && this.entryWidgetFixedHeight > 0)
            {
                int relIndex = relativeY / this.entryWidgetFixedHeight;

                if (relIndex >= 0 && relIndex < this.getEntryWidgetList().size())
                {
                    BaseListEntryWidget widget = this.getEntryWidgetList().get(relIndex);

                    if (widget.isMouseOver(mouseX, mouseY))
                    {
                        return widget;
                    }
                }
            }
            */

            for (BaseListEntryWidget widget : this.getEntryWidgetList())
            {
                if (widget.isMouseOver(mouseX, mouseY))
                {
                    return widget;
                }
            }
        }

        return null;
    }

    @Override
    public void collectMatchingWidgets(Predicate<InteractableWidget> predicate, ToIntFunction<InteractableWidget> priorityFunction, List<InteractableWidget> outputList)
    {
        super.collectMatchingWidgets(predicate, priorityFunction, outputList);
        this.getEntryWidgetList().forEach(w -> w.collectMatchingWidgets(predicate, priorityFunction, outputList));
    }

    @Override
    public List<BaseTextFieldWidget> getAllTextFields()
    {
        List<BaseTextFieldWidget> textFields = new ArrayList<>(super.getAllTextFields());

        if (this.getEntryWidgetList().isEmpty() == false)
        {
            for (InteractableWidget widget : this.getEntryWidgetList())
            {
                textFields.addAll(widget.getAllTextFields());
            }
        }

        return textFields;
    }

    public void onSearchBarTextChanged()
    {
        this.resetScrollBarPositionWithoutNotify();
        this.refreshFilteredEntries();
    }

    public void refreshEntries()
    {
        this.fetchCurrentEntries();
        this.refreshFilteredEntries();
    }

    public void refreshFilteredEntries()
    {
        this.reAddFilteredEntries();
        this.onEntriesRefreshed();
        this.notifyListWidgetFactory();
        this.reCreateListEntryWidgets();
    }

    protected void fetchCurrentEntries()
    {
    }

    protected void reAddFilteredEntries()
    {
    }

    protected void notifyListWidgetFactory()
    {
        if (this.listEntryWidgetFactory != null)
        {
            this.listEntryWidgetFactory.onListRefreshed();
        }
    }

    public void focusWidget(int listIndex)
    {
        for (BaseListEntryWidget widget : this.getEntryWidgetList())
        {
            if (widget.getDataListIndex() == listIndex)
            {
                widget.focusWidget();
                break;
            }
        }
    }

    protected void onEntriesRefreshed()
    {
        this.updateWidgetInitializer();

        if (this.entryRefreshListener != null)
        {
            this.entryRefreshListener.onEvent();
        }
    }

    protected void onPreListEntryWidgetsCreation(int firstListIndex)
    {
    }

    protected void updateWidgetInitializer()
    {
    }

    protected void applyWidgetInitializer()
    {
    }

    protected void clampScrollBarPosition()
    {
        int expectedVisibleEntries = this.entryWidgetFixedHeight > 0 ? this.listHeight / this.entryWidgetFixedHeight : 10;
        int max = this.getFactoryTotalListWidgetCount() - expectedVisibleEntries;
        this.scrollBar.setMaxValueNoNotify(max);

        // This "request" workaround is needed because the ConfigScreenTabButtonListener
        // can't set the scroll bar value before re-creating the widgets, as the
        // maximum allowed value for the scroll bar isn't set yet to the correct value,
        // which only happens once the amount of visible widgets is known.
        if (this.requestedScrollBarPosition >= 0)
        {
            this.scrollBar.setValueNoNotify(this.requestedScrollBarPosition);
        }
    }

    protected int getListStartIndex()
    {
        return this.scrollBar.getValue();
    }

    public void reCreateListEntryWidgets()
    {
        for (BaseListEntryWidget widget : this.getEntryWidgetList())
        {
            widget.onAboutToDestroy();
        }

        this.clampScrollBarPosition();

        int startIndex = this.getListStartIndex();

        this.getEntryWidgetList().clear();
        this.onPreListEntryWidgetsCreation(startIndex);

        this.listEntryWidgetFactory.createEntryWidgets(this.entryWidgetStartX, this.entryWidgetStartY,
                                                       this.listHeight, startIndex, this::addNewEntryWidget);

        this.visibleListEntries = this.getEntryWidgetList().size();

        this.onListEntryWidgetsCreated();
    }

    protected int getFactoryTotalListWidgetCount()
    {
        return this.listEntryWidgetFactory.getTotalListWidgetCount();
    }

    @Override
    public void createEntryWidgets(int startX, int startY, int usableSpace,
                                   int startIndex, Consumer<BaseListEntryWidget> widgetConsumer)
    {
        final int totalEntryCount = this.getTotalListWidgetCount();
        int x = startX;
        int y = startY;
        int usableHeight = usableSpace;
        int usedHeight = 0;

        for (int listIndex = startIndex ; listIndex < totalEntryCount; ++listIndex)
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

            widgetConsumer.accept(widget);

            usedHeight += widgetHeight;
            y += widgetHeight;
        }
    }

    /**
     * Called after the list entry widgets have been (re-)created
     */
    protected void onListEntryWidgetsCreated()
    {
        if (this.getKeyboardNavigationIndex() >= this.getFactoryTotalListWidgetCount())
        {
            this.setKeyboardNavigationIndex(-1);
        }

        this.updateScrollBarHeight();
        this.applyWidgetInitializer();

        // This is a bit of a "meh" fix for the early call that comes
        // from BaseScreen#initScreen() -> updateWidgetPositions() before the
        // data list has been populated. This prevents the requestedScrollBarPosition
        // from being essentially ignored and cleared too early.
        if (this.getFactoryTotalListWidgetCount() > 0)
        {
            this.requestedScrollBarPosition = -1;
        }
    }

    @Override
    protected void renderSubWidgets(int x, int y, float z, ScreenContext ctx)
    {
        super.renderSubWidgets(x, y, z, ctx);

        int xOffset = x - this.getX();
        int yOffset = y - this.getY();
        float zOffset = z - this.getZ();

        // Draw the currently visible widgets
        for (InteractableWidget widget : this.getEntryWidgetList())
        {
            widget.renderAtOffset(xOffset, yOffset, zOffset, ctx);
        }
    }

    @Override
    public void renderDebug(boolean hovered, ScreenContext ctx)
    {
        super.renderDebug(hovered, ctx);

        BaseScreen.renderWidgetDebug(this.getEntryWidgetList(), ctx);
    }
}

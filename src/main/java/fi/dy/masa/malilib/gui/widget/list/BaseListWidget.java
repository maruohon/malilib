package fi.dy.masa.malilib.gui.widget.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.util.EdgeInt;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.ContainerWidget;
import fi.dy.masa.malilib.gui.widget.InteractableWidget;
import fi.dy.masa.malilib.gui.widget.ScrollBarWidget;
import fi.dy.masa.malilib.gui.widget.SearchBarWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseListEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListHeaderWidget;
import fi.dy.masa.malilib.gui.widget.util.DefaultWidgetPositioner;
import fi.dy.masa.malilib.gui.widget.util.WidgetPositioner;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.RenderUtils;

public abstract class BaseListWidget extends ContainerWidget
{
    protected final List<BaseListEntryWidget> entryWidgets = new ArrayList<>();
    protected final EdgeInt listPosition = new EdgeInt(2, 2, 2, 2);
    protected final ScrollBarWidget scrollBar;

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

    public BaseListWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);

        // The position gets updated in setSize()
        this.scrollBar = new ScrollBarWidget(8, height);
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

    public void setAllowKeyboardNavigation(boolean allowKeyboardNavigation)
    {
        this.allowKeyboardNavigation = allowKeyboardNavigation;
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

        int bw = this.renderNormalBorder ? this.normalBorderWidth : 0;
        int x = this.getX() + bw;
        int startY = this.getY() + bw;
        int listWidth = this.getListMaxWidthForTotalWidth(this.getWidth());

        SearchBarWidget searchBarWidget = this.getSearchBarWidget();

        if (searchBarWidget != null)
        {
            this.updateSearchBarPosition(x, startY, listWidth - bw * 2);
            startY = searchBarWidget.getBottom() + searchBarWidget.getMargin().getBottom();
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
                                                   HorizontalAlignment.LEFT,
                                                   this::onSearchBarChange,
                                                   this::refreshFilteredEntries);
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
            this.offsetScrollbarPosition(mouseWheelDelta < 0 ? amount : -amount);
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
                 if (keyCode == Keyboard.KEY_UP)    this.keyboardNavigateByOne(false);
            else if (keyCode == Keyboard.KEY_DOWN)  this.keyboardNavigateByOne(true);
            else if (keyCode == Keyboard.KEY_PRIOR) this.keyboardNavigateByOnePage(false);
            else if (keyCode == Keyboard.KEY_NEXT)  this.keyboardNavigateByOnePage(true);
            else if (keyCode == Keyboard.KEY_HOME)  this.keyboardNavigateToEnd(false);
            else if (keyCode == Keyboard.KEY_END)   this.keyboardNavigateToEnd(true);
            else if (keyCode == Keyboard.KEY_SPACE) this.toggleKeyboardNavigationPositionSelection();
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

    public void setKeyboardNavigationIndex(int listIndex)
    {
        this.keyboardNavigationIndex = listIndex;
    }

    protected int getKeyboardNavigationIndex()
    {
        return this.keyboardNavigationIndex;
    }

    protected void offsetScrollbarPosition(int amount)
    {
        int old = this.scrollBar.getValue();
        this.scrollBar.offsetValue(amount);

        if (old != this.scrollBar.getValue())
        {
            this.reCreateListEntryWidgets();
        }
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
            int totalCount = this.getTotalListWidgetCount();
            int newKeyboardIndex = MathHelper.clamp(oldKeyboardIndex + offset, 0, totalCount - 1);
            this.setKeyboardNavigationIndex(newKeyboardIndex);

            // keyboard index went off-screen on the top
            if (newKeyboardIndex < scrollPosition)
            {
                // scroll up so the keyboard index is the second to last visible row
                int newScrollIndex = newKeyboardIndex - visibleMinusOne + 1;
                this.offsetScrollbarPosition(newScrollIndex - scrollPosition);
            }
            // keyboard index went off-screen on the bottom
            else if (newKeyboardIndex > scrollPosition + visibleMinusOne)
            {
                // scroll down so the keyboard index is the second visible row
                int newScrollIndex = newKeyboardIndex - 1;
                this.offsetScrollbarPosition(newScrollIndex - scrollPosition);
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
            this.offsetScrollbarPosition(scrollAmount);
        }

        newKeyboardIndex = this.scrollBar.getValue() + (down ? visibleMinusOne : 0);

        this.setKeyboardNavigationIndex(newKeyboardIndex);
    }

    protected void keyboardNavigateToEnd(boolean down)
    {
        int totalCount = this.getTotalListWidgetCount();
        int newIndex = down ? totalCount - 1 : 0;

        this.scrollBar.setValue(newIndex); // gets clamped
        this.setKeyboardNavigationIndex(newIndex);
    }

    public void toggleKeyboardNavigationPositionSelection()
    {
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
        this.fetchCurrentEntries();
        this.refreshFilteredEntries();
    }

    public void refreshFilteredEntries()
    {
        this.reAddFilteredEntries();
        this.onEntriesRefreshed();
        this.reCreateListEntryWidgets();
    }

    protected void fetchCurrentEntries()
    {
    }

    protected void reAddFilteredEntries()
    {
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

        int max = this.getTotalListWidgetCount() - this.visibleListEntries;

        if (this.getScrollbar().getValue() > max)
        {
            this.getScrollbar().setValue(max - 1);
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
        this.getScrollbar().setMaxValue(this.getTotalListWidgetCount() - this.visibleListEntries);
        this.updateScrollBarHeight();

        if (this.getKeyboardNavigationIndex() >= this.getTotalListWidgetCount())
        {
            this.setKeyboardNavigationIndex(-1);
        }

        if (this.requestedScrollBarPosition >= 0)
        {
            this.getScrollbar().setValue(this.requestedScrollBarPosition);
            this.requestedScrollBarPosition = -1;
        }
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        int diffX = x - this.getX();
        int diffY = y - this.getY();
        float diffZ = z - this.getZLevel();

        RenderUtils.color(1f, 1f, 1f, 1f);

        super.renderAt(x, y, z, ctx);

        // Draw the currently visible widgets
        for (int i = 0; i < this.entryWidgets.size(); i++)
        {
            this.renderWidget(i, diffX, diffY, diffZ, ctx);
        }
    }

    protected void renderWidget(int widgetIndex, int diffX, int diffY, float diffZ, ScreenContext ctx)
    {
        BaseListEntryWidget widget =  this.entryWidgets.get(widgetIndex);
        int wx = widget.getX() + diffX;
        int wy = widget.getY() + diffY;
        float wz = widget.getZLevel() + diffZ;

        widget.renderAt(wx, wy, wz, ctx);
    }

    @Override
    public void renderDebug(boolean hovered, ScreenContext ctx)
    {
        super.renderDebug(hovered, ctx);

        BaseScreen.renderWidgetDebug(this.entryWidgets, ctx);
    }
}

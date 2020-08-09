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
import fi.dy.masa.malilib.gui.icon.BaseIcon;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.position.Padding;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.gui.widget.ContainerWidget;
import fi.dy.masa.malilib.gui.widget.ScrollBarWidget;
import fi.dy.masa.malilib.gui.widget.SearchBarWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseListEntryWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.RenderUtils;

public abstract class BaseListWidget extends ContainerWidget
{
    @Nullable protected BaseScreen parentScreen;
    @Nullable protected SearchBarWidget searchBarWidget;
    @Nullable protected ContainerWidget headerWidget;
    @Nullable protected EventListener entryRefreshListener;
    protected final ArrayList<BaseListEntryWidget> listWidgets = new ArrayList<>();
    protected final Padding listPosition = new Padding(2, 2, 2, 2);
    protected final ScrollBarWidget scrollBar;

    protected int entryWidgetStartX;
    protected int entryWidgetStartY;
    protected int entryWidgetFixedHeight = 22;
    protected int entryWidgetWidth;
    protected int lastScrollbarPosition;
    protected int listHeight;
    protected int visibleListEntries;

    protected boolean allowKeyboardNavigation;
    protected boolean areEntriesFixedHeight = true;

    public BaseListWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);

        // The position gets updated in setSize()
        this.scrollBar = new ScrollBarWidget(0, 0, 8, height);
        this.scrollBar.setArrowTextures(BaseIcon.SMALL_ARROW_UP, BaseIcon.SMALL_ARROW_DOWN);
    }

    public abstract int getTotalListWidgetCount();

    @Nullable
    protected abstract BaseListEntryWidget createListEntryWidget(int x, int y, int listIndex);

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

        int rightPadding = this.listPosition.getRightPadding();
        int bottomPadding = this.listPosition.getBottomPadding();

        this.entryWidgetStartX = x + this.listPosition.getLeftPadding();
        this.entryWidgetStartY = startY + this.listPosition.getTopPadding();
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
            searchBarWidget.setPosition(defaultX, defaultY);
            searchBarWidget.setWidth(defaultWidth);
        }
    }

    protected void updateHeaderWidgetPosition(int defaultX, int defaultY, int defaultWidth)
    {
        ContainerWidget widget = this.headerWidget;

        if (widget != null)
        {
            widget.setPosition(defaultX, defaultY);
            widget.setWidth(defaultWidth);
        }
    }

    public void initWidget()
    {
        this.clearWidgets();

        this.createHeaderWidget();
        this.reAddSubWidgets();
        this.updatePositioningAndElements();

        Keyboard.enableRepeatEvents(true);
    }

    protected void updatePositioningAndElements()
    {
        this.updateSubWidgetsToGeometryChanges();
        this.refreshEntries();
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
                totalHeight += this.getHeightForListEntryWidget(i);
            }
        }

        this.scrollBar.setTotalHeight(Math.max(totalHeight, this.scrollBar.getHeight()));
    }

    protected int getHeightForListEntryWidget(int listIndex)
    {
        if (this.areEntriesFixedHeight || listIndex >= this.listWidgets.size())
        {
            return this.entryWidgetFixedHeight;
        }
        else
        {
            return this.listWidgets.get(listIndex).getHeight();
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

    public Padding getListPosition()
    {
        return listPosition;
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

    public BaseListWidget setParentScreen(GuiScreen parent)
    {
        if (parent instanceof BaseScreen)
        {
            this.parentScreen = (BaseScreen) parent;
        }

        return this;
    }

    public void addDefaultSearchBar()
    {
        this.searchBarWidget = new SearchBarWidget(this.getX() + 2, this.getY() + 3,
                                                   this.getWidth() - 14, 14, 0, BaseIcon.SEARCH,
                                                   HorizontalAlignment.LEFT);
    }

    public void onGuiClosed()
    {
        for (BaseListEntryWidget widget : this.listWidgets)
        {
            widget.onAboutToDestroy();
        }
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.onMouseClickedSearchBar(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        if (this.headerWidget != null && this.headerWidget.onMouseClicked(mouseX, mouseY, mouseButton))
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

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (this.headerWidget != null)
        {
            this.headerWidget.onMouseReleased(mouseX, mouseY, mouseButton);
        }

        for (BaseListEntryWidget listWidget : this.listWidgets)
        {
            listWidget.onMouseReleased(mouseX, mouseY, mouseButton);
        }

        super.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (this.getSearchBarWidget() != null &&
            this.getSearchBarWidget().onMouseScrolled(mouseX, mouseY, mouseWheelDelta))
        {
            return true;
        }

        if (this.headerWidget != null &&
            this.headerWidget.onMouseScrolled(mouseX, mouseY, mouseWheelDelta))
        {
            return true;
        }

        if (super.onMouseScrolled(mouseX, mouseY, mouseWheelDelta))
        {
            return true;
        }

        // The scroll event could be/should be distributed to the entry widgets here
        // It's not done (for now?) to prevent accidentally messing up stuff when scrolling over lists that have buttons

        if (GuiUtils.isMouseInRegion(mouseX, mouseY, this.getX(), this.entryWidgetStartY, this.getWidth(), this.listHeight))
        {
            int amount = MathHelper.clamp(3, 1, this.visibleListEntries);
            this.offsetSelectionOrScrollbar(mouseWheelDelta < 0 ? amount : -amount, false);
            return true;
        }

        return false;
    }

    protected boolean onEntryWidgetClicked(BaseListEntryWidget widget, int mouseX, int mouseY, int mouseButton)
    {
        return widget.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    protected boolean onMouseClickedSearchBar(int mouseX, int mouseY, int mouseButton)
    {
        SearchBarWidget widget = this.getSearchBarWidget();

        if (widget != null)
        {
            boolean searchOpenPre = widget.isSearchOpen();
            String filterPre = widget.getFilter();

            if (widget.onMouseClicked(mouseX, mouseY, mouseButton))
            {
                // Toggled the search bar on or off, or cleared the filter with a right click
                if (widget.isSearchOpen() != searchOpenPre || filterPre.equals(widget.getFilter()) == false)
                {
                    this.refreshEntries();
                    this.resetScrollbarPosition();
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        if (this.headerWidget != null && this.headerWidget.onKeyTyped(typedChar, keyCode))
        {
            return true;
        }

        for (BaseListEntryWidget widget : this.listWidgets)
        {
            if (widget.onKeyTyped(typedChar, keyCode))
            {
                return true;
            }
        }

        if (this.onKeyTypedSearchBar(typedChar, keyCode))
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
            else return super.onKeyTyped(typedChar, keyCode);

            return true;
        }

        return super.onKeyTyped(typedChar, keyCode);
    }

    protected boolean onKeyTypedSearchBar(char typedChar, int keyCode)
    {
        if (this.getSearchBarWidget() != null && this.getSearchBarWidget().onKeyTyped(typedChar, keyCode))
        {
            this.refreshEntries();
            this.resetScrollbarPosition();
            return true;
        }

        return false;
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

    public void resetScrollbarPosition()
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
                return relIndex < this.listWidgets.size() ? this.listWidgets.get(relIndex) : null;
            }
            else
            {
                for (BaseListEntryWidget widget : this.listWidgets)
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
    public BaseWidget getTopHoveredWidget(int mouseX, int mouseY, BaseWidget highestFoundWidget)
    {
        highestFoundWidget = super.getTopHoveredWidget(mouseX, mouseY, highestFoundWidget);
        highestFoundWidget = BaseWidget.getTopHoveredWidgetFromList(this.listWidgets, mouseX, mouseY, highestFoundWidget);
        return highestFoundWidget;
    }

    @Override
    public List<BaseTextFieldWidget> getAllTextFields()
    {
        List<BaseTextFieldWidget> textFields = new ArrayList<>(super.getAllTextFields());

        if (this.listWidgets.isEmpty() == false)
        {
            for (BaseWidget widget : this.listWidgets)
            {
                textFields.addAll(widget.getAllTextFields());
            }
        }

        return textFields;
    }

    public void refreshEntries()
    {
        this.reCreateListEntryWidgets();
    }

    protected void onEntriesRefreshed()
    {
        if (this.entryRefreshListener != null)
        {
            this.entryRefreshListener.onEvent();
        }
    }

    protected void reCreateListEntryWidgets()
    {
        for (BaseListEntryWidget widget : this.listWidgets)
        {
            widget.onAboutToDestroy();
        }

        int usableHeight = this.listHeight;
        int usedHeight = 0;
        int x = this.entryWidgetStartX;
        int y = this.entryWidgetStartY;
        int listIndex = this.scrollBar.getValue();

        this.listWidgets.clear();
        this.visibleListEntries = 0;

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

            widget.setIsOdd((listIndex & 0x1) != 0);
            this.onSubWidgetAdded(widget);
            this.listWidgets.add(widget);
            ++this.visibleListEntries;

            usedHeight += widgetHeight;
            y += widgetHeight;
        }

        this.onListEntryWidgetsCreated();
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
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        if (this.getSearchBarWidget() != null)
        {
            this.getSearchBarWidget().render(mouseX, mouseY, isActiveGui, hoveredWidgetId);
        }

        super.render(mouseX, mouseY, isActiveGui, hoveredWidgetId);

        RenderUtils.color(1f, 1f, 1f, 1f);

        this.scrollBar.render(mouseX, mouseY);

        // The value gets updated in the drawScrollBar() method above, if dragging
        if (this.scrollBar.getValue() != this.lastScrollbarPosition)
        {
            this.lastScrollbarPosition = this.scrollBar.getValue();
            this.reCreateListEntryWidgets();
        }

        // Draw the currently visible widgets
        for (int i = 0; i < this.listWidgets.size(); i++)
        {
            this.renderWidget(i, mouseX, mouseY, isActiveGui, hoveredWidgetId);
        }

        GlStateManager.disableLighting();
        RenderUtils.color(1f, 1f, 1f, 1f);
    }

    protected void renderWidget(int widgetIndex, int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        this.listWidgets.get(widgetIndex).render(mouseX, mouseY, isActiveGui, hoveredWidgetId, false);
    }

    @Override
    public void renderDebug(int mouseX, int mouseY, boolean hovered, boolean renderAll, boolean infoAlways)
    {
        super.renderDebug(mouseX, mouseY, hovered, renderAll, infoAlways);
        BaseScreen.renderWidgetDebug(this.listWidgets, mouseX, mouseY, renderAll, infoAlways);
    }
}

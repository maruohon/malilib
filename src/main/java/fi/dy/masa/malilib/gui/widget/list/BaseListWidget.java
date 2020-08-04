package fi.dy.masa.malilib.gui.widget.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.util.BaseGuiIcon;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.Padding;
import fi.dy.masa.malilib.gui.widget.WidgetBase;
import fi.dy.masa.malilib.gui.widget.WidgetContainer;
import fi.dy.masa.malilib.gui.widget.WidgetScrollBar;
import fi.dy.masa.malilib.gui.widget.WidgetSearchBar;
import fi.dy.masa.malilib.gui.widget.WidgetTextFieldBase;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseListEntryWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.HorizontalAlignment;

public abstract class BaseListWidget extends WidgetContainer
{
    @Nullable protected BaseScreen parentScreen;
    @Nullable protected WidgetSearchBar searchBarWidget;
    @Nullable protected WidgetContainer headerWidget;
    @Nullable protected EventListener entryRefreshListener;
    protected final WidgetScrollBar scrollBar;
    protected final ArrayList<BaseListEntryWidget> listWidgets = new ArrayList<>();
    protected final Padding listPosition = new Padding();

    protected int entryWidgetsStartX;
    protected int entryWidgetsStartY;
    protected int entryWidgetFixedHeight = 22;
    protected int entryWidgetWidth;
    protected int lastScrollbarPosition;
    protected int listHeight;
    protected int listWidth;
    protected int usedHeight;
    protected int visibleListEntries;

    protected boolean allowKeyboardNavigation;
    protected boolean areEntriesFixedHeight = true;

    public BaseListWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);

        // The position gets updated in setSize()
        this.scrollBar = new WidgetScrollBar(0, 0, 8, height);
        this.scrollBar.setArrowTextures(BaseGuiIcon.SMALL_ARROW_UP, BaseGuiIcon.SMALL_ARROW_DOWN);

        this.setSize(width, height);
    }

    public abstract int getTotalListEntryCount();

    @Nullable
    protected abstract BaseListEntryWidget createListEntryWidget(int x, int y, int listIndex);

    /**
     * Creates a header widget, that will be displayed before the first entry of the list.
     * @param x
     * @param y
     * @return the created header widget, or null if there is no separate header widget
     */
    @Nullable
    protected abstract WidgetContainer createHeaderWidget(int x, int y);

    @Override
    public void setSize(int width, int height)
    {
        this.setWidth(width);
        this.setHeight(height);

        this.listWidth = width;
        this.listHeight = height;

        this.updateEntryWidgetPositioning();
        this.updateScrollbarPosition();
    }

    protected void updateEntryWidgetPositioning()
    {
        int leftPadding = this.listPosition.getLeftPadding();
        int topPadding = this.listPosition.getTopPadding();
        int offY = 0;

        WidgetSearchBar sb = this.getSearchBarWidget();
        WidgetContainer header = this.headerWidget;
        if (sb != null) { offY += sb.getHeight(); }
        if (header != null) { offY += header.getHeight(); }

        this.entryWidgetsStartX = this.getX() + leftPadding;
        this.entryWidgetsStartY = this.getY() + topPadding + offY;
        this.entryWidgetWidth = this.listWidth - leftPadding - this.listPosition.getRightPadding();
    }

    protected void updateScrollbarPosition()
    {
        int scrollBarX = this.getX() + this.listWidth - 9;
        int scrollBarY = this.entryWidgetsStartY;

        this.scrollBar.setPosition(scrollBarX, scrollBarY);
        this.scrollBar.setHeight(this.listHeight - (this.getY() - this.entryWidgetsStartY));
    }

    protected void updateScrollbarHeight()
    {
        final int count = this.getTotalListEntryCount();
        int scrollbarHeight = this.listHeight - (this.getY() - this.entryWidgetsStartY) - 6;
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

        this.scrollBar.setTotalHeight(Math.max(totalHeight, scrollbarHeight));
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

    public WidgetScrollBar getScrollbar()
    {
        return this.scrollBar;
    }

    @Nullable
    public WidgetSearchBar getSearchBarWidget()
    {
        return this.searchBarWidget;
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

    protected WidgetSearchBar addSearchBarWidget(WidgetSearchBar searchBar)
    {
        this.searchBarWidget = this.addWidget(searchBar);
        return this.searchBarWidget;
    }

    public WidgetSearchBar addDefaultSearchBar()
    {
        return this.addSearchBarWidget(new WidgetSearchBar(this.getX() + 2, this.getY() + 4,
                                                           this.getWidth() - 14, 14, 0, BaseGuiIcon.SEARCH, HorizontalAlignment.LEFT));
    }

    public void initWidget()
    {
        this.clearWidgets();

        this.usedHeight = 0;
        int x = this.getX();
        int y = this.getY();

        this.addWidget(this.scrollBar);
        this.createAndAddHeaderWidget(x, y);

        this.refreshEntries();
        Keyboard.enableRepeatEvents(true);
    }

    public void onGuiClosed()
    {
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

        if (GuiUtils.isMouseInRegion(mouseX, mouseY, this.getX(), this.getY(), this.listWidth, this.listHeight))
        {
            this.offsetSelectionOrScrollbar(mouseWheelDelta < 0 ? 3 : -3, false);
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
        WidgetSearchBar widget = this.getSearchBarWidget();

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
            else if (keyCode == Keyboard.KEY_HOME)  this.offsetSelectionOrScrollbar(-this.getTotalListEntryCount(), true);
            else if (keyCode == Keyboard.KEY_END)   this.offsetSelectionOrScrollbar(this.getTotalListEntryCount(), true);
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
            this.scrollBar.offsetValue(amount);
            this.reCreateListEntryWidgets();
        }
    }

    public void resetScrollbarPosition()
    {
        this.scrollBar.setValue(0);
    }

    @Nullable
    protected BaseListEntryWidget getHoveredListWidget(int mouseX, int mouseY)
    {
        final int relativeY = mouseY - this.entryWidgetsStartY;

        if (relativeY >= 0 && mouseY <= this.getY() + this.listHeight &&
            mouseX >= this.entryWidgetsStartX &&
            mouseX < this.entryWidgetsStartX + this.entryWidgetWidth)
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
    public WidgetBase getTopHoveredWidget(int mouseX, int mouseY, WidgetBase highestFoundWidget)
    {
        highestFoundWidget = super.getTopHoveredWidget(mouseX, mouseY, highestFoundWidget);
        highestFoundWidget = WidgetBase.getTopHoveredWidgetFromList(this.listWidgets, mouseX, mouseY, highestFoundWidget);
        return highestFoundWidget;
    }

    @Override
    public List<WidgetTextFieldBase> getAllTextFields()
    {
        List<WidgetTextFieldBase> textFields = new ArrayList<>(super.getAllTextFields());

        if (this.listWidgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.listWidgets)
            {
                textFields.addAll(widget.getAllTextFields());
            }
        }

        return textFields;
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

    protected void createAndAddHeaderWidget(int x, int y)
    {
        this.headerWidget = this.createHeaderWidget(x, y);

        if (this.headerWidget != null)
        {
            this.addWidget(this.headerWidget);
            this.usedHeight += this.headerWidget.getHeight();
        }
    }

    protected void reCreateListEntryWidgets()
    {
        this.listWidgets.clear();
        this.visibleListEntries = 0;

        int usableHeight = this.listHeight;
        int usedHeight = this.usedHeight;
        int x = this.entryWidgetsStartX;
        int y = this.entryWidgetsStartY + usedHeight;
        int listIndex = this.scrollBar.getValue();

        final int totalEntryCount = this.getTotalListEntryCount();

        for ( ; listIndex < totalEntryCount; ++listIndex)
        {
            BaseListEntryWidget widget = this.createListEntryWidget(x, y, listIndex);

            //System.out.printf("i: %d, usable: %d, used: %d, lh: %d, sy: %d\n", listIndex, usableHeight, usedHeight, this.listHeight, this.entryWidgetsStartY);
            if (widget == null || usedHeight + widget.getHeight() > usableHeight)
            {
                break;
            }

            widget.setIsOdd((listIndex & 0x1) != 0);
            this.onSubWidgetAdded(widget);
            this.listWidgets.add(widget);
            ++this.visibleListEntries;

            usedHeight += widget.getHeight();
            y += widget.getHeight();
        }

        this.onListEntryWidgetsCreated();
    }

    /**
     * Called after the list entry widgets have been (re-)created
     */
    protected void onListEntryWidgetsCreated()
    {
        this.scrollBar.setMaxValue(this.getTotalListEntryCount() - this.visibleListEntries);
        this.updateScrollbarHeight();
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

        int scrollbarHeight = this.listHeight - (this.getY() - this.entryWidgetsStartY) - 6;
        this.scrollBar.render(mouseX, mouseY, scrollbarHeight);

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

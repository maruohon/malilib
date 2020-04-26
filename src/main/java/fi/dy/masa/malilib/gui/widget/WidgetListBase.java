package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.util.GuiIconBase;
import fi.dy.masa.malilib.gui.widget.util.IListEntryWidgetFactory;
import fi.dy.masa.malilib.gui.widget.util.WidgetSelectionHandler;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.HorizontalAlignment;

public class WidgetListBase extends WidgetContainer
{
    @Nullable private net.minecraft.client.gui.GuiScreen parentGui;
    @Nullable private WidgetSearchBar widgetSearchBar;
    @Nullable protected IListEntryWidgetFactory widgetFactory;
    protected final WidgetScrollBar scrollBar;
    protected final List<WidgetListEntryBase> listWidgets = new ArrayList<>();
    protected int totalEntryCount;
    protected int browserWidth;
    protected int browserHeight;
    protected int entryHeight;
    protected int browserEntriesStartX;
    protected int browserEntriesStartY;
    protected int browserEntriesOffsetY;
    protected int browserEntryWidth;
    protected int browserEntryHeight = 22;
    protected int browserPaddingX;
    protected int browserPaddingY;
    protected int maxVisibleBrowserEntries;
    protected int lastScrollbarPosition;
    protected boolean allowKeyboardNavigation;
    protected boolean shouldSortList;

    public WidgetListBase(int x, int y, int width, int height)
    {
        this(x, y, width, height, null);
    }

    public WidgetListBase(int x, int y, int width, int height, @Nullable IListEntryWidgetFactory widgetFactory)
    {
        super(x, y, width, height);

        this.widgetFactory = widgetFactory;

        // The position gets updated in setSize()
        this.scrollBar = new WidgetScrollBar(0, 0, 8, height);
        this.scrollBar.setArrowTextures(BaseGuiIcon.SMALL_ARROW_UP, BaseGuiIcon.SMALL_ARROW_DOWN);
        this.addWidget(this.scrollBar);

        this.setSize(width, height);
    }

    public void setWidgetFactory(IListEntryWidgetFactory widgetFactory)
    {
        this.widgetFactory = widgetFactory;
    }

    public WidgetListBase setShouldSortList(boolean shouldSort)
    {
        this.shouldSortList = shouldSort;
        return this;
    }

    protected WidgetSearchBar addSearchBarWidget(WidgetSearchBar searchBar)
    {
        this.widgetSearchBar = this.addWidget(searchBar);
        this.browserEntriesOffsetY = searchBar.getHeight() + 3;
        return this.widgetSearchBar;
    }

    public WidgetSearchBar addDefaultSearchBar()
    {
        return this.addSearchBarWidget(new WidgetSearchBar(this.getX() + 2, this.getY() + 4, this.getWidth() - 14, 14, 0, GuiIconBase.SEARCH, HorizontalAlignment.LEFT));
    }

    public void initGui()
    {
        this.refreshEntries();
        Keyboard.enableRepeatEvents(true);
    }

    public void onGuiClosed()
    {
    }

    public WidgetListBase setParentGui(GuiScreen parentGui)
    {
        this.parentGui = parentGui;
        return this;
    }

    public GuiScreen getParentGui()
    {
        return this.parentGui;
    }

    public WidgetListBase setBrowserEntryHeight(int height)
    {
        this.browserEntryHeight = height;
        return this;
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.onMouseClickedSearchBar(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        final int relativeY = mouseY - this.browserEntriesStartY - this.browserEntriesOffsetY;

        if (relativeY >= 0 &&
            mouseX >= this.browserEntriesStartX &&
            mouseX < this.browserEntriesStartX + this.browserEntryWidth)
        {
            for (int i = 0; i < this.listWidgets.size(); ++i)
            {
                WidgetListEntryBase widget = this.listWidgets.get(i);

                if (widget.isMouseOver(mouseX, mouseY) &&
                    this.onEntryWidgetClicked(widget, mouseX, mouseY, mouseButton))
                {
                    return true;
                }
            }
        }

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        for (int i = 0; i < this.listWidgets.size(); ++i)
        {
            this.listWidgets.get(i).onMouseReleased(mouseX, mouseY, mouseButton);
        }

        super.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (this.getSearchBarWidget() != null && this.getSearchBarWidget().onMouseScrolled(mouseX, mouseY, mouseWheelDelta))
        {
            return true;
        }

        if (super.onMouseScrolled(mouseX, mouseY, mouseWheelDelta))
        {
            return true;
        }

        // The scroll event could be/should be distributed to the entry widgets here
        // It's not done (for now?) to prevent accidentally messing up stuff when scrolling over lists that have buttons

        if (GuiBase.isMouseOver(mouseX, mouseY, this.getX(), this.getY(), this.browserWidth, this.browserHeight))
        {
            this.offsetSelectionOrScrollbar(mouseWheelDelta < 0 ? 3 : -3, false);
            return true;
        }

        return false;
    }

    protected boolean getShouldSortList()
    {
        return this.shouldSortList;
    }

    protected int getTotalEntryCount()
    {
        return this.totalEntryCount;
    }

    protected boolean onEntryWidgetClicked(WidgetListEntryBase widget, int mouseX, int mouseY, int mouseButton)
    {
        if (widget.onMouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        return false;
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
                    this.refreshBrowserEntries();
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
        for (WidgetListEntryBase widget : this.listWidgets)
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
            else if (keyCode == Keyboard.KEY_PRIOR) this.offsetSelectionOrScrollbar(-this.maxVisibleBrowserEntries / 2, true);
            else if (keyCode == Keyboard.KEY_NEXT)  this.offsetSelectionOrScrollbar( this.maxVisibleBrowserEntries / 2, true);
            else if (keyCode == Keyboard.KEY_HOME)  this.offsetSelectionOrScrollbar(-this.getTotalEntryCount(), true);
            else if (keyCode == Keyboard.KEY_END)   this.offsetSelectionOrScrollbar( this.getTotalEntryCount(), true);
            else return super.onKeyTyped(typedChar, keyCode);

            return true;
        }

        return super.onKeyTyped(typedChar, keyCode);
    }

    protected boolean onKeyTypedSearchBar(char typedChar, int keyCode)
    {
        if (this.getSearchBarWidget() != null && this.getSearchBarWidget().onKeyTyped(typedChar, keyCode))
        {
            this.refreshBrowserEntries();
            this.resetScrollbarPosition();
            return true;
        }

        return false;
    }

    protected boolean hasFilter()
    {
        return this.getSearchBarWidget() != null && this.getSearchBarWidget().hasFilter();
    }

    public boolean isSearchOpen()
    {
        return this.getSearchBarWidget() != null && this.getSearchBarWidget().isSearchOpen();
    }

    @Nullable
    public WidgetSearchBar getSearchBarWidget()
    {
        return this.widgetSearchBar;
    }

    @Nullable
    public WidgetSelectionHandler<?> getWidgetSelectionHandler()
    {
        return null;
    }

    protected String getFilterText()
    {
        return this.getSearchBarWidget() != null ? this.getSearchBarWidget().getFilter().toLowerCase() : "";
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
        List<WidgetTextFieldBase> textFields = new ArrayList<>();

        textFields.addAll(super.getAllTextFields());

        if (this.listWidgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.listWidgets)
            {
                textFields.addAll(widget.getAllTextFields());
            }
        }

        return textFields;
    }

    @Override
    public void setSize(int width, int height)
    {
        this.setWidth(width);
        this.setHeight(height);
        this.browserWidth = width;
        this.browserHeight = height;
        this.browserPaddingX = 3;
        this.browserPaddingY = 4;
        this.browserEntriesStartX = this.getX() + this.browserPaddingX;
        this.browserEntriesStartY = this.getY() + this.browserPaddingY;
        this.browserEntryWidth = this.browserWidth - 14;

        this.updateScrollbarPosition();
    }

    protected void updateScrollbarPosition()
    {
        int scrollBarX = this.getX() + this.browserWidth - 9;
        int scrollBarY = this.browserEntriesStartY + this.browserEntriesOffsetY;

        this.scrollBar.setPosition(scrollBarX, scrollBarY);
        this.scrollBar.setHeight(this.browserHeight - this.browserEntriesOffsetY);
    }

    protected void updateScrollbarHeight()
    {
        final int count = this.getTotalEntryCount();
        int scrollbarHeight = this.browserHeight - this.browserEntriesOffsetY - 6;
        int totalHeight = 0;

        for (int i = 0; i < count; ++i)
        {
            totalHeight += this.getHeightForListEntryWidget(i);
        }

        this.scrollBar.setTotalheight(Math.max(totalHeight, scrollbarHeight));
    }

    protected int getHeightForListEntryWidget(int listIndex)
    {
        return this.browserEntryHeight;
    }

    public void refreshEntries()
    {
        this.refreshBrowserEntries();
    }

    protected void refreshBrowserEntries()
    {
        // TODO
        this.updateScrollbarHeight();
    }

    protected void reCreateListEntryWidgets()
    {
        this.listWidgets.clear();
        this.maxVisibleBrowserEntries = 0;

        int usableHeight = this.browserHeight - this.browserPaddingY - this.browserEntriesOffsetY;
        int usedHeight = 0;
        int x = this.getX() + 2;
        int y = this.getY() + 4 + this.browserEntriesOffsetY;
        int index = this.scrollBar.getValue();
        final int totalEntryCount = this.getTotalEntryCount();
        WidgetListEntryBase widget = this.createHeaderWidget(x, y, index, usableHeight, usedHeight);

        if (widget != null)
        {
            this.onSubWidgetAdded(widget);
            this.listWidgets.add(widget);
            //this.maxVisibleBrowserEntries++;

            usedHeight += widget.getHeight();
            y += widget.getHeight();
        }

        for ( ; index < totalEntryCount; ++index)
        {
            widget = this.createListEntryWidgetIfSpace(x, y, index, usableHeight, usedHeight);

            if (widget == null)
            {
                break;
            }

            this.onSubWidgetAdded(widget);
            this.listWidgets.add(widget);
            this.maxVisibleBrowserEntries++;

            usedHeight += widget.getHeight();
            y += widget.getHeight();
        }

        this.scrollBar.setMaxValue(totalEntryCount - this.maxVisibleBrowserEntries);
    }

    @Nullable
    protected WidgetListEntryBase createListEntryWidgetIfSpace(int x, int y, int listIndex, int usableHeight, int usedHeight)
    {
        int height = this.getHeightForListEntryWidget(listIndex);

        if ((usedHeight + height) > usableHeight)
        {
            return null;
        }

        return this.createListEntryWidget(x, y, listIndex, (listIndex & 0x1) != 0);
    }

    /**
     * Create a header widget, that will always be displayed as the first entry of the list.
     * If no such header should be used, then return null,
     * @param x
     * @param y
     * @param listIndexStart the listContents index of the first visible entry
     * @param usableHeight the total usable height available for the list entry widgets
     * @param usedHeight the currently used up height. Check that (usedHeight + widgetHeight) <= usableHeight before adding an entry widget.
     * @return the created header widget, or null if there is no separate header widget
     */
    @Nullable
    protected WidgetListEntryBase createHeaderWidget(int x, int y, int listIndexStart, int usableHeight, int usedHeight)
    {
        return null;
    }

    @Nullable
    protected WidgetListEntryBase createListEntryWidget(int x, int y, int listIndex, boolean isOdd)
    {
        if (this.widgetFactory != null)
        {
            int width = this.browserEntryWidth;
            int height = this.getHeightForListEntryWidget(listIndex);
            this.widgetFactory.create(x, y, width, height, listIndex, isOdd);
        }

        return null;
    }

    public void setLastSelectedEntry(int listIndex)
    {
        if (this.getWidgetSelectionHandler() != null)
        {
            int index = listIndex >= 0 && listIndex < this.getTotalEntryCount() ? listIndex : -1;
            this.getWidgetSelectionHandler().setLastSelectedEntry(index);
        }
    }

    public void clearSelection()
    {
        this.setLastSelectedEntry(-1);
    }

    protected void offsetSelectionOrScrollbar(int amount, boolean changeSelection)
    {
        final int totalEntryCount = this.getTotalEntryCount();
        final int lastSelectedEntryIndex = this.getWidgetSelectionHandler() != null ? this.getWidgetSelectionHandler().getLastSelectedEntryIndex() : -1;

        if (changeSelection == false)
        {
            this.scrollBar.offsetValue(amount);
        }
        else if (lastSelectedEntryIndex >= 0 && totalEntryCount > 0)
        {
            int index = MathHelper.clamp(lastSelectedEntryIndex + amount, 0, totalEntryCount - 1);

            if (index != lastSelectedEntryIndex)
            {
                if (index < this.scrollBar.getValue() || index >= this.scrollBar.getValue() + this.maxVisibleBrowserEntries)
                {
                    this.scrollBar.offsetValue(index - lastSelectedEntryIndex);
                }

                this.setLastSelectedEntry(index);
            }
        }
        else
        {
            if (lastSelectedEntryIndex >= 0)
            {
                this.scrollBar.offsetValue(amount);
            }

            int index = this.scrollBar.getValue();

            if (index >= 0 && index < totalEntryCount)
            {
                this.setLastSelectedEntry(index);
            }
        }

        this.reCreateListEntryWidgets();
    }

    public void resetScrollbarPosition()
    {
        this.scrollBar.setValue(0);
    }

    public WidgetScrollBar getScrollbar()
    {
        return this.scrollBar;
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

        int scrollbarHeight = this.browserHeight - this.browserEntriesOffsetY - 6;
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
        WidgetListEntryBase widget = this.listWidgets.get(widgetIndex);
        widget.render(mouseX, mouseY, isActiveGui, hoveredWidgetId, false);
    }

    @Override
    public void renderDebug(int mouseX, int mouseY, boolean hovered, boolean renderAll, boolean infoAlways)
    {
        super.renderDebug(mouseX, mouseY, hovered, renderAll, infoAlways);
        GuiBase.renderWidgetDebug(this.listWidgets, mouseX, mouseY, renderAll, infoAlways);
    }
}

package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.util.GuiIconBase;
import fi.dy.masa.malilib.render.RenderUtils;

public abstract class WidgetListBase<TYPE, WIDGET extends WidgetListEntryBase<TYPE>> extends GuiBase
{
    protected final List<TYPE> listContents = new ArrayList<>();
    protected final List<WIDGET> listWidgets = new ArrayList<>();
    protected final WidgetScrollBar scrollBar;
    protected final Set<TYPE> selectedEntries = new HashSet<>();
    protected final int posX;
    protected final int posY;
    protected int totalWidth;
    protected int totalHeight;
    protected int browserWidth;
    protected int browserHeight;
    protected int entryHeight;
    protected int browserEntriesStartX;
    protected int browserEntriesStartY;
    protected int browserEntriesOffsetY;
    protected int browserEntryWidth;
    protected int browserEntryHeight;
    protected int browserPaddingX;
    protected int browserPaddingY;
    protected int maxVisibleBrowserEntries;
    protected int lastSelectedEntryIndex = -1;
    protected int lastScrollbarPosition = -1;
    protected boolean allowKeyboardNavigation;
    protected boolean allowMultiSelection;
    protected boolean shouldSortList;
    @Nullable private TYPE lastSelectedEntry;
    @Nullable private ISelectionListener<TYPE> selectionListener;
    @Nullable protected WidgetSearchBar widgetSearchBar;

    public WidgetListBase(int x, int y, int width, int height, @Nullable ISelectionListener<TYPE> selectionListener)
    {
        this.posX = x;
        this.posY = y;
        this.selectionListener = selectionListener;
        this.browserEntryHeight = 14;

        // The positions gets updated in setSize()
        this.scrollBar = new WidgetScrollBar(0, 0, 8, height);
        this.scrollBar.setArrowTextures(GuiIconBase.SMALL_ARROW_UP, GuiIconBase.SMALL_ARROW_DOWN);

        this.setSize(width, height);
    }

    protected void setSelectionListener(ISelectionListener<TYPE> listener)
    {
        this.selectionListener = listener;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.addWidget(this.scrollBar);
        this.refreshEntries();

        Keyboard.enableRepeatEvents(true);
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
                WIDGET widget = this.listWidgets.get(i);

                if (widget.isMouseOver(mouseX, mouseY))
                {
                    if (widget.canSelectAt(mouseX, mouseY, mouseButton))
                    {
                        int entryIndex = widget.getListIndex();

                        if (entryIndex >= 0 && entryIndex < this.listContents.size())
                        {
                            this.onEntryClicked(this.listContents.get(entryIndex), entryIndex);
                        }
                    }

                    return widget.onMouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        for (int i = 0; i < this.listWidgets.size(); ++i)
        {
            this.listWidgets.get(i).onMouseReleased(mouseX, mouseY, mouseButton);
        }

        return super.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (super.onMouseScrolled(mouseX, mouseY, mouseWheelDelta))
        {
            return true;
        }

        // The scroll event could be/should be distributed to the entry widgets here
        // It's not done (for now?) to prevent accidentally messing up stuff when scrolling over lists that have buttons

        if (mouseX >= this.posX && mouseX <= this.posX + this.browserWidth &&
            mouseY >= this.posY && mouseY <= this.posY + this.browserHeight)
        {
            this.offsetSelectionOrScrollbar(mouseWheelDelta < 0 ? 3 : -3, false);
            return true;
        }

        return false;
    }

    protected boolean onMouseClickedSearchBar(int mouseX, int mouseY, int mouseButton)
    {
        if (this.getSearchBarWidget() != null)
        {
            boolean searchOpenPre = this.getSearchBarWidget().isSearchOpen();
            String filterPre = this.getSearchBarWidget().getFilter();

            if (this.getSearchBarWidget().onMouseClickedImpl(mouseX, mouseY, mouseButton))
            {
                // Toggled the search bar on or off, or cleared the filter with a right click
                if (this.getSearchBarWidget().isSearchOpen() != searchOpenPre ||
                    filterPre.equals(this.getSearchBarWidget().getFilter()) == false)
                {
                    this.clearSelection();
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
        if (this.onKeyTypedSearchBar(typedChar, keyCode))
        {
            return true;
        }
        else if (this.allowKeyboardNavigation)
        {
                 if (keyCode == Keyboard.KEY_UP)    this.offsetSelectionOrScrollbar(-1, true);
            else if (keyCode == Keyboard.KEY_DOWN)  this.offsetSelectionOrScrollbar( 1, true);
            else if (keyCode == Keyboard.KEY_PRIOR) this.offsetSelectionOrScrollbar(-this.maxVisibleBrowserEntries / 2, true);
            else if (keyCode == Keyboard.KEY_NEXT)  this.offsetSelectionOrScrollbar( this.maxVisibleBrowserEntries / 2, true);
            else if (keyCode == Keyboard.KEY_HOME)  this.offsetSelectionOrScrollbar(-this.listContents.size(), true);
            else if (keyCode == Keyboard.KEY_END)   this.offsetSelectionOrScrollbar( this.listContents.size(), true);
            else return false;

            return true;
        }

        return false;
    }

    protected boolean onKeyTypedSearchBar(char typedChar, int keyCode)
    {
        if (this.getSearchBarWidget() != null && this.getSearchBarWidget().onKeyTyped(typedChar, keyCode))
        {
            this.clearSelection();
            this.refreshBrowserEntries();
            this.resetScrollbarPosition();
            return true;
        }

        return false;
    }

    protected boolean getShouldSortList()
    {
        return this.shouldSortList;
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

    public List<TYPE> getCurrentEntries()
    {
        return this.listContents;
    }

    protected Collection<TYPE> getAllEntries()
    {
        return Collections.emptyList();
    }

    @Nullable
    protected Comparator<TYPE> getComparator()
    {
        return null;
    }

    protected void refreshBrowserEntries()
    {
        this.listContents.clear();

        Collection<TYPE> entries = this.getAllEntries();

        if (this.hasFilter())
        {
            this.addFilteredContents(entries);
        }
        else
        {
            this.addNonFilteredContents(entries);
        }

        this.sortEntryList(this.listContents);
        this.reCreateListEntryWidgets();
    }

    protected void sortEntryList(List<TYPE> list)
    {
        if (this.getShouldSortList())
        {
            Comparator<TYPE> comparator = this.getComparator();

            if (comparator != null)
            {
                Collections.sort(list, comparator);
            }
        }
    }

    protected boolean filterMatchesEmptyEntry(TYPE entry)
    {
        return true;
    }

    protected String getFilterText()
    {
        return this.getSearchBarWidget() != null ? this.getSearchBarWidget().getFilter().toLowerCase() : "";
    }

    protected boolean entryMatchesFilter(TYPE entry, String filterText)
    {
        List<String> entryStrings = this.getEntryStringsForFilter(entry);

        if (entryStrings.isEmpty())
        {
            return this.filterMatchesEmptyEntry(entry);
        }

        return this.matchesFilter(entryStrings, filterText);
    }

    protected boolean matchesFilter(List<String> entryStrings, String filterText)
    {
        if (filterText.isEmpty())
        {
            return true;
        }

        for (String str : entryStrings)
        {
            if (this.matchesFilter(str, filterText))
            {
                return true;
            }
        }

        return false;
    }

    protected boolean matchesFilter(String entryString, String filterText)
    {
        if (filterText.isEmpty())
        {
            return true;
        }

        for (String filter : filterText.split("\\|"))
        {
            if (entryString.indexOf(filter) != -1)
            {
                return true;
            }
        }

        return false;
    }

    protected List<String> getEntryStringsForFilter(TYPE entry)
    {
        return Collections.emptyList();
    }

    protected void addNonFilteredContents(Collection<TYPE> placements)
    {
        this.listContents.addAll(placements);
    }

    protected void addFilteredContents(Collection<TYPE> entries)
    {
        String filterText = this.getFilterText();

        for (TYPE entry : entries)
        {
            if (filterText.isEmpty() || this.entryMatchesFilter(entry, filterText))
            {
                this.listContents.add(entry);
            }
        }
    }

    @Override
    public void drawContents(int mouseX, int mouseY, float partialTicks)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        if (this.getSearchBarWidget() != null)
        {
            this.getSearchBarWidget().render(mouseX, mouseY, false);
        }

        WidgetBase hovered = null;
        boolean hoveredSelected = false;
        int scrollbarHeight = this.browserHeight - this.browserEntriesOffsetY - 6;
        int totalHeight = 0;

        for (int i = 0; i < this.listContents.size(); ++i)
        {
            totalHeight += this.getBrowserEntryHeightFor(this.listContents.get(i));
        }

        totalHeight = Math.max(totalHeight, scrollbarHeight);

        this.scrollBar.render(mouseX, mouseY, scrollbarHeight, totalHeight);

        // The value gets updated in the drawScrollBar() method above, if dragging
        if (this.scrollBar.getValue() != this.lastScrollbarPosition)
        {
            this.lastScrollbarPosition = this.scrollBar.getValue();
            this.reCreateListEntryWidgets();
        }

        // Draw the currently visible directory entries
        for (int i = 0; i < this.listWidgets.size(); i++)
        {
            WIDGET widget = this.listWidgets.get(i);
            TYPE entry = widget.getEntry();
            boolean isSelected = this.allowMultiSelection ? this.selectedEntries.contains(entry) : entry != null && entry.equals(this.getLastSelectedEntry());
            widget.render(mouseX, mouseY, isSelected);

            if (widget.isMouseOver(mouseX, mouseY))
            {
                hovered = widget;
                hoveredSelected = isSelected;
            }
        }

        if (hovered == null && this.getSearchBarWidget() != null && this.getSearchBarWidget().isMouseOver(mouseX, mouseY))
        {
            hovered = this.getSearchBarWidget();
        }

        if (hovered != null)
        {
            hovered.postRenderHovered(mouseX, mouseY, hoveredSelected);
        }

        GlStateManager.disableLighting();
        RenderUtils.color(1f, 1f, 1f, 1f);
    }

    public void setSize(int width, int height)
    {
        this.totalWidth = width;
        this.totalHeight = height;
        this.browserWidth = width;
        this.browserHeight = height;
        this.browserPaddingX = 3;
        this.browserPaddingY = 4;
        this.browserEntriesStartX = this.posX + this.browserPaddingX;
        this.browserEntriesStartY = this.posY + this.browserPaddingY;
        this.browserEntryWidth = this.browserWidth - 14;

        this.updateScrollbarPosition();
    }

    protected void updateScrollbarPosition()
    {
        int scrollBarX = this.posX + this.browserWidth - 9;
        int scrollBarY = this.browserEntriesStartY + this.browserEntriesOffsetY;

        this.scrollBar.setPosition(scrollBarX, scrollBarY);
        this.scrollBar.setHeight(this.browserHeight - this.browserEntriesOffsetY);
    }

    protected int getBrowserEntryHeightFor(@Nullable TYPE type)
    {
        return this.browserEntryHeight;
    }

    protected void reCreateListEntryWidgets()
    {
        this.listWidgets.clear();
        this.maxVisibleBrowserEntries = 0;

        final int numEntries = this.listContents.size();
        int usableHeight = this.browserHeight - this.browserPaddingY - this.browserEntriesOffsetY;
        int usedHeight = 0;
        int x = this.posX + 2;
        int y = this.posY + 4 + this.browserEntriesOffsetY;
        int index = this.scrollBar.getValue();
        WIDGET widget = this.createHeaderWidget(x, y, index, usableHeight, usedHeight);

        if (widget != null)
        {
            this.listWidgets.add(widget);
            //this.maxVisibleBrowserEntries++;

            usedHeight += widget.getHeight();
            y += widget.getHeight();
        }

        for ( ; index < numEntries; ++index)
        {
            widget = this.createListEntryWidgetIfSpace(x, y, index, usableHeight, usedHeight);

            if (widget == null)
            {
                break;
            }

            this.listWidgets.add(widget);
            this.maxVisibleBrowserEntries++;

            usedHeight += widget.getHeight();
            y += widget.getHeight();
        }

        this.scrollBar.setMaxValue(this.listContents.size() - this.maxVisibleBrowserEntries);
    }

    @Nullable
    protected WIDGET createListEntryWidgetIfSpace(int x, int y, int listIndex, int usableHeight, int usedHeight)
    {
        TYPE entry = this.listContents.get(listIndex);
        int height = this.getBrowserEntryHeightFor(entry);

        if ((usedHeight + height) > usableHeight)
        {
            return null;
        }

        return this.createListEntryWidget(x, y, listIndex, (listIndex & 0x1) != 0, entry);
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
    protected WIDGET createHeaderWidget(int x, int y, int listIndexStart, int usableHeight, int usedHeight)
    {
        return null;
    }

    public void refreshEntries()
    {
        this.refreshBrowserEntries();
    }

    protected abstract WIDGET createListEntryWidget(int x, int y, int listIndex, boolean isOdd, TYPE entry);

    @Nullable
    public TYPE getLastSelectedEntry()
    {
        return this.lastSelectedEntry;
    }

    public Set<TYPE> getSelectedEntries()
    {
        return this.selectedEntries;
    }

    protected boolean onEntryClicked(@Nullable TYPE entry, int index)
    {
        this.setLastSelectedEntry(entry, index);
        return true;
    }

    public void setLastSelectedEntry(@Nullable TYPE entry, int index)
    {
        this.lastSelectedEntry = entry;
        this.lastSelectedEntryIndex = index;

        if (this.allowMultiSelection && entry != null)
        {
            if (this.selectedEntries.contains(entry))
            {
                this.selectedEntries.remove(entry);
            }
            else
            {
                this.selectedEntries.add(entry);
            }
        }

        if (entry != null && this.selectionListener != null)
        {
            this.selectionListener.onSelectionChange(entry);
        }
    }

    public void clearSelection()
    {
        this.setLastSelectedEntry(null, -1);
    }

    public void clearAllSelections()
    {
        this.clearSelection();
        this.selectedEntries.clear();
    }

    protected void offsetSelectionOrScrollbar(int amount, boolean changeSelection)
    {
        if (changeSelection == false)
        {
            this.scrollBar.offsetValue(amount);
        }
        else if (this.lastSelectedEntryIndex >= 0 && this.listContents.size() > 0)
        {
            int index = MathHelper.clamp(this.lastSelectedEntryIndex + amount, 0, this.listContents.size() - 1);

            if (index != this.lastSelectedEntryIndex)
            {
                if (index < this.scrollBar.getValue() || index >= this.scrollBar.getValue() + this.maxVisibleBrowserEntries)
                {
                    this.scrollBar.offsetValue(index - this.lastSelectedEntryIndex);
                }

                this.setLastSelectedEntry(this.listContents.get(index), index);
            }
        }
        else
        {
            if (this.lastSelectedEntryIndex >= 0)
            {
                this.scrollBar.offsetValue(amount);
            }

            int index = this.scrollBar.getValue();

            if (index >= 0 && index < this.listContents.size())
            {
                this.setLastSelectedEntry(this.listContents.get(index), index);
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
}

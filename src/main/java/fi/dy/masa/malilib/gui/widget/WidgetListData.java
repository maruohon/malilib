package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.widget.util.DataListEntrySelectionHandler;
import fi.dy.masa.malilib.gui.widget.util.DataListEntryWidgetFactory;
import fi.dy.masa.malilib.gui.widget.util.ListHeaderWidgetFactory;

public class WidgetListData<DATATYPE> extends WidgetListBase
{
    protected final Supplier<Collection<DATATYPE>> entrySupplier;
    protected final List<DATATYPE> listContents = new ArrayList<>();
    @Nullable protected ListHeaderWidgetFactory<DATATYPE> headerWidgetFactory;
    @Nullable protected Comparator<DATATYPE> listSortComparator;
    @Nullable protected DataListEntrySelectionHandler<DATATYPE> selectionHandler;
    protected DataListEntryWidgetFactory<DATATYPE> entryWidgetFactory;

    protected boolean shouldSortList;
    protected int lastSelectedEntryIndex = -1;

    public WidgetListData(int x, int y, int width, int height, Supplier<Collection<DATATYPE>> entrySupplier)
    {
        super(x, y, width, height);

        this.entrySupplier = entrySupplier;
        this.selectionHandler = new DataListEntrySelectionHandler<>(this::getCurrentEntries);
    }

    public WidgetListData<DATATYPE> setHeaderWidgetFactory(@Nullable ListHeaderWidgetFactory<DATATYPE> headerWidgetFactory)
    {
        this.headerWidgetFactory = headerWidgetFactory;
        return this;
    }

    public WidgetListData<DATATYPE> setEntryWidgetFactory(DataListEntryWidgetFactory<DATATYPE> entryWidgetFactory)
    {
        this.entryWidgetFactory = entryWidgetFactory;
        return this;
    }

    /**
     * Sets the comparator used for sorting the list contents.
     * A header widget which wants to sort the list contents based on the column
     * that was clicked on, should use this to set the comparator at least once
     * and then update it based on the column that is clicked on, or even to
     * switch the comparator each time.
     * @param comparator
     */
    public void setListSortComparator(@Nullable Comparator<DATATYPE> comparator)
    {
        this.listSortComparator = comparator;
    }

    @Override
    @Nullable
    protected WidgetListEntryBase createListEntryWidget(int x, int y, int listIndex)
    {
        if (this.entryWidgetFactory != null && listIndex < this.getCurrentEntries().size())
        {
            DATATYPE data = this.getCurrentEntries().get(listIndex);
            return this.entryWidgetFactory.createWidget(x, y, this.entryWidgetWidth, -1, listIndex, data, this);
        }

        return null;
    }

    /**
     * Creates a header widget, that will be displayed before the first entry of the list.
     * @param x
     * @param y
     * @return the created header widget, or null if there is no separate header widget
     */
    @Override
    @Nullable
    protected WidgetDataListHeader createHeaderWidget(int x, int y)
    {
        if (this.headerWidgetFactory != null)
        {
            return this.headerWidgetFactory.createWidget(x, y, this.entryWidgetWidth, -1, this);
        }

        return null;
    }

    @Override
    protected boolean onEntryWidgetClicked(WidgetListEntryBase widget, int mouseX, int mouseY, int mouseButton)
    {
        if (widget.canSelectAt(mouseX, mouseY, mouseButton))
        {
            int listIndex = widget.getListIndex();

            if (listIndex >= 0 && listIndex < this.getTotalListEntryCount())
            {
                this.setLastSelectedEntry(listIndex);
            }
        }

        return super.onEntryWidgetClicked(widget, mouseX, mouseY, mouseButton);
    }

    protected boolean shouldSortList()
    {
        return this.shouldSortList;
    }

    @Override
    public int getTotalListEntryCount()
    {
        return this.listContents.size();
    }

    public List<DATATYPE> getCurrentEntries()
    {
        return this.listContents;
    }

    protected Collection<DATATYPE> getAllEntries()
    {
        return this.entrySupplier != null ? this.entrySupplier.get() : Collections.emptyList();
    }

    @Nullable
    public DATATYPE getLastSelectedEntry()
    {
        DataListEntrySelectionHandler<DATATYPE> handler = this.getEntrySelectionHandler();
        return handler != null ? handler.getLastSelectedEntry() : null;
    }

    @Nullable
    protected Comparator<DATATYPE> getComparator()
    {
        return this.listSortComparator;
    }

    @Nullable
    public DataListEntrySelectionHandler<DATATYPE> getEntrySelectionHandler()
    {
        return this.selectionHandler;
    }

    public WidgetListData<DATATYPE> setEntrySelectionHandler(DataListEntrySelectionHandler<DATATYPE> selectionHandler)
    {
        this.selectionHandler = selectionHandler;
        return this;
    }

    public WidgetListData<DATATYPE> setShouldSortList(boolean shouldSort)
    {
        this.shouldSortList = shouldSort;
        return this;
    }

    @Override
    protected void refreshBrowserEntries()
    {
        this.listContents.clear();

        Collection<DATATYPE> entries = this.getAllEntries();

        if (this.hasFilter())
        {
            this.addFilteredContents(entries);
        }
        else
        {
            this.addNonFilteredContents(entries);
        }

        if (this.shouldSortList())
        {
            this.sortEntryList(this.listContents);
        }

        this.reCreateListEntryWidgets();
    }

    protected void sortEntryList(List<DATATYPE> list)
    {
        Comparator<DATATYPE> comparator = this.getComparator();

        if (comparator != null)
        {
            list.sort(comparator);
        }
    }

    protected boolean filterMatchesEmptyEntry(DATATYPE entry)
    {
        return true;
    }

    protected List<String> getEntryStringsForFilter(DATATYPE entry)
    {
        return Collections.emptyList();
    }

    protected void addNonFilteredContents(Collection<DATATYPE> entries)
    {
        this.listContents.addAll(entries);
    }

    protected void addFilteredContents(Collection<DATATYPE> entries)
    {
        String filterText = this.getFilterText();

        for (DATATYPE entry : entries)
        {
            if (filterText.isEmpty() || this.entryMatchesFilter(entry, filterText))
            {
                this.listContents.add(entry);
            }
        }
    }

    protected boolean entryMatchesFilter(DATATYPE entry, String filterText)
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

        entryString = entryString.toLowerCase(Locale.ROOT);

        for (String filter : filterText.split("\\|"))
        {
            if (entryString.contains(filter))
            {
                return true;
            }
        }

        return false;
    }

    protected int getLastSelectedEntryIndex()
    {
        if (this.getEntrySelectionHandler() != null)
        {
            return this.getEntrySelectionHandler().getLastSelectedEntryIndex();
        }

        return this.lastSelectedEntryIndex;
    }

    public void setLastSelectedEntry(int listIndex)
    {
        int index = listIndex >= 0 && listIndex < this.getTotalListEntryCount() ? listIndex : -1;

        if (this.getEntrySelectionHandler() != null)
        {
            this.getEntrySelectionHandler().setLastSelectedEntry(index);
        }
    }

    public void clearSelection()
    {
        this.setLastSelectedEntry(-1);
    }

    @Override
    protected void offsetSelectionOrScrollbar(int amount, boolean changeSelection)
    {
        if (changeSelection == false)
        {
            super.offsetSelectionOrScrollbar(amount, changeSelection);
        }
        else
        {
            final int totalEntryCount = this.getTotalListEntryCount();
            final int lastSelectedEntryIndex = this.getLastSelectedEntryIndex();

            if (lastSelectedEntryIndex >= 0 && totalEntryCount > 0)
            {
                int index = MathHelper.clamp(lastSelectedEntryIndex + amount, 0, totalEntryCount - 1);

                if (index != lastSelectedEntryIndex)
                {
                    if (index < this.scrollBar.getValue() || index >= this.scrollBar.getValue() + this.visibleListEntries)
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
        }

        this.reCreateListEntryWidgets();
    }

    @Override
    protected void renderWidget(int widgetIndex, int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        WidgetListEntryBase widget = this.listWidgets.get(widgetIndex);
        DATATYPE entry = this.listContents.get(widget.getListIndex());
        boolean isSelected = this.getEntrySelectionHandler() != null && this.getEntrySelectionHandler().isEntrySelected(entry);

        this.listWidgets.get(widgetIndex).render(mouseX, mouseY, isActiveGui, hoveredWidgetId, isSelected);
    }
}

package fi.dy.masa.malilib.gui.widget.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseListEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntrySelectionHandler;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetFactory;

public class DataListWidget<DATATYPE> extends BaseListWidget
{
    protected final Supplier<List<DATATYPE>> entrySupplier;
    protected final ArrayList<DATATYPE> currentContents;
    protected final ArrayList<DATATYPE> filteredContents = new ArrayList<>();
    protected final ArrayList<Integer> filteredIndices = new ArrayList<>();
    @Nullable protected ListHeaderWidgetFactory<DATATYPE> headerWidgetFactory;
    @Nullable protected DataListEntryWidgetFactory<DATATYPE> entryWidgetFactory;
    @Nullable protected DataListEntrySelectionHandler<DATATYPE> selectionHandler;
    @Nullable protected Comparator<DATATYPE> listSortComparator;
    protected Function<DATATYPE, List<String>> entryFilterStringFactory = (e) -> Collections.singletonList(e.toString());

    protected boolean fetchFromSupplierOnRefresh;
    protected boolean filterMatchesEmptyEntry;
    protected boolean shouldSortList;
    protected int lastSelectedEntryIndex = -1;

    public DataListWidget(int x, int y, int width, int height, Supplier<List<DATATYPE>> entrySupplier)
    {
        super(x, y, width, height);

        this.entrySupplier = entrySupplier;
        this.currentContents = new ArrayList<>(entrySupplier.get());
        this.selectionHandler = new DataListEntrySelectionHandler<>(this::getFilteredEntries);
    }

    public DataListWidget<DATATYPE> setAllowSelection(boolean allowSelection)
    {
        DataListEntrySelectionHandler<DATATYPE> handler = this.getEntrySelectionHandler();

        if (handler != null)
        {
            handler.setAllowSelection(allowSelection);
        }

        return this;
    }

    public DataListWidget<DATATYPE> setHeaderWidgetFactory(@Nullable ListHeaderWidgetFactory<DATATYPE> factory)
    {
        this.headerWidgetFactory = factory;
        return this;
    }

    public DataListWidget<DATATYPE> setEntryWidgetFactory(@Nullable DataListEntryWidgetFactory<DATATYPE> entryWidgetFactory)
    {
        this.entryWidgetFactory = entryWidgetFactory;
        return this;
    }

    /**
     * This sets the data to be re-fetched from the list supplier to the
     * currentContents list any time the refreshEntries method is called.<br><br>
     * If this is false, then the entrySupplier is only used once, in the constructor,
     * to fetch the contents to the list.<br><br>
     * The no-re-fetch case allows the currentContents list (returned by the {@link #getCurrentContents()} method)
     * to be used as a backing data list for other things.
     * For example the {@link fi.dy.masa.malilib.gui.widget.list.entry.BaseOrderableListEditEntryWidget}
     * uses the list returned by getCurrentEntries() to store the edited list,
     * before the changes are stored back to the actual underlying config option later on
     * in {@link fi.dy.masa.malilib.gui.config.BaseValueListEditScreen#onGuiClosed()}.
     */
    public DataListWidget<DATATYPE> setFetchFromSupplierOnRefresh(boolean fetch)
    {
        this.fetchFromSupplierOnRefresh = fetch;
        return this;
    }

    public DataListWidget<DATATYPE> setFilterMatchesEmptyEntry(boolean matchesEmpty)
    {
        this.filterMatchesEmptyEntry = matchesEmpty;
        return this;
    }

    /**
     * Sets the function that creates the strings from the data entries that the
     * search strings are matched against.
     * By default this is just a singleton list of the toString() return value of the entry.
     */
    public DataListWidget<DATATYPE> setEntryFilterStringFactory(Function<DATATYPE, List<String>> factory)
    {
        this.entryFilterStringFactory = factory;
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
    protected BaseListEntryWidget createListEntryWidget(int x, int y, int listIndex)
    {
        List<DATATYPE> list = this.getFilteredEntries();

        if (this.entryWidgetFactory != null && listIndex < list.size())
        {
            List<Integer> indices = this.filteredIndices;
            DATATYPE data = list.get(listIndex);
            int originalDataIndex = listIndex < indices.size() ? indices.get(listIndex) : listIndex;
            int height = this.getHeightForListEntryWidgetCreation(listIndex);
            return this.entryWidgetFactory.createWidget(x, y, this.entryWidgetWidth, height,
                                                        listIndex, originalDataIndex, data, this);
        }

        return null;
    }

    @Override
    protected void createHeaderWidget()
    {
        if (this.headerWidgetFactory != null)
        {
            int x = this.getX() + this.listPosition.getLeft();
            int y = this.getY();
            this.headerWidget = this.headerWidgetFactory.createWidget(x, y, this.entryWidgetWidth, -1, this);
        }
    }

    @Override
    protected boolean onEntryWidgetClicked(BaseListEntryWidget widget, int mouseX, int mouseY, int mouseButton)
    {
        if (widget.canSelectAt(mouseX, mouseY, mouseButton))
        {
            int listIndex = widget.getListIndex();

            if (listIndex >= 0 && listIndex < this.getTotalListWidgetCount())
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
    public int getTotalListWidgetCount()
    {
        return this.getFilteredEntries().size();
    }

    /**
     * Returns the current full list of data entries.
     * This may be different from the original list of data,
     * if this list widget/screen allows modifying the contents.
     * @return
     */
    public ArrayList<DATATYPE> getCurrentContents()
    {
        return this.currentContents;
    }

    /**
     * Returns the current list of data entries that is shown,
     * meaning that any possible search/filter effects have been applied.
     * @return
     */
    public List<DATATYPE> getFilteredEntries()
    {
        return this.hasFilter() ? this.filteredContents : this.getCurrentContents();
    }

    @Nullable
    public DATATYPE getLastSelectedEntry()
    {
        DataListEntrySelectionHandler<DATATYPE> handler = this.getEntrySelectionHandler();
        return handler != null ? handler.getLastSelectedEntry() : null;
    }

    public Set<DATATYPE> getSelectedEntries()
    {
        DataListEntrySelectionHandler<DATATYPE> handler = this.getEntrySelectionHandler();
        return handler != null ? handler.getSelectedEntries() : Collections.emptySet();
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

    public DataListWidget<DATATYPE> setEntrySelectionHandler(@Nullable DataListEntrySelectionHandler<DATATYPE> selectionHandler)
    {
        this.selectionHandler = selectionHandler;
        return this;
    }

    public DataListWidget<DATATYPE> setShouldSortList(boolean shouldSort)
    {
        this.shouldSortList = shouldSort;
        return this;
    }

    @Override
    public void refreshEntries()
    {
        if (this.fetchFromSupplierOnRefresh)
        {
            this.currentContents.clear();
            this.currentContents.addAll(this.entrySupplier.get());
        }

        this.refreshFilteredEntries();
    }

    @Override
    public void refreshFilteredEntries()
    {
        this.filteredContents.clear();
        this.filteredIndices.clear();

        List<DATATYPE> entries = this.getCurrentContents();

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
            this.sortEntryList(this.filteredContents);
        }

        this.onEntriesRefreshed();
        this.reCreateListEntryWidgets();
    }

    protected void sortEntryList(List<DATATYPE> list)
    {
        // FIXME how to get the indices in the same order?
        Comparator<DATATYPE> comparator = this.getComparator();

        if (comparator != null)
        {
            list.sort(comparator);
        }
    }

    protected boolean filterMatchesEmptySearchTerms(DATATYPE entry)
    {
        return false;
    }

    protected List<String> getSearchStringsForEntry(DATATYPE entry)
    {
        return this.entryFilterStringFactory.apply(entry);
    }

    protected void addNonFilteredContents(List<DATATYPE> entries)
    {
        this.filteredContents.addAll(entries);
    }

    protected void addFilteredContents(List<DATATYPE> entries)
    {
        String filterText = this.getFilterText();
        final int size = entries.size();

        for (int i = 0; i < size; ++i)
        {
            DATATYPE entry = entries.get(i);

            if (this.entryMatchesFilter(entry, filterText))
            {
                this.filteredContents.add(entry);
                this.filteredIndices.add(i);
            }
        }
    }

    protected boolean entryMatchesFilter(DATATYPE entry, String filterText)
    {
        if (filterText.isEmpty())
        {
            return true;
        }

        List<String> searchStrings = this.getSearchStringsForEntry(entry);

        if (searchStrings.isEmpty())
        {
            return this.filterMatchesEmptySearchTerms(entry);
        }

        return this.searchTermsMatchFilter(searchStrings, filterText);
    }

    protected boolean searchTermsMatchFilter(List<String> searchTermsFromData, String filterText)
    {
        for (String str : searchTermsFromData)
        {
            if (this.searchTermsMatchFilter(str, filterText))
            {
                return true;
            }
        }

        return false;
    }

    protected boolean searchTermsMatchFilter(String searchTermFromData, String filterText)
    {
        searchTermFromData = searchTermFromData.toLowerCase(Locale.ROOT);

        for (String filter : filterText.split("\\|"))
        {
            if (searchTermFromData.contains(filter))
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
        if (this.getEntrySelectionHandler() != null)
        {
            int index = listIndex >= 0 && listIndex < this.getTotalListWidgetCount() ? listIndex : -1;
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
            final int totalEntryCount = this.getTotalListWidgetCount();
            final int lastSelectedEntryIndex = this.getLastSelectedEntryIndex();

            if (lastSelectedEntryIndex >= 0 && totalEntryCount > 0)
            {
                int index = MathHelper.clamp(lastSelectedEntryIndex + amount, 0, totalEntryCount - 1);

                if (index != lastSelectedEntryIndex)
                {
                    if (index < this.scrollBar.getValue() ||
                        index >= this.scrollBar.getValue() + this.visibleListEntries)
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
    }

    @Override
    protected void renderWidget(int widgetIndex, int diffX, int diffY, float diffZ, int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        BaseListEntryWidget widget = this.entryWidgets.get(widgetIndex);
        DataListEntrySelectionHandler<DATATYPE> handler = this.getEntrySelectionHandler();
        boolean isSelected = handler != null && handler.isEntrySelected(widget.getListIndex());

        int wx = widget.getX() + diffX;
        int wy = widget.getY() + diffY;
        float wz = widget.getZLevel() + diffZ;
        widget.renderAt(wx, wy, wz, mouseX, mouseY, isActiveGui, hoveredWidgetId, isSelected);
    }
}

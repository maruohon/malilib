package fi.dy.masa.malilib.gui.widget.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.value.SortDirection;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseDataListEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseListEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetFactory;
import fi.dy.masa.malilib.gui.widget.list.header.DataColumn;
import fi.dy.masa.malilib.gui.widget.list.header.DataListHeaderWidget;

public class DataListWidget<DATATYPE> extends BaseListWidget
{
    protected final Supplier<List<DATATYPE>> entrySupplier;
    protected final ArrayList<DATATYPE> currentContents;
    protected final ArrayList<DATATYPE> filteredContents = new ArrayList<>();
    protected final ArrayList<Integer> filteredIndices = new ArrayList<>();
    protected final ArrayList<BaseDataListEntryWidget<DATATYPE>> entryWidgets = new ArrayList<>();
    protected final ArrayList<DataColumn<DATATYPE>> columns = new ArrayList<>();
    protected SortDirection sortDirection = SortDirection.ASCENDING;
    protected EntryFilter<DATATYPE> entryFilter;
    protected Function<DATATYPE, List<String>> entrySearchStringFunction = (e) -> Collections.singletonList(e.toString());
    @Nullable protected Function<DataListWidget<DATATYPE>, DataListHeaderWidget<DATATYPE>> headerWidgetFactory;
    @Nullable protected Function<DataListWidget<DATATYPE>, DataListHeaderWidget<DATATYPE>> defaultHeaderWidgetFactory;
    @Nullable protected DataListEntryWidgetFactory<DATATYPE> entryWidgetFactory;
    @Nullable protected DataListEntrySelectionHandler<DATATYPE> selectionHandler;
    @Nullable protected Consumer<BaseDataListEntryWidget<DATATYPE>> widgetInitializer;
    @Nullable protected Supplier<List<DataColumn<DATATYPE>>> columnSupplier;
    @Nullable protected DataColumn<DATATYPE> activeSortColumn;
    @Nullable protected DataColumn<DATATYPE> defaultSortColumn;
    @Nullable protected Comparator<DATATYPE> activeListSortComparator;
    @Nullable protected Comparator<DATATYPE> defaultListSortComparator;

    protected boolean fetchFromSupplierOnRefresh;
    protected boolean filterMatchesEmptyEntry;
    protected boolean hasDataColumns;
    protected boolean shouldSortList;

    public DataListWidget(int x, int y, int width, int height, Supplier<List<DATATYPE>> entrySupplier)
    {
        super(x, y, width, height);

        this.entrySupplier = entrySupplier;
        this.currentContents = new ArrayList<>(entrySupplier.get());
        this.selectionHandler = new DataListEntrySelectionHandler<>(this::getFilteredEntries);
        this.entryFilter = this::defaultEntryFilter;
    }

    @Override
    public List<BaseDataListEntryWidget<DATATYPE>> getEntryWidgetList()
    {
        return this.entryWidgets;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addNewEntryWidget(BaseListEntryWidget widget)
    {
        this.entryWidgets.add((BaseDataListEntryWidget<DATATYPE>) widget);
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

    public DataListWidget<DATATYPE> setHeaderWidgetFactory(@Nullable Function<DataListWidget<DATATYPE>, DataListHeaderWidget<DATATYPE>> factory)
    {
        this.headerWidgetFactory = factory;
        this.defaultHeaderWidgetFactory = factory;
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
     * Sets the entry filter function that is used to search for entries from the list.
     * The default filter just compares the {@link Object#toString()} value of an
     * entry to the search terms.
     */
    public DataListWidget<DATATYPE> setEntryFilter(EntryFilter<DATATYPE> filter)
    {
        this.entryFilter = filter;
        return this;
    }

    /**
     * Sets the function that outputs the strings to match the search terms against.
     * The default function is just a singleton list of {@link Object#toString()} of the entry.
     */
    public DataListWidget<DATATYPE> setEntryFilterStringFunction(Function<DATATYPE, List<String>> function)
    {
        this.entrySearchStringFunction = function;
        return this;
    }

    public void setColumnSupplier(@Nullable Supplier<List<DataColumn<DATATYPE>>> columnSupplier)
    {
        this.columnSupplier = columnSupplier;
    }

    public void setDataColumns(List<DataColumn<DATATYPE>> columns)
    {
        this.columns.clear();
        this.columns.addAll(columns);
    }

    public void setHasDataColumns(boolean hasDataColumns)
    {
        this.hasDataColumns = hasDataColumns;
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
        this.activeListSortComparator = comparator;
        this.defaultListSortComparator = comparator;
    }

    public void updateActiveColumns()
    {
        if (this.hasDataColumns)
        {
            this.createColumns();
            this.headerWidgetFactory = this.defaultHeaderWidgetFactory;
        }
        else
        {
            this.columns.clear();
            this.activeSortColumn = this.defaultSortColumn;
            this.activeListSortComparator = this.defaultListSortComparator;
            this.headerWidgetFactory = null;
        }

        this.initListWidget();
    }

    protected void createColumns()
    {
        this.columns.clear();

        if (this.columnSupplier != null)
        {
            this.columns.addAll(this.columnSupplier.get());
        }
    }

    @Override
    @Nullable
    protected BaseListEntryWidget createListEntryWidget(int x, int y, int listIndex)
    {
        List<DATATYPE> list = this.getFilteredEntries();

        if (this.entryWidgetFactory != null && listIndex < list.size())
        {
            DATATYPE entryData = list.get(listIndex);
            List<Integer> indices = this.filteredIndices;
            int originalDataIndex = listIndex < indices.size() ? indices.get(listIndex) : listIndex;
            int height = this.getHeightForListEntryWidgetCreation(listIndex);
            DataListEntryWidgetData constructData
                    = new DataListEntryWidgetData(x, y, this.entryWidgetWidth, height,
                                                  listIndex, originalDataIndex, this);

            return this.entryWidgetFactory.createWidget(entryData, constructData);
        }

        return null;
    }

    @Override
    protected void createAndSetHeaderWidget()
    {
        if (this.headerWidgetFactory != null)
        {
            this.headerWidget = this.headerWidgetFactory.apply(this);

            if (this.headerWidget != null)
            {
                int x = this.getX() + this.listPosition.getLeft();
                int y = this.getY();
                this.headerWidget.setPosition(x, y);
                this.headerWidget.setHeaderWidgetSize(this.entryWidgetWidth, -1);
            }
        }
        else
        {
            this.headerWidget = null;
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
                return this.clickEntry(listIndex);
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

    @Nullable
    public DATATYPE getKeyboardNavigationEntry()
    {
        int index = this.getKeyboardNavigationIndex();
        List<DATATYPE> list = this.getFilteredEntries();
        return index >= 0 && index < list.size() ? list.get(index) : null;
    }

    public Set<DATATYPE> getSelectedEntries()
    {
        DataListEntrySelectionHandler<DATATYPE> handler = this.getEntrySelectionHandler();
        return handler != null ? handler.getSelectedEntries() : Collections.emptySet();
    }

    public Optional<SortDirection> getSortDirectionFor(DataColumn<DATATYPE> column)
    {
        if (this.activeSortColumn == column)
        {
            return Optional.ofNullable(this.sortDirection);
        }

        return Optional.empty();
    }

    public void toggleSortByColumn(DataColumn<DATATYPE> column)
    {
        if (column.getCanSortBy())
        {
            if (this.activeSortColumn == column)
            {
                this.sortDirection = this.sortDirection.getOpposite();
            }
            else
            {
                this.sortDirection = SortDirection.ASCENDING;
            }

            Optional<Comparator<DATATYPE>> optional = column.getComparator();

            if (optional.isPresent())
            {
                if (this.sortDirection == SortDirection.DESCENDING)
                {
                    this.activeListSortComparator = optional.get().reversed();
                }
                else
                {
                    this.activeListSortComparator = optional.get();
                }
            }

            this.activeSortColumn = column;

            this.refreshFilteredEntries();
        }
    }

    @Nullable
    protected Comparator<DATATYPE> getComparator()
    {
        return this.activeListSortComparator;
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
    protected void fetchCurrentEntries()
    {
        if (this.fetchFromSupplierOnRefresh)
        {
            this.currentContents.clear();
            this.currentContents.addAll(this.entrySupplier.get());
        }
    }

    @Override
    protected void reAddFilteredEntries()
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
    }

    protected void addNonFilteredContents(List<DATATYPE> entries)
    {
        this.filteredContents.addAll(entries);
    }

    protected void addFilteredContents(List<DATATYPE> entries)
    {
        List<String> searchTerms = this.getSearchTerms();
        final int size = entries.size();

        for (int i = 0; i < size; ++i)
        {
            DATATYPE entry = entries.get(i);

            if (this.entryMatchesFilter(entry, searchTerms))
            {
                this.filteredContents.add(entry);
                this.filteredIndices.add(i);
            }
        }
    }

    protected List<String> getSearchTerms()
    {
        return Arrays.asList(this.getFilterText().split("\\|"));
    }

    protected boolean entryMatchesFilter(DATATYPE entry, List<String> searchTerms)
    {
        return searchTerms.isEmpty() || this.entryFilter.matches(entry, searchTerms);
    }

    protected boolean defaultEntryFilter(DATATYPE entry, List<String> searchTerms)
    {
        List<String> entrySearchTerms = this.entrySearchStringFunction.apply(entry);

        for (String entrySearchTerm : entrySearchTerms)
        {
            for (String searchTerm : searchTerms)
            {
                if (entrySearchTerm.toLowerCase(Locale.ROOT).contains(searchTerm))
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void onEntriesRefreshed()
    {
        super.onEntriesRefreshed();

        this.clearWidgetInitializer();
    }

    @Override
    protected void onListEntryWidgetsCreated()
    {
        super.onListEntryWidgetsCreated();

        this.createWidgetInitializer();
        this.applyWidgetInitializer();
    }

    protected void clearWidgetInitializer()
    {
        this.widgetInitializer = null;
    }

    protected void reInitializeWidgets()
    {
        this.clearWidgetInitializer();
        this.createWidgetInitializer();
        this.applyWidgetInitializer();
    }

    @SuppressWarnings("unchecked")
    protected void createWidgetInitializer()
    {
        if (this.widgetInitializer == null && this.entryWidgets.isEmpty() == false)
        {
            BaseDataListEntryWidget<DATATYPE> widget = this.entryWidgets.get(0);
            this.widgetInitializer = (Consumer<BaseDataListEntryWidget<DATATYPE>>) widget.createWidgetInitializer(this.getFilteredEntries());
        }
    }

    protected void applyWidgetInitializer()
    {
        if (this.widgetInitializer != null)
        {
            for (BaseDataListEntryWidget<DATATYPE> widget : this.getEntryWidgetList())
            {
                this.widgetInitializer.accept(widget);
            }
        }
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

    public void clearSelection()
    {
        if (this.getEntrySelectionHandler() != null)
        {
            this.getEntrySelectionHandler().clearSelection();
        }
    }

    public boolean clickEntry(int listIndex)
    {
        if (this.getEntrySelectionHandler() != null)
        {
            this.getEntrySelectionHandler().clickEntry(listIndex);
            return true;
        }

        return false;
    }

    @Override
    protected int getKeyboardNavigationIndex()
    {
        if (this.getEntrySelectionHandler() != null)
        {
            return this.getEntrySelectionHandler().getKeyboardNavigationIndex();
        }

        return super.getKeyboardNavigationIndex();
    }

    @Override
    public void setKeyboardNavigationIndex(int listIndex)
    {
        if (this.getEntrySelectionHandler() != null)
        {
            this.getEntrySelectionHandler().setKeyboardNavigationIndex(listIndex);
        }
        else
        {
            super.setKeyboardNavigationIndex(listIndex);
        }
    }

    @Override
    public void toggleKeyboardNavigationPositionSelection()
    {
        if (this.getEntrySelectionHandler() != null)
        {
            this.getEntrySelectionHandler().toggleKeyboardNavigationPositionSelection();
        }
    }

    public interface EntryFilter<T>
    {
        boolean matches(T entry, List<String> searchTerms);
    }
}

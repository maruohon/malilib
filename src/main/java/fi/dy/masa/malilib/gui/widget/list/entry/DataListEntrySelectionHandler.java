package fi.dy.masa.malilib.gui.widget.list.entry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class DataListEntrySelectionHandler<DATATYPE>
{
    protected final ArrayList<DATATYPE> selectedEntries = new ArrayList<>();
    protected final Supplier<List<DATATYPE>> dataListSupplier;

    @Nullable protected SelectionListener<DATATYPE> selectionListener;
    @Nullable protected DATATYPE lastSelectedEntry;
    protected boolean allowMultiSelection;
    protected int lastSelectedEntryIndex = -1;

    public DataListEntrySelectionHandler(Supplier<List<DATATYPE>> dataListSupplier)
    {
        this(dataListSupplier, false);
    }

    public DataListEntrySelectionHandler(Supplier<List<DATATYPE>> dataListSupplier, boolean allowMultiSelection)
    {
        this.dataListSupplier = dataListSupplier;
        this.allowMultiSelection = allowMultiSelection;
    }

    public DataListEntrySelectionHandler<DATATYPE> setSelectionListener(SelectionListener<DATATYPE> listener)
    {
        this.selectionListener = listener;
        return this;
    }

    public DataListEntrySelectionHandler<DATATYPE> setAllowMultiSelection(boolean allowMultiSelection)
    {
        this.allowMultiSelection = allowMultiSelection;
        return this;
    }

    @Nullable
    public SelectionListener<DATATYPE> getSelectionListener()
    {
        return this.selectionListener;
    }

    public int getLastSelectedEntryIndex()
    {
        return this.lastSelectedEntryIndex;
    }

    @Nullable
    public DATATYPE getLastSelectedEntry()
    {
        return this.lastSelectedEntry;
    }

    public List<DATATYPE> getSelectedEntries()
    {
        return this.selectedEntries;
    }

    public boolean isEntrySelected(@Nullable DATATYPE entry)
    {
        if (entry == null)
        {
            return false;
        }

        return this.allowMultiSelection ? this.selectedEntries.contains(entry) : entry.equals(this.getLastSelectedEntry());
    }

    public void clearSelection()
    {
        this.lastSelectedEntryIndex = -1;
        this.lastSelectedEntry = null;
        this.selectedEntries.clear();
    }

    public void setLastSelectedEntry(int listIndex)
    {
        List<DATATYPE> dataList = this.dataListSupplier.get();

        if (listIndex >= 0 && listIndex < dataList.size())
        {
            this.lastSelectedEntryIndex = listIndex;
        }
        else
        {
            this.lastSelectedEntryIndex = -1;
        }

        @Nullable DATATYPE entry = null;

        if (this.lastSelectedEntryIndex >= 0 && this.lastSelectedEntryIndex < dataList.size())
        {
            entry = dataList.get(this.lastSelectedEntryIndex);
        }

        this.lastSelectedEntry = entry;

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

        if (this.selectionListener != null)
        {
            this.selectionListener.onSelectionChange(entry);
        }
    }
}

package malilib.gui.widget.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;

import malilib.gui.BaseScreen;

public class DataListEntrySelectionHandler<DATATYPE>
{
    protected final Set<Integer> selectedEntryIndices = new HashSet<>();
    protected final Supplier<List<DATATYPE>> dataListSupplier;

    @Nullable protected List<DATATYPE> selectedEntries;
    @Nullable protected SelectionListener<DATATYPE> selectionListener;
    @Nullable protected DATATYPE lastSelectedEntry;
    protected boolean allowMultiSelection;
    protected boolean allowSelection;
    protected boolean modifierKeyMultiSelection;
    protected int lastClickedEntryIndex = -1;
    protected int lastSelectedEntryIndex = -1;
    protected int keyboardNavigationEntryIndex = -1;

    public DataListEntrySelectionHandler(Supplier<List<DATATYPE>> dataListSupplier)
    {
        this(dataListSupplier, false);
    }

    public DataListEntrySelectionHandler(Supplier<List<DATATYPE>> dataListSupplier, boolean allowMultiSelection)
    {
        this.dataListSupplier = dataListSupplier;
        this.allowMultiSelection = allowMultiSelection;
    }

    public DataListEntrySelectionHandler<DATATYPE> setSelectionListener(@Nullable SelectionListener<DATATYPE> listener)
    {
        this.selectionListener = listener;
        return this;
    }

    public DataListEntrySelectionHandler<DATATYPE> setAllowMultiSelection(boolean allowMultiSelection)
    {
        this.allowMultiSelection = allowMultiSelection;
        return this;
    }

    public DataListEntrySelectionHandler<DATATYPE> setModifierKeyMultiSelection(boolean modifierKeyMultiSelection)
    {
        this.modifierKeyMultiSelection = modifierKeyMultiSelection;
        return this;
    }

    public DataListEntrySelectionHandler<DATATYPE> setAllowSelection(boolean allowSelection)
    {
        this.allowSelection = allowSelection;

        if (allowSelection == false)
        {
            this.clearSelection();
        }

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

    public int getKeyboardNavigationIndex()
    {
        return this.keyboardNavigationEntryIndex;
    }

    @Nullable
    public DATATYPE getLastSelectedEntry()
    {
        return this.lastSelectedEntry;
    }

    public List<DATATYPE> getDataList()
    {
        return this.dataListSupplier.get();
    }

    public List<DATATYPE> getSelectedEntries()
    {
        if (this.selectedEntries == null)
        {
            this.selectedEntries = this.rebuildSelectedEntries();
        }

        return this.selectedEntries;
    }

    public Set<Integer> getSelectedEntryIndices()
    {
        return this.selectedEntryIndices;
    }

    public int getSelectedEntryCount()
    {
        return this.selectedEntryIndices.size();
    }

    public int getListSize()
    {
        return this.getDataList().size();
    }

    public boolean isNonModifierMultiSelection()
    {
        return this.allowSelection && this.allowMultiSelection && this.modifierKeyMultiSelection == false;
    }

    public boolean isEntrySelected(@Nullable DATATYPE entry)
    {
        if (entry == null)
        {
            return false;
        }

        Collection<DATATYPE> selectedEntries = this.getSelectedEntries();
        return this.allowMultiSelection ? selectedEntries.contains(entry) : entry.equals(this.getLastSelectedEntry());
    }

    public boolean isEntrySelected(int listIndex)
    {
        return this.allowMultiSelection ? this.selectedEntryIndices.contains(listIndex) : listIndex == this.lastSelectedEntryIndex;
    }

    /**
     * Selects exactly one entry, if the given entry is not null and can be found on the list.
     * If the given entry is null, then the selection is cleared.
     */
    public void setSelectedEntry(@Nullable DATATYPE entry)
    {
        if (entry != null)
        {
            int index = this.getDataList().indexOf(entry);
            this.setSelectedEntryByIndex(index);
        }
        else
        {
            this.clearSelection();
        }
    }

    /**
     * Selects exactly one entry, if the given index is valid, otherwise the selection is cleared.
     */
    public void setSelectedEntryByIndex(int listIndex)
    {
        this.clearSelection();

        if (listIndex >= 0 && listIndex < this.getListSize())
        {
            this.selectedEntryIndices.add(listIndex);
        }
    }

    /**
     * Set the selected entries by finding their indices from the list.
     * Note that this will only select the first occurrence of each entry if selectAllMatches is false,
     * and it will select all occurrences if it's true.
     * If the backing list contains multiple identical entries, and you want to select
     * more than one but not all of a given equal-considered entry, then you need to use the
     * {@link #setSelectedEntriesByIndices(it.unimi.dsi.fastutil.ints.AbstractIntCollection)}
     * method instead to select them by the indices.
     */
    public void setSelectedEntries(Collection<DATATYPE> entries, boolean selectAllMatches)
    {
        this.clearSelection();

        List<DATATYPE> list = this.getDataList();
        HashSet<DATATYPE> set = new HashSet<>(list);
        final int size = list.size();

        for (DATATYPE e : entries)
        {
            if (set.contains(e) == false)
            {
                continue;
            }

            int index = list.indexOf(e);

            if (index >= 0)
            {
                this.selectedEntryIndices.add(index);

                if (selectAllMatches)
                {
                    for (int i = index + 1; i < size; ++i)
                    {
                        if (Objects.equals(list.get(i), e))
                        {
                            this.selectedEntryIndices.add(i);
                        }
                    }
                }
            }
        }
    }

    /**
     * Sets the selection to the given list of indices (for any indices on the list that are in the valid range).
     * The old selection is cleared first.
     */
    public void setSelectedEntriesByIndices(AbstractIntCollection indices)
    {
        this.clearSelection();

        int listSize = this.getListSize();

        for (int index : indices)
        {
            if (index >= 0 && index < listSize)
            {
                this.selectedEntryIndices.add(index);
            }
        }
    }

    public void notifyListener()
    {
        if (this.selectionListener != null)
        {
            this.selectionListener.onSelectionChange(this.lastSelectedEntry);
        }
    }

    public void clearSelection()
    {
        this.lastSelectedEntryIndex = -1;
        this.lastClickedEntryIndex = -1;
        this.lastSelectedEntry = null;
        this.selectedEntries = null;
        this.selectedEntryIndices.clear();
    }

    public void setKeyboardNavigationIndex(int listIndex)
    {
        if (listIndex >= -1 && listIndex < this.getListSize())
        {
            this.keyboardNavigationEntryIndex = listIndex;
        }
    }

    public void toggleKeyboardNavigationPositionSelection()
    {
        if (this.keyboardNavigationEntryIndex >= 0 &&
            this.keyboardNavigationEntryIndex < this.getListSize())
        {
            this.selectEntry(this.keyboardNavigationEntryIndex);
        }
    }

    public void clickEntry(int listIndex)
    {
        if (listIndex >= 0 && listIndex < this.getListSize())
        {
            this.selectEntry(listIndex);
            this.lastClickedEntryIndex = listIndex;
        }
    }

    protected void selectEntry(int listIndex)
    {
        if (this.allowSelection == false)
        {
            return;
        }

        if (this.modifierKeyMultiSelection &&
            BaseScreen.isCtrlDown() == false &&
            BaseScreen.isShiftDown() == false)
        {
            this.clearSelection();
        }

        List<DATATYPE> dataList = this.getDataList();
        boolean validIndex = listIndex >= 0 && listIndex < dataList.size();
        boolean unselect = validIndex == false ||
                           listIndex == this.lastSelectedEntryIndex ||
                           this.selectedEntryIndices.contains(listIndex);

        @Nullable DATATYPE entry = validIndex ? dataList.get(listIndex) : null;

        if (validIndex &&
            this.doModifierKeyMultiSelection(listIndex, dataList) == false &&
            this.allowMultiSelection && entry != null)
        {
            if (this.selectedEntryIndices.contains(listIndex))
            {
                this.selectedEntryIndices.remove(listIndex);
            }
            else if (unselect == false)
            {
                this.selectedEntryIndices.add(listIndex);
            }

            this.selectedEntries = null; // re-build
        }

        this.lastSelectedEntryIndex = unselect ? -1 : listIndex;
        this.lastSelectedEntry = unselect ? null : entry;

        this.notifyListener();
    }

    protected boolean doModifierKeyMultiSelection(int clickedIndex, List<DATATYPE> dataList)
    {
        int lastClickedIndex = this.lastClickedEntryIndex;

        if (this.allowMultiSelection && BaseScreen.isShiftDown() &&
            lastClickedIndex >= 0 && clickedIndex >= 0)
        {
            int min = Math.min(lastClickedIndex, clickedIndex);
            int max = Math.max(lastClickedIndex, clickedIndex);
            max = Math.min(max, dataList.size() - 1);

            // Don't toggle the previously clicked/selected entry,
            // to be able to toggle ranges properly without inverting the starting end again.
            if (lastClickedIndex < clickedIndex)
            {
                ++min;
            }
            else
            {
                --max;
            }

            for (int i = min; i <= max; ++i)
            {
                if (this.selectedEntryIndices.contains(i))
                {
                    this.selectedEntryIndices.remove(i);
                }
                else
                {
                    this.selectedEntryIndices.add(i);
                }
            }

            this.selectedEntries = null; // re-build

            return true;
        }

        return false;
    }

    protected List<DATATYPE> rebuildSelectedEntries()
    {
        List<DATATYPE> selectedEntries = new ArrayList<>();
        List<DATATYPE> dataList = this.getDataList();
        IntArrayList indexList = new IntArrayList(this.selectedEntryIndices);
        final int size = dataList.size();

        indexList.sort(IntComparators.NATURAL_COMPARATOR);

        for (int index : indexList)
        {
            if (index >= 0 && index < size)
            {
                DATATYPE val = dataList.get(index);

                if (val != null)
                {
                    selectedEntries.add(val);
                }
            }
        }

        return selectedEntries;
    }
}

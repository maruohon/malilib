package fi.dy.masa.malilib.gui.widget.list.entry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.BaseScreen;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

public class DataListEntrySelectionHandler<DATATYPE>
{
    protected final Set<DATATYPE> selectedEntries = new HashSet<>();
    protected final IntOpenHashSet selectedEntryIndices = new IntOpenHashSet();
    protected final Supplier<List<DATATYPE>> dataListSupplier;

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

    public Set<DATATYPE> getSelectedEntries()
    {
        return this.selectedEntries;
    }

    public Set<Integer> getSelectedEntryIndices()
    {
        return this.selectedEntryIndices;
    }

    public int getListSize()
    {
        return this.dataListSupplier.get().size();
    }

    public boolean isEntrySelected(@Nullable DATATYPE entry)
    {
        if (entry == null)
        {
            return false;
        }

        return this.allowMultiSelection ? this.selectedEntries.contains(entry) : entry.equals(this.getLastSelectedEntry());
    }

    public boolean isEntrySelected(int listIndex)
    {
        return this.allowMultiSelection ? this.selectedEntryIndices.contains(listIndex) : listIndex == this.lastSelectedEntryIndex;
    }

    public void setSelectedEntry(@Nullable DATATYPE entry)
    {
        if (entry != null)
        {
            int index = this.dataListSupplier.get().indexOf(entry);

            if (index >= 0)
            {
                this.setSelectedEntry(index);
            }
        }
    }

    public void setSelectedEntry(int listIndex)
    {
        this.setSelectedEntries(Collections.singletonList(listIndex));
    }

    public void setSelectedEntries(Collection<Integer> indices)
    {
        this.clearSelection();

        List<DATATYPE> dataList = this.dataListSupplier.get();
        int listSize = dataList.size();

        for (int index : indices)
        {
            if (index >= 0 && index < listSize)
            {
                @Nullable DATATYPE entry = dataList.get(index);

                if (entry != null)
                {
                    this.selectedEntryIndices.add(index);
                    this.selectedEntries.add(entry);
                }
            }
        }
    }

    public void clearSelection()
    {
        this.lastSelectedEntryIndex = -1;
        this.lastClickedEntryIndex = -1;
        this.lastSelectedEntry = null;
        this.selectedEntries.clear();
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

        boolean unselect = listIndex == this.lastSelectedEntryIndex || this.selectedEntryIndices.contains(listIndex);
        List<DATATYPE> dataList = this.dataListSupplier.get();
        @Nullable DATATYPE entry = dataList.get(listIndex);

        if (this.doModifierKeyMultiSelection(listIndex, dataList) == false &&
            this.allowMultiSelection && entry != null)
        {
            if (this.selectedEntryIndices.contains(listIndex))
            {
                this.selectedEntries.remove(entry);
                this.selectedEntryIndices.remove(listIndex);
            }
            else if (unselect == false)
            {
                this.selectedEntries.add(entry);
                this.selectedEntryIndices.add(listIndex);
            }
        }

        this.lastSelectedEntryIndex = unselect ? -1 : listIndex;
        this.lastSelectedEntry = unselect ? null : entry;

        if (this.selectionListener != null)
        {
            this.selectionListener.onSelectionChange(entry);
        }
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
                DATATYPE val = dataList.get(i);

                if (this.selectedEntryIndices.contains(i))
                {
                    this.selectedEntries.remove(val);
                    this.selectedEntryIndices.remove(i);
                }
                else
                {
                    this.selectedEntries.add(val);
                    this.selectedEntryIndices.add(i);
                }
            }

            return true;
        }

        return false;
    }
}

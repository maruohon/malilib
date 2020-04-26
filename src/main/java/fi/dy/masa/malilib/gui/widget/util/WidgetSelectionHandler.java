package fi.dy.masa.malilib.gui.widgets.util;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;

public class WidgetSelectionHandler<TYPE>
{
    protected final Set<TYPE> selectedEntries = new HashSet<>();

    @Nullable private ISelectionListener<TYPE> selectionListener;
    @Nullable private TYPE lastSelectedEntry;
    protected int lastSelectedEntryIndex = -1;
    protected boolean allowMultiSelection;

    public WidgetSelectionHandler()
    {
        
    }

    protected WidgetSelectionHandler<TYPE> setSelectionListener(ISelectionListener<TYPE> listener)
    {
        this.selectionListener = listener;
        return this;
    }

    public WidgetSelectionHandler<TYPE> setAllowMultiSelection(boolean allowMultiSelection)
    {
        this.allowMultiSelection = allowMultiSelection;
        return this;
    }

    public int getLastSelectedEntryIndex()
    {
        return this.lastSelectedEntryIndex;
    }

    @Nullable
    public TYPE getLastSelectedEntry()
    {
        return this.lastSelectedEntry;
    }

    public Set<TYPE> getSelectedEntries()
    {
        return this.selectedEntries;
    }

    public boolean isEntrySelected(TYPE entry)
    {
        return this.allowMultiSelection ? this.selectedEntries.contains(entry) : entry != null && entry.equals(this.getLastSelectedEntry());
    }

    public void clearSelection()
    {
        this.lastSelectedEntryIndex = -1;
        this.lastSelectedEntry = null;
        this.selectedEntries.clear();
    }

    public void setLastSelectedEntry(int listIndex)
    {
        if (listIndex >= 0 && listIndex < this.getTotalEntryCount())
        {
            this.lastSelectedEntryIndex = listIndex;
        }
        else
        {
            this.lastSelectedEntryIndex = -1;
        }

        @Nullable TYPE entry = this.lastSelectedEntryIndex != -1 ? this.listContents.get(this.lastSelectedEntryIndex) : null;
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

        if (entry != null && this.selectionListener != null)
        {
            this.selectionListener.onSelectionChange(entry);
        }
    }
}

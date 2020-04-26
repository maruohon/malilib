package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.util.WidgetSelectionHandler;

public class WidgetDataListBase<TYPE> extends WidgetListBase
{
    protected final List<TYPE> listContents = new ArrayList<>();
    @Nullable protected final Supplier<Collection<TYPE>> entrySupplier;
    @Nullable protected WidgetSelectionHandler<TYPE> widgetSelectionHandler;

    public WidgetDataListBase(int x, int y, int width, int height,
            @Nullable Supplier<Collection<TYPE>> entrySupplier,
            @Nullable ISelectionListener<TYPE> selectionListener)
    {
        super(x, y, width, height);

        this.entrySupplier = entrySupplier;
    }

    public void setWidgetSelectionHandler(WidgetSelectionHandler<TYPE> handler)
    {
        this.widgetSelectionHandler = handler;
    }

    @Override
    protected boolean onEntryWidgetClicked(WidgetListEntryBase widget, int mouseX, int mouseY, int mouseButton)
    {
        if (widget.canSelectAt(mouseX, mouseY, mouseButton))
        {
            int listIndex = widget.getListIndex();

            if (listIndex >= 0 && listIndex < this.getTotalEntryCount())
            {
                this.setLastSelectedEntry(listIndex);
            }
        }

        return super.onEntryWidgetClicked(widget, mouseX, mouseY, mouseButton);
    }

    @Override
    protected int getTotalEntryCount()
    {
        return this.listContents.size();
    }

    @Override
    public WidgetSelectionHandler<TYPE> getWidgetSelectionHandler()
    {
        return this.widgetSelectionHandler;
    }

    public List<TYPE> getCurrentEntries()
    {
        return this.listContents;
    }

    protected Collection<TYPE> getAllEntries()
    {
        return this.entrySupplier != null ? this.entrySupplier.get() : Collections.emptyList();
    }

    @Nullable
    protected Comparator<TYPE> getComparator()
    {
        return null;
    }

    @Override
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

        if (this.getShouldSortList())
        {
            this.sortEntryList(this.listContents);
        }

        this.reCreateListEntryWidgets();
    }

    protected void sortEntryList(List<TYPE> list)
    {
        Comparator<TYPE> comparator = this.getComparator();

        if (comparator != null)
        {
            Collections.sort(list, comparator);
        }
    }

    protected boolean filterMatchesEmptyEntry(TYPE entry)
    {
        return true;
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

    protected void addNonFilteredContents(Collection<TYPE> entries)
    {
        this.listContents.addAll(entries);
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
    protected void renderWidget(int widgetIndex, int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        WidgetListEntryBase widget = this.listWidgets.get(widgetIndex);
        TYPE entry = this.listContents.get(widget.getListIndex());
        boolean isSelected = this.getWidgetSelectionHandler() != null && this.getWidgetSelectionHandler().isEntrySelected(entry);
        this.listWidgets.get(widgetIndex).render(mouseX, mouseY, isActiveGui, hoveredWidgetId, isSelected);
    }
}

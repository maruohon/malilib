package malilib.gui;

import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import malilib.gui.icon.Icon;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.DataListWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetFactory;
import malilib.gui.widget.list.entry.GenericListEntryWidget;
import malilib.render.text.StyledTextLine;

public abstract class BaseImportExportEntriesListScreen<T> extends BaseListScreen<DataListWidget<T>>
{
    protected final List<T> entries;
    protected final GenericButton deselectAllButton;
    protected final GenericButton selectAllButton;

    @Nullable protected Function<T, String> entryNameFunction;
    @Nullable protected Function<T, Icon> entryIconFunction;
    @Nullable protected Function<T, ImmutableList<StyledTextLine>> hoverInfoFunction;
    @Nullable protected Function<T, List<String>> entryFilterStringFunction;
    @Nullable protected DataListEntryWidgetFactory<T> widgetFactory;
    protected int listEntryWidgetHeight = 16;

    public BaseImportExportEntriesListScreen(int listX, int listY, int totalListMarginX, int totalListMarginY,
                                             List<T> entries)
    {
        super(listX, listY, totalListMarginX, totalListMarginY);

        this.entries = entries;

        this.deselectAllButton = GenericButton.create(16, "malilib.button.export_entries.deselect_all", this::deselectAll);
        this.selectAllButton   = GenericButton.create(16, "malilib.button.export_entries.select_all", this::selectAll);
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.selectAllButton);
        this.addWidget(this.deselectAllButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + this.getListX();
        int y = this.getListWidget().getY() - 18;

        this.selectAllButton.setPosition(x, y);
        this.deselectAllButton.setPosition(this.selectAllButton.getRight() + 2, y);
    }

    protected List<T> getEntryList()
    {
        return this.entries;
    }

    public void setListEntryWidgetHeight(int listEntryWidgetHeight)
    {
        this.listEntryWidgetHeight = listEntryWidgetHeight;
    }

    public void setEntryFilterStringFunction(@Nullable Function<T, List<String>> filterStringFunction)
    {
        this.entryFilterStringFunction = filterStringFunction;
    }

    public void setWidgetFactory(@Nullable DataListEntryWidgetFactory<T> widgetFactory)
    {
        this.widgetFactory = widgetFactory;
    }

    public void setEntryNameFunction(@Nullable Function<T, String> entryNameFunction)
    {
        this.entryNameFunction = entryNameFunction;
    }

    public void setEntryIconFunction(@Nullable Function<T, Icon> entryIconFunction)
    {
        this.entryIconFunction = entryIconFunction;
    }

    public void setHoverInfoFunction(@Nullable Function<T, ImmutableList<StyledTextLine>> hoverInfoFunction)
    {
        this.hoverInfoFunction = hoverInfoFunction;
    }

    protected void selectAll()
    {
        this.getListWidget().getEntrySelectionHandler().setSelectedEntries(this.getEntryList(), true);
    }

    protected void deselectAll()
    {
        this.getListWidget().getEntrySelectionHandler().clearSelection();
    }

    @Override
    protected DataListWidget<T> createListWidget()
    {
        DataListWidget<T> listWidget = new DataListWidget<>(this::getEntryList, true);

        listWidget.addDefaultSearchBar();
        listWidget.getEntrySelectionHandler()
                .setAllowSelection(true)
                .setAllowMultiSelection(true)
                .setModifierKeyMultiSelection(true);

        if (this.entryFilterStringFunction != null)
        {
            listWidget.setEntryFilterStringFunction(this.entryFilterStringFunction);
        }
        else if (this.entryNameFunction != null)
        {
            listWidget.setEntryFilterStringFunction(i -> ImmutableList.of(this.entryNameFunction.apply(i)));
        }

        if (this.widgetFactory != null)
        {
            listWidget.setDataListEntryWidgetFactory(this.widgetFactory);
        }
        else if (this.entryNameFunction != null)
        {
            listWidget.setListEntryWidgetFixedHeight(this.listEntryWidgetHeight);
            listWidget.setDataListEntryWidgetFactory((d, cd) -> new GenericListEntryWidget<>(
                    d, cd, this.entryNameFunction, this.entryIconFunction, this.hoverInfoFunction));
        }

        return listWidget;
    }
}

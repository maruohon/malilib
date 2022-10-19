package malilib.gui;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import com.google.common.collect.ImmutableList;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.DataListWidget;
import malilib.gui.widget.list.entry.StringListEntryWidget;

public class StringListSelectionScreen extends BaseListScreen<DataListWidget<String>>
{
    protected final ImmutableList<String> strings;
    protected final Consumer<Collection<String>> consumer;
    protected final GenericButton confirmButton;
    protected final GenericButton cancelButton;
    protected boolean hasSearch;

    public StringListSelectionScreen(Collection<String> strings, Consumer<Collection<String>> consumer)
    {
        super(10, 30, 20, 58);

        this.strings = ImmutableList.copyOf(strings);
        this.consumer = consumer;

        this.confirmButton = GenericButton.create("malilib.button.misc.ok.caps", this::onConfirm);
        this.cancelButton = GenericButton.create("malilib.button.misc.cancel", this::openParentScreen);
    }

    public void setHasSearch(boolean hasSearch)
    {
        this.hasSearch = hasSearch;
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.confirmButton);
        this.addWidget(this.cancelButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = 12;
        int y = this.getBottom() - 24;

        this.confirmButton.setPosition(x, y);
        this.cancelButton.setPosition(this.confirmButton.getRight() + 6, y);
    }

    protected void onConfirm()
    {
        this.consumer.accept(this.getListWidget().getSelectedEntries());
    }

    public List<String> getStrings()
    {
        return this.strings;
    }

    @Override
    protected DataListWidget<String> createListWidget()
    {
        DataListWidget<String> listWidget = new DataListWidget<>(this::getStrings, false);
        listWidget.setListEntryWidgetFixedHeight(16);
        listWidget.setShouldSortList(true);
        listWidget.setListSortComparator(String::compareTo);
        listWidget.setDataListEntryWidgetFactory(StringListEntryWidget::new);
        listWidget.getEntrySelectionHandler().setAllowSelection(true);
        listWidget.getEntrySelectionHandler().setAllowMultiSelection(true);
        listWidget.getEntrySelectionHandler().setModifierKeyMultiSelection(false);

        if (this.hasSearch)
        {
            listWidget.addDefaultSearchBar();
        }

        return listWidget;
    }
}

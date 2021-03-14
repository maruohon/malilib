package fi.dy.masa.malilib.gui;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;

public class StringListSelectionScreen extends BaseListScreen<DataListWidget<String>>
{
    protected final ImmutableList<String> strings;
    protected final Consumer<Set<String>> consumer;

    public StringListSelectionScreen(Collection<String> strings, Consumer<Set<String>> consumer)
    {
        super(10, 30, 20, 60);

        this.strings = ImmutableList.copyOf(strings);
        this.consumer = consumer;
    }

    public List<String> getStrings()
    {
        return this.strings;
    }

    @Override
    protected DataListWidget<String> createListWidget(int listX, int listY, int listWidth, int listHeight)
    {
        return new DataListWidget<>(listX, listY, listWidth, listHeight, this::getStrings);
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        int x = 12;
        int y = this.height - 32;

        GenericButton button = new GenericButton(x, y, -1, 20, "litematica.gui.button.ok");
        x = button.getRight() + 2;
        this.addButton(button, (btn, mbtn) -> this.consumer.accept(this.getListWidget().getEntrySelectionHandler().getSelectedEntries()));

        button = new GenericButton(x, y, -1, 20, "litematica.gui.button.cancel");
        this.addButton(button, (btn, mbtn) -> BaseScreen.openScreen(this.getParent()));
    }
}

package fi.dy.masa.malilib.gui;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;

public class StringListSelectionScreen extends BaseListScreen<DataListWidget<String>>
{
    protected final ImmutableList<String> strings;
    protected final Consumer<Collection<String>> consumer;
    protected final GenericButton confirmButton;
    protected final GenericButton cancelButton;

    public StringListSelectionScreen(Collection<String> strings, Consumer<Collection<String>> consumer)
    {
        super(10, 30, 20, 60);

        this.strings = ImmutableList.copyOf(strings);
        this.consumer = consumer;

        this.confirmButton = GenericButton.create("malilib.button.misc.ok.caps", this::onConfirm);
        this.cancelButton = GenericButton.create("malilib.button.misc.cancel", this::openParentScreen);
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
        int y = this.height - 32;

        this.confirmButton.setPosition(x, y);
        this.cancelButton.setPosition(this.confirmButton.getRight() + 6, y);
    }

    protected void onConfirm()
    {
        this.consumer.accept(this.getListWidget().getSelectedEntries());
    }
}

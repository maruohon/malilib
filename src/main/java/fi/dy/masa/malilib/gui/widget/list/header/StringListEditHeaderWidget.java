package fi.dy.masa.malilib.gui.widget.list.header;

import java.util.List;
import fi.dy.masa.malilib.gui.icon.BaseIcon;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListHeaderWidget;

public class StringListEditHeaderWidget extends DataListHeaderWidget<String>
{
    protected final List<String> stringList;
    protected final GenericButton addButton;

    public StringListEditHeaderWidget(int x, int y, int width, int height, DataListWidget<String> listWidget)
    {
        super(x, y, 15, 15, listWidget);

        // This is a reference to the current entries list, which can be modified
        this.stringList = listWidget.getCurrentEntries();

        this.addButton = new GenericButton(x, y, BaseIcon.PLUS, "malilib.gui.button.hover.list.add_first");
        this.addButton.setRenderOutline(true);
        this.addButton.setActionListener((btn, mbtn) -> this.insertEntry());
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.addButton);
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        this.addButton.setPosition(this.getX(), this.getY());
    }

    protected void insertEntry()
    {
        this.stringList.add(0, "");
        this.listWidget.refreshEntries();
        this.listWidget.focusWidget(0);
    }
}

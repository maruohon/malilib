package fi.dy.masa.malilib.gui.widget.list.header;

import java.util.List;
import java.util.function.Supplier;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;

public class BaseDataListEditHeaderWidget<DATATYPE> extends DataListHeaderWidget<DATATYPE>
{
    protected final List<DATATYPE> dataList;
    protected final Supplier<DATATYPE> dataFactory;
    protected final GenericButton addButton;
    protected int fixedWidth;
    protected int fixedHeight;

    public BaseDataListEditHeaderWidget(DataListWidget<DATATYPE> listWidget,
                                        String buttonHover,
                                        Supplier<DATATYPE> dataFactory)
    {
        this(15, 15, listWidget, buttonHover, dataFactory);
    }

    public BaseDataListEditHeaderWidget(int width, int height,
                                        DataListWidget<DATATYPE> listWidget,
                                        String buttonHover,
                                        Supplier<DATATYPE> dataFactory)
    {
        super(width, height, listWidget);

        this.fixedWidth = width > 0 ? width : -1;
        this.fixedHeight = height > 0 ? height : -1;

        // This is a reference to the current entries list, which can be modified
        this.dataList = listWidget.getNonFilteredDataList();
        this.dataFactory = dataFactory;

        this.addButton = GenericButton.create(DefaultIcons.LIST_ADD_PLUS_13, this::insertEntry);
        this.addButton.translateAndAddHoverString(buttonHover);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();
        this.addWidget(this.addButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();
        this.addButton.setPosition(this.getX(), this.getY());
    }

    @Override
    public void setHeaderWidgetSize(int width, int height)
    {
        width = this.fixedWidth > 0 ? this.fixedWidth : (width > 0 ? width : this.getWidth());
        height = this.fixedHeight > 0 ? this.fixedHeight : (height > 0 ? height : this.getHeight());

        this.setSize(width, height);
    }

    protected DATATYPE getNewDataEntry()
    {
        return this.dataFactory.get();
    }

    protected void insertEntry()
    {
        this.dataList.add(0, this.getNewDataEntry());
        this.listWidget.refreshEntries();
        this.listWidget.focusWidget(0);
    }
}

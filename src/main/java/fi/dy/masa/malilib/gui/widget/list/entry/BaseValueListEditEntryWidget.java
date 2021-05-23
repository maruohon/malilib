package fi.dy.masa.malilib.gui.widget.list.entry;

import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget.IconWidgetFactory;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;

public class BaseValueListEditEntryWidget<TYPE> extends BaseOrderableListEditEntryWidget<TYPE>
{
    protected final TYPE defaultValue;
    protected final TYPE initialValue;
    protected final DropDownListWidget<TYPE> dropDownWidget;
    protected final GenericButton resetButton;

    public BaseValueListEditEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                        TYPE initialValue, TYPE defaultValue, DataListWidget<TYPE> parent,
                                        List<TYPE> possibleValues,
                                        Function<TYPE, String> toStringConverter,
                                        @Nullable IconWidgetFactory<TYPE> iconWidgetFactory)
    {
        super(x, y, width, height, listIndex, originalListIndex, initialValue, parent);

        this.defaultValue = defaultValue;
        this.initialValue = initialValue;
        this.newEntryFactory = () -> this.defaultValue;

        this.labelWidget = new LabelWidget(0xC0C0C0C0, String.format("%3d:", originalListIndex + 1));

        this.resetButton = new GenericButton(16, "malilib.gui.button.reset.caps");
        this.resetButton.setRenderButtonBackgroundTexture(false);
        this.resetButton.setRenderNormalBorder(true);
        this.resetButton.setNormalBorderColor(0xFF404040);
        this.resetButton.setTextColorDisabled(0xFF505050);

        int ddWidth = width - this.resetButton.getWidth() - this.labelWidget.getWidth()
                            - this.addButton.getWidth() - this.removeButton.getWidth()
                            - this.upButton.getWidth() - this.downButton.getWidth() - 20;
        this.dropDownWidget = new DropDownListWidget<>(-ddWidth, 18, 216, 12, possibleValues, toStringConverter, iconWidgetFactory);

        this.dropDownWidget.setSelectedEntry(this.initialValue);
        this.dropDownWidget.setSelectionListener((entry) -> {
            if (this.originalListIndex < this.dataList.size())
            {
                this.dataList.set(this.originalListIndex, entry);
            }

            this.resetButton.setEnabled(this.defaultValue.equals(entry) == false);
        });

        this.resetButton.setEnabled(initialValue.equals(this.defaultValue) == false);
        this.resetButton.setActionListener(() -> {
            this.dropDownWidget.setSelectedEntry(this.defaultValue);

            if (this.originalListIndex < this.dataList.size())
            {
                this.dataList.set(this.originalListIndex, this.defaultValue);
            }

            this.resetButton.setEnabled(this.defaultValue.equals(this.dropDownWidget.getSelectedEntry()) == false);
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.dropDownWidget);
        this.addWidget(this.resetButton);
    }

    @Override
    protected void updateSubWidgetsToGeometryChangesPre(int x, int y)
    {
        int lx = this.getX();
        this.labelWidget.setPosition(lx + 2, y + 6);
        this.dropDownWidget.setPosition(x, y + 1);
        this.nextWidgetX = this.dropDownWidget.getRight() + 2;
    }

    @Override
    protected void updateSubWidgetsToGeometryChangesPost(int x, int y)
    {
        this.resetButton.setPosition(x, y + 2);
    }
}

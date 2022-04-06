package fi.dy.masa.malilib.gui.widget.list.entry;

import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.value.HorizontalAlignment;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget.IconWidgetFactory;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;

public class BaseValueListEditEntryWidget<DATATYPE> extends BaseOrderableListEditEntryWidget<DATATYPE>
{
    protected final DATATYPE defaultValue;
    protected final DATATYPE initialValue;
    protected final DropDownListWidget<DATATYPE> dropDownWidget;
    protected final GenericButton resetButton;

    public BaseValueListEditEntryWidget(DATATYPE initialValue,
                                        DataListEntryWidgetData constructData,
                                        DATATYPE defaultValue,
                                        List<DATATYPE> possibleValues,
                                        Function<DATATYPE, String> toStringConverter,
                                        @Nullable IconWidgetFactory<DATATYPE> iconWidgetFactory)
    {
        super(initialValue, constructData);

        this.defaultValue = defaultValue;
        this.initialValue = initialValue;
        this.newEntryFactory = () -> this.defaultValue;

        this.labelWidget = new LabelWidget(0xC0C0C0C0, String.format("%3d:", this.originalListIndex + 1));
        this.labelWidget.setAutomaticWidth(false);
        this.labelWidget.setWidth(24);
        this.labelWidget.setHorizontalAlignment(HorizontalAlignment.RIGHT);

        this.resetButton = GenericButton.create(16, "malilib.button.misc.reset.caps");
        this.resetButton.getBorderRenderer().getNormalSettings().setBorderWidthAndColor(1, 0xFF404040);

        this.resetButton.setRenderButtonBackgroundTexture(false);
        this.resetButton.setDisabledTextColor(0xFF505050);

        int ddWidth = this.getWidth() - this.resetButton.getWidth() - this.labelWidget.getWidth()
                            - this.addButton.getWidth() - this.removeButton.getWidth()
                            - this.upButton.getWidth() - this.downButton.getWidth() - 20;
        this.dropDownWidget = new DropDownListWidget<>(18, 12, possibleValues, toStringConverter, iconWidgetFactory);

        this.dropDownWidget.setMaxWidth(ddWidth);
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
        this.labelWidget.setPosition(this.getX() + 2, y + 6);
        this.dropDownWidget.setPosition(x, y + 1);
        this.nextWidgetX = this.dropDownWidget.getRight() + 2;
    }

    @Override
    protected void updateSubWidgetsToGeometryChangesPost(int x, int y)
    {
        this.resetButton.setPosition(x, y + 2);
    }
}

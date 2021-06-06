package fi.dy.masa.malilib.gui.widget.list.entry;

import java.util.function.Function;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;

public abstract class BaseStringListEditEntryWidget<TYPE> extends BaseOrderableListEditEntryWidget<TYPE>
{
    protected final TYPE defaultValue;
    protected final TYPE initialValue;
    protected final Function<TYPE, String> toStringConverter;
    protected final Function<String, TYPE> fromStringConverter;
    protected final BaseTextFieldWidget textField;
    protected final GenericButton resetButton;

    public BaseStringListEditEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                         TYPE initialValue, TYPE defaultValue,
                                         Function<TYPE, String> toStringConverter,
                                         Function<String, TYPE> fromStringConverter,
                                         DataListWidget<TYPE> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, initialValue, listWidget);

        this.defaultValue = defaultValue;
        this.initialValue = initialValue;
        this.toStringConverter = toStringConverter;
        this.fromStringConverter = fromStringConverter;

        int textFieldWidth = width - 142;

        this.labelWidget = new LabelWidget(0xC0C0C0C0, String.format("%5d:", originalListIndex + 1));
        this.textField = new BaseTextFieldWidget(textFieldWidth, 16, toStringConverter.apply(initialValue));
        this.textField.setShowCursorPosition(true);

        this.resetButton = new GenericButton(16, "malilib.gui.button.reset.caps");
        this.resetButton.setRenderButtonBackgroundTexture(false);
        this.resetButton.setRenderNormalBorder(true);
        this.resetButton.setNormalBorderColor(0xFF404040);
        this.resetButton.setDisabledTextColor(0xFF505050);

        this.resetButton.setEnabled(initialValue.equals(this.defaultValue) == false);
        this.resetButton.setActionListener(() -> {
            this.textField.setText(this.toStringConverter.apply(this.defaultValue));

            if (this.originalListIndex < this.dataList.size())
            {
                this.dataList.set(this.originalListIndex, this.defaultValue);
            }

            this.resetButton.setEnabled(this.textField.getText().equals(this.toStringConverter.apply(this.defaultValue)) == false);
        });

        this.textField.setUpdateListenerAlways(true);
        this.textField.setListener((newText) -> {
            if (this.originalListIndex < this.dataList.size())
            {
                TYPE value = this.fromStringConverter.apply(newText);

                if (value != null)
                {
                    this.dataList.set(this.originalListIndex, value);
                }
            }

            this.resetButton.setEnabled(newText.equals(this.toStringConverter.apply(this.defaultValue)) == false);
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.textField);
        this.addWidget(this.resetButton);
    }

    @Override
    public void focusWidget()
    {
        this.textField.setFocused(true);
    }

    @Override
    protected void updateSubWidgetsToGeometryChangesPre(int x, int y)
    {
        int lx = this.getX();
        this.labelWidget.setPosition(lx + 2, y + 6);
        this.textField.setPosition(x, y + 2);
        this.nextWidgetX = this.textField.getRight() + 2;
        this.draggableRegionEndX = x - 1;
    }

    @Override
    protected void updateSubWidgetsToGeometryChangesPost(int x, int y)
    {
        this.resetButton.setPosition(x, y + 2);
    }
}

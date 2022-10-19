package malilib.gui.widget.list.entry;

import java.util.function.Function;
import malilib.gui.widget.BaseTextFieldWidget;
import malilib.gui.widget.LabelWidget;
import malilib.gui.widget.button.GenericButton;

public abstract class BaseStringListEditEntryWidget<DATATYPE> extends BaseOrderableListEditEntryWidget<DATATYPE>
{
    protected final DATATYPE defaultValue;
    protected final DATATYPE initialValue;
    protected final Function<DATATYPE, String> toStringConverter;
    protected final Function<String, DATATYPE> fromStringConverter;
    protected final BaseTextFieldWidget textField;
    protected final GenericButton resetButton;

    public BaseStringListEditEntryWidget(DATATYPE initialValue,
                                         DataListEntryWidgetData constructData,
                                         DATATYPE defaultValue,
                                         Function<DATATYPE, String> toStringConverter,
                                         Function<String, DATATYPE> fromStringConverter)
    {
        super(initialValue, constructData);

        this.defaultValue = defaultValue;
        this.initialValue = initialValue;
        this.toStringConverter = toStringConverter;
        this.fromStringConverter = fromStringConverter;

        int textFieldWidth = this.getWidth() - 142;

        this.labelWidget = new LabelWidget(0xC0C0C0C0, String.format("%5d:", this.originalListIndex + 1));
        this.textField = new BaseTextFieldWidget(textFieldWidth, 16, toStringConverter.apply(initialValue));
        this.textField.setShowCursorPosition(true);

        this.resetButton = GenericButton.create(16, "malilib.button.misc.reset.caps");
        this.resetButton.getBorderRenderer().getNormalSettings().setBorderWidthAndColor(1, 0xFF404040);
        this.resetButton.setRenderButtonBackgroundTexture(false);
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

        this.textField.setListener(this::onTextChanged);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.textField);
        this.addWidget(this.resetButton);
    }

    @Override
    protected void updateSubWidgetsToGeometryChangesPre(int x, int y)
    {
        this.labelWidget.setPosition(this.getX() + 2, y + 6);
        this.textField.setPosition(x, y + 2);
        this.nextWidgetX = this.textField.getRight() + 2;
        this.draggableRegionEndX = x - 1;
    }

    @Override
    protected void updateSubWidgetsToGeometryChangesPost(int x, int y)
    {
        this.resetButton.setPosition(x, y + 2);
    }

    @Override
    public void focusWidget()
    {
        this.textField.setFocused(true);
    }

    protected void onTextChanged(String newText)
    {
        if (this.originalListIndex < this.dataList.size())
        {
            DATATYPE value = this.fromStringConverter.apply(newText);

            if (value != null)
            {
                this.dataList.set(this.originalListIndex, value);
            }
        }

        this.resetButton.setEnabled(newText.equals(this.toStringConverter.apply(this.defaultValue)) == false);
    }
}

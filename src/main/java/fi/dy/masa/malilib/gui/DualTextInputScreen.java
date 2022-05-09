package fi.dy.masa.malilib.gui;

import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.render.text.StyledText;

public class DualTextInputScreen extends BaseTextInputScreen
{
    protected final BiFunction<String, String, Boolean> stringConsumer;
    protected final LabelWidget labelWidget2;
    protected final BaseTextFieldWidget textField2;
    protected final String originalText2;

    public DualTextInputScreen(String titleKey,
                               String defaultText1,
                               String defaultText2,
                               BiFunction<String, String, Boolean> stringConsumer)
    {
        super(titleKey, defaultText1);

        this.baseHeight = 120;
        this.stringConsumer = stringConsumer;
        this.originalText2 = defaultText2;

        this.labelWidget2 = new LabelWidget();
        this.textField2 = new BaseTextFieldWidget(240, 20, this.originalText2);
    }

    public DualTextInputScreen(String titleKey,
                               String labelKey1,
                               String labelKey2,
                               String defaultText1,
                               String defaultText2,
                               BiFunction<String, String, Boolean> stringConsumer,
                               @Nullable Screen parent)
    {
        this(titleKey, defaultText1, defaultText2, stringConsumer);

        this.setLabelText(labelKey1);
        this.labelWidget2.setLabelText(labelKey2);
        this.setParent(parent);
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.labelWidget2);
        this.addWidget(this.textField2);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + 10;
        int y = this.textField.getBottom() + 6;

        this.labelWidget2.setPosition(x, y);
        this.textField2.setPosition(x, y + 12);

        y = this.textField2.getBottom() + 6;
        this.okButton.setY(y);
        this.resetButton.setY(y);
        this.cancelButton.setY(y);
    }

    public void setLabelText2(String translationKey)
    {
        this.setLabelText2(StyledText.translate(translationKey));
    }

    public void setLabelText2(@Nullable StyledText labelText)
    {
        StyledText text = this.wrapTextToWidth(labelText);

        if (text != null)
        {
            this.labelWidget2.setLabelStyledText(text);
        }
        else
        {
            this.labelWidget2.clearText();
        }

        this.updateHeight();
    }

    @Override
    protected void resetTextFieldToOriginalText()
    {
        super.resetTextFieldToOriginalText();

        this.textField2.setText(this.originalText2);
    }

    @Override
    protected boolean applyValue()
    {
        return this.stringConsumer.apply(this.textField.getText(), this.textField2.getText());
    }
}

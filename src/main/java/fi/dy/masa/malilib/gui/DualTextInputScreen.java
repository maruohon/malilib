package fi.dy.masa.malilib.gui;

import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;

public class DualTextInputScreen extends BaseTextInputScreen
{
    protected final BiFunction<String, String, Boolean> stringConsumer;
    protected final LabelWidget labelWidget1;
    protected final LabelWidget labelWidget2;
    protected final BaseTextFieldWidget textField2;
    protected final String originalText2;

    public DualTextInputScreen(String titleKey,
                               String labelKey1,
                               String labelKey2,
                               String defaultText1,
                               String defaultText2,
                               BiFunction<String, String, Boolean> stringConsumer,
                               @Nullable GuiScreen parent)
    {
        super(titleKey, defaultText1, parent);

        this.stringConsumer = stringConsumer;
        this.originalText2 = defaultText2;

        this.labelWidget1 = new LabelWidget(0, 0, -1, 12, 0xFFFFFFFF, labelKey1);
        this.labelWidget2 = new LabelWidget(0, 0, -1, 12, 0xFFFFFFFF, labelKey2);
        this.textField2 = new BaseTextFieldWidget(0, 0, 240, 20, this.originalText2);

        this.setScreenWidthAndHeight(280, 140);
        this.centerOnScreen();
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        int x = this.x + 10;
        int y = this.y + 28;

        this.labelWidget1.setPosition(x, y);
        this.textField.setPosition(x, y + 12);

        y += 40;
        this.labelWidget2.setPosition(x, y);
        this.textField2.setPosition(x, y + 12);

        y += 40;
        this.okButton.setY(y);
        this.resetButton.setY(y);
        this.cancelButton.setY(y);

        this.addWidget(this.labelWidget1);
        this.addWidget(this.labelWidget2);
        this.addWidget(this.textField2);
    }

    @Override
    protected void closeScreenIfValueApplied()
    {
        // Only close the GUI if the value was successfully applied
        if (this.stringConsumer.apply(this.textField.getText(), this.textField2.getText()))
        {
            this.closeScreen(true);
        }
    }

    @Override
    protected void resetTextFieldToOriginalText()
    {
        super.resetTextFieldToOriginalText();

        this.textField2.setText(this.originalText2);
    }

    @Override
    protected boolean applyValue(String string)
    {
        return false;
    }
}

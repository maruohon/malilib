package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.button.ButtonActionListener;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class BaseTextInputScreen extends BaseScreen
{
    protected final BaseTextFieldWidget textField;
    protected final String originalText;

    public BaseTextInputScreen(String titleKey, String defaultText, @Nullable GuiScreen parent)
    {
        this.setParent(parent);
        this.title = StringUtils.translate(titleKey);
        this.useTitleHierarchy = false;
        this.originalText = defaultText;

        this.setScreenWidthAndHeight(260, 100);
        this.centerOnScreen();

        this.textField = new BaseTextFieldWidget(this.x + 12, this.y + 40, 240, 20, this.originalText);
        this.textField.setFocused(true);
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        int x = this.x + 10;
        int y = this.y + 70;

        this.textField.setPosition(this.x + 12, this.y + 40);
        this.addWidget(this.textField);

        x += 2 + this.createButton(x, y, "malilib.gui.button.colored.ok", (btn, mbtn) -> this.closeScreenIfValueApplied());
        x += 2 + this.createButton(x, y, "malilib.gui.button.reset", (btn, mbtn) -> this.resetTextFieldToOriginalText());
        this.createButton(x, y, "malilib.gui.button.colored.cancel", (btn, mbtn) -> this.closeScreen(true));
    }

    protected int createButton(int x, int y, String translationKey, ButtonActionListener listener)
    {
        GenericButton button = new GenericButton(x, y, -1, 20, translationKey);
        button.setWidth(Math.max(40, button.getWidth()));
        return this.addButton(button, listener).getWidth();
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == Keyboard.KEY_RETURN)
        {
            this.closeScreenIfValueApplied();
            return true;
        }
        else if (keyCode == Keyboard.KEY_ESCAPE)
        {
            this.closeScreen(true);
            return true;
        }

        return super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    protected void closeScreenIfValueApplied()
    {
        // Only close the GUI if the value was successfully applied
        if (this.applyValue(this.textField.getText()))
        {
            this.closeScreen(true);
        }
    }

    protected void resetTextFieldToOriginalText()
    {
        this.textField.setText(this.originalText);
        this.textField.setCursorToStart();
        this.textField.setFocused(true);
    }

    protected abstract boolean applyValue(String string);
}

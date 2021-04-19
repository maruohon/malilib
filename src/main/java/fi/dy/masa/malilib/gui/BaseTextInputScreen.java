package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class BaseTextInputScreen extends BaseScreen
{
    protected final BaseTextFieldWidget textField;
    protected final GenericButton okButton;
    protected final GenericButton resetButton;
    protected final GenericButton cancelButton;
    protected final String originalText;

    public BaseTextInputScreen(String titleKey, String defaultText, @Nullable GuiScreen parent)
    {
        this.title = StringUtils.translate(titleKey);
        this.useTitleHierarchy = false;
        this.originalText = defaultText;

        this.okButton = createButton("malilib.gui.button.colored.ok", this::closeScreenIfValueApplied);
        this.resetButton = createButton("malilib.gui.button.reset", this::resetTextFieldToOriginalText);
        this.cancelButton = createButton("malilib.gui.button.cancel", () -> this.closeScreen(true));

        this.textField = new BaseTextFieldWidget(this.x + 12, this.y + 40, 240, 20, this.originalText);
        this.textField.setFocused(true);

        this.setParent(parent);
        this.setScreenWidthAndHeight(260, 100);
        this.centerOnScreen();
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        int x = this.x + 10;
        int y = this.y + 40;

        this.textField.setPosition(x + 2, y);

        y += 30;
        this.okButton.setPosition(x, y);
        this.resetButton.setPosition(this.okButton.getRight() + 6, y);
        this.cancelButton.setPosition(this.resetButton.getRight() + 6, y);

        this.addWidget(this.textField);
        this.addWidget(this.okButton);
        this.addWidget(this.resetButton);
        this.addWidget(this.cancelButton);
    }

    protected static GenericButton createButton(String translationKey, EventListener listener)
    {
        GenericButton button = new GenericButton(0, 0, -1, 20, translationKey);
        button.setActionListener(listener);
        button.setWidth(Math.max(40, button.getWidth()));
        button.setAutomaticWidth(false);
        return button;
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

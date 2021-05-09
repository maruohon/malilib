package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.ScreenContext;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.text.StyledText;
import fi.dy.masa.malilib.render.text.TextRenderer;
import fi.dy.masa.malilib.util.StyledTextUtils;

public abstract class BaseTextInputScreen extends BaseScreen
{
    protected final BaseTextFieldWidget textField;
    protected final GenericButton okButton;
    protected final GenericButton resetButton;
    protected final GenericButton cancelButton;
    protected final String originalText;
    @Nullable protected StyledText infoText;
    @Nullable protected StyledText labelText;
    protected int baseHeight = 80;
    protected int elementsOffsetY;

    public BaseTextInputScreen(String titleKey, String defaultText, @Nullable GuiScreen parent)
    {
        this.useTitleHierarchy = false;
        this.originalText = defaultText;
        this.screenWidth = 260;
        this.backgroundColor = 0xFF000000;
        this.setTitle(titleKey);

        this.okButton = createButton("malilib.gui.button.colored.ok", this::closeScreenIfValueApplied);
        this.resetButton = createButton("malilib.gui.button.reset", this::resetTextFieldToOriginalText);
        this.cancelButton = createButton("malilib.gui.button.cancel", () -> this.closeScreen(true));

        this.textField = new BaseTextFieldWidget(this.x + 12, this.y + 40, 240, 20, this.originalText);
        this.textField.setFocused(true);

        this.setParent(parent);
    }

    @Override
    protected void initScreen()
    {
        this.updateHeight();
        super.initScreen();
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.textField);
        this.addWidget(this.okButton);
        this.addWidget(this.resetButton);
        this.addWidget(this.cancelButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + 10;
        int y = this.y + 26 + this.elementsOffsetY;

        this.textField.setPosition(x, y);

        y += 26;
        this.okButton.setPosition(x, y);
        this.resetButton.setPosition(this.okButton.getRight() + 6, y);
        this.cancelButton.setPosition(this.resetButton.getRight() + 6, y);
    }

    protected void updateHeight()
    {
        this.updateTextHeightOffset();
        this.setScreenWidthAndHeight(260, this.baseHeight + this.elementsOffsetY);
        this.centerOnScreen();
    }

    protected void updateTextHeightOffset()
    {
        this.elementsOffsetY = 0;
        int lineHeight = TextRenderer.INSTANCE.getFontHeight() + 1;

        if (this.infoText != null)
        {
            this.elementsOffsetY += this.infoText.lines.size() * lineHeight + 16;
        }

        if (this.labelText != null)
        {
            this.elementsOffsetY += this.labelText.lines.size() * lineHeight + 4;
        }
    }

    protected static GenericButton createButton(String translationKey, EventListener listener)
    {
        GenericButton button = new GenericButton(0, 0, -1, 20, translationKey);
        button.setActionListener(listener);
        button.setWidth(Math.max(40, button.getWidth()));
        button.setAutomaticWidth(false);
        return button;
    }

    public void setInfoText(@Nullable StyledText infoText)
    {
        if (infoText != null)
        {
            infoText = StyledTextUtils.wrapStyledTextToMaxWidth(infoText, this.screenWidth - 20);
        }

        this.infoText = infoText;

        this.updateHeight();
    }

    public void setLabelText(@Nullable StyledText labelText)
    {
        if (labelText != null)
        {
            labelText = StyledTextUtils.wrapStyledTextToMaxWidth(labelText, this.screenWidth - 20);
        }

        this.labelText = labelText;

        this.updateHeight();
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

    @Override
    protected void renderCustomContents(int mouseX, int mouseY, ScreenContext ctx)
    {
        if (this.infoText != null)
        {
            int x = this.x + 10;
            int y = this.y + 28;

            TextRenderer.INSTANCE.renderText(x, y, this.zLevel + 0.025f, 0xFFE0E0E0, true, this.infoText);
        }

        if (this.labelText != null)
        {
            int x = this.x + 10;
            int y = this.textField.getY() - 12;

            TextRenderer.INSTANCE.renderText(x, y, this.zLevel + 0.025f, 0xFFE0E0E0, true, this.labelText);
        }
    }

    protected void closeScreenIfValueApplied()
    {
        // Only close the screen if the value was successfully applied,
        // and this screen is still the active screen
        if (this.applyValue(this.textField.getText()) && GuiUtils.getCurrentScreen() == this)
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

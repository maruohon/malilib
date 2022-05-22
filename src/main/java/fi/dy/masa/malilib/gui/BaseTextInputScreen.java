package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.input.Keys;
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
    @Nullable protected EventListener confirmListener;
    @Nullable protected EventListener cancelListener;
    @Nullable protected StyledText infoText;
    @Nullable protected StyledText labelText;
    protected int baseHeight = 80;
    protected int elementsOffsetY;
    protected boolean closeScreenWhenApplied = true;

    public BaseTextInputScreen(String titleKey)
    {
        this(titleKey, "");
    }

    public BaseTextInputScreen(String titleKey, String defaultText)
    {
        this.useTitleHierarchy = false;
        this.originalText = defaultText;
        this.screenWidth = 260;
        this.backgroundColor = 0xFF000000;
        this.renderBorder = true;
        this.setTitle(titleKey);

        this.okButton = createButton("malilib.button.misc.ok.caps_colored", this::closeScreenIfValueApplied);
        this.resetButton = createButton("malilib.button.misc.reset", this::resetTextFieldToOriginalText);
        this.cancelButton = createButton("malilib.button.misc.cancel", this::cancel);

        this.textField = new BaseTextFieldWidget(240, 20, this.originalText);
        this.textField.setFocused(true);
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
        this.textField.setWidth(this.screenWidth - 20);

        y += 26;
        this.okButton.setPosition(x, y);
        this.resetButton.setPosition(this.okButton.getRight() + 6, y);
        this.cancelButton.setPosition(this.resetButton.getRight() + 6, y);
    }

    public void setCloseScreenWhenApplied(boolean closeScreenWhenApplied)
    {
        this.closeScreenWhenApplied = closeScreenWhenApplied;
    }

    protected void updateHeight()
    {
        this.updateTextHeightOffset();
        this.setScreenHeight(this.baseHeight + this.elementsOffsetY);
        this.centerOnScreen();
    }

    protected void updateTextHeightOffset()
    {
        this.elementsOffsetY = 0;
        int lineHeight = TextRenderer.INSTANCE.getLineHeight();

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
        GenericButton button = GenericButton.create(translationKey, listener);
        button.setWidth(Math.max(40, button.getWidth()));
        button.setAutomaticWidth(false);
        return button;
    }

    public void setConfirmListener(@Nullable EventListener confirmListener)
    {
        this.confirmListener = confirmListener;
    }

    public void setCancelListener(@Nullable EventListener cancelListener)
    {
        this.cancelListener = cancelListener;
    }

    @Nullable
    protected StyledText wrapTextToWidth(@Nullable StyledText text)
    {
        if (text != null)
        {
            return StyledTextUtils.wrapStyledTextToMaxWidth(text, this.screenWidth - 20);
        }

        return null;
    }

    public void setInfoText(String translationKey)
    {
        this.setInfoText(StyledText.translate(translationKey));
    }

    public void setInfoText(@Nullable StyledText infoText)
    {
        this.infoText = this.wrapTextToWidth(infoText);
        this.updateHeight();
    }

    public void setLabelText(String translationKey)
    {
        this.setLabelText(StyledText.translate(translationKey));
    }

    public void setLabelText(@Nullable StyledText labelText)
    {
        this.labelText = this.wrapTextToWidth(labelText);
        this.updateHeight();
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == Keys.KEY_ENTER)
        {
            this.closeScreenIfValueApplied();
            return true;
        }
        else if (keyCode == Keys.KEY_ESCAPE)
        {
            this.openParentScreen();
            return true;
        }

        return super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    protected void renderCustomContents(ScreenContext ctx)
    {
        if (this.infoText != null)
        {
            int x = this.x + 10;
            int y = this.y + 28;

            TextRenderer.INSTANCE.renderText(x, y, this.z + 0.025f, 0xFFC0C0C0, true, this.infoText);
        }

        if (this.labelText != null)
        {
            int x = this.x + 10;
            int y = this.textField.getY() - 12;

            TextRenderer.INSTANCE.renderText(x, y, this.z + 0.025f, 0xFFFFFFFF, true, this.labelText);
        }
    }

    protected void closeScreenIfValueApplied()
    {
        if (this.applyValue())
        {
            // Only close the screen if the value was successfully applied,
            // and this screen is still the active screen
            if (this.closeScreenWhenApplied && GuiUtils.getCurrentScreen() == this)
            {
                this.openParentScreen();
            }

            if (this.confirmListener != null)
            {
                this.confirmListener.onEvent();
            }
        }
    }

    protected void resetTextFieldToOriginalText()
    {
        this.textField.setText(this.originalText);
        this.textField.setCursorToStart();
        this.textField.setFocused(true);
    }

    protected void cancel()
    {
        this.openParentScreen();

        if (this.cancelListener != null)
        {
            this.cancelListener.onEvent();
        }
    }

    protected abstract boolean applyValue();
}

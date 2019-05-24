package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public abstract class GuiTextInputBase extends GuiDialogBase
{
    protected final GuiTextField textField;
    protected final String originalText;

    public GuiTextInputBase(int maxTextLength, String titleKey, String defaultText, @Nullable GuiScreen parent)
    {
        this.mc = Minecraft.getMinecraft();
        this.setParent(parent);
        this.title = I18n.format(titleKey);
        this.useTitleHierarchy = false;
        this.originalText = defaultText;

        this.setWidthAndHeight(260, 100);
        this.centerOnScreen();

        int width = Math.min(maxTextLength * 10, 240);
        this.textField = new GuiTextFieldGeneric(this.dialogLeft + 12, this.dialogTop + 40, width, 20, this.mc.fontRenderer);
        this.textField.setMaxStringLength(maxTextLength);
        this.textField.setFocused(true);
        this.textField.setText(this.originalText);
        this.textField.setCursorPositionEnd();
        this.zLevel = 1f;
    }

    @Override
    public void initGui()
    {
        int x = this.dialogLeft + 10;
        int y = this.dialogTop + 70;
        int buttonWidth = 80;

        this.createButton(x, y, buttonWidth, ButtonType.OK);
        x += buttonWidth + 2;

        this.createButton(x, y, buttonWidth, ButtonType.RESET);
        x += buttonWidth + 2;

        this.createButton(x, y, buttonWidth, ButtonType.CANCEL);

        Keyboard.enableRepeatEvents(true);
    }

    protected void createButton(int x, int y, int buttonWidth, ButtonType type)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, buttonWidth, 20, I18n.format(type.getLabelKey()));
        this.addButton(button, this.createActionListener(type));
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return this.getParent() != null && this.getParent().doesGuiPauseGame();
    }

    @Override
    public void drawContents(int mouseX, int mouseY, float partialTicks)
    {
        if (this.getParent() != null)
        {
            this.getParent().drawScreen(mouseX, mouseY, partialTicks);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, this.zLevel);

        RenderUtils.drawOutlinedBox(this.dialogLeft, this.dialogTop, this.dialogWidth, this.dialogHeight, 0xE0000000, COLOR_HORIZONTAL_BAR);

        // Draw the title
        this.drawString(this.fontRenderer, this.getTitle(), this.dialogLeft + 10, this.dialogTop + 4, COLOR_WHITE);

        //super.drawScreen(mouseX, mouseY, partialTicks);
        this.textField.drawTextBox();

        this.drawButtons(mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_RETURN)
        {
            // Only close the GUI if the value was successfully applied
            if (this.applyValue(this.textField.getText()))
            {
                this.mc.displayGuiScreen(this.getParent());
            }

            return true;
        }
        else if (keyCode == Keyboard.KEY_ESCAPE)
        {
            this.mc.displayGuiScreen(this.getParent());
            return true;
        }

        if (this.textField.isFocused())
        {
            return this.textField.textboxKeyTyped(typedChar, keyCode);
        }

        return super.onKeyTyped(typedChar, keyCode);
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int button)
    {
        if (this.textField.mouseClicked(mouseX, mouseY, button))
        {
            return true;
        }

        return super.onMouseClicked(mouseX, mouseY, button);
    }

    protected ButtonListener createActionListener(ButtonType type)
    {
        return new ButtonListener(type, this);
    }

    protected abstract boolean applyValue(String string);

    protected static class ButtonListener implements IButtonActionListener
    {
        private final GuiTextInputBase gui;
        private final ButtonType type;

        public ButtonListener(ButtonType type, GuiTextInputBase gui)
        {
            this.type = type;
            this.gui = gui;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            if (this.type == ButtonType.OK)
            {
                // Only close the GUI if the value was successfully applied
                if (this.gui.applyValue(this.gui.textField.getText()))
                {
                    this.gui.mc.displayGuiScreen(this.gui.getParent());
                }
            }
            else if (this.type == ButtonType.CANCEL)
            {
                this.gui.mc.displayGuiScreen(this.gui.getParent());
            }
            else if (this.type == ButtonType.RESET)
            {
                this.gui.textField.setText(this.gui.originalText);
                this.gui.textField.setCursorPosition(0);
                this.gui.textField.setFocused(true);
            }
        }
    }

    protected enum ButtonType
    {
        OK      ("malilib.gui.button.ok"),
        CANCEL  ("malilib.gui.button.cancel"),
        RESET   ("malilib.gui.button.reset");

        private final String labelKey;

        private ButtonType(String labelKey)
        {
            this.labelKey = labelKey;
        }

        public String getLabelKey()
        {
            return this.labelKey;
        }
    }
}

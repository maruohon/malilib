package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.interfaces.IStringConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;

public class GuiTextInput extends GuiBase
{
    private final String title;
    private final GuiTextField textField;
    private final String originalText;
    private final IStringConsumer consumer;
    private int dialogWidth;
    private int dialogHeight;
    private int dialogLeft;
    private int dialogTop;

    public GuiTextInput(int maxTextLength, String titleKey, String defaultText, @Nullable GuiBase parent, IStringConsumer consumer)
    {
        this.mc = Minecraft.getMinecraft();
        this.setParent(parent);
        this.title = I18n.format(titleKey);
        this.originalText = defaultText;
        this.consumer = consumer;

        this.setWidthAndHeight(260, 100);
        this.centerOnScreen();

        int width = Math.min(maxTextLength * 10, 240);
        this.textField = new GuiTextFieldGeneric(0, this.mc.fontRenderer, this.dialogLeft + 12, this.dialogTop + 40, width, 20);
        this.textField.setMaxStringLength(maxTextLength);
        this.textField.setFocused(true);
        this.textField.setText(this.originalText);
        this.textField.setCursorPositionEnd();
    }

    public void setWidthAndHeight(int width, int height)
    {
        this.dialogWidth = width;
        this.dialogHeight = height;
    }

    public void setPosition(int left, int top)
    {
        this.dialogLeft = left;
        this.dialogTop = top;
    }

    public void centerOnScreen()
    {
        if (this.getParent() != null)
        {
            this.dialogLeft = this.getParent().width / 2 - this.dialogWidth / 2;
            this.dialogTop = this.getParent().height / 2 - this.dialogHeight / 2;
        }
        else
        {
            ScaledResolution res = new ScaledResolution(this.mc);
            this.dialogLeft = res.getScaledWidth() / 2 - this.dialogWidth / 2;
            this.dialogTop = res.getScaledHeight() / 2 - this.dialogHeight / 2;
        }
    }

    @Override
    protected String getTitle()
    {
        return this.title;
    }

    @Override
    public void initGui()
    {
        int x = this.dialogLeft + 10;
        int y = this.dialogTop + 70;
        int buttonWidth = 80;

        ButtonGeneric button;

        button = new ButtonGeneric(0, x, y, buttonWidth, 20, I18n.format("litematica.gui.button.ok"));
        this.addButton(button, this.createActionListener(ButtonListener.Type.OK));
        x += buttonWidth + 2;

        button = new ButtonGeneric(0, x, y, buttonWidth, 20, I18n.format("litematica.gui.button.reset"));
        this.addButton(button, this.createActionListener(ButtonListener.Type.RESET));
        x += buttonWidth + 2;

        button = new ButtonGeneric(0, x, y, buttonWidth, 20, I18n.format("litematica.gui.button.cancel"));
        this.addButton(button, this.createActionListener(ButtonListener.Type.CANCEL));

        Keyboard.enableRepeatEvents(true);
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

        RenderUtils.drawOutlinedBox(this.dialogLeft, this.dialogTop, this.dialogWidth, this.dialogHeight, 0xB0000000, COLOR_HORIZONTAL_BAR);

        // Draw the title
        this.drawString(this.fontRenderer, this.getTitle(), this.dialogLeft + 10, this.dialogTop + 4, COLOR_WHITE);

        //super.drawScreen(mouseX, mouseY, partialTicks);
        this.textField.drawTextBox();

        this.drawButtons(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_RETURN)
        {
            this.consumer.setString(this.textField.getText());
            this.mc.displayGuiScreen(this.getParent());
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

    private ButtonListener createActionListener(ButtonListener.Type type)
    {
        return new ButtonListener(type, this);
    }

    private static class ButtonListener implements IButtonActionListener<ButtonGeneric>
    {
        private final GuiTextInput gui;
        private final Type type;

        public ButtonListener(Type type, GuiTextInput gui)
        {
            this.type = type;
            this.gui = gui;
        }

        @Override
        public void actionPerformed(ButtonGeneric control)
        {
            if (this.type == Type.OK)
            {
                this.gui.consumer.setString(this.gui.textField.getText());
                this.gui.mc.displayGuiScreen(this.gui.getParent());
            }
            else if (this.type == Type.CANCEL)
            {
                this.gui.mc.displayGuiScreen(this.gui.getParent());
            }
            else if (this.type == Type.RESET)
            {
                this.gui.textField.setText(this.gui.originalText);
                this.gui.textField.setCursorPosition(0);
                this.gui.textField.setFocused(true);
            }
        }

        @Override
        public void actionPerformedWithButton(ButtonGeneric control, int mouseButton)
        {
            this.actionPerformed(control);
        }

        public enum Type
        {
            OK,
            CANCEL,
            RESET;
        }
    }
}

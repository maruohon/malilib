package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.interfaces.IConfirmationListener;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class GuiConfirmAction extends GuiDialogBase
{
    protected final List<String> messageLines = new ArrayList<>();
    protected final IConfirmationListener listener;
    protected int textColor = 0xFFC0C0C0;

    public GuiConfirmAction(int width, String titleKey, IConfirmationListener listener, @Nullable GuiScreen parent, String messageKey, Object... args)
    {
        this.mc = Minecraft.getMinecraft();
        this.setParent(parent);
        this.title = I18n.format(titleKey);
        this.listener = listener;
        this.useTitleHierarchy = false;
        this.zLevel = 1f;

        StringUtils.splitTextToLines(this.messageLines, I18n.format(messageKey, args), width - 30, this.mc.fontRenderer);

        this.setWidthAndHeight(width, this.getMessageHeight() + 50);
        this.centerOnScreen();
    }

    @Override
    public void initGui()
    {
        int x = this.dialogLeft + 10;
        int y = this.dialogTop + this.dialogHeight - 24;
        int buttonWidth = this.getButtonWidth();

        this.createButton(x, y, buttonWidth, ButtonType.OK);
        x += buttonWidth + 10;

        this.createButton(x, y, buttonWidth, ButtonType.CANCEL);

        Keyboard.enableRepeatEvents(true);
    }

    public void setTextColor(int textColor)
    {
        this.textColor = textColor;
    }

    public int getMessageHeight()
    {
        return this.messageLines.size() * (this.mc.fontRenderer.FONT_HEIGHT + 1) - 1 + 5;
    }

    protected int getButtonWidth()
    {
        int width = 0;

        for (ButtonType type : ButtonType.values())
        {
            width = Math.max(width, this.mc.fontRenderer.getStringWidth(type.getDisplayName()) + 10);
        }

        return width;
    }

    protected void createButton(int x, int y, int buttonWidth, ButtonType type)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, buttonWidth, 20, type.getDisplayName());
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
        int y = this.dialogTop + 20;

        for (String text : this.messageLines)
        {
            this.fontRenderer.drawString(text, this.dialogLeft + 10, y, this.textColor);
            y += this.fontRenderer.FONT_HEIGHT + 1;
        }

        this.drawButtons(mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();
    }

    protected ButtonListener createActionListener(ButtonType type)
    {
        return new ButtonListener(type, this);
    }

    protected static class ButtonListener implements IButtonActionListener<ButtonGeneric>
    {
        private final GuiConfirmAction gui;
        private final ButtonType type;

        public ButtonListener(ButtonType type, GuiConfirmAction gui)
        {
            this.type = type;
            this.gui = gui;
        }

        @Override
        public void actionPerformed(ButtonGeneric control)
        {
            if (this.type == ButtonType.OK)
            {
                this.gui.listener.onActionConfirmed();
            }
            else if (this.type == ButtonType.CANCEL)
            {
                this.gui.listener.onActionCancelled();
            }

            this.gui.mc.displayGuiScreen(this.gui.getParent());
        }

        @Override
        public void actionPerformedWithButton(ButtonGeneric control, int mouseButton)
        {
            this.actionPerformed(control);
        }
    }

    protected enum ButtonType
    {
        OK      ("malilib.gui.button.ok"),
        CANCEL  ("malilib.gui.button.cancel");

        private final String labelKey;

        private ButtonType(String labelKey)
        {
            this.labelKey = labelKey;
        }

        public String getDisplayName()
        {
            return (this == ButtonType.OK ? GuiBase.TXT_GREEN : GuiBase.TXT_RED) + I18n.format(this.labelKey) + GuiBase.TXT_RST;
        }
    }
}

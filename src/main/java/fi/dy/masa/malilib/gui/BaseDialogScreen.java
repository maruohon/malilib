package fi.dy.masa.malilib.gui;

import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.render.RenderUtils;

public class BaseDialogScreen extends BaseScreen
{
    protected int dialogWidth;
    protected int dialogHeight;
    protected int dialogLeft;
    protected int dialogTop;

    protected BaseDialogScreen()
    {
        this.useTitleHierarchy = false;
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
        int left;
        int top;

        if (this.getParent() != null)
        {
            left = this.getParent().width / 2 - this.dialogWidth / 2;
            top = this.getParent().height / 2 - this.dialogHeight / 2;
        }
        else
        {
            left = GuiUtils.getScaledWindowWidth() / 2 - this.dialogWidth / 2;
            top = GuiUtils.getScaledWindowHeight() / 2 - this.dialogHeight / 2;
        }

        this.setPosition(left, top);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return this.getParent() != null && this.getParent().doesGuiPauseGame();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.getParent() != null)
        {
            this.getParent().drawScreen(mouseX, mouseY, partialTicks);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawScreenBackground(int mouseX, int mouseY)
    {
        RenderUtils.renderOutlinedBox(this.dialogLeft, this.dialogTop, this.dialogWidth, this.dialogHeight, 0xF0000000, COLOR_HORIZONTAL_BAR, (int) this.zLevel);
    }

    @Override
    protected void drawTitle(int mouseX, int mouseY, float partialTicks)
    {
        this.drawStringWithShadow(this.getTitle(), this.dialogLeft + 10, this.dialogTop + 6, 0xFFFFFFFF);
    }
}

package fi.dy.masa.malilib.gui;

import net.minecraft.client.gui.ScaledResolution;

public class GuiDialogBase extends GuiBase
{
    protected int dialogWidth;
    protected int dialogHeight;
    protected int dialogLeft;
    protected int dialogTop;

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
            ScaledResolution res = new ScaledResolution(this.mc);
            left = res.getScaledWidth() / 2 - this.dialogWidth / 2;
            top = res.getScaledHeight() / 2 - this.dialogHeight / 2;
        }

        this.setPosition(left, top);
    }
}

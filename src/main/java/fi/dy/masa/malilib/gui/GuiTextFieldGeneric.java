package fi.dy.masa.malilib.gui;

import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;

public class GuiTextFieldGeneric extends GuiTextField
{
    public GuiTextFieldGeneric(int x, int y, int width, int height, FontRenderer fontrenderer)
    {
        super(0, fontrenderer, x, y, width, height);

        this.setMaxStringLength(256);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        boolean ret = super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 1 && this.isMouseOver(mouseX, mouseY))
        {
            this.setText("");
            this.setFocused(true);
            return true;
        }

        return ret;
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return mouseX >= this.x && mouseX < this.x + this.getWidth() &&
               mouseY >= this.y && mouseY < this.y + this.height;
    }

    @Override
    public void setFocused(boolean isFocusedIn)
    {
        boolean wasFocused = this.isFocused();
        super.setFocused(isFocusedIn);

        if (this.isFocused() != wasFocused)
        {
            Keyboard.enableRepeatEvents(this.isFocused());
        }
    }

    public GuiTextFieldGeneric setZLevel(float zLevel)
    {
        this.zLevel = zLevel;
        return this;
    }

    @Override
    public void drawTextBox()
    {
        if (this.zLevel != 0)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, this.zLevel);

            super.drawTextBox();

            GlStateManager.popMatrix();
        }
        else
        {
            super.drawTextBox();
        }
    }
}

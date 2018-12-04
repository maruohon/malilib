package fi.dy.masa.malilib.gui;

import fi.dy.masa.malilib.mixin.IGuiTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiTextFieldGeneric extends GuiTextField
{
    public GuiTextFieldGeneric(int id, FontRenderer fontrenderer, int x, int y, int width, int height)
    {
        super(id, fontrenderer, x, y, width, height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        boolean ret = super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 1 && this.isMouseOver((int) mouseX, (int) mouseY))
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
               mouseY >= this.y && mouseY < this.y + ((IGuiTextField) (Object) this).getHeight();
    }

    @Override
    public void setFocused(boolean isFocusedIn)
    {
        boolean wasFocused = this.isFocused();
        super.setFocused(isFocusedIn);

        if (this.isFocused() != wasFocused)
        {
            Minecraft.getInstance().keyboardListener.enableRepeatEvents(this.isFocused());
        }
    }
}

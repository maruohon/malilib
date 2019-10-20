package fi.dy.masa.malilib.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class GuiTextFieldGeneric extends TextFieldWidget
{
    public GuiTextFieldGeneric(int x, int y, int width, int height, FontRenderer fontrenderer)
    {
        super(fontrenderer, x, y, width, height, "");

        this.setMaxStringLength(256);
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
               mouseY >= this.y && mouseY < this.y + this.getHeight();
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

    public GuiTextFieldGeneric setZLevel(int zLevel)
    {
        this.blitOffset = zLevel;
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        if (this.blitOffset != 0)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0, 0, this.blitOffset);

            super.render(mouseX, mouseY, partialTicks);

            GlStateManager.popMatrix();
        }
        else
        {
            super.render(mouseX, mouseY, partialTicks);
        }
    }
}

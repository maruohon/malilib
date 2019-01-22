package fi.dy.masa.malilib.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class GuiTextFieldGeneric extends TextFieldWidget
{
    protected final int x;
    protected final int y;
    protected final int width;
    protected final int height;

    public GuiTextFieldGeneric(int id, FontRenderer fontrenderer, int x, int y, int width, int height)
    {
        super(id, fontrenderer, x, y, width, height);

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
        return mouseX >= this.x && mouseX < this.x + this.width &&
               mouseY >= this.y && mouseY < this.y + this.height;
    }

    @Override
    public void setFocused(boolean isFocusedIn)
    {
        boolean wasFocused = this.isFocused();
        super.setFocused(isFocusedIn);

        if (this.isFocused() != wasFocused)
        {
            MinecraftClient.getInstance().keyboard.enableRepeatEvents(this.isFocused());
        }
    }
}

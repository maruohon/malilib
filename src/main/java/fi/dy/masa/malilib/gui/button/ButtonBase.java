package fi.dy.masa.malilib.gui.button;

import net.minecraft.client.gui.GuiButton;

public abstract class ButtonBase extends GuiButton
{
    public ButtonBase(int id, int x, int y, int width, int height)
    {
        this(id, x, y, width, height, "");
    }

    public ButtonBase(int id, int x, int y, int width, int height, String text)
    {
        super(id, x, y, width, height, text);
    }

    public int getButtonHeight()
    {
        return this.height;
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return this.isPressable(mouseX, mouseY);
    }

    public void onMouseButtonClicked(int mouseButton)
    {
    }

    public void updateDisplayString()
    {
    }
}

package fi.dy.masa.malilib.gui.button;

import net.minecraft.client.Minecraft;
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

        if (width == -1)
        {
            this.width = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
        }
    }

    public int getButtonHeight()
    {
        return this.height;
    }

    public void onMouseButtonClicked(int mouseButton)
    {
        this.playPressSound(Minecraft.getMinecraft().getSoundHandler());
    }

    public void updateDisplayString()
    {
    }
}

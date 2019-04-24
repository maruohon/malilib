package fi.dy.masa.malilib.gui.button;

import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;

public abstract class ButtonBase extends AbstractPressableButtonWidget
{
    public ButtonBase(int id, int x, int y, int width, int height)
    {
        this(id, x, y, width, height, "");
    }

    public ButtonBase(int id, int x, int y, int width, int height, String text)
    {
        super(x, y, width, height, text);
    }

    public int getButtonHeight()
    {
        return this.height;
    }

    @Override
    public void onPress()
    {
    }

    public void onMouseButtonClicked(int mouseButton)
    {
    }

    public void updateDisplayString()
    {
    }
}

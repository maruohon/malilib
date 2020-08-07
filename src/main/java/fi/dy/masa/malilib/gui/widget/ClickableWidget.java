package fi.dy.masa.malilib.gui.widget;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.listener.EventListener;

public class ClickableWidget extends ContainerWidget
{
    @Nullable protected EventListener clickListener;

    public ClickableWidget(int x, int y, int width, int height, @Nullable EventListener clickListener)
    {
        super(x, y, width, height);

        this.clickListener = clickListener;
    }

    public void setClickListener(@Nullable EventListener listener)
    {
        this.clickListener = listener;
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.clickListener != null)
        {
            this.clickListener.onEvent();
            return true;
        }

        return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }
}

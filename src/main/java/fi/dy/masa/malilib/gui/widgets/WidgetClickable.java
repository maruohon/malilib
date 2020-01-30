package fi.dy.masa.malilib.gui.widgets;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.interfaces.IClickListener;

public class WidgetClickable extends WidgetContainer
{
    @Nullable protected IClickListener listener;

    public WidgetClickable(int x, int y, int width, int height, @Nullable IClickListener listener)
    {
        super(x, y, width, height);

        this.listener = listener;
    }

    public void setClickListener(@Nullable IClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.listener != null)
        {
            this.listener.onClicked();
            return true;
        }

        return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }
}

package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;

public class WidgetIcon extends WidgetBase
{
    protected final IGuiIcon icon;
    protected boolean doHilight;
    protected boolean enabled;

    public WidgetIcon(int x, int y, IGuiIcon icon)
    {
        super(x, y, icon.getWidth(), icon.getHeight());

        this.icon = icon;
    }

    public WidgetIcon setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    public WidgetIcon setDoHilight(boolean doHilight)
    {
        this.doHilight = doHilight;
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        this.icon.renderAt(this.getX(), this.getY(), this.getZLevel(), this.enabled, this.doHilight && this.isMouseOver(mouseX, mouseY));
    }
}

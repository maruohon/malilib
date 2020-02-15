package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;

public class WidgetInfoIcon extends WidgetHoverInfo
{
    protected final IGuiIcon icon;

    public WidgetInfoIcon(int x, int y, IGuiIcon icon, String key, Object... args)
    {
        super(x, y, icon.getWidth(), icon.getHeight(), key, args);

        this.icon = icon;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        this.icon.renderAt(this.getX(), this.getY(), this.getZLevel(), false, hovered);
    }
}

package fi.dy.masa.malilib.gui.widget;

import fi.dy.masa.malilib.gui.icon.Icon;

public class InfoIconWidget extends HoverInfoWidget
{
    protected final Icon icon;

    public InfoIconWidget(int x, int y, Icon icon, String key, Object... args)
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

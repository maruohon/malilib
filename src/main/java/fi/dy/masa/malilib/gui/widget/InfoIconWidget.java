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
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        this.icon.renderAt(x, y, z + 0.1f, false, hovered);
    }
}

package malilib.gui.icon;

import malilib.util.position.Vec2i;

public class PositionedIcon
{
    public final Vec2i pos;
    public final Icon icon;

    public PositionedIcon(Vec2i pos, Icon icon)
    {
        this.pos = pos;
        this.icon = icon;
    }

    public static PositionedIcon of(Vec2i pos, Icon icon)
    {
        return new PositionedIcon(pos, icon);
    }
}

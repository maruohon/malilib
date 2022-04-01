package fi.dy.masa.malilib.config.option;

import fi.dy.masa.malilib.util.position.Vec2i;

public class Vec2iConfig extends BaseDualValueConfig<Vec2i>
{
    public Vec2iConfig(String name, Vec2i defaultValue)
    {
        this(name, defaultValue, name);
    }

    public Vec2iConfig(String name, Vec2i defaultValue, String comment)
    {
        super(name, defaultValue, comment);
    }
}

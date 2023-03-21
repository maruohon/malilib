package malilib.config.option;

import net.minecraft.util.Mth;

import malilib.util.position.Vec2i;

public class Vec2iConfig extends BaseDualValueConfig<Vec2i>
{
    protected final int minValue;
    protected final int maxValue;

    public Vec2iConfig(String name, Vec2i defaultValue)
    {
        this(name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public Vec2iConfig(String name, int defaultX, int defaultY)
    {
        this(name, new Vec2i(defaultX, defaultY), Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public Vec2iConfig(String name, int defaultX, int defaultY, int minValue, int maxValue)
    {
        this(name, new Vec2i(defaultX, defaultY), minValue, maxValue);
    }

    public Vec2iConfig(String name, Vec2i defaultValue, int minValue, int maxValue)
    {
        super(name, defaultValue, name);

        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public boolean setValue(Vec2i newValue)
    {
        newValue = this.getClampedValue(newValue);
        return super.setValue(newValue);
    }

    public int getMinValue()
    {
        return this.minValue;
    }

    public int getMaxValue()
    {
        return this.maxValue;
    }

    protected Vec2i getClampedValue(Vec2i value)
    {
        int x = Mth.clamp(value.x, this.minValue, this.maxValue);
        int y = Mth.clamp(value.y, this.minValue, this.maxValue);

        return new Vec2i(x, y);
    }
}

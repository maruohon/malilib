package malilib.util.position;

public enum BlockRotation
{
    NONE    (0, "none"),
    CW_90   (1, "rotate_90"),
    CW_180  (2, "rotate_180"),
    CCW_90  (3, "rotate_270");

    public static final BlockRotation[] VALUES = values();

    private final String name;
    private final int index;

    BlockRotation(int index, String name)
    {
        this.index = index;
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public BlockRotation add(BlockRotation rotation)
    {
        int index = (this.index + rotation.index) & 3;
        return VALUES[index];
    }

    public Direction rotate(Direction direction)
    {
        if (direction.getAxis() != Direction.Axis.Y)
        {
            switch(this)
            {
                case CW_90:     return direction.rotateY();
                case CW_180:    return direction.getOpposite();
                case CCW_90:    return direction.rotateYCCW();
            }
        }

        return direction;
    }

    public BlockRotation cycle(boolean reverse)
    {
        int index = (this.index + (reverse ? -1 : 1)) & 3;
        return VALUES[index];
    }

    public static BlockRotation byName(String name)
    {
        for (BlockRotation rot : VALUES)
        {
            if (rot.name.equalsIgnoreCase(name))
            {
                return rot;
            }
        }

        return NONE;
    }
}

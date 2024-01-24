package malilib.util.position;

import javax.annotation.Nullable;

import malilib.util.position.Direction.Axis;

public enum BlockMirror
{
    NONE (0, "none", null),
    X    (1, "x", Axis.X),
    Y    (2, "y", Axis.Y),
    Z    (3, "z", Axis.Z);

    public static final BlockMirror[] VALUES = values();

    private final String name;
    private final int index;
    @Nullable private final Direction.Axis axis;

    BlockMirror(int index, String name, Direction.Axis axis)
    {
        this.index = index;
        this.name = name;
        this.axis = axis;
    }

    public String getName()
    {
        return this.name;
    }

    /**
     * Determines the rotation that is equivalent to this mirror if the rotating object faces in the given direction
     */
    public BlockRotation toRotation(Direction direction)
    {
        if (direction.getAxis() == this.axis)
        {
            return BlockRotation.CW_180;
        }

        return BlockRotation.NONE;
    }

    /**
     * Mirror the given direction according to this mirror
     */
    public Direction mirror(Direction direction)
    {
        if (direction.getAxis() == this.axis)
        {
            return direction.getOpposite();
        }

        return direction;
    }

    public BlockMirror cycle(boolean reverse)
    {
        int index = (this.index + (reverse ? -1 : 1)) & 3;
        return VALUES[index];
    }

    public static BlockMirror byName(String name)
    {
        for (BlockMirror mirror : VALUES)
        {
            if (mirror.name.equalsIgnoreCase(name))
            {
                return mirror;
            }
        }

        return NONE;
    }
}

package malilib.util.position;

import javax.annotation.Nullable;

import net.minecraft.util.Mirror;

import malilib.util.StringUtils;
import malilib.util.position.Direction.Axis;

public enum BlockMirror
{
    NONE (0, "none", null, Mirror.NONE),
    X    (1, "x", Axis.X, Mirror.FRONT_BACK),
    Y    (2, "y", Axis.Y, Mirror.NONE),
    Z    (3, "z", Axis.Z, Mirror.LEFT_RIGHT);

    public static final BlockMirror[] VALUES = values();

    private final int index;
    private final String name;
    private final String translationKey;
    private final Mirror vanillaMirror;
    @Nullable private final Direction.Axis axis;

    BlockMirror(int index, String name, Direction.Axis axis, Mirror vanillaMirror)
    {
        this.index = index;
        this.name = name;
        this.vanillaMirror = vanillaMirror;
        this.translationKey = "malilib.label.block_mirror." + name;
        this.axis = axis;
    }

    public int getIndex()
    {
        return this.index;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
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

    public Mirror getVanillaMirror()
    {
        return this.vanillaMirror;
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

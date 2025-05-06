package malilib.util.position;

import malilib.util.StringUtils;

public enum BlockRotation
{
    NONE    (0, "none"),//,       Rotation.NONE),
    CW_90   (1, "rotate_90"),//,  Rotation.CLOCKWISE_90),
    CW_180  (2, "rotate_180"),//, Rotation.CLOCKWISE_180),
    CCW_90  (3, "rotate_270");//, Rotation.COUNTERCLOCKWISE_90);

    public static final BlockRotation[] VALUES = values();

    private final int index;
    private final String name;
    private final String translationKey;
    //private final Rotation vanillaRotation;

    BlockRotation(int index, String name)//, Rotation vanillaRotation)
    {
        this.index = index;
        this.name = name;
        this.translationKey = "malilib.label.block_rotation." + name;
        //this.vanillaRotation = vanillaRotation;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
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

    public BlockRotation getReverseRotation()
    {
        switch (this)
        {
            case CCW_90:    return BlockRotation.CW_90;
            case CW_90:     return BlockRotation.CCW_90;
            case CW_180:    return BlockRotation.CW_180;
            default:
        }

        return this;
    }

    public BlockRotation cycle(boolean reverse)
    {
        int index = (this.index + (reverse ? -1 : 1)) & 3;
        return VALUES[index];
    }

    /*
    public Rotation getVanillaRotation()
    {
        return this.vanillaRotation;
    }
    */

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

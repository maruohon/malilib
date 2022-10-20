package malilib.config.value;

import com.google.common.collect.ImmutableList;

public class BlockSnap extends BaseOptionListConfigValue
{
    public static final BlockSnap NONE   = new BlockSnap("none",    "malilibdev.name.block_snap.none");
    public static final BlockSnap CENTER = new BlockSnap("center",  "malilibdev.name.block_snap.center");
    public static final BlockSnap CORNER = new BlockSnap("corner",  "malilibdev.name.block_snap.corner");

    public static final ImmutableList<BlockSnap> VALUES = ImmutableList.of(NONE, CENTER, CORNER);

    private BlockSnap(String name, String translationKey)
    {
        super(name, translationKey);
    }
}

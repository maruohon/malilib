package fi.dy.masa.malilib.config.value;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.util.StringUtils;

public enum BlockSnap implements IConfigOptionListEntry<BlockSnap>
{
    NONE        ("none",    "malilib.gui.label.block_snap.none"),
    CENTER      ("center",  "malilib.gui.label.block_snap.center"),
    CORNER      ("corner",  "malilib.gui.label.block_snap.corner");

    public static final ImmutableList<BlockSnap> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;

    BlockSnap(String configString, String translationKey)
    {
        this.configString = configString;
        this.translationKey = translationKey;
    }

    @Override
    public String getStringValue()
    {
        return this.configString;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    @Override
    public BlockSnap cycle(boolean forward)
    {
        return ConfigOptionListEntry.cycleValue(VALUES, this.ordinal(), forward);
    }

    @Override
    public BlockSnap fromString(String name)
    {
        return ConfigOptionListEntry.findValueByName(name, VALUES);
    }
}

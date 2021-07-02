package fi.dy.masa.malilib.config.value;

import com.google.common.collect.ImmutableList;

public class SortDirection extends BaseOptionListConfigValue
{
    public static final SortDirection NONE       = new SortDirection("none",        "malilib.gui.label.sort_direction.none");
    public static final SortDirection ASCENDING  = new SortDirection("ascending",   "malilib.gui.label.sort_direction.ascending");
    public static final SortDirection DESCENDING = new SortDirection("descending",  "malilib.gui.label.sort_direction.descending");

    public static final ImmutableList<SortDirection> VALUES_ALL = ImmutableList.of(NONE, ASCENDING, DESCENDING);
    public static final ImmutableList<SortDirection> VALUES_ASC_DESC = ImmutableList.of(ASCENDING, DESCENDING);

    private SortDirection(String name, String translationKey)
    {
        super(name, translationKey);
    }

    public SortDirection getOpposite()
    {
        return this == ASCENDING ? DESCENDING : ASCENDING;
    }
}

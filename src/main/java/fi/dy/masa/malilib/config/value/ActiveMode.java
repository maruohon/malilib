package fi.dy.masa.malilib.config.value;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.util.StringUtils;

public enum ActiveMode implements ConfigOptionListEntry<ActiveMode>
{
    NEVER       ("never",       "malilib.label.active_mode.never"),
    WITH_KEY    ("with_key",    "malilib.label.active_mode.with_key"),
    ALWAYS      ("always",      "malilib.label.active_mode.always");

    public static final ImmutableList<ActiveMode> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;

    ActiveMode(String configString, String translationKey)
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
    public ActiveMode cycle(boolean forward)
    {
        return BaseConfigOptionListEntry.cycleValue(VALUES, this.ordinal(), forward);
    }

    @Override
    public ActiveMode fromString(String name)
    {
        return BaseConfigOptionListEntry.findValueByName(name, VALUES);
    }
}

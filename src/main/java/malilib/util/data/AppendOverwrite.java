package malilib.util.data;

import com.google.common.collect.ImmutableList;

import malilib.util.StringUtils;

public enum AppendOverwrite
{
    APPEND   ("malilib.name.append_overwrite.append"),
    OVERWRITE("malilib.name.append_overwrite.overwrite");

    public static final ImmutableList<AppendOverwrite> VALUES = ImmutableList.copyOf(values());

    private final String translationKey;

    AppendOverwrite(String translationKey)
    {
        this.translationKey = translationKey;
    }

    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }
}

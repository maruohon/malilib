package fi.dy.masa.malilib.config;

public interface IConfigOptionListEntry<T extends IConfigOptionListEntry<T>>
{
    String getStringValue();

    String getDisplayName();

    T cycle(boolean forward);

    T fromString(String value);
}

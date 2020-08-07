package fi.dy.masa.malilib.config.value;

public interface ConfigOptionListEntry<T extends ConfigOptionListEntry<T>>
{
    String getStringValue();

    String getDisplayName();

    T cycle(boolean forward);

    T fromString(String value);
}

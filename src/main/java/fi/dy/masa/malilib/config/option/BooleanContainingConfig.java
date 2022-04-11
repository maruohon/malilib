package fi.dy.masa.malilib.config.option;

import fi.dy.masa.malilib.util.data.BooleanStorageWithDefault;

public interface BooleanContainingConfig<T> extends BooleanStorageWithDefault, ConfigOption<T>, OverridableConfig<T>
{
}

package malilib.config.option;

import malilib.util.data.BooleanStorageWithDefault;

public interface BooleanContainingConfig<T> extends BooleanStorageWithDefault, ConfigOption<T>, OverridableConfig<T>
{
}

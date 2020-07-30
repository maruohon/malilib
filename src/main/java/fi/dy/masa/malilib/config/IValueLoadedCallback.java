package fi.dy.masa.malilib.config;

import fi.dy.masa.malilib.config.option.ConfigOption;

public interface IValueLoadedCallback<T>
{
    /**
     * Called after the config's value is loaded from file, or rather, from JSON, in the method
     * {@link ConfigOption#setValueFromJsonElement(com.google.gson.JsonElement element, String configName)}
     * @param newValue
     */
    default void onValueLoaded(T newValue) {}
}

package fi.dy.masa.malilib.config;

public interface ValueLoadCallback<T>
{
    /**
     * Called after the config's value is loaded from file
     * @param newValue the new value the config was set to
     */
    void onValueLoaded(T newValue);
}

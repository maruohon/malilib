package fi.dy.masa.malilib.config.options;

public interface IConfigSavable
{
    /**
     * Returns true if the value of this config has been changed since
     * it was last requested as a JSON element.
     * @return
     */
    boolean isDirty();
}

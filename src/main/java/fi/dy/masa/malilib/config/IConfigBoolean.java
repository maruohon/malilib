package fi.dy.masa.malilib.config;

public interface IConfigBoolean extends IConfigValue
{
    /**
     * Returns the "pretty name" for this config.
     * This is used in the toggle message.
     * @return
     */
    String getPrettyName();

    boolean getBooleanValue();

    void setBooleanValue(boolean value);
}

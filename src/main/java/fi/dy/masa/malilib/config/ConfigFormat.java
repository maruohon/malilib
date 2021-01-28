package fi.dy.masa.malilib.config;

public class ConfigFormat
{
    public final String name;

    public ConfigFormat(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        ConfigFormat that = (ConfigFormat) o;

        return this.name.equals(that.name);
    }

    @Override
    public int hashCode()
    {
        return this.name.hashCode();
    }
}
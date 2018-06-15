package fi.dy.masa.malilib.config;

public interface IConfigInteger extends IConfigValue
{
    int getIntegerValue();

    int getDefaultIntegerValue();

    void setIntegerValue(int value);
}

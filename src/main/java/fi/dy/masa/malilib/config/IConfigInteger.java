package fi.dy.masa.malilib.config;

public interface IConfigInteger extends IConfigValue, IConfigSlider
{
    int getIntegerValue();

    int getDefaultIntegerValue();

    void setIntegerValue(int value);

    int getMinIntegerValue();

    int getMaxIntegerValue();
}

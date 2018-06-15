package fi.dy.masa.malilib.config;

public interface IConfigDouble extends IConfigValue
{
    double getDoubleValue();

    double getDefaultDoubleValue();

    void setDoubleValue(double value);
}

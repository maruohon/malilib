package fi.dy.masa.malilib.config.options;

public interface IConfigDouble extends IConfigValue, IConfigSlider
{
    double getDoubleValue();

    default float getFloatValue()
    {
        return (float) this.getDoubleValue();
    }

    double getDefaultDoubleValue();

    void setDoubleValue(double value);

    double getMinDoubleValue();

    double getMaxDoubleValue();
}

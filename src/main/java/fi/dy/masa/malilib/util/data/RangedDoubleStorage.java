package fi.dy.masa.malilib.util.data;

public interface RangedDoubleStorage
{
    double getDoubleValue();

    boolean setDoubleValue(double newValue);

    double getMinDoubleValue();

    double getMaxDoubleValue();
}

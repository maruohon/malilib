package fi.dy.masa.malilib.util.data;

public interface RangedIntegerStorage
{
    int getIntegerValue();

    boolean setIntegerValue(int newValue);

    int getMinIntegerValue();

    int getMaxIntegerValue();
}

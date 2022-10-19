package malilib.util.data;

public interface BooleanStorageWithDefault extends BooleanStorage
{
    boolean getDefaultBooleanValue();

    boolean isModified();
}

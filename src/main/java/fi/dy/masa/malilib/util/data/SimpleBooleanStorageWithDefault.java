package fi.dy.masa.malilib.util.data;

public class SimpleBooleanStorageWithDefault extends SimpleBooleanStorage implements BooleanStorageWithDefault
{
    protected final boolean defaultValue;

    public SimpleBooleanStorageWithDefault()
    {
        this(false);
    }

    public SimpleBooleanStorageWithDefault(boolean value)
    {
        super(value);

        this.defaultValue = value;
    }

    @Override
    public boolean getDefaultBooleanValue()
    {
        return this.defaultValue;
    }

    @Override
    public boolean isModified()
    {
        return this.defaultValue != this.value;
    }
}

package malilib.util.data;

public class SimpleBooleanStorage implements BooleanStorage
{
    protected boolean value;

    public SimpleBooleanStorage()
    {
    }

    public SimpleBooleanStorage(boolean value)
    {
        this.value = value;
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.value;
    }

    @Override
    public boolean setBooleanValue(boolean value)
    {
        if (this.value != value)
        {
            this.value = value;
            return true;
        }

        return false;
    }
}

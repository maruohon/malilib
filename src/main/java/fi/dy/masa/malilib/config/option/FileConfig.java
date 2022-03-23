package fi.dy.masa.malilib.config.option;

import java.io.File;

public class FileConfig extends BaseStringConfig<File>
{
    public FileConfig(String name, File defaultValue)
    {
        this(name, defaultValue, name);
    }

    public FileConfig(String name, File defaultValue, String comment)
    {
        super(name, defaultValue, comment);

        this.stringValue = defaultValue.getAbsolutePath();
    }

    @Override
    public boolean setValue(File newValue)
    {
        if (this.locked == false)
        {
            this.stringValue = newValue.getAbsolutePath();
            return super.setValue(newValue);
        }

        return false;
    }

    @Override
    public void setValueFromString(String newValue)
    {
        if (this.stringValue.equals(newValue) == false)
        {
            this.setValue(new File(newValue));
        }
    }

    public void loadStringValueFromConfig(String value)
    {
        this.stringValue = value;
        this.value = new File(value);
        this.cacheSavedValue();
        this.updateEffectiveValue();
        this.onValueLoaded(this.effectiveValue);
    }
}

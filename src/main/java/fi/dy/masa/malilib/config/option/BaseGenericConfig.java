package fi.dy.masa.malilib.config.option;

public abstract class BaseGenericConfig<T> extends BaseConfig<T>
{
    protected final T defaultValue;
    protected T value;
    protected T lastSavedValue;

    public BaseGenericConfig(String name, T defaultValue)
    {
        super(name);

        this.value = defaultValue;
        this.defaultValue = defaultValue;

        this.cacheSavedValue();
    }

    public BaseGenericConfig(String name, T defaultValue, String commentTranslationKey, Object... commentArgs)
    {
        super(name, commentTranslationKey, commentArgs);

        this.value = defaultValue;
        this.defaultValue = defaultValue;

        this.cacheSavedValue();
    }

    public BaseGenericConfig(String name, T defaultValue, String nameTranslationKey, String prettyNameTranslationKey, String commentTranslationKey, Object... commentArgs)
    {
        super(name, nameTranslationKey, prettyNameTranslationKey, commentTranslationKey, commentArgs);

        this.value = defaultValue;
        this.defaultValue = defaultValue;

        this.cacheSavedValue();
    }

    @Override
    public T getValue()
    {
        return this.value;
    }

    public T getDefaultValue()
    {
        return this.defaultValue;
    }

    public void setValue(T newValue)
    {
        if (this.value.equals(newValue) == false)
        {
            T oldValue = this.value;
            this.value = newValue;
            this.onValueChanged(newValue, oldValue);
        }
    }

    @Override
    public boolean isModified()
    {
        return this.value.equals(this.defaultValue) == false;
    }

    @Override
    public boolean isDirty()
    {
        return this.value.equals(this.lastSavedValue) == false;
    }

    @Override
    public void cacheSavedValue()
    {
        this.lastSavedValue = this.value;
    }

    @Override
    public void resetToDefault()
    {
        this.setValue(this.defaultValue);
    }
}

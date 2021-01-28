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

    public boolean setValue(T newValue)
    {
        T oldValue = this.getValue();

        if (this.locked == false && oldValue.equals(newValue) == false)
        {
            this.value = newValue;

            // Re-fetch the current value, to take into account a possible value override
            newValue = this.getValue();

            if (oldValue.equals(newValue) == false)
            {
                this.onValueChanged(newValue, oldValue);
            }

            return true;
        }

        return false;
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

    /**
     * Sets the value of this config option, when loaded from a config file.
     * This should set the value without calling the ValueChangeCallback,
     * but instead call the ValueLoadCallback and cache the current value
     * for the dirty checks by the {@link ConfigOption#isDirty()} method later on.
     * @param value
     */
    public void loadValueFromConfig(T value)
    {
        this.value = value;
        this.cacheSavedValue();
        this.onValueLoaded(value);
    }
}

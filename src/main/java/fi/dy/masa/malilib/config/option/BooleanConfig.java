package fi.dy.masa.malilib.config.option;

import java.util.ArrayList;
import java.util.List;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.BooleanStorage;

public class BooleanConfig extends BaseGenericConfig<Boolean> implements BooleanStorage, OverridableConfig<Boolean>
{
    protected final List<EventListener> enableListeners = new ArrayList<>(0);
    protected Boolean effectiveValue;
    protected boolean booleanValue;
    protected boolean effectiveBooleanValue;
    protected boolean hasOverride;
    protected boolean overrideValue;

    public BooleanConfig(String name, boolean defaultValue)
    {
        this(name, defaultValue, name);
    }

    public BooleanConfig(String name, boolean defaultValue, String comment)
    {
        this(name, defaultValue, name, comment);
    }

    public BooleanConfig(String name, boolean defaultValue, String prettyNameTranslationKey, String comment)
    {
        super(name, defaultValue, name, prettyNameTranslationKey, comment);

        this.booleanValue = defaultValue;
        this.effectiveBooleanValue = defaultValue;
        this.effectiveValue = defaultValue;
    }

    @Override
    public Boolean getValue()
    {
        return this.effectiveValue;
    }

    @Override
    public boolean setValue(Boolean newValue)
    {
        return this.setBooleanValue(newValue);
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.effectiveBooleanValue;
    }

    @Override
    public boolean setBooleanValue(boolean newValue)
    {
        if (this.isLocked() == false)
        {
            boolean changed = this.booleanValue != newValue;

            this.booleanValue = newValue;
            // Update the effective value before the value change callback is called from the super method
            this.updateEffectiveValue();
            super.setValue(newValue);

            return changed;
        }

        return false;
    }

    @Override
    public void toggleBooleanValue()
    {
        this.setBooleanValue(! this.booleanValue);
    }

    protected void updateEffectiveValue()
    {
        this.effectiveBooleanValue = this.hasOverride ? this.overrideValue : this.booleanValue;
        this.effectiveValue = this.effectiveBooleanValue;
    }

    @Override
    public void onValueChanged(Boolean newValue, Boolean oldValue)
    {
        super.onValueChanged(newValue, oldValue);

        if (newValue)
        {
            for (EventListener listener : this.enableListeners)
            {
                listener.onEvent();
            }
        }
    }

    @Override
    public boolean isLocked()
    {
        return super.isLocked() || this.hasOverride;
    }

    @Override
    public boolean hasOverride()
    {
        return this.hasOverride;
    }

    @Override
    public void enableOverrideWithValue(Boolean overrideValue)
    {
        this.hasOverride = true;
        this.overrideValue = overrideValue;
        this.updateEffectiveValue();
        this.rebuildLockOverrideMessages();
    }

    @Override
    public void disableOverride()
    {
        this.hasOverride = false;
        this.updateEffectiveValue();
        this.rebuildLockOverrideMessages();
    }

    @Override
    protected void rebuildLockOverrideMessages()
    {
        super.rebuildLockOverrideMessages();

        if (this.hasOverride && this.overrideMessage != null)
        {
            this.lockOverrideMessages.add(StringUtils.translate(this.overrideMessage));
        }
    }

    @Override
    public void loadValueFromConfig(Boolean value)
    {
        this.booleanValue = value;
        this.updateEffectiveValue();
        super.loadValueFromConfig(value);
    }

    /**
     * Adds a listener that will get called when the value changes to true.
     * Note that these are not called when the value is deserialized from a config file.
     */
    public void addEnableListener(EventListener listener)
    {
        this.enableListeners.add(listener);
    }
}

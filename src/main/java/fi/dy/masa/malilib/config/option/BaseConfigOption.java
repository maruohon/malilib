package fi.dy.masa.malilib.config.option;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.ValueChangeCallback;
import fi.dy.masa.malilib.config.ValueLoadCallback;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public abstract class BaseConfigOption<T> extends BaseConfig implements ConfigOption<T>
{
    protected final List<String> lockOverrideMessages = new ArrayList<>(0);
    protected final List<EventListener> valueChangeListeners = new ArrayList<>(0);
    protected String prettyNameTranslationKey;
    protected boolean locked;
    @Nullable protected ValueChangeCallback<T> valueChangeCallback;
    @Nullable protected ValueLoadCallback<T> valueLoadCallback;
    @Nullable protected String lockMessage;
    @Nullable protected String overrideMessage;

    public BaseConfigOption(String name)
    {
        this(name, name, name, name);
    }

    public BaseConfigOption(String name, String commentTranslationKey, Object... commentArgs)
    {
        this(name, name, name, commentTranslationKey, commentArgs);
    }

    public BaseConfigOption(String name, String nameTranslationKey, String prettyNameTranslationKey,
                            @Nullable String commentTranslationKey, Object... commentArgs)
    {
        super(name, nameTranslationKey, commentTranslationKey, commentArgs);

        this.prettyNameTranslationKey = prettyNameTranslationKey;
    }

    @Override
    public String getPrettyName()
    {
        return StringUtils.translate(this.prettyNameTranslationKey);
    }

    @Override
    public List<String> getOldNames()
    {
        return this.oldNames;
    }

    /**
     * Returns the possible custom messages set to inform the user
     * about a locked or overridden config value.
     */
    public List<String> getLockAndOverrideMessages()
    {
        return this.lockOverrideMessages;
    }

    public void setLockMessage(@Nullable String translationKey)
    {
        this.lockMessage = translationKey;
        this.rebuildLockOverrideMessages();
    }

    public void setOverrideMessage(@Nullable String translationKey)
    {
        this.overrideMessage = translationKey;
        this.rebuildLockOverrideMessages();
    }

    protected void rebuildLockOverrideMessages()
    {
        this.lockOverrideMessages.clear();

        if (this.isLocked() && this.lockMessage != null)
        {
            StringUtils.translateAndLineSplit(this.lockOverrideMessages::add, this.lockMessage);
        }
    }

    public BaseConfigOption<T> setPrettyNameTranslationKey(String key)
    {
        this.prettyNameTranslationKey = key;
        return this;
    }

    @Override
    public void setModInfo(ModInfo modInfo)
    {
        super.setModInfo(modInfo);

        // TODO this method used to use getPrettyName() for the search strings... does that matter?

        if (this.prettyNameTranslationKey.equals(this.name))
        {
            this.prettyNameTranslationKey = this.createPrettyNameTranslationKey(this.getModInfo().getModId());
        }
    }

    @Override
    public void setValueChangeCallback(@Nullable ValueChangeCallback<T> callback)
    {
        this.valueChangeCallback = callback;
    }

    @Override
    public void setValueLoadCallback(@Nullable ValueLoadCallback<T> callback)
    {
        this.valueLoadCallback = callback;
    }

    @Override
    public void addValueChangeListener(EventListener listener)
    {
        this.valueChangeListeners.add(listener);
    }

    public void onValueChanged(T newValue, T oldValue)
    {
        if (this.valueChangeCallback != null)
        {
            this.valueChangeCallback.onValueChanged(newValue, oldValue);
        }

        for (EventListener listener : this.valueChangeListeners)
        {
            listener.onEvent();
        }
    }

    @Override
    public void onValueLoaded(T newValue)
    {
        if (this.valueLoadCallback != null)
        {
            this.valueLoadCallback.onValueLoaded(newValue);
        }
    }

    @Override
    public boolean isLocked()
    {
        return this.locked;
    }

    @Override
    public void setLocked(boolean isLocked)
    {
        this.locked = isLocked;
        this.rebuildLockOverrideMessages();
    }
}

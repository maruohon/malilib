package fi.dy.masa.malilib.config.option;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.ValueChangeCallback;
import fi.dy.masa.malilib.config.ValueLoadCallback;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class BaseConfig<T> implements ConfigOption<T>
{
    protected final String name;
    protected final List<String> searchStrings = new ArrayList<>();
    protected final List<String> lockOverrideMessages = new ArrayList<>();
    protected final List<String> oldNames = new ArrayList<>();
    protected String nameTranslationKey;
    protected String prettyNameTranslationKey;
    protected String commentTranslationKey;
    protected Object[] commentArgs;
    protected String modId = "?";
    protected String modName = "?";
    protected boolean locked;
    @Nullable protected ValueChangeCallback<T> valueChangeCallback;
    @Nullable protected ValueLoadCallback<T> valueLoadCallback;
    @Nullable protected EventListener labelClickHandler;
    @Nullable protected String lockMessage;
    @Nullable protected String overrideMessage;

    public BaseConfig(String name)
    {
        this(name, name, name, name);
    }

    public BaseConfig(String name, String commentTranslationKey, Object... commentArgs)
    {
        this(name, name, name, commentTranslationKey, commentArgs);
    }

    public BaseConfig(String name, String nameTranslationKey, String prettyNameTranslationKey,
                      String commentTranslationKey, Object... commentArgs)
    {
        this.name = name;
        this.nameTranslationKey = nameTranslationKey;
        this.prettyNameTranslationKey = prettyNameTranslationKey;
        this.commentTranslationKey = commentTranslationKey;
        this.commentArgs = commentArgs;
    }

    @Override
    public String getModId()
    {
        return this.modId;
    }

    @Override
    public String getModName()
    {
        return this.modName;
    }

    @Override
    public List<String> getSearchStrings()
    {
        return this.searchStrings;
    }

    /**
     * Returns the possible custom messages set to inform the user
     * about a locked or overridden config value.
     * @return
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

    @Override
    public void setModId(String modId)
    {
        this.modId = modId;

        // If these are still using the default values, generate the proper keys
        if (this.nameTranslationKey.equals(this.name))
        {
            this.nameTranslationKey = this.createNameTranslationKey(modId);
        }

        if (this.prettyNameTranslationKey.equals(this.name))
        {
            this.prettyNameTranslationKey = this.createPrettyNameTranslationKey(modId);
        }

        if (this.commentTranslationKey.equals(this.name))
        {
            this.commentTranslationKey = this.createCommentTranslationKey(modId);
        }

        if (this.searchStrings.isEmpty())
        {
            this.searchStrings.add(this.getPrettyName());
        }
    }

    @Override
    public void setModName(String modName)
    {
        this.modName = modName;
    }

    protected String createNameTranslationKey(String modId)
    {
        String nameLower = this.getName().toLowerCase(Locale.ROOT);
        return modId + ".config.name." + nameLower;
    }

    protected String createPrettyNameTranslationKey(String modId)
    {
        return this.createNameTranslationKey(modId);
    }

    protected String createCommentTranslationKey(String modId)
    {
        String nameLower = this.getName().toLowerCase(Locale.ROOT);
        return modId + ".config.comment." + nameLower;
    }

    public BaseConfig<T> setNameTranslationKey(String key)
    {
        this.nameTranslationKey = key;
        return this;
    }

    public BaseConfig<T> setPrettyNameTranslationKey(String key)
    {
        this.prettyNameTranslationKey = key;
        return this;
    }

    public BaseConfig<T> setCommentTranslationKey(String key)
    {
        this.commentTranslationKey = key;
        return this;
    }

    /**
     * Adds additional search terms to this config.
     * By default the pretty name is used for searching against.
     *
     * @param searchTerms
     * @return
     */
    public BaseConfig<T> addSearchTerms(Collection<String> searchTerms)
    {
        this.searchStrings.addAll(searchTerms);
        return this;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getDisplayName()
    {
        return getDefaultDisplayName(this, this.nameTranslationKey);
    }

    @Override
    public String getPrettyName()
    {
        return StringUtils.translate(this.prettyNameTranslationKey);
    }

    @Override
    @Nullable
    public String getComment()
    {
        return StringUtils.translate(this.commentTranslationKey, this.commentArgs);
    }

    public BaseConfig<T> setCommentArgs(Object... args)
    {
        this.commentArgs = args;
        return this;
    }

    @Override
    public List<String> getOldNames()
    {
        return this.oldNames;
    }

    public void setOldNames(String... names)
    {
        this.oldNames.clear();
        this.oldNames.addAll(Arrays.asList(names));
    }

    @Override
    @Nullable
    public EventListener getLabelClickHandler()
    {
        return this.labelClickHandler;
    }

    public void setLabelClickHandler(@Nullable EventListener clickHandler)
    {
        this.labelClickHandler = clickHandler;
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

    public void onValueChanged(T newValue, T oldValue)
    {
        if (this.valueChangeCallback != null)
        {
            this.valueChangeCallback.onValueChanged(newValue, oldValue);
        }
    }

    public void onValueLoaded(T newValue)
    {
        if (this.valueLoadCallback != null)
        {
            this.valueLoadCallback.onValueLoaded(newValue);
        }
    }

    public static String getDefaultDisplayName(ConfigInfo config, String nameTranslationKey)
    {
        String key = nameTranslationKey;
        String translatedName = StringUtils.translate(key);

        // If there is no translation for the config name, then show the actual base name
        return translatedName.equals(key) ? config.getName() : translatedName;
    }

    /**
     * Whether or not this config is currently locked to its current value,
     * and can not be changed without unlocking.
     *
     * @return true if the config is locked
     */
    @Override
    public boolean isLocked()
    {
        return this.locked;
    }

    /**
     * Set whether or not this config should be locked to its current value
     * @param isLocked
     */
    @Override
    public void setLocked(boolean isLocked)
    {
        this.locked = isLocked;
    }
}

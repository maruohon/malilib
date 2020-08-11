package fi.dy.masa.malilib.config.option;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.ValueChangeCallback;
import fi.dy.masa.malilib.config.ValueLoadedCallback;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class BaseConfig<T> implements ConfigOption<T>
{
    protected final String name;
    protected final List<String> searchStrings = new ArrayList<>();
    protected String nameTranslationKey;
    protected String prettyNameTranslationKey;
    protected String commentTranslationKey;
    protected Object[] commentArgs;
    protected String modId = "?";
    @Nullable
    protected ValueChangeCallback<T> valueChangeCallback;
    @Nullable
    protected ValueLoadedCallback<T> valueLoadCallback;

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
    public List<String> getSearchStrings()
    {
        return this.searchStrings;
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
    public String getConfigNameTranslationKey()
    {
        return this.nameTranslationKey;
    }

    @Override
    public String getPrettyName()
    {
        return StringUtils.translate(this.prettyNameTranslationKey);
    }

    @Override
    @Nullable
    public String getCommentTranslationKey()
    {
        return this.commentTranslationKey;
    }

    @Override
    @Nullable
    public String getComment()
    {
        return StringUtils.translate(this.getCommentTranslationKey(), this.commentArgs);
    }

    public BaseConfig<T> setCommentArgs(Object... args)
    {
        this.commentArgs = args;
        return this;
    }

    @Override
    public void setValueChangeCallback(@Nullable ValueChangeCallback<T> callback)
    {
        this.valueChangeCallback = callback;
    }

    @Override
    public void setValueLoadCallback(@Nullable ValueLoadedCallback<T> callback)
    {
        this.valueLoadCallback = callback;
    }

    @Override
    public void onValueChanged(T newValue, T oldValue)
    {
        if (this.valueChangeCallback != null)
        {
            this.valueChangeCallback.onValueChanged(newValue, oldValue);
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
}

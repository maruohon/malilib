package fi.dy.masa.malilib.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class NamedAction
{
    protected final ModInfo mod;
    protected final String name;
    protected final String registryName;
    protected String translationKey;
    @Nullable
    protected String commentTranslationKey;
    protected Action action;
    protected boolean needsArguments;

    protected NamedAction(ModInfo mod, String name)
    {
        this(mod, name, null);
    }

    public NamedAction(ModInfo mod, String name, Action action)
    {
        this(mod, name, createRegistryNameFor(mod, name), createTranslationKeyFor(mod, name), action);
    }

    public NamedAction(ModInfo mod, String name, String registryName,
                       String translationKey, @Nullable Action action)
    {
        this.mod = mod;
        this.name = name;
        this.registryName = registryName;
        this.translationKey = translationKey;
        this.action = action;
    }

    public ModInfo getMod()
    {
        return this.mod;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    public String getRegistryName()
    {
        return this.registryName;
    }

    public Action getAction()
    {
        return this.action;
    }

    public boolean getNeedsArguments()
    {
        return this.needsArguments;
    }

    public AliasAction createAlias(String aliasName, @Nullable String argument)
    {
        return new AliasAction(aliasName, this);
    }

    @Nullable
    public String getComment()
    {
        if (this.commentTranslationKey != null)
        {
            return StringUtils.translate(this.commentTranslationKey);
        }

        return null;
    }

    public List<String> getSearchString()
    {
        return ImmutableList.of(this.getName(), this.getDisplayName());
    }

    public String getWidgetDisplayName()
    {
        String name = this.getName();
        String modName = this.getMod().getModName();
        return StringUtils.translate("malilib.label.named_action_entry_widget.name", name, modName);
    }

    public List<String> getHoverInfo()
    {
        List<String> list = new ArrayList<>();

        list.add(StringUtils.translate("malilib.hover_info.action.mod", this.mod.getModName()));
        list.add(StringUtils.translate("malilib.hover_info.action.name", this.name));
        list.add(StringUtils.translate("malilib.hover_info.action.display_name", this.getDisplayName()));
        list.add(StringUtils.translate("malilib.hover_info.action.registry_name", this.registryName));

        return list;
    }

    public NamedAction setNameTranslationKey(String translationKey)
    {
        this.translationKey = translationKey;
        return this;
    }

    public NamedAction setCommentTranslationKey(String translationKey)
    {
        this.commentTranslationKey = translationKey;
        return this;
    }

    /**
     * Sets a comment translation key in the format "modid.action.comment.action_name",
     * if a translation exists for that key.
     */
    public void setCommentIfTranslationExists(String modId, String name)
    {
        String key = modId + ".action.comment." + name.toLowerCase(Locale.ROOT);
        String comment = StringUtils.translate(key);

        if (key.equals(comment) == false)
        {
            this.setCommentTranslationKey(key);
        }
    }

    @Nullable
    public JsonObject toJson()
    {
        return null;
    }

    public NamedAction fromJson(JsonElement el)
    {
        return this;
    }

    public static NamedAction of(ModInfo mod, String name, Action action)
    {
        return new NamedAction(mod, name, action);
    }

    public static NamedAction of(ModInfo mod, String name, EventListener listener)
    {
        return new NamedAction(mod, name, EventAction.of(listener));
    }

    public static NamedAction createToggleActionWithToggleMessage(ModInfo mod, String name, BooleanConfig config)
    {
        return createToggleActionWithToggleMessage(mod, name, config, null);
    }

    public static NamedAction createToggleActionWithToggleMessage(ModInfo mod, String name, BooleanConfig config,
                                                                  @Nullable Function<BooleanConfig, String> messageFactory)
    {
        return new NamedAction(mod, name, BooleanToggleAction.of(config, messageFactory));
    }

    public static NamedAction register(ModInfo modInfo, String name, EventListener action)
    {
        NamedAction namedAction = NamedAction.of(modInfo, name, action);
        namedAction.setCommentIfTranslationExists(modInfo.getModId(), name);
        ActionRegistry.INSTANCE.registerAction(namedAction);
        return namedAction;
    }

    public static NamedAction register(ModInfo modInfo, String name, Action action)
    {
        NamedAction namedAction = NamedAction.of(modInfo, name, action);
        namedAction.setCommentIfTranslationExists(modInfo.getModId(), name);
        ActionRegistry.INSTANCE.registerAction(namedAction);
        return namedAction;
    }

    public static NamedAction registerToggle(ModInfo modInfo, String name, BooleanConfig config)
    {
        NamedAction namedAction = NamedAction.createToggleActionWithToggleMessage(modInfo, name, config);
        namedAction.setCommentTranslationKey(config.getCommentTranslationKey());
        ActionRegistry.INSTANCE.registerAction(namedAction);
        return namedAction;
    }

    public static NamedAction registerToggleKey(ModInfo modInfo, String name, HotkeyedBooleanConfig config)
    {
        NamedAction namedAction = of(modInfo, name, config.getToggleAction());
        ActionRegistry.INSTANCE.registerAction(namedAction);
        return namedAction;
    }

    /**
     * Constructs the default registry name for the given action,
     * in the format "modid:action_name".
     */
    public static String createRegistryNameFor(ModInfo modInfo, String name)
    {
        return modInfo.getModId() + ":" + name;
    }

    /**
     * Constructs the default translation key for the given action.
     * Tries, in order the keys in the format "modid.action.name.action_name",
     * "modid.hotkey.name.action_name" and "modid.config.name.action_name"
     * to see which one has a translation.
     * If none of them do, then the name is returned as-is.
     */
    public static String createTranslationKeyFor(ModInfo modInfo, String name)
    {
        String modId = modInfo.getModId();
        String key = modId + ".action.name." + name.toLowerCase(Locale.ROOT);

        if (StringUtils.translate(key).equals(key) == false)
        {
            return key;
        }

        key = modId + ".hotkey.name." + name.toLowerCase(Locale.ROOT);

        if (StringUtils.translate(key).equals(key) == false)
        {
            return key;
        }

        key = modId + ".config.name." + name.toLowerCase(Locale.ROOT);

        if (StringUtils.translate(key).equals(key) == false)
        {
            return key;
        }

        return name;
    }
}

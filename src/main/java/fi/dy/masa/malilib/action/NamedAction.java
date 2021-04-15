package fi.dy.masa.malilib.action;

import java.util.Locale;
import java.util.function.Function;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class NamedAction
{
    protected final Action action;
    protected final ModInfo mod;
    protected final String name;
    protected final String registryName;
    protected final String translationKey;
    @Nullable protected String commentTranslationKey;

    public NamedAction(ModInfo mod, String name, Action action)
    {
        this.mod = mod;
        this.name = name;
        this.action = action;
        this.registryName = createRegistryNameFor(mod, name);
        this.translationKey = createTranslationKeyFor(mod, name);
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

    @Nullable
    public String getComment()
    {
        if (this.commentTranslationKey != null)
        {
            return StringUtils.translate(this.commentTranslationKey);
        }

        return null;
    }

    public NamedAction setCommentTranslationKey(String commentTranslationKey)
    {
        this.commentTranslationKey = commentTranslationKey;
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
     * Constructs the default translation key for the given action,
     * in the format "modid.action.name.action_name".
     */
    public static String createTranslationKeyFor(ModInfo modInfo, String name)
    {
        return modInfo.getModId() + ".action.name." + name.toLowerCase(Locale.ROOT);
    }
}

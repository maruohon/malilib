package fi.dy.masa.malilib.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class NamedAction
{
    protected final ModInfo mod;
    protected final String name;
    protected final String registryName;
    protected String translationKey;
    @Nullable protected String commentTranslationKey;
    protected Action action;
    protected boolean needsArguments;

    protected NamedAction(ModInfo mod, String name)
    {
        this(mod, name, null);
    }

    public NamedAction(ModInfo mod, String name, Action action)
    {
        this(mod, name,
             ActionUtils.createRegistryNameFor(mod, name),
             ActionUtils.createTranslationKeyFor(mod, name), action);
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

    public ActionResult execute()
    {
        return this.execute(new ActionContext());
    }

    public ActionResult execute(ActionContext ctx)
    {
        return this.action.execute(ctx);
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

    public StyledTextLine getWidgetDisplayName()
    {
        String name = this.getName();
        String modName = this.getMod().getModName();
        return StyledTextLine.translate("malilib.label.named_action_entry_widget.name", name, modName);
    }

    public List<StyledTextLine> getHoverInfo()
    {
        List<StyledTextLine> list = new ArrayList<>();

        list.add(StyledTextLine.translate("malilib.hover_info.action.mod", this.mod.getModName()));
        list.add(StyledTextLine.translate("malilib.hover_info.action.name", this.name));
        list.add(StyledTextLine.translate("malilib.hover_info.action.display_name", this.getDisplayName()));
        list.add(StyledTextLine.translate("malilib.hover_info.action.registry_name", this.registryName));

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

}

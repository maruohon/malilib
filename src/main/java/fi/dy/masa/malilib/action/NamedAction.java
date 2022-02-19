package fi.dy.masa.malilib.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.option.CommonDescription;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public abstract class NamedAction extends CommonDescription
{
    protected final ActionType<?> type;
    protected String coloredDisplayNameTranslationKey = "malilib.label.actions.simple_entry_widget_name";
    @Nullable protected String registryName;

    public NamedAction(ActionType<?> type,
                       String name,
                       String translationKey,
                       ModInfo mod)
    {
        super(name, mod);

        this.type = type;
        this.nameTranslationKey = translationKey;
        this.setActionCommentIfTranslationExists(mod.getModId(), name);
    }

    public abstract ActionResult execute(ActionContext ctx);

    public ActionResult execute()
    {
        return this.execute(ActionContext.COMMON);
    }

    @Nullable
    public String getRegistryName()
    {
        return this.registryName;
    }

    public void setRegistryName(@Nullable String registryName)
    {
        this.registryName = registryName;
    }

    public List<String> getSearchString()
    {
        return ImmutableList.of(this.getName(), this.getDisplayName());
    }

    public StyledTextLine getColoredWidgetDisplayName()
    {
        String name = this.getName();
        String modName = this.modInfo.getModName();
        return StyledTextLine.translate(this.coloredDisplayNameTranslationKey, name, modName);
    }

    public List<StyledTextLine> getHoverInfo()
    {
        List<StyledTextLine> lines = new ArrayList<>();

        lines.add(StyledTextLine.translate("malilib.hover.action.name", this.name));
        lines.add(StyledTextLine.translate("malilib.hover.action.mod", this.modInfo.getModName()));
        lines.add(StyledTextLine.translate("malilib.hover.action.display_name", this.getDisplayName()));
        lines.add(StyledTextLine.translate("malilib.hover.action.action_type", this.type.getDisplayName()));

        if (this.registryName != null)
        {
            lines.add(StyledTextLine.translate("malilib.hover.action.registry_name", this.registryName));
        }

        return lines;
    }

    /**
     * Sets a comment translation key in the format "modid.action.comment.action_name",
     * if a translation exists for that key.
     */
    public void setActionCommentIfTranslationExists(String modId, String name)
    {
        String key = modId + ".action.comment." + name.toLowerCase(Locale.ROOT);
        this.setCommentIfTranslationExists(key);
    }

    public AliasAction createAlias(String aliasName)
    {
        return new AliasAction(aliasName, this);
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("type", this.type.getId());

        if (this.registryName != null)
        {
            obj.addProperty("reg_name", this.registryName);
        }

        return obj;
    }

    public NamedAction loadFromJson(JsonObject obj)
    {
        return this;
    }

    @Nullable
    public static NamedAction baseActionFromJson(JsonObject obj)
    {
        if (JsonUtils.hasString(obj, "reg_name"))
        {
            String regName = JsonUtils.getString(obj, "reg_name");
            return Registry.ACTION_REGISTRY.getAction(regName);
        }

        return null;
    }
}

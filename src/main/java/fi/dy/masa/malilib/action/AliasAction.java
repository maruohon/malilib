package fi.dy.masa.malilib.action;

import java.util.List;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class AliasAction extends NamedAction
{
    protected static final ModInfo ALIAS_MOD_INFO = createAliasModInfo();

    protected final NamedAction originalAction;

    public AliasAction(String alias, NamedAction action)
    {
        super(alias, "<alias>:" + alias, action.getNameTranslationKey(), ALIAS_MOD_INFO);

        this.originalAction = action;
    }

    public String getOriginalRegistryName()
    {
        return this.originalAction.getRegistryName();
    }

    @Override
    public ActionResult execute(ActionContext ctx)
    {
        return this.originalAction.execute(ctx);
    }

    @Override
    public StyledTextLine getWidgetDisplayName()
    {
        String originalName = this.originalAction.getName();
        String modName = this.originalAction.getModInfo().getModName();
        return StyledTextLine.translate("malilib.label.named_action_alias_entry_widget.name",
                                        this.getName(), modName, originalName);
    }

    @Override
    public List<StyledTextLine> getHoverInfo()
    {
        List<StyledTextLine> list = super.getHoverInfo();
        String origRegName = this.originalAction.getRegistryName();

        list.add(0, StyledTextLine.translate("malilib.hover_info.action.alias", this.getName()));
        list.add(StyledTextLine.translate("malilib.hover_info.action.original_registry_name", origRegName));

        return list;
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("alias", this.name);
        obj.addProperty("reg_name", this.originalAction.getRegistryName());
        return obj;
    }

    public static AliasAction aliasFromJson(ActionRegistry registry, JsonElement el)
    {
        NamedAction action = null;
        String aliasName = "?";
        String regName = "?";

        if (el.isJsonObject())
        {
            JsonObject obj = el.getAsJsonObject();

            if (JsonUtils.hasString(obj, "alias") &&
                JsonUtils.hasString(obj, "reg_name"))
            {
                aliasName = JsonUtils.getString(obj, "alias");
                regName = JsonUtils.getString(obj, "reg_name");
                action = registry.getAction(regName);
            }
        }

        if (action == null)
        {
            // Preserve entries in the config file if a mod is temporarily disabled/removed, for example
            // FIXME use a DummyAction that contains the JsonObject?
            action = new SimpleNamedAction(aliasName, regName, aliasName, ModInfo.NO_MOD, (ctx) -> ActionResult.PASS);
        }

        return new AliasAction(aliasName, action);
    }

    public static ModInfo createAliasModInfo()
    {
        return new ModInfo("<alias>", StringUtils.translate("malilib.label.actions.alias_action"));
    }

    @Override
    public AliasAction createAlias(String aliasName)
    {
        return new AliasAction(aliasName, this.originalAction);
    }
}

package fi.dy.masa.malilib.action;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class AliasAction extends NamedAction
{
    protected static final ModInfo ALIAS_MOD_INFO = createAliasModInfo();

    protected final NamedAction baseAction;

    public AliasAction(String alias, NamedAction action)
    {
        super(ActionType.ALIAS, alias, action.getNameTranslationKey(), ALIAS_MOD_INFO);

        this.baseAction = action;
    }

    @Override
    public ActionResult execute(ActionContext ctx)
    {
        return this.baseAction.execute(ctx);
    }

    @Override
    public StyledTextLine getWidgetDisplayName()
    {
        String originalName = this.baseAction.getName();
        String modName = this.baseAction.getModInfo().getModName();
        return StyledTextLine.translate("malilib.label.named_action_alias_entry_widget.name",
                                        this.getName(), modName, originalName);
    }

    @Override
    public List<StyledTextLine> getHoverInfo()
    {
        List<StyledTextLine> lines = new ArrayList<>();
        String origRegName = this.baseAction.getRegistryName();

        lines.add(StyledTextLine.translate("malilib.hover_info.action.alias", this.getName()));

        if (this.registryName != null)
        {
            lines.add(StyledTextLine.translate("malilib.hover_info.action.registry_name", this.registryName));
        }

        lines.add(StyledTextLine.translate("malilib.hover_info.action.base_action_name", this.baseAction.getName()));
        lines.add(StyledTextLine.translate("malilib.hover_info.action.display_name", this.baseAction.getDisplayName()));
        lines.add(StyledTextLine.translate("malilib.hover_info.action.mod", this.baseAction.getModInfo().getModName()));

        if (origRegName != null)
        {
            lines.add(StyledTextLine.translate("malilib.hover_info.action.original_registry_name", origRegName));
        }

        return lines;
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();
        String regName = this.baseAction.getRegistryName();

        if (regName != null)
        {
            obj.addProperty("parent", regName);
        }

        obj.addProperty("alias", this.name);

        return obj;
    }

    public static AliasAction aliasActionFromJson(JsonObject obj)
    {
        NamedAction action = NamedAction.baseActionFromJson(obj);

        if (action instanceof AliasAction)
        {
            return (AliasAction) action;
        }

        String aliasName = "?";
        NamedAction baseAction = null;

        if (JsonUtils.hasString(obj, "parent") &&
            JsonUtils.hasString(obj, "alias"))
        {
            String regName = JsonUtils.getString(obj, "parent");
            aliasName = JsonUtils.getString(obj, "alias");
            baseAction = Registry.ACTION_REGISTRY.getAction(regName);
        }

        if (baseAction == null)
        {
            // Preserve entries in the config file if a mod is temporarily disabled/removed, for example
            // FIXME use a DummyAction that contains the JsonObject?
            baseAction = new SimpleNamedAction(aliasName, aliasName, ModInfo.NO_MOD, (ctx) -> ActionResult.PASS);
        }

        return new AliasAction(aliasName, baseAction);
    }

    public static ModInfo createAliasModInfo()
    {
        return new ModInfo("<alias>", StringUtils.translate("malilib.label.actions.alias_action"));
    }

    @Override
    public AliasAction createAlias(String aliasName)
    {
        return new AliasAction(aliasName, this.baseAction);
    }
}

package fi.dy.masa.malilib.action;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.data.ModInfo;
import fi.dy.masa.malilib.util.data.json.JsonUtils;

public class ParameterizedNamedAction extends NamedAction
{
    protected final ParameterizableNamedAction baseAction;
    protected final String argument;

    public ParameterizedNamedAction(String name,
                                    ParameterizableNamedAction baseAction,
                                    String argument)
    {
        super(ActionType.PARAMETERIZED, name, baseAction.getNameTranslationKey(), baseAction.getModInfo());

        this.baseAction = baseAction;
        this.argument = argument;
        this.coloredDisplayNameTranslationKey = "malilib.label.actions.parameterized_entry_widget_name";
    }

    @Override
    public boolean isUserAdded()
    {
        return true;
    }

    @Override
    public ActionResult execute(ActionContext ctx)
    {
        return this.baseAction.action.executeWithArgument(ctx, this.argument);
    }

    public String getArgument()
    {
        return this.argument;
    }

    @Override
    public StyledTextLine getColoredWidgetDisplayName()
    {
        String name = this.getName();
        String originalName = this.baseAction.getName();
        String modName = this.baseAction.getModInfo().getModName();
        return StyledTextLine.translate(this.coloredDisplayNameTranslationKey, name, modName, originalName);
    }

    @Override
    public List<StyledTextLine> getHoverInfo()
    {
        List<StyledTextLine> lines = new ArrayList<>();

        lines.add(StyledTextLine.translate("malilib.hover.action.name", this.getName()));
        lines.add(StyledTextLine.translate("malilib.hover.action.mod", this.baseAction.getModInfo().getModName()));
        lines.add(StyledTextLine.translate("malilib.hover.action.display_name", this.baseAction.getDisplayName()));
        lines.add(StyledTextLine.translate("malilib.hover.action.action_type", this.type.getDisplayName()));
        lines.add(StyledTextLine.translate("malilib.hover.action.base_action_name", this.baseAction.getName()));

        String regName = this.baseAction.getRegistryName();

        if (regName != null)
        {
            lines.add(StyledTextLine.translate("malilib.hover.action.base_action_registry_name", regName));
        }

        StyledTextLine start = StyledTextLine.translate("malilib.hover.action.parameterized_action_argument");
        lines.add(start.append(StyledTextLine.rawWithStyle(this.argument, start.getLastStyle())));

        return lines;
    }

    @Override
    @Nullable
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();
        String regName = this.baseAction.getRegistryName();

        if (regName != null)
        {
            obj.addProperty("parent", regName);
        }

        obj.addProperty("name", this.name);
        obj.addProperty("arg", this.argument);

        return obj;
    }

    public ParameterizedNamedAction createCopy(String newName, String newArgument)
    {
        return this.baseAction.parameterize(newName, newArgument);
    }

    @Nullable
    public static ParameterizedNamedAction parameterizedActionFromJson(JsonObject obj)
    {
        NamedAction action = NamedAction.baseActionFromJson(obj);

        if (action instanceof ParameterizedNamedAction)
        {
            return (ParameterizedNamedAction) action;
        }

        if (JsonUtils.hasString(obj, "parent") &&
            JsonUtils.hasString(obj, "name") &&
            JsonUtils.hasString(obj, "arg"))
        {
            String parentRegName = JsonUtils.getString(obj, "parent");
            String name = JsonUtils.getString(obj, "name");
            String argument = JsonUtils.getString(obj, "arg");
            NamedAction baseAction = Registry.ACTION_REGISTRY.getAction(parentRegName);

            if ((baseAction instanceof ParameterizableNamedAction) == false)
            {
                // Preserve entries in the config if the mod adding the action is temporarily disabled/removed
                baseAction = new ParameterizableNamedAction("<N/A>", "<N/A>", ModInfo.NO_MOD, (ctx, a) -> ActionResult.PASS);
                baseAction.setRegistryName(parentRegName);
            }

            return of(name, (ParameterizableNamedAction) baseAction, argument);
        }

        return null;
    }

    public static ParameterizedNamedAction of(String name, ParameterizableNamedAction baseAction, String argument)
    {
        //String registryName = "<parameterized>:" + baseAction.getRegistryName() + ":" + argument;
        return new ParameterizedNamedAction(name, baseAction, argument);
    }
}

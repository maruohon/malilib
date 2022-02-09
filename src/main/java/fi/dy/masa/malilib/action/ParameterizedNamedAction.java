package fi.dy.masa.malilib.action;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

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
        this.coloredDisplayNameTranslationKey = "malilib.label.name.action.parameterized_entry_widget_name";
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

        lines.add(StyledTextLine.translate("malilib.hover_info.action.name", this.getName()));
        lines.add(StyledTextLine.translate("malilib.hover_info.action.action_type", this.type.getGroup().getDisplayName()));
        lines.add(StyledTextLine.translate("malilib.hover_info.action.mod", this.baseAction.getModInfo().getModName()));
        lines.add(StyledTextLine.translate("malilib.hover_info.action.base_action_name", this.baseAction.getName()));
        StyledTextLine start = StyledTextLine.translate("malilib.hover_info.action.argument.colon");
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
                baseAction = new ParameterizableNamedAction("?", "?", ModInfo.NO_MOD, (ctx, a) -> ActionResult.PASS);
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

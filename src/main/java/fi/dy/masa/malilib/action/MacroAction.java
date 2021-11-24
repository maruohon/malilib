package fi.dy.masa.malilib.action;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class MacroAction extends NamedAction
{
    protected static final ModInfo MACRO_MOD_INFO = getMacroModInfo();

    protected ImmutableList<NamedAction> actionList;

    public MacroAction(String name, ImmutableList<NamedAction> actionList)
    {
        super(MACRO_MOD_INFO, name, name, name);

        this.actionList = actionList;
    }

    public ImmutableList<NamedAction> getActionList()
    {
        return this.actionList;
    }

    @Override
    public ActionResult execute(ActionContext ctx)
    {
        for (NamedAction action : this.actionList)
        {
            action.execute(ctx);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public StyledTextLine getWidgetDisplayName()
    {
        String name = this.getName();
        int size = this.getActionList().size();
        return StyledTextLine.translate("malilib.label.macro_action_entry_widget.name", name, size);
    }

    @Override
    public List<StyledTextLine> getHoverInfo()
    {
        List<StyledTextLine> lines = new ArrayList<>();
        lines.add(StyledTextLine.translate("malilib.hover_info.action.name", this.getName()));

        int size = this.actionList.size();

        if (size > 0)
        {
            lines.add(StyledTextLine.translate("malilib.hover_info.action.contained_actions", size));

            int count = Math.min(size, 8);

            for (int i = 0; i < count; ++i)
            {
                NamedAction action = this.actionList.get(i);
                lines.add(StyledTextLine.of("  " + action.getName()));
            }

            if (size > count)
            {
                lines.add(StyledTextLine.translate("malilib.gui.button.hover.entries_more", size - count));
            }
        }

        return lines;
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();

        for (NamedAction action : this.actionList)
        {
            arr.add(action.getRegistryName());
        }

        obj.add("actions", arr);

        return obj;
    }

    public static MacroAction macroFromJson(ActionRegistry registry, String name, JsonElement el)
    {
        ArrayList<NamedAction> actions = new ArrayList<>();

        if (el.isJsonObject())
        {
            JsonUtils.readArrayElementsIfPresent(el.getAsJsonObject(), "actions",
                                                 (e) -> MacroAction.readAction(e, registry, actions));
        }

        return new MacroAction(name, ImmutableList.copyOf(actions));
    }

    protected static void readAction(JsonElement el, ActionRegistry registry, ArrayList<NamedAction> actions)
    {
        String registryName = el.getAsString();
        NamedAction action = registry.getAction(registryName);

        if (action == null)
        {
            // Preserve entries in the config file if a mod is temporarily disabled/removed, for example
            action = new SimpleNamedAction((ctx) -> ActionResult.PASS, ModInfo.NO_MOD, registryName,
                                           registryName, registryName);
        }

        actions.add(action);
    }

    public static ModInfo getMacroModInfo()
    {
        String name = StringUtils.translate("malilib.label.macro.angle_brackets");
        return new ModInfo(name, name);
    }
}

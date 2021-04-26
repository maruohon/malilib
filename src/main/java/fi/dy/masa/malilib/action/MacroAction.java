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
    protected ImmutableList<NamedAction> actionList;

    public MacroAction(String name, ImmutableList<NamedAction> actionList)
    {
        super(getMacroModInfo(), name, name, name, null);

        this.action = this::executeMacro;
        this.actionList = actionList;
    }

    public ImmutableList<NamedAction> getActionList()
    {
        return this.actionList;
    }

    public ActionResult executeMacro(ActionContext ctx)
    {
        for (NamedAction action : this.actionList)
        {
            action.getAction().execute(ctx);
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

    @Override
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

        if (el.isJsonObject() && JsonUtils.hasArray(el.getAsJsonObject(), "actions"))
        {
            JsonArray arr = el.getAsJsonObject().get("actions").getAsJsonArray();
            int size = arr.size();

            for (int i = 0; i < size; ++i)
            {
                String registryName = arr.get(i).getAsString();
                NamedAction action = registry.getAction(registryName);

                if (action == null)
                {
                    // Preserve entries in the config file if a mod is temporarily disabled/removed, for example
                    action = new NamedAction(ModInfo.NO_MOD, registryName, registryName, registryName, (ctx) -> ActionResult.PASS);
                }

                actions.add(action);
            }
        }

        return new MacroAction(name, ImmutableList.copyOf(actions));
    }

    public static ModInfo getMacroModInfo()
    {
        String name = StringUtils.translate("malilib.label.macro.angle_brackets");
        return new ModInfo(name, name);
    }
}

package fi.dy.masa.malilib.action;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
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
        super(ActionType.MACRO, name, name, MACRO_MOD_INFO);

        this.actionList = actionList;
    }

    public ImmutableList<NamedAction> getActionList()
    {
        return this.actionList;
    }

    public void setActionList(ImmutableList<NamedAction> actionList)
    {
        this.actionList = actionList;
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

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();
        JsonArray arr = new JsonArray();

        for (NamedAction action : this.actionList)
        {
            arr.add(action.toJson());
        }

        obj.addProperty("name", this.name);
        obj.add("actions", arr);

        return obj;
    }

    @Nullable
    public static MacroAction macroActionFromJson(JsonObject obj)
    {
        ImmutableList.Builder<NamedAction> builder = ImmutableList.builder();

        if (JsonUtils.hasArray(obj, "actions") &&
            JsonUtils.hasString(obj, "name"))
        {
            String name = JsonUtils.getString(obj, "name");
            JsonUtils.readArrayElementsIfPresent(obj, "actions", (e) -> readMacroMemberAction(e, builder::add));

            return new MacroAction(name, builder.build());
        }

        return null;
    }

    protected static void readMacroMemberAction(JsonElement el, Consumer<NamedAction> consumer)
    {
        if (el.isJsonObject())
        {
            NamedAction action = ActionType.loadActionFromJson(el);

            if (action == null)
            {
                // Preserve entries in the config file if a mod is temporarily disabled/removed, for example
                //action = new PlaceholderAction(el.getAsJsonObject());
            }

            consumer.accept(action);
        }
    }

    public static ModInfo getMacroModInfo()
    {
        return new ModInfo("<macro>", StringUtils.translate("malilib.label.actions.macro_action"));
    }
}

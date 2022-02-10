package fi.dy.masa.malilib.action;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
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
        this.coloredDisplayNameTranslationKey = "malilib.label.name.action.macro_entry_widget_name";
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
    public StyledTextLine getColoredWidgetDisplayName()
    {
        String name = this.getName();
        int size = this.getActionList().size();
        return StyledTextLine.translate(this.coloredDisplayNameTranslationKey, name, size);
    }

    @Override
    public List<StyledTextLine> getHoverInfo()
    {
        List<StyledTextLine> lines = new ArrayList<>();
        lines.add(StyledTextLine.translate("malilib.hover_info.action.name", this.getName()));
        lines.add(StyledTextLine.translate("malilib.hover_info.action.action_type", this.type.getDisplayName()));

        if (this.registryName != null)
        {
            lines.add(StyledTextLine.translate("malilib.hover_info.action.registry_name", this.registryName));
        }

        getContainedActionsTooltip(this.actionList, lines::add, 8);

        return lines;
    }

    public static void getContainedActionsTooltip(List<NamedAction> actions,
                                                  Consumer<StyledTextLine> consumer,
                                                  int maxEntriesShown)
    {
        int size = actions.size();

        if (size > 0)
        {
            consumer.accept(StyledTextLine.translate("malilib.hover_info.action.contained_actions", size));

            int count = Math.min(size, maxEntriesShown);

            for (int i = 0; i < count; ++i)
            {
                consumer.accept(StyledTextLine.of("  " + actions.get(i).getName()));
            }

            if (size > count)
            {
                consumer.accept(StyledTextLine.translate("malilib.hover_info.action.contained_actions.more", size - count));
            }
        }
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();
        obj.addProperty("name", this.name);
        obj.add("actions", JsonUtils.toArray(this.actionList, NamedAction::toJson));
        return obj;
    }

    @Nullable
    public static MacroAction macroActionFromJson(JsonObject obj)
    {
        NamedAction action = NamedAction.baseActionFromJson(obj);

        if (action instanceof MacroAction)
        {
            return (MacroAction) action;
        }

        if (JsonUtils.hasArray(obj, "actions") &&
            JsonUtils.hasString(obj, "name"))
        {
            String name = JsonUtils.getString(obj, "name");
            return new MacroAction(name, ActionUtils.readActionsFromList(obj, "actions"));
        }

        return null;
    }

    public static ModInfo getMacroModInfo()
    {
        return new ModInfo("<macro>", StringUtils.translate("malilib.label.actions.macro_action"));
    }
}

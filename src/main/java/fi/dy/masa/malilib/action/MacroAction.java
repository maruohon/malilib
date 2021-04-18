package fi.dy.masa.malilib.action;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.input.ActionResult;
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
    public List<String> getHoverInfo()
    {
        List<String> lines = new ArrayList<>();
        lines.add(StringUtils.translate("malilib.hover_info.action.name", this.getName()));

        int size = this.actionList.size();

        if (size > 0)
        {
            lines.add(StringUtils.translate("malilib.hover_info.action.contained_actions", size));

            int count = Math.min(size, 8);

            for (int i = 0; i < count; ++i)
            {
                NamedAction action = this.actionList.get(i);
                lines.add("  " + action.getName());
            }

            if (size > count)
            {
                lines.add(StringUtils.translate("malilib.gui.button.hover.entries_more", size - count));
            }
        }

        return lines;
    }

    public static ModInfo getMacroModInfo()
    {
        String name = StringUtils.translate("malilib.label.macro.angle_brackets");
        return new ModInfo(name, name);
    }
}

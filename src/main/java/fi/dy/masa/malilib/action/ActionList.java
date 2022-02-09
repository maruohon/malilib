package fi.dy.masa.malilib.action;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.StringUtils;

public class ActionList
{
    public static final ActionList ALL_ACTIONS = new ActionList("all", "malilib.label.actions.lists.all_actions", Registry.ACTION_REGISTRY::getAllActions);
    public static final ActionList ALIASES = new ActionList("aliases", "malilib.label.actions.lists.all_aliases", Registry.ACTION_REGISTRY::getAliases);

    protected final String name;
    protected final String displayName;
    protected final Supplier<List<? extends NamedAction>> listSupplier;

    public ActionList(String name, String displayName, Supplier<List<? extends NamedAction>> listSupplier)
    {
        this.name = name;
        this.displayName = displayName;
        this.listSupplier = listSupplier;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDisplayName()
    {
        return StringUtils.translate(this.displayName);
    }

    public List<? extends NamedAction> getActions()
    {
        return this.listSupplier.get();
    }

    public static List<ActionList> getActionLists()
    {
        List<ActionList> list = new ArrayList<>();

        list.add(ALL_ACTIONS);
        list.add(ALIASES);

        for (MacroAction macro : Registry.ACTION_REGISTRY.getMacros())
        {
            String name = macro.getRegistryName();
            String displayName = StringUtils.translate("malilib.label.actions.lists.macro", macro.getName());
            list.add(new ActionList(name, displayName, macro::getActionList));
        }

        return list;
    }

    public static ActionList getSelectedList(List<ActionList> lists)
    {
        String name = MaLiLibConfigs.Internal.ACTION_PROMPT_SELECTED_LIST.getStringValue();

        for (ActionList list : lists)
        {
            if (list.getName().equals(name))
            {
                return list;
            }
        }

        return ALL_ACTIONS;
    }
}

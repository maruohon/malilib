package fi.dy.masa.malilib.action;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.MaLiLib;

public class ActionRegistryImpl implements ActionRegistry
{
    protected final HashMap<String, NamedAction> actionsByName = new HashMap<>();
    protected final HashMap<String, NamedAction> actionsByAlias = new HashMap<>();
    protected final ArrayListMultimap<NamedAction, String> aliasesForActions = ArrayListMultimap.create();
    protected final HashSet<NamedAction> allActions = new HashSet<>();
    @Nullable protected ImmutableList<NamedAction> allActionsImmutable;

    @Override
    public void registerAction(NamedAction action)
    {
        String name = action.getRegistryName();

        if (this.allActions.contains(action) == false)
        {
            this.allActions.add(action);
            this.actionsByName.put(name, action);
            this.actionsByAlias.put(name, action);
            this.aliasesForActions.put(action, name);
            this.allActionsImmutable = null; // set to be re-built
        }
        else
        {
            MaLiLib.LOGGER.warn("The action '{}' already exists, not registering it again", name);
        }
    }

    @Override
    public void unregisterAction(NamedAction action)
    {
        if (this.allActions.remove(action))
        {
            String name = action.getRegistryName();
            this.actionsByName.remove(name);

            for (String alias : this.aliasesForActions.get(action))
            {
                this.actionsByAlias.remove(alias);
            }

            this.aliasesForActions.removeAll(action);
            this.allActionsImmutable = null; // set to be re-built
        }
    }

    @Override
    public void addAlias(String alias, NamedAction action)
    {
        if (this.actionsByAlias.containsKey(alias) == false)
        {
            this.actionsByAlias.put(alias, action);
            this.aliasesForActions.put(action, alias);
        }
        else
        {
            MaLiLib.LOGGER.warn("The action alias '{}' already exists, not adding it again", alias);
        }
    }

    @Override
    public void removeAlias(String alias)
    {
        NamedAction action = this.actionsByAlias.remove(alias);

        if (action != null)
        {
            this.aliasesForActions.remove(action, alias);
        }
    }

    @Nullable
    @Override
    public NamedAction getAction(String name)
    {
        return this.actionsByAlias.get(name);
    }

    @Override
    public ImmutableList<NamedAction> getAllActions()
    {
        if (this.allActionsImmutable == null)
        {
            this.allActionsImmutable = ImmutableList.copyOf(this.getSortedActions());
        }

        return this.allActionsImmutable;
    }

    protected List<NamedAction> getSortedActions()
    {
        List<NamedAction> list = new ArrayList<>(this.allActions);
        list.sort(Comparator.comparing(NamedAction::getName));
        return list;
    }
}

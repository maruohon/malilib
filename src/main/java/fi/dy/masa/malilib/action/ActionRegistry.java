package fi.dy.masa.malilib.action;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

public interface ActionRegistry
{
    ActionRegistry INSTANCE = new ActionRegistryImpl();

    /**
     * Registers the given action.
     * The registry name of the action will be "modid:actionName"
     */
    void registerAction(NamedAction action);

    /**
     * Unregisters the given action
     */
    void unregisterAction(NamedAction action);

    /**
     * Adds an alias for an action
     */
    void addAlias(String alias, NamedAction action);

    /**
     * Removes the given alias
     */
    void removeAlias(String alias);

    /**
     * Gets an action by name, if one exists. Otherwise returns null.
     * The name should be either the registry name, ie. "modid:actionName", or an existing alias.
     */
    @Nullable
    NamedAction getAction(String name);

    /**
     * Returns a list of all the registered actions
     */
    ImmutableList<NamedAction> getAllActions();
}

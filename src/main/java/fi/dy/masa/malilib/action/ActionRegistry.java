package fi.dy.masa.malilib.action;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

public interface ActionRegistry
{
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
    boolean addAlias(AliasAction action);

    /**
     * Removes the given alias
     */
    void removeAlias(String name);

    /**
     * Adds a macro action
     */
    boolean addMacro(MacroAction action);

    /**
     * Removes a macro action by the given name
     */
    void removeMacro(String name);

    /**
     * Returns a MacroAction, if one exists by the given name
     */
    @Nullable
    MacroAction getMacro(String name);

    /**
     * 
     * Gets an action by name, if one exists. Otherwise returns null.
     * The name should be either the registry name, ie. "modid:actionName", or an existing alias.
     */
    @Nullable
    NamedAction getAction(String name);

    /**
     * @return Returns a list of all the registered "base" actions,
     *         ie. excluding aliases and macros.
     */
    ImmutableList<NamedAction> getBaseActions();

    /**
     * @return a list of all the currently defined action aliases
     */
    ImmutableList<AliasAction> getAliases();

    /**
     * @return a list of all macros (which are lists of other actions or aliases or macros)
     */
    ImmutableList<MacroAction> getMacros();

    /**
     * @return returns a full list of all actions, including aliases and macros
     */
    ImmutableList<NamedAction> getAllActions();
}

package fi.dy.masa.malilib.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.util.ConfigUtils;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.util.JsonUtils;

public class ActionRegistry
{
    protected final ActionStorage<NamedAction> baseActions = new ActionStorage<>();
    protected final ActionStorage<NamedAction> allActions = new ActionStorage<>();
    protected final ActionStorage<AliasAction> aliases = new ActionStorage<>();
    protected final ActionStorage<MacroAction> macros = new ActionStorage<>();
    protected final ActionStorage<ParameterizedNamedAction> parameterized = new ActionStorage<>();
    protected boolean dirty;

    protected void clearUserAddedActions()
    {
        this.allActions.clear();
        this.aliases.clear();
        this.macros.clear();
        this.parameterized.clear();
        this.allActions.putAll(this.baseActions);
    }

    /**
     * Registers the given action.
     * The registry name of the action will be "modid:actionName"
     * @return true on success, false on failure (if an action already exists by that name)
     */
    public boolean registerAction(NamedAction action)
    {
        String name = action.getRegistryName();

        if (this.baseActions.contains(name) == false)
        {
            this.baseActions.put(name, action);
            this.allActions.put(name, action);
            return true;
        }
        else
        {
            MaLiLib.LOGGER.warn("The action '{}' already exists, not registering it again", name);
        }

        return false;
    }

    /**
     * Adds an alias action, which is basically an existing action with a different name
     * @return true on success, false on failure (if an action already exists by that name)
     */
    public boolean addAlias(AliasAction action)
    {
        String name = action.getRegistryName();

        if (this.allActions.contains(name) == false)
        {
            this.aliases.put(name, action);
            this.allActions.put(name, action);
            this.dirty = true;
            return true;
        }
        else
        {
            MessageDispatcher.error().console().translate("malilib.message.error.action.action_name_exists", name);
        }

        return false;
    }

    /**
     * Adds a macro action, which is basically a list of other actions
     * @return true on success, false on failure (if an action already exists by that name)
     */
    public boolean addMacro(MacroAction action)
    {
        String name = action.getRegistryName();

        if (this.allActions.contains(name) == false)
        {
            // TODO/FIXME add checking for circular macros
            this.macros.put(name, action);
            this.allActions.put(name, action);
            this.dirty = true;
            return true;
        }
        else
        {
            MessageDispatcher.error().console().translate("malilib.message.error.action.action_name_exists", name);
        }

        return false;
    }

    /**
     * Adds a macro action, which is basically a list of other actions
     * @return true on success, false on failure (if an action already exists by that name)
     */
    public boolean addParameterizedAction(@Nullable ParameterizedNamedAction action)
    {
        if (action == null)
        {
            return false;
        }

        String name = action.getRegistryName();

        if (this.allActions.contains(name) == false)
        {
            this.parameterized.put(name, action);
            this.allActions.put(name, action);
            this.dirty = true;
            return true;
        }
        else
        {
            MessageDispatcher.error().console().translate("malilib.message.error.action.action_name_exists", name);
        }

        return false;
    }

    /**
     * Removes the given alias action, if it exists
     */
    public void removeAlias(String name)
    {
        if (this.aliases.remove(name) != null)
        {
            this.allActions.remove(name);
            this.dirty = true;
        }
    }

    /**
     * Removes a macro action by the given name, if one exists
     */
    public void removeMacro(String name)
    {
        if (this.macros.remove(name) != null)
        {
            this.allActions.remove(name);
            this.dirty = true;
        }
    }

    /**
     * Removes a parameterized action by the given name, if one exists
     */
    public void removeParameterizedAction(String name)
    {
        if (this.parameterized.remove(name) != null)
        {
            this.allActions.remove(name);
            this.dirty = true;
        }
    }

    /**
     * @return an action by the name, if one exists, otherwise returns null.
     * The name should be the registry name, for example "modid:actionName".
     */
    @Nullable
    public NamedAction getAction(String name)
    {
        return this.allActions.get(name);
    }

    /**
     * @return the MacroAction by the given name, if one exists
     */
    @Nullable
    public MacroAction getMacro(String name)
    {
        return this.macros.get(name);
    }

    /**
     * @return a list of all the registered "base" actions,
     *         ie. actions directly registered by mods,
     *         excluding any user-added aliases, macros or parameterized actions.
     */
    public ImmutableList<NamedAction> getBaseActions()
    {
        return this.baseActions.getActionList();
    }

    /**
     * @return a list of all the user-added action aliases
     */
    public ImmutableList<AliasAction> getAliases()
    {
        return this.aliases.getActionList();
    }

    /**
     * @return a list of all user-added macro actions (which are lists of other actions)
     */
    public ImmutableList<MacroAction> getMacros()
    {
        return this.macros.getActionList();
    }

    /**
     * @return a list of all user-added parameterized actions
     */
    public ImmutableList<ParameterizedNamedAction> getParameterizedActions()
    {
        return this.parameterized.getActionList();
    }

    /**
     * @return returns a full list of all actions, including aliases and macros
     */
    public ImmutableList<NamedAction> getAllActions()
    {
        return this.allActions.getActionList();
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.add("aliases", JsonUtils.toArray(this.aliases.getActionList(), AliasAction::toJson));
        obj.add("macros", JsonUtils.toArray(this.macros.getActionList(), MacroAction::toJson));
        obj.add("parameterized", JsonUtils.toArray(this.parameterized.getActionList(), ParameterizedNamedAction::toJson));

        return obj;
    }

    public void fromJson(JsonElement el)
    {
        this.clearUserAddedActions();
        JsonUtils.readArrayElementsIfPresent(el, "aliases", (e) -> this.addAlias(AliasAction.aliasFromJson(this, e)));
        JsonUtils.readArrayElementsIfPresent(el, "macros", (e) -> this.addMacro(MacroAction.macroFromJson(this, e)));
        JsonUtils.readArrayElementsIfPresent(el, "parameterized", (e) -> this.addParameterizedAction(ParameterizedNamedAction.fromJson(this, e)));
        this.dirty = false;
    }

    public boolean saveToFileIfDirty()
    {
        if (this.dirty)
        {
            return this.saveToFile();
        }

        return false;
    }

    public boolean saveToFile()
    {
        File dir = ConfigUtils.getActiveConfigDirectory();
        File backupDir = new File(dir, "config_backups");
        File saveFile = new File(dir, MaLiLibReference.MOD_ID + "_actions.json");
        boolean antiDuplicate = MaLiLibConfigs.Generic.CONFIG_BACKUP_ANTI_DUPLICATE.getBooleanValue();
        boolean success = JsonUtils.saveToFile(dir, backupDir, saveFile, 10, antiDuplicate, this::toJson);

        if (success)
        {
            this.dirty = false;
        }

        return success;
    }

    public void loadFromFile()
    {
        File dir = ConfigUtils.getActiveConfigDirectory();
        JsonUtils.loadFromFile(dir, MaLiLibReference.MOD_ID + "_actions.json", this::fromJson);
    }

    public static <T extends NamedAction> ImmutableList<T> getActionsSortedByName(Collection<T> actions)
    {
        List<T> list = new ArrayList<>(actions);
        list.sort(Comparator.comparing(NamedAction::getName));
        return ImmutableList.copyOf(list);
    }
}

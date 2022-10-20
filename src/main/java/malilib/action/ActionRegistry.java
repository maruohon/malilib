package malilib.action;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import malilib.MaLiLib;
import malilib.MaLiLibReference;
import malilib.config.util.ConfigUtils;
import malilib.overlay.message.MessageDispatcher;
import malilib.util.BackupUtils;
import malilib.util.data.json.JsonUtils;

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
        String regName = ActionUtils.createRegistryNameFor(action.getModInfo(), action.getName());

        if (this.baseActions.contains(regName) == false)
        {
            action.setRegistryName(regName);
            this.baseActions.put(regName, action);
            this.allActions.put(regName, action);

            return true;
        }
        else
        {
            MaLiLib.LOGGER.warn("The action '{}' already exists, not registering it again", regName);
        }

        return false;
    }

    /**
     * Adds an alias action, which is basically an existing action with a different name
     * @return true on success, false on failure (if an action already exists by that name)
     */
    public boolean addAlias(@Nullable AliasAction action)
    {
        if (action == null)
        {
            return false;
        }

        String regName = ActionUtils.createRegistryNameFor(action.getModInfo(), action.getName());

        return this.addAction(action, regName, this.aliases);
    }

    /**
     * Adds a macro action, which is basically a list of other actions
     * @return true on success, false on failure (if an action already exists by that name)
     */
    public boolean addMacro(MacroAction action)
    {
        String regName = ActionUtils.createRegistryNameFor(action.getModInfo(), action.getName());
        return this.addAction(action, regName, this.macros);
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

        String baseRegName = ActionUtils.createRegistryNameFor(action.getModInfo(), action.getName());
        String regName = "<parameterized>:" + baseRegName + ";" + action.getArgument();

        return this.addAction(action, regName, this.parameterized);
    }

    private <T extends NamedAction> boolean addAction(T action, String registryName, ActionStorage<T> storage)
    {
        if (this.allActions.contains(registryName) == false)
        {
            action.setRegistryName(registryName);
            storage.put(registryName, action);
            this.allActions.put(registryName, action);
            this.dirty = true;

            return true;
        }
        else
        {
            MessageDispatcher.error().console().translate("malilibdev.message.error.action.action_name_exists", registryName);
        }

        return false;
    }

    /**
     * Removes the given alias action, if it exists
     */
    public boolean removeAlias(NamedAction action)
    {
        return this.removedAction(action, this.aliases);
    }

    /**
     * Removes a macro action by the given name, if one exists
     */
    public boolean removeMacro(NamedAction action)
    {
        return this.removedAction(action, this.macros);
    }

    /**
     * Removes a parameterized action by the given name, if one exists
     */
    public boolean removeParameterizedAction(NamedAction action)
    {
        return this.removedAction(action, this.parameterized);
    }

    private boolean removedAction(NamedAction action, ActionStorage<?> storage)
    {
        String regName = action.getRegistryName();

        if (regName != null)
        {
            action = storage.remove(regName);

            if (action != null)
            {
                action.setRegistryName(null);
                this.allActions.remove(regName);
                this.dirty = true;
                return true;
            }
        }

        return false;
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

        obj.add("parameterized", JsonUtils.toArray(this.parameterized.getActionList(), ParameterizedNamedAction::toJson));
        obj.add("aliases", JsonUtils.toArray(this.aliases.getActionList(), AliasAction::toJson));
        obj.add("macros", JsonUtils.toArray(this.macros.getActionList(), MacroAction::toJson));

        return obj;
    }

    public void fromJson(JsonElement el)
    {
        this.clearUserAddedActions();

        JsonUtils.readArrayElementsIfObjects(el, "parameterized", (o) -> this.addParameterizedAction(ParameterizedNamedAction.parameterizedActionFromJson(o)));
        JsonUtils.readArrayElementsIfObjects(el, "macros", (o) -> this.addMacro(MacroAction.macroActionFromJson(o)));
        JsonUtils.readArrayElementsIfObjects(el, "aliases", (o) -> this.addAlias(AliasAction.aliasActionFromJson(o)));

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
        Path configDir = ConfigUtils.getActiveConfigDirectory();
        Path saveFile = configDir.resolve(MaLiLibReference.MOD_ID).resolve("actions.json");
        Path backupDir = configDir.resolve("backups").resolve(MaLiLibReference.MOD_ID);

        if (BackupUtils.createRegularBackup(saveFile, backupDir) &&
            JsonUtils.writeJsonToFile(this.toJson(), saveFile))
        {
            this.dirty = false;
            return true;
        }

        return false;
    }

    public void loadFromFile()
    {
        Path configDir = ConfigUtils.getActiveConfigDirectory();
        Path saveFile = configDir.resolve(MaLiLibReference.MOD_ID).resolve("actions.json");
        JsonUtils.loadFromFile(saveFile, this::fromJson);
    }

    public static <T extends NamedAction> ImmutableList<T> getActionsSortedByName(Collection<T> actions)
    {
        List<T> list = new ArrayList<>(actions);
        list.sort(Comparator.comparing(NamedAction::getName));
        return ImmutableList.copyOf(list);
    }
}

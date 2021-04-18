package fi.dy.masa.malilib.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.overlay.message.MessageUtils;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class ActionRegistryImpl implements ActionRegistry
{
    protected static final NamedAction DUMMY = NamedAction.of(ModInfo.NO_MOD, "-", (ctx) -> ActionResult.PASS);

    protected final HashMap<String, NamedAction> baseActions = new HashMap<>();
    protected final HashMap<String, AliasAction> aliases = new HashMap<>();
    protected final HashMap<String, MacroAction> macros = new HashMap<>();
    protected final HashMap<String, NamedAction> allActions = new HashMap<>();
    protected final ArrayListMultimap<String, String> aliasesForActions = ArrayListMultimap.create();
    @Nullable protected ImmutableList<NamedAction> baseActionsImmutable;
    @Nullable protected ImmutableList<AliasAction> aliasesImmutable;
    @Nullable protected ImmutableList<MacroAction> macrosImmutable;
    @Nullable protected ImmutableList<NamedAction> allActionsImmutable;
    protected boolean dirty;

    protected void clearAliasesAndMacros()
    {
        this.allActions.clear();
        this.aliases.clear();
        this.macros.clear();
        this.aliasesForActions.clear();

        this.allActions.putAll(this.baseActions);

        // set to be re-built
        this.allActionsImmutable = null;
        this.aliasesImmutable = null;
        this.macrosImmutable = null;
    }

    @Override
    public void registerAction(NamedAction action)
    {
        String name = action.getRegistryName();

        if (this.baseActions.containsKey(name) == false)
        {
            this.baseActions.put(name, action);
            this.allActions.put(name, action);

            // set to be re-built
            this.baseActionsImmutable = null;
            this.allActionsImmutable = null;
        }
        else
        {
            MaLiLib.LOGGER.warn("The action '{}' already exists, not registering it again", name);
        }
    }

    @Override
    public void unregisterAction(NamedAction action)
    {
        String name = action.getRegistryName();

        if (this.baseActions.remove(name) != null)
        {
            this.allActions.remove(name);

            for (String alias : this.aliasesForActions.get(name))
            {
                this.allActions.remove(alias);
            }

            this.aliasesForActions.removeAll(name);

            // set to be re-built
            this.baseActionsImmutable = null;
            this.allActionsImmutable = null;
        }
    }

    @Override
    public boolean addAlias(AliasAction action)
    {
        boolean success = this.addAliasInternal(action);

        if (success)
        {
            this.dirty = true;
        }

        return success;
    }

    protected boolean addAliasInternal(AliasAction action)
    {
        String name = action.getRegistryName();

        if (this.allActions.containsKey(name) == false)
        {
            this.aliases.put(name, action);
            this.allActions.put(name, action);
            this.aliasesForActions.put(action.getOriginalRegistryName(), name);

            // set to be re-built
            this.allActionsImmutable = null;
            this.aliasesImmutable = null;

            return true;
        }
        else
        {
            MessageUtils.errorAndConsole("malilib.message.error.actions_edit.exists", name);
            return false;
        }
    }

    @Override
    public void removeAlias(String name)
    {
        if (this.aliases.containsKey(name) &&
            this.baseActions.containsKey(name) == false)
        {
            AliasAction action = this.aliases.remove(name);

            if (action != null)
            {
                this.allActions.remove(name);
                this.aliasesForActions.remove(action.getOriginalRegistryName(), name);

                // set to be re-built
                this.allActionsImmutable = null;
                this.aliasesImmutable = null;
                this.dirty = true;
            }
        }
    }

    @Override
    public boolean addMacro(MacroAction action)
    {
        boolean success = this.addMacroInternal(action);

        if (success)
        {
            this.dirty = true;
        }

        return success;
    }

    protected boolean addMacroInternal(MacroAction action)
    {
        String name = action.getRegistryName();

        if (this.allActions.containsKey(name) == false)
        {
            this.macros.put(name, action);
            this.allActions.put(name, action);

            // set to be re-built
            this.allActionsImmutable = null;
            this.macrosImmutable = null;

            return true;
        }
        else
        {
            MessageUtils.errorAndConsole("malilib.message.error.actions_edit.add_macro_exists", name);
        }

        return false;
    }

    @Override
    public void removeMacro(String name)
    {
        if (this.macros.remove(name) != null)
        {
            this.allActions.remove(name);

            // set to be re-built
            this.allActionsImmutable = null;
            this.macrosImmutable = null;
            this.dirty = true;
        }
    }

    @Nullable
    @Override
    public NamedAction getAction(String name)
    {
        return this.allActions.get(name);
    }

    @Nullable
    @Override
    public MacroAction getMacro(String name)
    {
        return this.macros.get(name);
    }

    @Override
    public ImmutableList<NamedAction> getBaseActions()
    {
        if (this.baseActionsImmutable == null)
        {
            this.baseActionsImmutable = getActionsSortedByName(this.baseActions.values());
        }

        return this.baseActionsImmutable;
    }

    @Override
    public ImmutableList<AliasAction> getAliases()
    {
        if (this.aliasesImmutable == null)
        {
            this.aliasesImmutable = getActionsSortedByName(this.aliases.values());
        }

        return this.aliasesImmutable;
    }

    @Override
    public ImmutableList<MacroAction> getMacros()
    {
        if (this.macrosImmutable == null)
        {
            this.macrosImmutable = getActionsSortedByName(this.macros.values());
        }

        return this.macrosImmutable;
    }

    @Override
    public ImmutableList<NamedAction> getAllActions()
    {
        if (this.allActionsImmutable == null)
        {
            this.allActionsImmutable = getActionsSortedByName(this.allActions.values());
        }

        return this.allActionsImmutable;
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();
        JsonObject aliases = new JsonObject();
        JsonObject macros = new JsonObject();

        for (Map.Entry<String, AliasAction> entry : this.aliases.entrySet())
        {
            aliases.addProperty(entry.getKey(), entry.getValue().getOriginalRegistryName());
        }

        for (Map.Entry<String, MacroAction> entry : this.macros.entrySet())
        {
            JsonArray arr = new JsonArray();
            String name = entry.getKey();

            for (NamedAction action : entry.getValue().getActionList())
            {
                arr.add(action.getRegistryName());
            }

            macros.add(name, arr);
        }

        obj.add("aliases", aliases);
        obj.add("macros", macros);

        return obj;
    }

    public void fromJson(JsonElement el)
    {
        if (el.isJsonObject() == false)
        {
            return;
        }

        this.clearAliasesAndMacros();

        JsonObject obj = el.getAsJsonObject();

        if (JsonUtils.hasObject(obj, "aliases"))
        {
            JsonObject aliases = obj.get("aliases").getAsJsonObject();
            this.loadAliases(aliases);
        }

        if (JsonUtils.hasObject(obj, "macros"))
        {
            JsonObject macros = obj.get("macros").getAsJsonObject();
            this.loadMacros(macros);
        }
    }

    protected void loadAliases(JsonObject aliases)
    {
        for (Map.Entry<String, JsonElement> entry : aliases.entrySet())
        {
            String name = entry.getKey();
            String registryName = entry.getValue().getAsString();
            NamedAction action = this.allActions.get(registryName);

            if (action == null)
            {
                // Preserve entries in the config file if a mod is temporarily disabled/removed, for example
                action = DUMMY;
            }

            AliasAction aliasAction = new AliasAction(name, action);
            this.addAliasInternal(aliasAction);
        }
    }

    protected void loadMacros(JsonObject macros)
    {
        for (Map.Entry<String, JsonElement> entry : macros.entrySet())
        {
            JsonElement e = entry.getValue();

            if (e.isJsonArray())
            {
                ArrayList<NamedAction> actions = new ArrayList<>();
                String name = entry.getKey();
                JsonArray arr = e.getAsJsonArray();
                int size = arr.size();

                for (int i = 0; i < size; ++i)
                {
                    String registryName = arr.get(i).getAsString();
                    NamedAction action = this.allActions.get(registryName);

                    if (action == null)
                    {
                        // Preserve entries in the config file if a mod is temporarily disabled/removed, for example
                        action = DUMMY;
                    }

                    actions.add(action);
                }

                MacroAction macro = new MacroAction(name, ImmutableList.copyOf(actions));
                this.addMacroInternal(macro);
            }
        }
    }

    public boolean saveToFileIfDirty()
    {
        if (this.dirty)
        {
            this.dirty = false;
            return this.saveToFile();
        }

        return false;
    }

    protected boolean saveToFile()
    {
        File dir = FileUtils.getConfigDirectory();
        File backupDir = new File(dir, "config_backups");
        File saveFile = new File(dir, MaLiLibReference.MOD_ID + "_actions.json");

        return JsonUtils.saveToFile(dir, backupDir, saveFile, 10, this::toJson);
    }

    public void loadFromFile()
    {
        File dir = FileUtils.getConfigDirectory();
        JsonUtils.loadFromFile(dir, MaLiLibReference.MOD_ID + "_actions.json", this::fromJson);
    }

    public static <T extends NamedAction> ImmutableList<T> getActionsSortedByName(Collection<T> actions)
    {
        List<T> list = new ArrayList<>(actions);
        list.sort(Comparator.comparing(NamedAction::getName));
        return ImmutableList.copyOf(list);
    }
}

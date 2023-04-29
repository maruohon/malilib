package malilib.action;

import java.util.Collection;
import java.util.HashMap;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

class ActionStorage<T extends NamedAction>
{
    private final HashMap<String, T> actionMap = new HashMap<>();
    @Nullable private ImmutableList<T> actionList;

    public Collection<T> getMapValues()
    {
        return this.actionMap.values();
    }

    public ImmutableList<T> getActionList()
    {
        if (this.actionList == null)
        {
            this.actionList = ActionRegistry.getActionsSortedByName(this.getMapValues());
        }

        return this.actionList;
    }

    @Nullable
    public T get(String key)
    {
        return this.actionMap.get(key);
    }

    public boolean contains(String key)
    {
        return this.actionMap.containsKey(key);
    }

    public void put(String key, T value)
    {
        this.actionMap.put(key, value);
        this.notifyChange();
    }

    public void putAll(ActionStorage<T> storage)
    {
        this.actionMap.putAll(storage.actionMap);
        this.notifyChange();
    }

    @Nullable
    public T remove(String key)
    {
        T value = this.actionMap.remove(key);

        if (value != null)
        {
            this.notifyChange();
        }

        return value;
    }

    public void notifyChange()
    {
        this.actionList = null; // set to be rebuilt
    }

    public void clear()
    {
        this.actionMap.clear();
        this.actionList = null;
    }
}

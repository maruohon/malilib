package fi.dy.masa.malilib.input;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class HotkeyManagerImpl implements HotkeyManager
{
    protected final Int2ObjectOpenHashMap<ArrayList<KeyBind>> hotkeyMap = new Int2ObjectOpenHashMap<>();
    protected final List<HotkeyCategory> keyBindCategories = new ArrayList<>();
    protected final List<HotkeyProvider> keyBindProviders = new ArrayList<>();
    @Nullable protected ImmutableList<HotkeyCategory> immutableKeyBindCategories;

    public HotkeyManagerImpl()
    {
    }

    @Override
    public void registerHotkeyProvider(HotkeyProvider provider)
    {
        if (this.keyBindProviders.contains(provider) == false)
        {
            this.keyBindProviders.add(provider);
        }

        for (HotkeyCategory category : provider.getHotkeysByCategories())
        {
            this.addKeyBindCategory(category);
        }
    }

    @Override
    public void unregisterHotkeyProvider(HotkeyProvider provider)
    {
        this.keyBindProviders.remove(provider);
    }

    @Override
    public ImmutableList<HotkeyCategory> getHotkeyCategories()
    {
        if (this.immutableKeyBindCategories == null)
        {
            this.immutableKeyBindCategories = ImmutableList.copyOf(this.keyBindCategories);
        }

        return this.immutableKeyBindCategories;
    }

    @Override
    public void updateUsedKeys()
    {
        this.hotkeyMap.clear();

        for (HotkeyProvider handler : this.keyBindProviders)
        {
            for (Hotkey hotkey : handler.getAllHotkeys())
            {
                this.addKeyBindToMap(hotkey.getKeyBind());
            }
        }

        this.hotkeyMap.values().forEach((list) -> list.sort(Comparator.comparingInt((v) -> v.getSettings().getPriority())));
    }

    protected void addKeyBindToMap(KeyBind keybind)
    {
        IntArrayList keys = new IntArrayList();
        keybind.getKeysToList(keys);
        final int size = keys.size();

        for (int i = 0; i < size; ++i)
        {
            int key = keys.getInt(i);
            this.hotkeyMap.computeIfAbsent(key, (k) -> new ArrayList<>()).add(keybind);
        }
    }

    protected void addKeyBindCategory(HotkeyCategory category)
    {
        // Remove a previous entry, if any (matched based on the modName and keyCategory only!)
        this.keyBindCategories.remove(category);
        this.keyBindCategories.add(category);
        this.immutableKeyBindCategories = null; // mark for rebuild
    }

    /**
     * NOT PUBLIC API - DO NOT CALL FROM MOD CODE
     */
    boolean checkKeyBindsForChanges(int eventKey)
    {
        boolean cancel = false;
        boolean isFirst = true;
        List<KeyBind> keyBinds = this.hotkeyMap.get(eventKey);

        if (keyBinds != null && keyBinds.isEmpty() == false)
        {
            // FIXME is there a better way to avoid CMEs when switching config screens?
            keyBinds = new ArrayList<>(keyBinds);

            for (KeyBind keyBind : keyBinds)
            {
                // Note: updateIsPressed() has to be called for key releases too, to reset the state
                KeyUpdateResult result = keyBind.updateIsPressed(isFirst);

                if (result.triggered)
                {
                    isFirst = false;
                }

                cancel |= result.cancel;
            }
        }

        return cancel;
    }
}

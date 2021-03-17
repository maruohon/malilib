package fi.dy.masa.malilib.input;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.google.common.collect.ArrayListMultimap;

public class HotkeyManagerImpl implements HotkeyManager
{
    private final ArrayListMultimap<Integer, KeyBind> hotkeyMap = ArrayListMultimap.create();
    private final List<HotkeyCategory> keyBindCategories = new ArrayList<>();
    private final List<HotkeyProvider> keyBindProviders = new ArrayList<>();

    HotkeyManagerImpl()
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
    public List<HotkeyCategory> getHotkeyCategories()
    {
        return this.keyBindCategories;
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

        for (Integer key : this.hotkeyMap.keySet())
        {
            this.hotkeyMap.get(key).sort(Comparator.comparingInt((v) -> v.getSettings().getPriority()));
        }
    }

    private void addKeyBindToMap(KeyBind keybind)
    {
        List<Integer> keys = keybind.getKeys();

        for (int key : keys)
        {
            this.hotkeyMap.put(key, keybind);
        }
    }

    private void addKeyBindCategory(HotkeyCategory category)
    {
        // Remove a previous entry, if any (matched based on the modName and keyCategory only!)
        this.keyBindCategories.remove(category);
        this.keyBindCategories.add(category);
    }

    /**
     * NOT PUBLIC API - DO NOT CALL FROM MOD CODE
     */
    boolean checkKeyBindsForChanges(int eventKey)
    {
        boolean cancel = false;
        boolean isFirst = true;
        List<KeyBind> keyBinds = this.hotkeyMap.get(eventKey);

        if (keyBinds.isEmpty() == false)
        {
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

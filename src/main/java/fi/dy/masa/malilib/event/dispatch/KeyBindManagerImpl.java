package fi.dy.masa.malilib.event.dispatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.google.common.collect.ArrayListMultimap;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindCategory;
import fi.dy.masa.malilib.input.KeyBindProvider;
import fi.dy.masa.malilib.input.KeyUpdateResult;

public class KeyBindManagerImpl implements KeyBindManager
{
    private final ArrayListMultimap<Integer, KeyBind> hotkeyMap = ArrayListMultimap.create();
    private final List<KeyBindCategory> keyBindCategories = new ArrayList<>();
    private final List<KeyBindProvider> keyBindProviders = new ArrayList<>();

    KeyBindManagerImpl()
    {
    }

    @Override
    public void registerKeyBindProvider(KeyBindProvider provider)
    {
        if (this.keyBindProviders.contains(provider) == false)
        {
            this.keyBindProviders.add(provider);
        }

        for (KeyBindCategory category : provider.getHotkeyCategoriesForCombinedView())
        {
            this.addKeyBindCategory(category);
        }
    }

    @Override
    public void unregisterKeyBindProvider(KeyBindProvider provider)
    {
        this.keyBindProviders.remove(provider);
    }

    @Override
    public List<KeyBindCategory> getKeyBindCategories()
    {
        return this.keyBindCategories;
    }

    @Override
    public void updateUsedKeys()
    {
        this.hotkeyMap.clear();

        for (KeyBindProvider handler : this.keyBindProviders)
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

    private void addKeyBindCategory(KeyBindCategory category)
    {
        // Remove a previous entry, if any (matched based on the modName and keyCategory only!)
        this.keyBindCategories.remove(category);
        this.keyBindCategories.add(category);
    }

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

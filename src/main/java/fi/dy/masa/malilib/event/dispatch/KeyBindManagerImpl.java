package fi.dy.masa.malilib.event.dispatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindCategory;
import fi.dy.masa.malilib.input.KeyBindProvider;

public class KeyBindManagerImpl implements KeyBindManager
{
    private final Multimap<Integer, KeyBind> hotkeyMap = ArrayListMultimap.create();
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
    }

    private void addKeyBindToMap(KeyBind keybind)
    {
        Collection<Integer> keys = keybind.getKeys();

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
        Collection<KeyBind> keyBinds = this.hotkeyMap.get(eventKey);

        if (keyBinds.isEmpty() == false)
        {
            for (KeyBind keyBind : keyBinds)
            {
                // Note: isPressed() has to get called for key releases too, to reset the state
                cancel |= keyBind.updateIsPressed();
            }
        }

        return cancel;
    }
}

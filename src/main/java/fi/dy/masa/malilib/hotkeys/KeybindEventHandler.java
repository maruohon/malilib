package fi.dy.masa.malilib.hotkeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.client.resources.I18n;

public class KeybindEventHandler implements IKeybindManager
{
    private static final KeybindEventHandler INSTANCE = new KeybindEventHandler();

    private final Multimap<Integer, IKeybind> hotkeyMap = ArrayListMultimap.create();
    private final List<KeybindCategory> allKeybinds = new ArrayList<>();
    private final IntOpenHashSet modifierKeys = new IntOpenHashSet();
    private final Set<IKeybindEventHandler> keybindHandlers = new HashSet<>();

    private KeybindEventHandler()
    {
        this.modifierKeys.add(Keyboard.KEY_LSHIFT);
        this.modifierKeys.add(Keyboard.KEY_RSHIFT);
        this.modifierKeys.add(Keyboard.KEY_LCONTROL);
        this.modifierKeys.add(Keyboard.KEY_RCONTROL);
        this.modifierKeys.add(Keyboard.KEY_LMENU);
        this.modifierKeys.add(Keyboard.KEY_RMENU);
    }

    public static KeybindEventHandler getInstance()
    {
        return INSTANCE;
    }

    public void registerKeyEventHandler(IKeybindEventHandler handler)
    {
        this.keybindHandlers.add(handler);

        handler.addHotkeys(this);
    }

    public void unregisterKeyEventHandler(IKeybindEventHandler handler)
    {
        this.keybindHandlers.remove(handler);
    }

    public void updateUsedKeys()
    {
        this.hotkeyMap.clear();

        for (IKeybindEventHandler handler : this.keybindHandlers)
        {
            handler.addKeysToMap(this);
        }
    }

    public List<KeybindCategory> getKeybindCategories()
    {
        return this.allKeybinds;
    }

    public boolean isModifierKey(int eventKey)
    {
        return this.modifierKeys.contains(eventKey);
    }

    @Override
    public void addKeybindToMap(IKeybind keybind)
    {
        Collection<Integer> keys = keybind.getKeys();

        for (int key : keys)
        {
            this.hotkeyMap.put(key, keybind);
        }
    }

    @Override
    public void addHotkeysForCategory(String modName, String keyCategory, IHotkey[] hotkeys)
    {
        KeybindCategory cat = new KeybindCategory(modName, keyCategory, hotkeys);

        // Remove a previous entry, if any (matched based on the modName and keyCategory only!)
        this.allKeybinds.remove(cat);
        this.allKeybinds.add(cat);
    }

    protected boolean checkKeyBindsForChanges(int eventKey)
    {
        boolean cancel = false;
        Collection<IKeybind> keybinds = this.hotkeyMap.get(eventKey);

        if (keybinds.isEmpty() == false)
        {
            for (IKeybind keybind : keybinds)
            {
                // Note: isPressed() has to get called for key releases too, to reset the state
                cancel |= keybind.updateIsPressed();
            }
        }

        return cancel;
    }

    public void tickKeybinds()
    {
        /*
        for (IKeybind keybind : this.hotkeyMap.values())
        {
            keybind.tick();
        }
        */
    }

    public boolean onKeyInput()
    {
        int eventKey = Keyboard.getEventKey();
        boolean eventKeyState = Keyboard.getEventKeyState();
        boolean cancel = false;

        // Update the cached pressed keys status
        KeybindMulti.onKeyInput(eventKey, eventKeyState);

        cancel = this.checkKeyBindsForChanges(eventKey);

        for (IKeybindEventHandler handler : this.keybindHandlers)
        {
            if (handler.onKeyInput(eventKey, eventKeyState))
            {
                return true;
            }
        }

        // Somewhat hacky fix to prevent eating the modifier keys... >_>
        // A proper fix would likely require adding a context for the keys,
        // and only cancel if the context is currently active/valid.
        return cancel && this.isModifierKey(eventKey) == false;
    }

    public boolean onMouseInput()
    {
        int eventButton = Mouse.getEventButton();
        int dWheel = Mouse.getEventDWheel();
        boolean eventButtonState = Mouse.getEventButtonState();
        boolean cancel = false;

        if (eventButton != -1 || dWheel != 0)
        {
            if (eventButton != -1)
            {
                // Update the cached pressed keys status
                KeybindMulti.onKeyInput(eventButton - 100, eventButtonState);

                cancel = this.checkKeyBindsForChanges(eventButton - 100);
            }

            for (IKeybindEventHandler handler : this.keybindHandlers)
            {
                if (handler.onMouseInput(eventButton, dWheel, eventButtonState))
                {
                    return true;
                }
            }
        }

        return cancel;
    }

    public static class KeybindCategory implements Comparable<KeybindCategory>
    {
        private final String modName;
        private final String category;
        private final IHotkey[] hotkeys;

        public KeybindCategory(String modName, String category, IHotkey[] hotkeys)
        {
            this.modName = modName;
            this.category = category;
            this.hotkeys = hotkeys;
        }

        public String getModName()
        {
            return this.modName;
        }

        public String getCategory()
        {
            return I18n.format(this.category);
        }

        public IHotkey[] getHotkeys()
        {
            return this.hotkeys;
        }

        @Override
        public int compareTo(KeybindCategory other)
        {
            int val = this.modName.compareTo(other.modName);

            if (val != 0)
            {
                return val;
            }

            return this.category.compareTo(other.category);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            KeybindCategory other = (KeybindCategory) obj;
            if (category == null)
            {
                if (other.category != null)
                    return false;
            }
            else if (!category.equals(other.category))
                return false;
            if (modName == null)
            {
                if (other.modName != null)
                    return false;
            }
            else if (!modName.equals(other.modName))
                return false;
            return true;
        }
    }
}

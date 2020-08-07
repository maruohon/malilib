package fi.dy.masa.malilib.event.dispatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindProvider;
import fi.dy.masa.malilib.input.KeyboardInputHandler;
import fi.dy.masa.malilib.input.MouseInputHandler;
import fi.dy.masa.malilib.input.KeyBindCategory;
import fi.dy.masa.malilib.input.KeyBindImpl;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

public class InputDispatcherImpl implements KeyBindManager, InputDispatcher
{
    private static final InputDispatcherImpl INSTANCE = new InputDispatcherImpl();

    private final Multimap<Integer, KeyBind> hotkeyMap = ArrayListMultimap.create();
    private final List<KeyBindCategory> allKeyBinds = new ArrayList<>();
    private final IntOpenHashSet modifierKeys = new IntOpenHashSet();
    private final List<KeyBindProvider> keyBindProviders = new ArrayList<>();
    private final List<KeyboardInputHandler> keyboardHandlers = new ArrayList<>();
    private final List<MouseInputHandler> mouseHandlers = new ArrayList<>();

    private InputDispatcherImpl()
    {
        this.modifierKeys.add(Keyboard.KEY_LSHIFT);
        this.modifierKeys.add(Keyboard.KEY_RSHIFT);
        this.modifierKeys.add(Keyboard.KEY_LCONTROL);
        this.modifierKeys.add(Keyboard.KEY_RCONTROL);
        this.modifierKeys.add(Keyboard.KEY_LMENU);
        this.modifierKeys.add(Keyboard.KEY_RMENU);
    }

    public static KeyBindManager getKeyBindManager()
    {
        return INSTANCE;
    }

    public static InputDispatcher getInputManager()
    {
        return INSTANCE;
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
            this.addHotkeysForCategory(category);
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
        return this.allKeyBinds;
    }

    @Override
    public void updateUsedKeys()
    {
        this.hotkeyMap.clear();

        for (KeyBindProvider handler : this.keyBindProviders)
        {
            for (Hotkey hotkey : handler.getAllHotkeys())
            {
                this.addKeybindToMap(hotkey.getKeyBind());
            }
        }
    }

    private void addKeybindToMap(KeyBind keybind)
    {
        Collection<Integer> keys = keybind.getKeys();

        for (int key : keys)
        {
            this.hotkeyMap.put(key, keybind);
        }
    }

    private void addHotkeysForCategory(KeyBindCategory category)
    {
        // Remove a previous entry, if any (matched based on the modName and keyCategory only!)
        this.allKeyBinds.remove(category);
        this.allKeyBinds.add(category);
    }

    @Override
    public void registerKeyboardInputHandler(KeyboardInputHandler handler)
    {
        if (this.keyboardHandlers.contains(handler) == false)
        {
            this.keyboardHandlers.add(handler);
        }
    }

    @Override
    public void unregisterKeyboardInputHandler(KeyboardInputHandler handler)
    {
        this.keyboardHandlers.remove(handler);
    }

    @Override
    public void registerMouseInputHandler(MouseInputHandler handler)
    {
        if (this.mouseHandlers.contains(handler) == false)
        {
            this.mouseHandlers.add(handler);
        }
    }

    @Override
    public void unregisterMouseInputHandler(MouseInputHandler handler)
    {
        this.mouseHandlers.remove(handler);
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public boolean onKeyInput()
    {
        int eventKey = Keyboard.getEventKey();
        boolean eventKeyState = Keyboard.getEventKeyState();

        // Update the cached pressed keys status
        KeyBindImpl.onKeyInputPre(eventKey, eventKeyState);

        boolean cancel = this.checkKeyBindsForChanges(eventKey);

        if (this.keyboardHandlers.isEmpty() == false)
        {
            for (KeyboardInputHandler handler : this.keyboardHandlers)
            {
                if (handler.onKeyInput(eventKey, 0, 0, eventKeyState))
                {
                    return true;
                }
            }
        }

        return this.isModifierKey(eventKey) == false && cancel;
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public boolean onMouseInput()
    {
        final int eventButton = Mouse.getEventButton();
        final int dWheel = Mouse.getEventDWheel();
        final boolean eventButtonState = Mouse.getEventButtonState();
        boolean cancel = false;

        if (eventButton != -1 || dWheel != 0)
        {
            if (eventButton != -1)
            {
                // Update the cached pressed keys status
                KeyBindImpl.onKeyInputPre(eventButton - 100, eventButtonState);

                cancel = this.checkKeyBindsForChanges(eventButton - 100);
            }

            if (this.mouseHandlers.isEmpty() == false)
            {
                for (MouseInputHandler handler : this.mouseHandlers)
                {
                    if (handler.onMouseInput(eventButton, dWheel, eventButtonState))
                    {
                        return true;
                    }
                }
            }
        }
        else if (this.mouseHandlers.isEmpty() == false)
        {
            for (MouseInputHandler handler : this.mouseHandlers)
            {
                handler.onMouseMoved();
            }
        }

        return cancel;
    }

    private boolean isModifierKey(int eventKey)
    {
        return this.modifierKeys.contains(eventKey);
    }

    private boolean checkKeyBindsForChanges(int eventKey)
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

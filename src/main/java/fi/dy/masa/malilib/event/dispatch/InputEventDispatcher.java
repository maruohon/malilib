package fi.dy.masa.malilib.event.dispatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import fi.dy.masa.malilib.input.IHotkey;
import fi.dy.masa.malilib.input.IKeyBind;
import fi.dy.masa.malilib.input.IKeyBindProvider;
import fi.dy.masa.malilib.input.IKeyboardInputHandler;
import fi.dy.masa.malilib.input.IMouseInputHandler;
import fi.dy.masa.malilib.input.KeyBindCategory;
import fi.dy.masa.malilib.input.KeyBindMulti;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

public class InputEventDispatcher implements IKeyBindManager, IInputDispatcher
{
    private static final InputEventDispatcher INSTANCE = new InputEventDispatcher();

    private final Multimap<Integer, IKeyBind> hotkeyMap = ArrayListMultimap.create();
    private final List<KeyBindCategory> allKeyBinds = new ArrayList<>();
    private final IntOpenHashSet modifierKeys = new IntOpenHashSet();
    private final List<IKeyBindProvider> keyBindProviders = new ArrayList<>();
    private final List<IKeyboardInputHandler> keyboardHandlers = new ArrayList<>();
    private final List<IMouseInputHandler> mouseHandlers = new ArrayList<>();

    private InputEventDispatcher()
    {
        this.modifierKeys.add(Keyboard.KEY_LSHIFT);
        this.modifierKeys.add(Keyboard.KEY_RSHIFT);
        this.modifierKeys.add(Keyboard.KEY_LCONTROL);
        this.modifierKeys.add(Keyboard.KEY_RCONTROL);
        this.modifierKeys.add(Keyboard.KEY_LMENU);
        this.modifierKeys.add(Keyboard.KEY_RMENU);
    }

    public static IKeyBindManager getKeyBindManager()
    {
        return INSTANCE;
    }

    public static IInputDispatcher getInputManager()
    {
        return INSTANCE;
    }

    @Override
    public void registerKeyBindProvider(IKeyBindProvider provider)
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
    public void unregisterKeyBindProvider(IKeyBindProvider provider)
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

        for (IKeyBindProvider handler : this.keyBindProviders)
        {
            for (IHotkey hotkey : handler.getAllHotkeys())
            {
                this.addKeybindToMap(hotkey.getKeyBind());
            }
        }
    }

    private void addKeybindToMap(IKeyBind keybind)
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
    public void registerKeyboardInputHandler(IKeyboardInputHandler handler)
    {
        if (this.keyboardHandlers.contains(handler) == false)
        {
            this.keyboardHandlers.add(handler);
        }
    }

    @Override
    public void unregisterKeyboardInputHandler(IKeyboardInputHandler handler)
    {
        this.keyboardHandlers.remove(handler);
    }

    @Override
    public void registerMouseInputHandler(IMouseInputHandler handler)
    {
        if (this.mouseHandlers.contains(handler) == false)
        {
            this.mouseHandlers.add(handler);
        }
    }

    @Override
    public void unregisterMouseInputHandler(IMouseInputHandler handler)
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
        KeyBindMulti.onKeyInputPre(eventKey, eventKeyState);

        boolean cancel = this.checkKeyBindsForChanges(eventKey);

        if (this.keyboardHandlers.isEmpty() == false)
        {
            for (IKeyboardInputHandler handler : this.keyboardHandlers)
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
                KeyBindMulti.onKeyInputPre(eventButton - 100, eventButtonState);

                cancel = this.checkKeyBindsForChanges(eventButton - 100);
            }

            if (this.mouseHandlers.isEmpty() == false)
            {
                for (IMouseInputHandler handler : this.mouseHandlers)
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
            for (IMouseInputHandler handler : this.mouseHandlers)
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
        Collection<IKeyBind> keyBinds = this.hotkeyMap.get(eventKey);

        if (keyBinds.isEmpty() == false)
        {
            for (IKeyBind keyBind : keyBinds)
            {
                // Note: isPressed() has to get called for key releases too, to reset the state
                cancel |= keyBind.updateIsPressed();
            }
        }

        return cancel;
    }
}

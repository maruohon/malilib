package fi.dy.masa.malilib.input;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import fi.dy.masa.malilib.registry.Registry;

public class InputDispatcherImpl implements InputDispatcher
{
    protected final IntOpenHashSet modifierKeys = new IntOpenHashSet();
    protected final List<KeyboardInputHandler> keyboardHandlers = new ArrayList<>();
    protected final List<MouseInputHandler> mouseHandlers = new ArrayList<>();

    public InputDispatcherImpl()
    {
        this.modifierKeys.add(Keyboard.KEY_LSHIFT);
        this.modifierKeys.add(Keyboard.KEY_RSHIFT);
        this.modifierKeys.add(Keyboard.KEY_LCONTROL);
        this.modifierKeys.add(Keyboard.KEY_RCONTROL);
        this.modifierKeys.add(Keyboard.KEY_LMENU);
        this.modifierKeys.add(Keyboard.KEY_RMENU);
    }

    @Override
    public void registerKeyboardInputHandler(KeyboardInputHandler handler)
    {
        if (this.keyboardHandlers.contains(handler) == false)
        {
            this.keyboardHandlers.add(handler);
            this.keyboardHandlers.sort(Comparator.comparing(KeyboardInputHandler::getPriority));
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
            this.mouseHandlers.sort(Comparator.comparing(MouseInputHandler::getPriority));
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
        char eventChar = Keyboard.getEventCharacter();
        boolean isChar = eventKey == 0 && eventChar >= ' ';

        if (isChar)
        {
            eventKey = (int) eventChar + 256;
        }

        // Update the cached pressed keys status
        KeyBindImpl.onKeyInputPre(eventKey, 0, 0, eventChar, eventKeyState);

        boolean cancel = ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).checkKeyBindsForChanges(eventKey);

        // Since char-only keys can't be properly held down (there is no properly detectable release event,
        // the char value in the release event is not set), clear them immediately.
        if (isChar)
        {
            KeyBindImpl.onKeyInputPre(eventKey, 0, 0, eventChar, false);
            ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).checkKeyBindsForChanges(eventKey);
        }

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
            // Support mouse scrolls in the keybind system
            int keyCode = eventButton != -1 ? eventButton - 100 : (dWheel < 0 ? -201 : -199);
            boolean isScroll = dWheel != 0 && eventButton == -1;
            boolean keyState = isScroll || eventButtonState;

            // Update the cached pressed keys status
            KeyBindImpl.onKeyInputPre(keyCode, 0, 0, (char) 0, keyState);

            cancel = ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).checkKeyBindsForChanges(keyCode);

            // Since scroll "keys" can't be held down, clear them immediately
            if (isScroll)
            {
                KeyBindImpl.onKeyInputPre(keyCode, 0, 0, (char) 0, false);
                ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).checkKeyBindsForChanges(keyCode);
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

    protected boolean isModifierKey(int eventKey)
    {
        return this.modifierKeys.contains(eventKey);
    }
}

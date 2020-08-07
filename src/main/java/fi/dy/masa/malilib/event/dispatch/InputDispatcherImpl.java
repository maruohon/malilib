package fi.dy.masa.malilib.event.dispatch;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import fi.dy.masa.malilib.input.KeyBindImpl;
import fi.dy.masa.malilib.input.KeyboardInputHandler;
import fi.dy.masa.malilib.input.MouseInputHandler;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

public class InputDispatcherImpl implements InputDispatcher
{
    private final IntOpenHashSet modifierKeys = new IntOpenHashSet();
    private final List<KeyboardInputHandler> keyboardHandlers = new ArrayList<>();
    private final List<MouseInputHandler> mouseHandlers = new ArrayList<>();

    InputDispatcherImpl()
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

        boolean cancel = ((KeyBindManagerImpl) KeyBindManager.INSTANCE).checkKeyBindsForChanges(eventKey);

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

                cancel = ((KeyBindManagerImpl) KeyBindManager.INSTANCE).checkKeyBindsForChanges(eventButton - 100);
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
}

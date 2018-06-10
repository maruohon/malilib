package fi.dy.masa.malilib.hotkeys;

import java.util.HashSet;
import java.util.Set;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KeybindEventHandler
{
    private static final KeybindEventHandler INSTANCE = new KeybindEventHandler();

    private final Set<IKeyEventHandler> keyHandlers = new HashSet<>();
    private final Set<IMouseEventHandler> mouseHandlers = new HashSet<>();

    public static KeybindEventHandler getInstance()
    {
        return INSTANCE;
    }

    public void registerKeyEventHandler(IKeyEventHandler handler)
    {
        this.keyHandlers.add(handler);
    }

    public void unregisterKeyEventHandler(IKeyEventHandler handler)
    {
        this.keyHandlers.remove(handler);
    }

    public void registerMouseEventHandler(IMouseEventHandler handler)
    {
        this.mouseHandlers.add(handler);
    }

    public void unregisterMouseEventHandler(IMouseEventHandler handler)
    {
        this.mouseHandlers.remove(handler);
    }

    public boolean onKeyInput()
    {
        int eventKey = Keyboard.getEventKey();
        boolean eventKeyState = Keyboard.getEventKeyState();

        // Update the cached pressed keys status
        KeybindMulti.onKeyInput(eventKey, eventKeyState);

        for (IKeyEventHandler handler : this.keyHandlers)
        {
            if (handler.onKeyInput(eventKey, eventKeyState))
            {
                return true;
            }
        }

        return false;
    }

    public boolean onMouseInput()
    {
        int eventButton = Mouse.getEventButton();
        int dWheel = Mouse.getDWheel();
        boolean eventButtonState = Mouse.getEventButtonState();

        if (eventButton != -1 || dWheel != 0)
        {
            if (eventButton != -1)
            {
                // Update the cached pressed keys status
                KeybindMulti.onKeyInput(eventButton - 100, eventButtonState);
            }

            for (IMouseEventHandler handler : this.mouseHandlers)
            {
                if (handler.onMouseInput(eventButton, dWheel, eventButtonState))
                {
                    return true;
                }
            }
        }

        return false;
    }
}

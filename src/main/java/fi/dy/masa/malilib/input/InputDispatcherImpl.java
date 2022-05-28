package fi.dy.masa.malilib.input;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.registry.Registry;

public class InputDispatcherImpl implements InputDispatcher
{
    protected final IntOpenHashSet modifierKeys = new IntOpenHashSet();
    protected final List<KeyboardInputHandler> keyboardHandlers = new ArrayList<>();
    protected final List<MouseClickHandler> mouseClickHandlers = new ArrayList<>();
    protected final List<MouseScrollHandler> mouseScrollHandlers = new ArrayList<>();
    protected final List<MouseMoveHandler> mouseMoveHandlers = new ArrayList<>();

    public InputDispatcherImpl()
    {
        this.modifierKeys.add(Keys.KEY_LEFT_SHIFT);
        this.modifierKeys.add(Keys.KEY_RIGHT_SHIFT);
        this.modifierKeys.add(Keys.KEY_LEFT_CONTROL);
        this.modifierKeys.add(Keys.KEY_RIGHT_CONTROL);
        this.modifierKeys.add(Keys.KEY_LEFT_ALT);
        this.modifierKeys.add(Keys.KEY_RIGHT_ALT);
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
    public void registerMouseClickHandler(MouseClickHandler handler)
    {
        if (this.mouseClickHandlers.contains(handler) == false)
        {
            this.mouseClickHandlers.add(handler);
            this.mouseClickHandlers.sort(Comparator.comparing(MouseClickHandler::getPriority));
        }
    }

    @Override
    public void registerMouseScrollHandler(MouseScrollHandler handler)
    {
        if (this.mouseScrollHandlers.contains(handler) == false)
        {
            this.mouseScrollHandlers.add(handler);
            this.mouseScrollHandlers.sort(Comparator.comparing(MouseScrollHandler::getPriority));
        }
    }

    @Override
    public void registerMouseMoveHandler(MouseMoveHandler handler)
    {
        if (this.mouseMoveHandlers.contains(handler) == false)
        {
            this.mouseMoveHandlers.add(handler);
            this.mouseMoveHandlers.sort(Comparator.comparing(MouseMoveHandler::getPriority));
        }
    }

    @Override
    public void unregisterMouseClickHandler(MouseClickHandler handler)
    {
        this.mouseClickHandlers.remove(handler);
    }

    @Override
    public void unregisterMouseScrollHandler(MouseScrollHandler handler)
    {
        this.mouseScrollHandlers.remove(handler);
    }

    @Override
    public void unregisterMouseMoveHandler(MouseMoveHandler handler)
    {
        this.mouseMoveHandlers.remove(handler);
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public boolean onKeyInput()
    {
        int keyCode = Keyboard.getEventKey();
        boolean keyState = Keyboard.getEventKeyState();
        char eventChar = Keyboard.getEventCharacter();
        boolean isChar = keyCode == 0 && eventChar >= ' ';

        if (isChar)
        {
            keyCode = (int) eventChar + 256;
        }

        // Update the cached pressed keys status
        KeyBindImpl.onKeyInputPre(keyCode, 0, 0, eventChar, keyState);

        boolean cancel = ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).checkKeyBindsForChanges(keyCode);

        // Since char-only keys can't be properly held down (there is no properly detectable release event,
        // the char value in the release event is not set), clear them immediately.
        if (isChar)
        {
            KeyBindImpl.onKeyInputPre(keyCode, 0, 0, eventChar, false);
            ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).checkKeyBindsForChanges(keyCode);
        }

        if (this.keyboardHandlers.isEmpty() == false)
        {
            for (KeyboardInputHandler handler : this.keyboardHandlers)
            {
                if (handler.onKeyInput(keyCode, 0, 0, keyState))
                {
                    return true;
                }
            }
        }

        return this.isModifierKey(keyCode) == false && cancel;
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
        boolean clickOrScroll = false;
        int width = GuiUtils.getScaledWindowWidth();
        int height = GuiUtils.getScaledWindowHeight();
        int mouseX = Mouse.getX() * width / GuiUtils.getDisplayWidth();
        int mouseY = height - Mouse.getY() * height / GuiUtils.getDisplayHeight() - 1;

        if (dWheel != 0)
        {
            // Support mouse scrolls in the keybind system
            int keyCode = dWheel < 0 ? -201 : -199;
            boolean keyState = true;

            // Update the cached pressed keys status
            KeyBindImpl.onKeyInputPre(keyCode, 0, 0, (char) 0, keyState);

            cancel = ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).checkKeyBindsForChanges(keyCode);

            // Since scroll "keys" can't be held down, clear them immediately
            KeyBindImpl.onKeyInputPre(keyCode, 0, 0, (char) 0, false);
            ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).checkKeyBindsForChanges(keyCode);

            if (this.mouseScrollHandlers.isEmpty() == false)
            {
                for (MouseScrollHandler handler : this.mouseScrollHandlers)
                {
                    if (handler.onMouseScroll(mouseX, mouseY, 0, dWheel))
                    {
                        return true;
                    }
                }
            }

            clickOrScroll = true;
        }

        if (eventButton != -1)
        {
            // Support mouse scrolls in the keybind system
            int keyCode = eventButton - 100;
            boolean keyState = eventButtonState;

            // Update the cached pressed keys status
            KeyBindImpl.onKeyInputPre(keyCode, 0, 0, (char) 0, keyState);

            cancel = ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).checkKeyBindsForChanges(keyCode);

            if (this.mouseClickHandlers.isEmpty() == false)
            {
                for (MouseClickHandler handler : this.mouseClickHandlers)
                {
                    if (handler.onMouseClick(mouseX, mouseY, eventButton, eventButtonState))
                    {
                        return true;
                    }
                }
            }

            clickOrScroll = true;
        }

        if (clickOrScroll == false && this.mouseMoveHandlers.isEmpty() == false)
        {
            for (MouseMoveHandler handler : this.mouseMoveHandlers)
            {
                handler.onMouseMove(mouseX, mouseY);
            }
        }

        return cancel;
    }

    protected boolean isModifierKey(int eventKey)
    {
        return this.modifierKeys.contains(eventKey);
    }
}

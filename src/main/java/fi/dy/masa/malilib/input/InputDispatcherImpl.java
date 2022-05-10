package fi.dy.masa.malilib.input;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import fi.dy.masa.malilib.registry.Registry;

public class InputDispatcherImpl implements InputDispatcher
{
    protected final List<KeyboardInputHandler> keyboardHandlers = new ArrayList<>();
    protected final List<MouseInputHandler> mouseHandlers = new ArrayList<>();
    protected final MinecraftClient mc = MinecraftClient.getInstance();
    protected double mouseWheelDeltaSum;

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
    public boolean onKeyInput(int keyCode, int scanCode, int modifiers, boolean eventKeyState)
    {
        // Update the cached pressed keys status
        KeyBindImpl.onKeyInputPre(keyCode, scanCode, modifiers, ' ', eventKeyState);

        boolean cancel = ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).checkKeyBindsForChanges(keyCode);

        if (this.keyboardHandlers.isEmpty() == false)
        {
            for (KeyboardInputHandler handler : this.keyboardHandlers)
            {
                if (handler.onKeyInput(keyCode, scanCode, modifiers, eventKeyState))
                {
                    return true;
                }
            }
        }

        return cancel;
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public boolean onMouseClick(int eventButton, boolean keyState)
    {
        boolean cancel = false;

        if (eventButton != -1)
        {
            // Support mouse scrolls in the keybind system
            int keyCode = eventButton != -1 ? eventButton - 100 : Keys.KEY_NONE;

            // Update the cached pressed keys status
            KeyBindImpl.onKeyInputPre(keyCode, 0, 0, (char) 0, keyState);

            cancel = ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).checkKeyBindsForChanges(keyCode);

            if (this.mouseHandlers.isEmpty() == false)
            {
                for (MouseInputHandler handler : this.mouseHandlers)
                {
                    // FIXME 1.13+ port dWheel/scrolling needs a separate handler?
                    if (handler.onMouseInput(eventButton, 0, keyState))
                    {
                        return true;
                    }
                }
            }
        }

        return cancel;
    }

    public boolean onMouseScroll(final double xOffset, final double yOffset)
    {
        boolean discrete = this.mc.options.getDiscreteMouseScroll().getValue();
        double sensitivity = this.mc.options.getMouseWheelSensitivity().getValue();
        double amount = (discrete ? Math.signum(yOffset) : yOffset) * sensitivity;
        boolean cancel = false;

        // FIXME 1.13+ port dWheel/scrolling needs a separate handler?
        /*
        if (MaLiLibConfigs.Debug.MOUSE_SCROLL_DEBUG.getBooleanValue())
        {
            int time = (int) (System.currentTimeMillis() & 0xFFFF);
            int tick = this.mc.world != null ? (int) (this.mc.world.getTime() & 0xFFFF) : 0;
            String timeStr = String.format("time: %04X, tick: %04X", time, tick);
            MaLiLib.LOGGER.info("{} - xOffset: {}, yOffset: {}, discrete: {}, sensitivity: {}, amount: {}",
                                timeStr, xOffset, yOffset, discrete, sensitivity, amount);
        }
        */

        if (amount != 0 && this.mouseHandlers.isEmpty() == false)
        {
            if (this.mouseWheelDeltaSum != 0.0 && Math.signum(amount) != Math.signum(this.mouseWheelDeltaSum))
            {
                this.mouseWheelDeltaSum = 0.0;
            }

            this.mouseWheelDeltaSum += amount;
            amount = (int) this.mouseWheelDeltaSum;

            if (amount != 0.0)
            {
                this.mouseWheelDeltaSum -= amount;

                int keyCode = amount < 0 ? -201 : -199;
                cancel = ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).checkKeyBindsForChanges(keyCode);

                // Since scroll "keys" can't be held down, clear them immediately
                KeyBindImpl.onKeyInputPre(keyCode, 0, 0, (char) 0, false);
                ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).checkKeyBindsForChanges(keyCode);

                for (MouseInputHandler handler : this.mouseHandlers)
                {
                    // FIXME 1.13+ port dWheel/scrolling needs a separate handler?
                    if (handler.onMouseInput(Keys.KEY_NONE, (int) amount, false))
                    {
                        return true;
                    }
                }
            }
        }

        return cancel;
    }

    public void onMouseMove()
    {
        if (this.mouseHandlers.isEmpty() == false)
        {
            for (MouseInputHandler handler : this.mouseHandlers)
            {
                handler.onMouseMoved();
            }
        }
    }
}

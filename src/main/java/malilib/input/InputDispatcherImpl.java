package malilib.input;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import malilib.MaLiLibConfigs;
import malilib.overlay.message.MessageDispatcher;
import malilib.overlay.message.MessageOutput;
import malilib.registry.Registry;
import malilib.util.game.wrap.GameUtils;
import net.minecraft.client.MinecraftClient;

public class InputDispatcherImpl implements InputDispatcher
{
    protected final List<KeyboardInputHandler> keyboardHandlers = new ArrayList<>();
    protected final List<MouseClickHandler> mouseClickHandlers = new ArrayList<>();
    protected final List<MouseScrollHandler> mouseScrollHandlers = new ArrayList<>();
    protected final List<MouseMoveHandler> mouseMoveHandlers = new ArrayList<>();

    protected final MinecraftClient mc = GameUtils.getClient();
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

    protected void printInputCancellationDebugMessage(Object handler)
    {
        if (MaLiLibConfigs.Debug.INPUT_CANCEL_DEBUG.getBooleanValue())
        {
            String key = "malilib.message.debug.input_handling_cancel_by_handler";
            MessageDispatcher.generic().console().type(MessageOutput.CUSTOM_HOTBAR)
                    .translate(key, handler.getClass().getName());
        }
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
                    this.printInputCancellationDebugMessage(handler);
                    return true;
                }
            }
        }

        return cancel;
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public boolean onMouseClick(int mouseButton, boolean buttonState)
    {
        int keyCode = mouseButton != -1 ? mouseButton - 100 : Keys.KEY_NONE;
        int mouseX = (int) InputUtils.getMouseX();
        int mouseY = (int) InputUtils.getMouseY();
        boolean cancel;

        // Update the cached pressed keys status
        KeyBindImpl.onKeyInputPre(keyCode, 0, 0, (char) 0, buttonState);

        cancel = ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).checkKeyBindsForChanges(keyCode);

        if (this.mouseClickHandlers.isEmpty() == false)
        {
            for (MouseClickHandler handler : this.mouseClickHandlers)
            {
                if (handler.onMouseClick(mouseX, mouseY, mouseButton, buttonState))
                {
                    this.printInputCancellationDebugMessage(handler);
                    return true;
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

        if (amount != 0 && this.mouseScrollHandlers.isEmpty() == false)
        {
            int mouseX = (int) InputUtils.getMouseX();
            int mouseY = (int) InputUtils.getMouseY();

            if (this.mouseWheelDeltaSum != 0.0 && Math.signum(amount) != Math.signum(this.mouseWheelDeltaSum))
            {
                this.mouseWheelDeltaSum = 0.0;
            }

            this.mouseWheelDeltaSum += amount;
            amount = this.mouseWheelDeltaSum;

            if ((int) amount != 0)
            {
                this.mouseWheelDeltaSum -= amount;

                // Support mouse scrolls in the keybind system
                int keyCode = amount < 0 ? -201 : -199;
                cancel = ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).checkKeyBindsForChanges(keyCode);

                // Since scroll "keys" can't be held down, clear them immediately
                KeyBindImpl.onKeyInputPre(keyCode, 0, 0, (char) 0, false);
                ((HotkeyManagerImpl) Registry.HOTKEY_MANAGER).checkKeyBindsForChanges(keyCode);

                for (MouseScrollHandler handler : this.mouseScrollHandlers)
                {
                    if (handler.onMouseScroll(mouseX, mouseY, xOffset, amount))
                    {
                        this.printInputCancellationDebugMessage(handler);
                        return true;
                    }
                }
            }
        }

        return cancel;
    }

    public void onMouseMove(double mouseX, double mouseY)
    {
        if (this.mouseMoveHandlers.isEmpty() == false)
        {
            for (MouseMoveHandler handler : this.mouseMoveHandlers)
            {
                handler.onMouseMove((int) mouseX, (int) mouseY);
            }
        }
    }
}

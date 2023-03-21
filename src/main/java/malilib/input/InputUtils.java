package malilib.input;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import malilib.util.game.wrap.GameUtils;

public class InputUtils
{
    public static double getMouseX()
    {
        MinecraftClient mc = GameUtils.getClient();
        Window window = mc.getWindow();
        return (mc.mouse.getX() * (double) window.getScaledWidth() / (double) window.getWidth());
    }

    public static double getMouseY()
    {
        MinecraftClient mc = GameUtils.getClient();
        Window window = mc.getWindow();
        return (mc.mouse.getY() * (double) window.getScaledHeight() / (double) window.getHeight());
    }
}

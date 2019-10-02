package fi.dy.masa.malilib.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public class InputUtils
{
    public static int getMouseX()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        Window window = mc.getWindow();
        return (int) (mc.mouse.getX() * (double) window.getScaledWidth() / (double) window.getWidth());
    }

    public static int getMouseY()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        Window window = mc.getWindow();
        return (int) (mc.mouse.getY() * (double) window.getScaledHeight() / (double) window.getHeight());
    }
}

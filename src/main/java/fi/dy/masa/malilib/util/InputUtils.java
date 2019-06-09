package fi.dy.masa.malilib.util;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;

public class InputUtils
{
    public static int getMouseX()
    {
        Minecraft mc = Minecraft.getInstance();
        MainWindow window = mc.mainWindow;
        return (int) (mc.mouseHelper.getMouseX() * (double) window.getScaledWidth() / (double) window.getWidth());
    }

    public static int getMouseY()
    {
        Minecraft mc = Minecraft.getInstance();
        MainWindow window = mc.mainWindow;
        return (int) (mc.mouseHelper.getMouseY() * (double) window.getScaledHeight() / (double) window.getHeight());
    }
}

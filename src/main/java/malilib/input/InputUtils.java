package malilib.input;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;

import malilib.util.game.wrap.GameUtils;

public class InputUtils
{
    public static double getMouseX()
    {
        Minecraft mc = GameUtils.getClient();
        Window window = mc.getWindow();
        return (mc.mouseHandler.xpos() * (double) window.getGuiScaledWidth() / (double) window.getScreenWidth());
    }

    public static double getMouseY()
    {
        Minecraft mc = GameUtils.getClient();
        Window window = mc.getWindow();
        return (mc.mouseHandler.ypos() * (double) window.getGuiScaledHeight() / (double) window.getScreenHeight());
    }
}

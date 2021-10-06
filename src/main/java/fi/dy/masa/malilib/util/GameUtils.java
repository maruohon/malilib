package fi.dy.masa.malilib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class GameUtils
{
    public static Minecraft getClient()
    {
        return Minecraft.getMinecraft();
    }

    public static EntityPlayerSP getClientPlayer()
    {
        return getClient().player;
    }

    public static boolean isSinglePlayer()
    {
        return getClient().isSingleplayer();
    }
}

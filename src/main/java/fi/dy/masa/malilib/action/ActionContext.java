package fi.dy.masa.malilib.action;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.GameUtils;

public class ActionContext
{
    public static final ActionContext COMMON = new ActionContext();

    public ActionContext()
    {
    }

    public Minecraft getClient()
    {
        return GameUtils.getClient();
    }

    @Nullable
    public WorldClient getWorld()
    {
        return GameUtils.getClient().world;
    }

    @Nullable
    public EntityPlayerSP getPlayer()
    {
        return GameUtils.getClient().player;
    }

    @Nullable
    public Entity getCameraEntity()
    {
        return EntityUtils.getCameraEntity();
    }
}

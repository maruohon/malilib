package fi.dy.masa.malilib.action;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.GameUtils;

public class ActionContext
{
    public final Minecraft mc;

    public ActionContext()
    {
        this.mc = GameUtils.getClient();
    }

    @Nullable
    public EntityPlayerSP getPlayer()
    {
        return this.mc.player;
    }

    @Nullable
    public Entity getCameraEntity()
    {
        return EntityUtils.getCameraEntity();
    }

    @Nullable
    public World getWorld()
    {
        return this.mc.world;
    }
}

package fi.dy.masa.malilib.action;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import fi.dy.masa.malilib.util.EntityUtils;

public class ActionContext
{
    public final Minecraft mc;

    public ActionContext()
    {
        this.mc = Minecraft.getMinecraft();
    }

    @Nullable
    public EntityPlayer getPlayer()
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

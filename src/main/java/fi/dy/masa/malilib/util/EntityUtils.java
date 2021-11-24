package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EntityUtils
{
    /**
     * @return The camera entity, if it's not null, otherwise returns the client player entity.
     */
    @Nullable
    public static Entity getCameraEntity()
    {
        Minecraft mc = GameUtils.getClient();
        Entity entity = mc.getRenderViewEntity();
        return entity != null ? entity : mc.player;
    }

    public static BlockPos getCameraEntityBlockPos()
    {
        Entity entity = getCameraEntity();
        return entity != null ? new BlockPos(entity) : BlockPos.ORIGIN;
    }

    public static Vec3d getCameraEntityPosition()
    {
        Entity entity = getCameraEntity();
        return entity != null ? entity.getPositionVector() : Vec3d.ZERO;
    }
}

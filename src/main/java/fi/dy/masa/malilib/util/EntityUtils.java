package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class EntityUtils
{
    /**
     * Returns the camera entity, if it's not null, otherwise returns the client player entity.
     * @return
     */
    @Nullable
    public static Entity getCameraEntity()
    {
        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.getRenderViewEntity();

        if (entity == null)
        {
            entity = mc.player;
        }

        return entity;
    }
}

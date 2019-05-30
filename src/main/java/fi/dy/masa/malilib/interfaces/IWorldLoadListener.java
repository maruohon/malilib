package fi.dy.masa.malilib.interfaces;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

public interface IWorldLoadListener
{
    /**
     * Called when the client world is going to be changed,
     * before the reference has been changed
     * @param world
     * @param mc
     */
    default void onWorldLoadPre(@Nullable WorldClient world, Minecraft mc) {}

    /**
     * Called after the client world reference has been changed
     * @param world
     * @param mc
     */
    default void onWorldLoadPost(@Nullable WorldClient world, Minecraft mc) {}
}

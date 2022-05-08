package fi.dy.masa.malilib.event;

import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

public interface ClientWorldChangeHandler
{
    /**
     * Called when the client world is going to be changed,
     * before the reference has been changed yet.
     * <br><br>
     * The classes implementing this method should be registered to {@link fi.dy.masa.malilib.event.dispatch.ClientWorldChangeEventDispatcherImpl}
     * @param worldBefore the old world reference, before the new one gets assigned
     * @param worldAfter the new world reference that is going to be assigned
     * @param mc
     */
    default void onPreClientWorldChange(@Nullable ClientWorld worldBefore, @Nullable ClientWorld worldAfter, MinecraftClient mc) {}

    /**
     * Called after the client world reference has been changed.
     * <br><br>
     * The classes implementing this method should be registered to {@link fi.dy.masa.malilib.event.dispatch.ClientWorldChangeEventDispatcherImpl}
     * @param worldBefore the old world reference, before the new one gets assigned
     * @param worldAfter the new world reference that is going to be assigned
     * @param mc
     */
    default void onPostClientWorldChange(@Nullable ClientWorld worldBefore, @Nullable ClientWorld worldAfter, MinecraftClient mc) {}
}

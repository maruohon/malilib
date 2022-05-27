package fi.dy.masa.malilib.event;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.WorldClient;

public interface ClientWorldChangeHandler extends PrioritizedEventHandler
{
    /**
     * Called when the client world is going to be changed,
     * before the reference has been changed yet.
     * <br><br>
     * The classes implementing this method should be registered to {@link fi.dy.masa.malilib.event.dispatch.ClientWorldChangeEventDispatcherImpl}
     * @param worldBefore the old world reference, before the new one gets assigned
     * @param worldAfter the new world reference that is going to be assigned
     */
    default void onPreClientWorldChange(@Nullable WorldClient worldBefore, @Nullable WorldClient worldAfter) {}

    /**
     * Called after the client world reference has been changed.
     * <br><br>
     * The classes implementing this method should be registered to {@link fi.dy.masa.malilib.event.dispatch.ClientWorldChangeEventDispatcherImpl}
     * @param worldBefore the old world reference, before the new one gets assigned
     * @param worldAfter the new world reference that is going to be assigned
     */
    default void onPostClientWorldChange(@Nullable WorldClient worldBefore, @Nullable WorldClient worldAfter) {}
}

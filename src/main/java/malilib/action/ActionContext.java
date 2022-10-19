package malilib.action;

import javax.annotation.Nullable;
import malilib.overlay.message.MessageOutput;
import malilib.util.game.wrap.GameUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public class ActionContext
{
    public static final ActionContext COMMON = new ActionContext();

    @Nullable public final MessageOutput messageOutput;

    public ActionContext()
    {
        this(null);
    }

    public ActionContext(@Nullable MessageOutput messageOutput)
    {
        this.messageOutput = messageOutput;
    }

    public MessageOutput getMessageOutputOrDefault(MessageOutput defaultOutput)
    {
        return this.messageOutput != null ? this.messageOutput : defaultOutput;
    }

    public MinecraftClient getClient()
    {
        return GameUtils.getClient();
    }

    @Nullable
    public ClientWorld getWorld()
    {
        return GameUtils.getClientWorld();
    }

    @Nullable
    public ClientPlayerEntity getPlayer()
    {
        return GameUtils.getClientPlayer();
    }

    @Nullable
    public Entity getCameraEntity()
    {
        return GameUtils.getCameraEntity();
    }
}

package fi.dy.masa.malilib.action;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.GameUtils;

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

    public Minecraft getClient()
    {
        return GameUtils.getClient();
    }

    @Nullable
    public WorldClient getWorld()
    {
        return GameUtils.getClientWorld();
    }

    @Nullable
    public EntityPlayerSP getPlayer()
    {
        return GameUtils.getClientPlayer();
    }

    @Nullable
    public Entity getCameraEntity()
    {
        return EntityUtils.getCameraEntity();
    }
}

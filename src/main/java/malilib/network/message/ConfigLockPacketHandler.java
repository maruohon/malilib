package malilib.network.message;

import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import malilib.MaLiLib;
import malilib.config.util.ConfigLockUtils;
import malilib.overlay.message.MessageDispatcher;
import malilib.registry.Registry;
import malilib.util.data.json.JsonUtils;

public class ConfigLockPacketHandler extends BasePacketHandler
{
    public static final String CHANNEL_NAME = "malilib:cfglock";
    public static final List<ResourceLocation> CHANNELS = ImmutableList.of(new ResourceLocation(CHANNEL_NAME));

    private static final ConfigLockPacketHandler INSTANCE = new ConfigLockPacketHandler();

    @Override
    public List<ResourceLocation> getChannels()
    {
        return CHANNELS;
    }

    @Override
    public void onPacketReceived(PacketBuffer buf)
    {
        try
        {
            boolean resetFirst = buf.readBoolean();
            String str = buf.readString(256 * 1024);
            JsonElement el = JsonUtils.parseJsonFromString(str);

            MaLiLib.debugLog("Received a config lock packet from the server (reset first: {})", resetFirst);

            if (el != null && el.isJsonObject())
            {
                if (resetFirst)
                {
                    ConfigLockUtils.resetConfigLocks();
                }

                ConfigLockUtils.applyConfigLocksFromServer(el.getAsJsonObject());

                return;
            }
        }
        catch (Exception e)
        {
            MessageDispatcher.error().console(e).translate("malilib.message.error.invalid_config_lock_packet");
            return;
        }

        MessageDispatcher.error().console().translate("malilib.message.error.invalid_config_lock_packet");
    }

    public static void updateRegistration(boolean enabled)
    {
        if (enabled)
        {
            Registry.CLIENT_PACKET_CHANNEL_HANDLER.registerClientChannelHandler(INSTANCE);
        }
        else
        {
            Registry.CLIENT_PACKET_CHANNEL_HANDLER.unregisterClientChannelHandler(INSTANCE);
        }
    }
}

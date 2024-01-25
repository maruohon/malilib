package malilib.network.message;

import java.io.DataInputStream;
import com.google.gson.JsonElement;

import malilib.MaLiLib;
import malilib.config.util.ConfigLockUtils;
import malilib.overlay.message.MessageDispatcher;
import malilib.registry.Registry;
import malilib.util.data.Identifier;
import malilib.util.data.json.JsonUtils;

public class ConfigLockPacketHandler extends BasePacketHandler
{
    public static final Identifier CHANNEL_NAME = new Identifier("malilib:cfglock");

    private static final ConfigLockPacketHandler INSTANCE = new ConfigLockPacketHandler();

    @Override
    public Identifier getChannel()
    {
        return CHANNEL_NAME;
    }

    @Override
    public boolean onPacketReceived(DataInputStream buf)
    {
        try
        {
            boolean resetFirst = buf.readBoolean();
            String str = buf.readUTF();
            JsonElement el = JsonUtils.parseJsonFromString(str);

            MaLiLib.debugLog("Received a config lock packet from the server (reset first: {})", resetFirst);

            if (el != null && el.isJsonObject())
            {
                if (resetFirst)
                {
                    ConfigLockUtils.resetConfigLocks();
                }

                ConfigLockUtils.applyConfigLocksFromServer(el.getAsJsonObject());

                return true;
            }
        }
        catch (Exception e)
        {
            MessageDispatcher.error().console(e).translate("malilib.message.error.invalid_config_lock_packet");
            return true;
        }

        MessageDispatcher.error().console().translate("malilib.message.error.invalid_config_lock_packet");

        return true;
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

package fi.dy.masa.malilib.network.message;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.network.PluginChannelHandler;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.registry.Registry;

/**
 * This packet is for receiving messages from the server that should be displayed
 * on one of the message renderers added by malilib, such as the Toast Renderer,
 * the Message Renderer or the custom "Action Bar" (which is also just a Message Renderer).
 * <br><br>
 * The packet format is:<br>
 *         - InfoType (string - one of: "message", "toast", "custom_hotbar", "vanilla_hotbar", "chat")<br>
 *         - hasLocation (boolean)<br>
 *         - hasMarker (boolean)<br>
 *         - displayTimeMs (varInt)<br>
 *         - [if type != "toast"] color (int) - the default text color, if the text does not use formatting/color codes<br>
 *         - [if hasLocation] ScreenLocation (string - one of: "bottom_left", "bottom_right", "bottom_center",
 *           "top_left", "top_right", "top_center", "center", "center_left", "center_right")<br>
 *         - [if hasMarker] marker (string) - this allows appending to a previous toast message with the same marker, if the previous message still young enough. Maximum length is 64 characters.<br>
 *         - message (string) - the message text, maximum length is 8192 characters. Supports malilib's custom text
 *           styling options as well as vanilla chat format codes.
 */
public class MessagePacketHandler implements PluginChannelHandler
{
    public static final String CHANNEL_NAME = "malilib:message";
    public static final List<ResourceLocation> CHANNELS = ImmutableList.of(new ResourceLocation(CHANNEL_NAME));

    private static final MessagePacketHandler INSTANCE = new MessagePacketHandler();

    @Override
    public List<ResourceLocation> getChannels()
    {
        return CHANNELS;
    }

    @Override
    public void onPacketReceived(PacketBuffer buf)
    {
        // type (string)
        // hasLocation (boolean)
        // hasMarker (boolean)
        // displayTimeMs (varInt)
        // [if type != "toast"] color (int)
        // [if hasLocation] ScreenLocation (string)
        // [if hasMarker] marker (string)
        // message (string)

        @Nullable ScreenLocation location = null;
        @Nullable String marker = null;
        MessageOutput type = MessageOutput.findValueByName(buf.readString(16), MessageOutput.getValues());
        boolean hasLocation = buf.readBoolean();
        boolean hasMarker = buf.readBoolean();
        int displayTimeMs = buf.readVarInt();
        int defaultColor = type != MessageOutput.TOAST ? buf.readInt() : 0xFFFFFFFF;
        String message;

        if (hasLocation)
        {
            location = ScreenLocation.findValueByName(buf.readString(16), ScreenLocation.VALUES);
        }

        if (hasMarker)
        {
            marker = buf.readString(64);
        }

        message = buf.readString(8192);

        MessageDispatcher.generic(defaultColor)
                      .type(type).location(location)
                      .time(displayTimeMs)
                      .rendererMarker(marker).append(true)
                      .send(message);
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

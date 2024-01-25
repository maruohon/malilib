package malilib.network.message;

import java.io.DataInputStream;
import java.io.IOException;
import javax.annotation.Nullable;

import malilib.config.value.ScreenLocation;
import malilib.overlay.message.MessageDispatcher;
import malilib.overlay.message.MessageOutput;
import malilib.registry.Registry;
import malilib.util.data.Identifier;

/**
 * This packet is for receiving messages from the server that should be displayed
 * on one of the message renderers added by malilib, such as the Toast Renderer,
 * the Message Renderer or the custom "Action Bar" (which is also just a Message Renderer).
 * <br><br>
 * The packet format is:<br>
 *         - InfoType (string - one of: "message", "toast", "custom_hotbar", "vanilla_hotbar", "chat")<br>
 *         - displayTimeMs (varInt)<br>
 *         - color (int) - the default text color, if the text does not use formatting/color codes<br>
 *         - hasLocation (boolean)<br>
 *         - [if hasLocation] ScreenLocation (string - one of: "bottom_left", "bottom_right", "bottom_center",
 *           "top_left", "top_right", "top_center", "center", "center_left", "center_right")<br>
 *         - hasMarker (boolean)<br>
 *         - [if hasMarker] marker (string) - this allows appending to a previous toast message with the same marker,
 *           if the previous message is still young enough. Maximum length is 64 characters.<br>
 *         - message (string) - the message text, maximum length is 8192 characters. Supports malilib's custom text
 *           styling options as well as vanilla chat format codes.
 */
public class MessagePacketHandler extends BasePacketHandler
{
    public static final Identifier CHANNEL_NAME = new Identifier("malilib:message");

    private static final MessagePacketHandler INSTANCE = new MessagePacketHandler();

    @Override
    public Identifier getChannel()
    {
        return CHANNEL_NAME;
    }

    @Override
    public boolean onPacketReceived(DataInputStream buf) throws IOException
    {
        // type (string)
        // displayTimeMs (varInt)
        // color (int)
        // hasLocation (boolean)
        // [if hasLocation] ScreenLocation (string)
        // hasMarker (boolean)
        // [if hasMarker] marker (string)
        // message (string)

        @Nullable ScreenLocation location = null;
        @Nullable String marker = null;
        MessageOutput type = MessageOutput.findValueByName(buf.readUTF(), MessageOutput.getValues());
        int displayTimeMs = buf.readInt();
        int defaultColor = buf.readInt();

        boolean hasLocation = buf.readBoolean();

        if (hasLocation)
        {
            location = ScreenLocation.findValueByName(buf.readUTF(), ScreenLocation.VALUES);
        }

        boolean hasMarker = buf.readBoolean();

        if (hasMarker)
        {
            marker = buf.readUTF();
        }

        String message = buf.readUTF();

        MessageDispatcher.generic(displayTimeMs)
                .type(type)
                .location(location)
                .color(defaultColor)
                .rendererMarker(marker).append(true)
                .send(message);

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

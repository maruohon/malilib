package fi.dy.masa.malilib.util;

import fi.dy.masa.malilib.interfaces.IStringConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class InfoUtils
{
    public static final IStringConsumer INFO_MESSAGE_CONSUMER = new InfoMessageConsumer();

    public static class InfoMessageConsumer implements IStringConsumer
    {
        @Override
        public void setString(String string)
        {
            Component message = new TranslatableComponent(string);
            MinecraftClient.getInstance().inGameHud.addChatMessage(ChatMessageType.GAME_INFO, message);
        }
    }
}

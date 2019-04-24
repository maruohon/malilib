package fi.dy.masa.malilib.util;

import fi.dy.masa.malilib.interfaces.IStringConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ChatMessageType;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class InfoUtils
{
    public static final IStringConsumer INFO_MESSAGE_CONSUMER = new InfoMessageConsumer();

    public static class InfoMessageConsumer implements IStringConsumer
    {
        @Override
        public void setString(String string)
        {
            TextComponent message = new TranslatableTextComponent(string);
            MinecraftClient.getInstance().inGameHud.addChatMessage(ChatMessageType.GAME_INFO, message);
        }
    }
}

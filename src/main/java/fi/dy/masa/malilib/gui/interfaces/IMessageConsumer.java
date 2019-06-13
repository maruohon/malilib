package fi.dy.masa.malilib.gui.interfaces;

import fi.dy.masa.malilib.gui.Message.MessageType;

public interface IMessageConsumer
{
    void addMessage(MessageType type, String messageKey, Object... args);

    void addMessage(MessageType type, int lifeTime, String messageKey, Object... args);
}

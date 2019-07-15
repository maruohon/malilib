package fi.dy.masa.malilib.gui.interfaces;

import fi.dy.masa.malilib.gui.Message.MessageType;

public interface IMessageConsumer
{
    default void addMessage(MessageType type, String messageKey, Object... args)
    {
        this.addMessage(type, 5000, messageKey, args);
    }

    void addMessage(MessageType type, int lifeTime, String messageKey, Object... args);
}

package fi.dy.masa.malilib.gui.interfaces;

import fi.dy.masa.malilib.gui.Message.MessageType;

public interface IMessageConsumer
{
    void addMessage(MessageType type, String messageKey);

    void addMessage(MessageType type, String messageKey, Object... args);
}

package fi.dy.masa.malilib.message;

public interface IMessageConsumer
{
    default void addMessage(MessageType type, String messageKey, Object... args)
    {
        this.addMessage(type, 5000, messageKey, args);
    }

    void addMessage(MessageType type, int lifeTimeMs, String messageKey, Object... args);
}

package fi.dy.masa.malilib.overlay.message;

public interface MessageConsumer
{
    default void addMessage(MessageType type, String messageKey, Object... args)
    {
        this.addMessage(type, 5000, messageKey, args);
    }

    void addMessage(MessageType type, int lifeTimeMs, String messageKey, Object... args);
}

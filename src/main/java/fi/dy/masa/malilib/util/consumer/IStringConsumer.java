package fi.dy.masa.malilib.util.consumer;

public interface IStringConsumer
{
    /**
     * Consumes the provided string, and returns true on success, or false on failure
     * @param string
     * @return true if the operation succeeded, false if it failed
     */
    boolean consumeString(String string);
}

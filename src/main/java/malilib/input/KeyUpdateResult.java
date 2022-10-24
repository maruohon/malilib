package malilib.input;

public class KeyUpdateResult
{
    public final boolean cancel;
    public final boolean triggered;

    public KeyUpdateResult(boolean cancel, boolean triggered)
    {
        this.cancel = cancel;
        this.triggered = triggered;
    }
}

package malilib.input.callback;

public class TimeIntervalValueScaler
{
    protected final long intervalMs;
    protected final int multiplier;
    protected long lastTime;

    public TimeIntervalValueScaler(long intervalMs, int multiplier)
    {
        this.intervalMs = intervalMs;
        this.multiplier = multiplier;
        this.lastTime = System.nanoTime() / 1000000L;
    }

    public int getScaledValue(int value)
    {
        long currentTime = System.nanoTime() / 1000000L;

        if (currentTime - this.lastTime <= this.intervalMs)
        {
            value *= this.multiplier;
        }

        this.lastTime = currentTime;

        return value;
    }
}

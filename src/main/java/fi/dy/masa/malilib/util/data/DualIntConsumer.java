package fi.dy.masa.malilib.util.data;

import java.util.function.IntConsumer;

public class DualIntConsumer implements IntConsumer
{
    protected final IntConsumer consumerOne;
    protected final IntConsumer consumerTwo;

    public DualIntConsumer(IntConsumer consumerOne, IntConsumer consumerTwo)
    {
        this.consumerOne = consumerOne;
        this.consumerTwo = consumerTwo;
    }

    @Override
    public void accept(int value)
    {
        this.consumerOne.accept(value);
        this.consumerTwo.accept(value);
    }
}

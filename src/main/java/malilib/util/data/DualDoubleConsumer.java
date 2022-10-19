package malilib.util.data;

import java.util.function.DoubleConsumer;

public class DualDoubleConsumer implements DoubleConsumer
{
    protected final DoubleConsumer consumerOne;
    protected final DoubleConsumer consumerTwo;

    public DualDoubleConsumer(DoubleConsumer consumerOne, DoubleConsumer consumerTwo)
    {
        this.consumerOne = consumerOne;
        this.consumerTwo = consumerTwo;
    }

    @Override
    public void accept(double value)
    {
        this.consumerOne.accept(value);
        this.consumerTwo.accept(value);
    }
}

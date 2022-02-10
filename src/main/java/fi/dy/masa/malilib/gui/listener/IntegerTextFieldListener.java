package fi.dy.masa.malilib.gui.listener;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class IntegerTextFieldListener implements Consumer<String>
{
    protected final IntConsumer consumer;

    public IntegerTextFieldListener(IntConsumer consumer)
    {
        this.consumer = consumer;
    }

    @Override
    public void accept(String newText)
    {
        try
        {
            this.consumer.accept(Integer.parseInt(newText));
        }
        catch (Exception ignore) {}
    }
}

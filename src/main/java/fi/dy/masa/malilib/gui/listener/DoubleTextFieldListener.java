package fi.dy.masa.malilib.gui.listener;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public class DoubleTextFieldListener implements Consumer<String>
{
    protected final DoubleConsumer consumer;

    public DoubleTextFieldListener(DoubleConsumer consumer)
    {
        this.consumer = consumer;
    }

    @Override
    public void accept(String newText)
    {
        try
        {
            this.consumer.accept(Double.parseDouble(newText));
        }
        catch (Exception ignore) {}
    }
}

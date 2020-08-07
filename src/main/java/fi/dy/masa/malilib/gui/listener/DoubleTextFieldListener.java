package fi.dy.masa.malilib.gui.listener;

import java.util.function.DoubleConsumer;
import fi.dy.masa.malilib.listener.TextChangeListener;

public class DoubleTextFieldListener implements TextChangeListener
{
    protected final DoubleConsumer consumer;

    public DoubleTextFieldListener(DoubleConsumer consumer)
    {
        this.consumer = consumer;
    }

    @Override
    public void onTextChange(String newText)
    {
        try
        {
            this.consumer.accept(Double.parseDouble(newText));
        }
        catch (Exception e) {}
    }
}

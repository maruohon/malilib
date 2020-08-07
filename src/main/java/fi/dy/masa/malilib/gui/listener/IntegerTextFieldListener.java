package fi.dy.masa.malilib.gui.listener;

import java.util.function.IntConsumer;
import fi.dy.masa.malilib.listener.TextChangeListener;

public class IntegerTextFieldListener implements TextChangeListener
{
    protected final IntConsumer consumer;

    public IntegerTextFieldListener(IntConsumer consumer)
    {
        this.consumer = consumer;
    }

    @Override
    public void onTextChange(String newText)
    {
        try
        {
            this.consumer.accept(Integer.parseInt(newText));
        }
        catch (Exception e) {}
    }
}

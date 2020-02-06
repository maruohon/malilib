package fi.dy.masa.malilib.gui.listener;

import java.util.function.DoubleConsumer;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;

public class TextFieldListenerDouble implements ITextFieldListener
{
    protected final DoubleConsumer consumer;

    public TextFieldListenerDouble(DoubleConsumer consumer)
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

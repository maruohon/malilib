package fi.dy.masa.malilib.gui.listener;

import java.util.function.IntConsumer;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;

public class TextFieldListenerInteger implements ITextFieldListener
{
    protected final IntConsumer consumer;

    public TextFieldListenerInteger(IntConsumer consumer)
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

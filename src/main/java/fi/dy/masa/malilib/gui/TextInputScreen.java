package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.listener.TaskCompletionListener;
import fi.dy.masa.malilib.util.consumer.StringConsumer;

public class TextInputScreen extends BaseTextInputScreen implements TaskCompletionListener
{
    protected final StringConsumer stringConsumer;

    public TextInputScreen(String titleKey, String defaultText, @Nullable GuiScreen parent, StringConsumer stringConsumer)
    {
        super(titleKey, defaultText, parent);

        this.stringConsumer = stringConsumer;
    }

    @Override
    protected boolean applyValue(String string)
    {
        return this.stringConsumer.consumeString(string);
    }

    @Override
    public void onTaskCompleted()
    {
        if (this.getParent() instanceof TaskCompletionListener)
        {
            ((TaskCompletionListener) this.getParent()).onTaskCompleted();
        }
    }

    @Override
    public void onTaskAborted()
    {
        if (this.getParent() instanceof TaskCompletionListener)
        {
            ((TaskCompletionListener) this.getParent()).onTaskAborted();
        }
    }
}

package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.listener.ICompletionListener;
import fi.dy.masa.malilib.message.IMessageConsumer;
import fi.dy.masa.malilib.message.MessageType;
import fi.dy.masa.malilib.util.consumer.IStringConsumer;

public class TextInputScreen extends BaseTextInputScreen implements ICompletionListener
{
    protected final IStringConsumer stringConsumer;

    public TextInputScreen(String titleKey, String defaultText, @Nullable GuiScreen parent, IStringConsumer stringConsumer)
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
        if (this.getParent() instanceof ICompletionListener)
        {
            ((ICompletionListener) this.getParent()).onTaskCompleted();
        }
    }

    @Override
    public void onTaskAborted()
    {
        if (this.getParent() instanceof ICompletionListener)
        {
            ((ICompletionListener) this.getParent()).onTaskAborted();
        }
    }

    @Override
    public void addMessage(MessageType type, int lifeTime, String messageKey, Object... args)
    {
        if (this.getParent() instanceof IMessageConsumer)
        {
            ((IMessageConsumer) this.getParent()).addMessage(type, lifeTime, messageKey, args);
        }
        else
        {
            super.addMessage(type, lifeTime, messageKey, args);
        }
    }
}

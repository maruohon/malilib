package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.util.consumer.StringConsumer;

public class TextInputScreen extends BaseTextInputScreen
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
}

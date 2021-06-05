package fi.dy.masa.malilib.util;

import java.io.File;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.widget.util.DirectoryNavigator;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.util.consumer.StringConsumer;

public class DirectoryCreator implements StringConsumer
{
    protected final File dir;
    @Nullable protected final DirectoryNavigator navigator;

    public DirectoryCreator(File dir, @Nullable DirectoryNavigator navigator)
    {
        this.dir = dir;
        this.navigator = navigator;
    }

    @Override
    public boolean consumeString(String string)
    {
        if (string.isEmpty())
        {
            MessageDispatcher.error("malilib.message.error.invalid_directory", string);
            return false;
        }

        File file = new File(this.dir, string);

        if (file.exists())
        {
            MessageDispatcher.error("malilib.message.error.file_or_directory_already_exists", file.getAbsolutePath());
            return false;
        }

        if (file.mkdirs() == false)
        {
            MessageDispatcher.error("malilib.message.error.failed_to_create_directory", file.getAbsolutePath());
            return false;
        }

        if (this.navigator != null)
        {
            this.navigator.switchToDirectory(file);
        }

        MessageDispatcher.success("malilib.message.success.directory_created", string);

        return true;
    }
}

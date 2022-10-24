package malilib.util;

import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;

import malilib.gui.widget.util.DirectoryNavigator;
import malilib.overlay.message.MessageDispatcher;
import malilib.util.data.ResultingStringConsumer;

public class DirectoryCreator implements ResultingStringConsumer
{
    protected final Path dir;
    @Nullable protected final DirectoryNavigator navigator;

    public DirectoryCreator(Path dir, @Nullable DirectoryNavigator navigator)
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

        Path file = this.dir.resolve(string);

        if (Files.exists(file))
        {
            MessageDispatcher.error("malilib.message.error.file_or_directory_already_exists",
                                    file.toAbsolutePath().toString());
            return false;
        }

        if (FileUtils.createDirectoriesIfMissing(file) == false)
        {
            MessageDispatcher.error("malilib.message.error.failed_to_create_directory",
                                    file.toAbsolutePath().toString());
            return false;
        }

        if (this.navigator != null)
        {
            this.navigator.switchToDirectory(file);
        }

        MessageDispatcher.success("malilib.message.info.directory_created", string);

        return true;
    }
}

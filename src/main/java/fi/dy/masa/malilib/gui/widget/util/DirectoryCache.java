package fi.dy.masa.malilib.gui.widget.util;

import java.nio.file.Path;
import javax.annotation.Nullable;

public interface DirectoryCache
{
    @Nullable
    Path getCurrentDirectoryForContext(String context);

    void setCurrentDirectoryForContext(String context, Path dir);
}

package fi.dy.masa.malilib.gui.widget.util;

import java.io.File;
import javax.annotation.Nullable;

public interface DirectoryCache
{
    @Nullable
    File getCurrentDirectoryForContext(String context);

    void setCurrentDirectoryForContext(String context, File dir);
}

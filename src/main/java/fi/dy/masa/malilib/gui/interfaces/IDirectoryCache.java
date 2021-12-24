package fi.dy.masa.malilib.gui.interfaces;

import java.io.File;
import org.jetbrains.annotations.Nullable;

public interface IDirectoryCache
{
    @Nullable
    File getCurrentDirectoryForContext(String context);

    void setCurrentDirectoryForContext(String context, File dir);
}

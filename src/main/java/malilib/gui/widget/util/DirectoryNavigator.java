package malilib.gui.widget.util;

import java.nio.file.Path;

public interface DirectoryNavigator
{
    Path getCurrentDirectory();

    void switchToDirectory(Path dir);

    void switchToParentDirectory();

    void switchToRootDirectory();
}

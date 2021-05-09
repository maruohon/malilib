package fi.dy.masa.malilib.gui;

import java.io.File;
import java.io.FileFilter;
import java.util.function.Consumer;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget;

public class FileSelectorScreen extends DirectorySelectorScreen
{
    public FileSelectorScreen(File currentDirectory, File rootDirectory, Consumer<File> fileConsumer)
    {
        super(currentDirectory, rootDirectory, fileConsumer);

        this.setTitle("malilib.gui.title.file_browser");
    }

    @Override
    protected FileFilter getFileFilter()
    {
        return BaseFileBrowserWidget.ALWAYS_TRUE_FILE_FILTER;
    }

    @Override
    protected String getButtonLabel()
    {
        return "malilib.gui.button.config.use_selected_file";
    }

    @Override
    protected void onConfirm()
    {
        BaseFileBrowserWidget.DirectoryEntry entry = this.getListWidget().getEntrySelectionHandler().getLastSelectedEntry();

        if (entry != null && entry.getType() == BaseFileBrowserWidget.DirectoryEntryType.FILE)
        {
            this.fileConsumer.accept(entry.getFullPath());
            BaseScreen.openScreen(this.getParent());
        }
    }
}

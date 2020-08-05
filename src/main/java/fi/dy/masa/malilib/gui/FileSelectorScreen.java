package fi.dy.masa.malilib.gui;

import java.io.File;
import java.io.FileFilter;
import java.util.function.Consumer;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class FileSelectorScreen extends DirectorySelectorScreen
{
    public FileSelectorScreen(File currentDirectory, File rootDirectory, Consumer<File> fileConsumer)
    {
        super(currentDirectory, rootDirectory, fileConsumer);

        this.title = StringUtils.translate("malilib.gui.title.file_browser");
    }

    @Override
    protected FileFilter getFileFilter()
    {
        return BaseFileBrowserWidget.ALWAYS_TRUE_FILE_FILTER;
    }

    @Override
    protected void addConfirmationButton()
    {
        ButtonGeneric button = new ButtonGeneric(10, this.height - 26, -1, 20, "malilib.gui.button.config.use_selected_file");

        this.addButton(button, (btn, mbtn) -> {
            BaseFileBrowserWidget.DirectoryEntry entry = this.getListWidget().getEntrySelectionHandler().getLastSelectedEntry();

            if (entry != null && entry.getType() == BaseFileBrowserWidget.DirectoryEntryType.FILE)
            {
                this.fileConsumer.accept(entry.getFullPath());
                BaseScreen.openGui(this.getParent());
            }
        });
    }
}

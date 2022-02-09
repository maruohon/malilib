package fi.dy.masa.malilib.gui;

import java.io.File;
import java.util.function.Consumer;
import fi.dy.masa.malilib.util.FileUtils;

public class DirectorySelectorScreen extends FileSelectorScreen
{
    public DirectorySelectorScreen(File currentDirectory, File rootDirectory, Consumer<File> fileConsumer)
    {
        super(currentDirectory, rootDirectory, fileConsumer);

        this.fileFilter = FileUtils.ALWAYS_FALSE_FILEFILTER;
        this.setTitle("malilib.gui.title.directory_browser");
    }

    @Override
    protected String getButtonLabel()
    {
        return "malilib.gui.button.config.use_current_directory";
    }

    @Override
    protected void onConfirm()
    {
        this.fileConsumer.accept(this.getListWidget().getCurrentDirectory());
        BaseScreen.openScreen(this.getParent());
    }
}

package fi.dy.masa.malilib.gui;

import java.nio.file.Path;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.data.ToBooleanFunction;

public class DirectorySelectorScreen extends FileSelectorScreen
{
    public DirectorySelectorScreen(Path currentDirectory, Path rootDirectory, ToBooleanFunction<Path> fileConsumer)
    {
        super(currentDirectory, rootDirectory, fileConsumer);

        this.fileFilter = FileUtils.ALWAYS_FALSE_FILEFILTER;
        this.setTitle("malilib.title.screen.directory_browser");
    }

    @Override
    protected String getButtonLabel()
    {
        return "malilib.button.config.use_current_directory";
    }

    @Override
    protected void onConfirm()
    {
        if (this.fileConsumer.applyAsBoolean(this.getListWidget().getCurrentDirectory()))
        {
            BaseScreen.openScreen(this.getParent());
        }
    }
}

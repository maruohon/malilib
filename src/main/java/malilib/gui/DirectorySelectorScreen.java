package malilib.gui;

import java.nio.file.Path;
import malilib.util.FileUtils;
import malilib.util.data.ToBooleanFunction;

public class DirectorySelectorScreen extends FileSelectorScreen
{
    public DirectorySelectorScreen(Path currentDirectory, Path rootDirectory, ToBooleanFunction<Path> fileConsumer)
    {
        super(currentDirectory, rootDirectory, fileConsumer);

        this.fileFilter = FileUtils.ALWAYS_FALSE_FILEFILTER;
        this.setTitle("malilibdev.title.screen.directory_browser");
    }

    @Override
    protected String getButtonLabel()
    {
        return "malilibdev.button.config.use_current_directory";
    }

    @Override
    protected void onConfirm()
    {
        if (this.fileConsumer.applyAsBoolean(this.getListWidget().getCurrentDirectory()))
        {
            openScreen(this.getParent());
        }
    }
}

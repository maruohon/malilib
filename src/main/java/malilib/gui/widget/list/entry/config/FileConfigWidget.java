package malilib.gui.widget.list.entry.config;

import java.nio.file.Path;
import malilib.config.option.FileConfig;
import malilib.gui.BaseScreen;
import malilib.gui.FileSelectorScreen;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class FileConfigWidget extends BaseFileConfigWidget<Path, FileConfig>
{
    public FileConfigWidget(FileConfig config,
                            DataListEntryWidgetData constructData,
                            ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);
    }

    @Override
    protected Path getFileFromConfig()
    {
        return this.config.getValue();
    }

    @Override
    protected void setFileToConfig(Path file)
    {
        this.config.setValue(file);
    }

    @Override
    protected BaseScreen createScreen(Path currentDir, Path rootDir)
    {
        return new FileSelectorScreen(currentDir, rootDir, this::onPathSelected);
    }

    @Override
    protected String getButtonLabelKey()
    {
        return "malilib.button.config.select_file";
    }

    @Override
    protected String getButtonHoverTextKey()
    {
        return "malilib.hover.button.config.selected_file";
    }
}

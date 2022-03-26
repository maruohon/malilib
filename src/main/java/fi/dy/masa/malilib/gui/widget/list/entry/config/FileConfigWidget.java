package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.io.File;
import fi.dy.masa.malilib.config.option.FileConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.FileSelectorScreen;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class FileConfigWidget extends BaseFileConfigWidget<File, FileConfig>
{
    public FileConfigWidget(FileConfig config,
                            DataListEntryWidgetData constructData,
                            ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);
    }

    @Override
    protected File getFileFromConfig()
    {
        return this.config.getValue();
    }

    @Override
    protected void setFileToConfig(File file)
    {
        this.config.setValue(file);
    }

    @Override
    protected BaseScreen createScreen(File currentDir, File rootDir)
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

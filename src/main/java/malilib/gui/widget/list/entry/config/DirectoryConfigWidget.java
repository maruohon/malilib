package malilib.gui.widget.list.entry.config;

import java.nio.file.Path;

import malilib.config.option.DirectoryConfig;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class DirectoryConfigWidget extends BaseFileConfigWidget<Path, DirectoryConfig>
{
    public DirectoryConfigWidget(DirectoryConfig config,
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
}

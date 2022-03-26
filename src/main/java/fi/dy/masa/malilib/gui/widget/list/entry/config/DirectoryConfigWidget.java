package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.io.File;
import fi.dy.masa.malilib.config.option.DirectoryConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class DirectoryConfigWidget extends BaseFileConfigWidget<File, DirectoryConfig>
{
    public DirectoryConfigWidget(DirectoryConfig config,
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
}

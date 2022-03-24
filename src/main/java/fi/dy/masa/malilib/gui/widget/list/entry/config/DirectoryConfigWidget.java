package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.io.File;
import fi.dy.masa.malilib.config.option.DirectoryConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.DirectorySelectorScreen;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class DirectoryConfigWidget extends FileConfigWidget
{
    public DirectoryConfigWidget(DirectoryConfig config,
                                 DataListEntryWidgetData constructData,
                                 ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);
    }

    @Override
    protected String getButtonLabelKey()
    {
        return "malilib.button.config.select_directory";
    }

    @Override
    protected String getButtonHoverTextKey()
    {
        return "malilib.hover.button.config.selected_directory";
    }

    @Override
    protected BaseScreen createScreen(File currentDir, File rootDir)
    {
        return new DirectorySelectorScreen(currentDir, rootDir, this::onPathSelected);
    }
}

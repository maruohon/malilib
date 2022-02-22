package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.io.File;
import fi.dy.masa.malilib.config.option.DirectoryConfig;
import fi.dy.masa.malilib.gui.DirectorySelectorScreen;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.util.FileUtils;

public class DirectoryConfigWidget extends BaseConfigOptionWidget<File, DirectoryConfig>
{
    public DirectoryConfigWidget(DirectoryConfig config,
                                 DataListEntryWidgetData constructData,
                                 ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        final File rootDir = FileUtils.getRootDirectory();
        final File file = FileUtils.getCanonicalFileIfPossible(this.config.getValue().getAbsoluteFile());
        final File dir = file == null || file.isDirectory() == false ? (file != null ? file.getParentFile() : rootDir) : file;

        FileSelectorScreenFactory factory = () -> new DirectorySelectorScreen(dir, rootDir, (d) -> {
            this.config.setValueFromString(d.getAbsolutePath());
            this.reAddSubWidgets();
            return true;
        });

        String labelKey = "malilib.button.config.select_directory";
        String hoverKey = "malilib.hover.button.config.selected_directory";
        this.createFileSelectorWidgets(this.getY(), this.config, factory, labelKey, hoverKey);
    }
}

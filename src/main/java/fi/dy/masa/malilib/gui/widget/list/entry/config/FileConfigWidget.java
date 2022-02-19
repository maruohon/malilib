package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.io.File;
import fi.dy.masa.malilib.config.option.FileConfig;
import fi.dy.masa.malilib.gui.FileSelectorScreen;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.util.FileUtils;

public class FileConfigWidget extends BaseConfigOptionWidget<File, FileConfig>
{
    public FileConfigWidget(int x, int y, int width, int height, int listIndex,
                            int originalListIndex, FileConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        final File rootDir = FileUtils.getRootDirectory();
        final File file = this.config.getValue().getAbsoluteFile();
        final File dir = file == null || file.isDirectory() == false ? (file != null ? file.getParentFile() : rootDir) : file;

        FileSelectorScreenFactory factory = () -> new FileSelectorScreen(dir, rootDir, (d) -> {
            this.config.setValueFromString(d.getAbsolutePath());
            this.reAddSubWidgets();
            return true;
        });

        String labelKey = "malilib.button.config.select_file";
        String hoverKey = "malilib.hover.button.config.selected_file";
        this.createFileSelectorWidgets(this.getY(), this.config, factory, labelKey, hoverKey);
    }
}

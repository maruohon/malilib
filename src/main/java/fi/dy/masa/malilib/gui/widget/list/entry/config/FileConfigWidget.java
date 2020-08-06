package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.io.File;
import fi.dy.masa.malilib.config.option.FileConfig;
import fi.dy.masa.malilib.gui.FileSelectorScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;

public class FileConfigWidget extends BaseConfigOptionWidget<FileConfig>
{
    protected final FileConfig config;
    protected final File initialValue;

    public FileConfigWidget(int x, int y, int width, int height, int listIndex, FileConfig config, BaseConfigScreen gui)
    {
        super(x, y, width, 22, listIndex, config, gui);

        this.config = config;
        this.initialValue = this.config.getFile();
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getX();
        int y = this.getY();

        File file = this.config.getFile().getAbsoluteFile();
        final File rootDir = new File("/");
        final File dir = file == null || file.isDirectory() == false ? (file != null ? file.getParentFile() : rootDir) : file;

        FileSelectorScreenFactory factory = () -> new FileSelectorScreen(dir, rootDir, (d) -> {
            this.config.setValueFromString(d.getAbsolutePath());
            this.reAddSubWidgets();
        });

        this.createFileSelectorWidgets(x, y, this.config, factory,
                                       "malilib.gui.button.config.select_file",
                                       "malilib.gui.button.config.hover.selected_file");
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getFile().equals(this.initialValue) == false;
    }
}

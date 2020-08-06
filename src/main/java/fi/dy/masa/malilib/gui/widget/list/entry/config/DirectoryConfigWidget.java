package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.io.File;
import fi.dy.masa.malilib.config.option.DirectoryConfig;
import fi.dy.masa.malilib.gui.DirectorySelectorScreen;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.util.FileUtils;

public class DirectoryConfigWidget extends BaseConfigOptionWidget<DirectoryConfig>
{
    protected final DirectoryConfig config;
    protected final File initialValue;

    public DirectoryConfigWidget(int x, int y, int width, int height, int listIndex, DirectoryConfig config, BaseConfigScreen gui)
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

        File file = FileUtils.getCanonicalFileIfPossible(this.config.getFile().getAbsoluteFile());
        final File rootDir = new File("/");
        final File dir = file == null || file.isDirectory() == false ? (file != null ? file.getParentFile() : rootDir) : file;

        FileSelectorScreenFactory factory = () -> new DirectorySelectorScreen(dir, rootDir, (d) -> {
            this.config.setValueFromString(d.getAbsolutePath());
            this.reAddSubWidgets();
        });

        this.createFileSelectorWidgets(x, y, this.config, factory,
                                       "malilib.gui.button.config.select_directory",
                                       "malilib.gui.button.config.hover.selected_directory");
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getFile().equals(this.initialValue) == false;
    }
}

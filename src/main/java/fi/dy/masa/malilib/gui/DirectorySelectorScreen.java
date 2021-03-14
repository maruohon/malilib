package fi.dy.masa.malilib.gui;

import java.io.File;
import java.io.FileFilter;
import java.util.function.Consumer;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class DirectorySelectorScreen extends BaseListScreen<BaseFileBrowserWidget>
{
    protected final File rootDirectory;
    protected final File currentDirectory;
    protected final Consumer<File> fileConsumer;

    public DirectorySelectorScreen(File currentDirectory, File rootDirectory, Consumer<File> fileConsumer)
    {
        super(10, 28, 20, 58);

        this.title = StringUtils.translate("malilib.gui.title.directory_browser");
        this.currentDirectory = currentDirectory;
        this.rootDirectory = rootDirectory;
        this.fileConsumer = fileConsumer;
    }

    protected FileFilter getFileFilter()
    {
        return BaseFileBrowserWidget.ALWAYS_FALSE_FILE_FILTER;
    }

    protected void addConfirmationButton()
    {
        GenericButton button = new GenericButton(10, this.height - 26, -1, 20, "malilib.gui.button.config.use_current_directory");

        this.addButton(button, (btn, mbtn) -> {
            this.fileConsumer.accept(this.getListWidget().getCurrentDirectory());
            BaseScreen.openScreen(this.getParent());
        });
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        this.addConfirmationButton();
    }

    @Override
    protected BaseFileBrowserWidget createListWidget(int listX, int listY, int listWidth, int listHeight)
    {
        BaseFileBrowserWidget widget = new BaseFileBrowserWidget(listX, listY, listWidth, listHeight,
                                                                 this.currentDirectory, this.rootDirectory, null, null);
        widget.setParentScreen(this.getParent());
        widget.setFileFilter(this.getFileFilter());

        return widget;
    }
}

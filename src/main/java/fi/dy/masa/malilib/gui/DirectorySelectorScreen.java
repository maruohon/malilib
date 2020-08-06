package fi.dy.masa.malilib.gui;

import java.io.File;
import java.io.FileFilter;
import java.util.function.Consumer;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class DirectorySelectorScreen extends BaseListScreen<BaseFileBrowserWidget>
{
    protected final File rootDirectory;
    protected final File currentDirectory;
    protected final Consumer<File> fileConsumer;

    public DirectorySelectorScreen(File currentDirectory, File rootDirectory, Consumer<File> fileConsumer)
    {
        super(10, 30);

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
        ButtonGeneric button = new ButtonGeneric(10, this.height - 26, -1, 20, "malilib.gui.button.config.use_current_directory");

        this.addButton(button, (btn, mbtn) -> {
            this.fileConsumer.accept(this.getListWidget().getCurrentDirectory());
            BaseScreen.openGui(this.getParent());
        });
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.addConfirmationButton();
    }

    @Override
    protected int getListWidth()
    {
        return this.width - 20;
    }

    @Override
    protected int getListHeight()
    {
        return this.height - 60;
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

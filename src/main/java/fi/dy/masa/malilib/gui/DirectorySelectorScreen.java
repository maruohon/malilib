package fi.dy.masa.malilib.gui;

import java.io.File;
import java.io.FileFilter;
import java.util.function.Consumer;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget;

public class DirectorySelectorScreen extends BaseListScreen<BaseFileBrowserWidget>
{
    protected final GenericButton confirmButton;
    protected final File rootDirectory;
    protected final File currentDirectory;
    protected final Consumer<File> fileConsumer;

    public DirectorySelectorScreen(File currentDirectory, File rootDirectory, Consumer<File> fileConsumer)
    {
        super(10, 28, 20, 58);

        this.currentDirectory = currentDirectory;
        this.rootDirectory = rootDirectory;
        this.fileConsumer = fileConsumer;

        this.confirmButton = new GenericButton(0, 0, -1, 20, this.getButtonLabel());
        this.confirmButton.setActionListener(this::onConfirm);

        this.setTitle("malilib.gui.title.directory_browser");
    }

    protected FileFilter getFileFilter()
    {
        return BaseFileBrowserWidget.ALWAYS_FALSE_FILE_FILTER;
    }

    protected String getButtonLabel()
    {
        return "malilib.gui.button.config.use_current_directory";
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.confirmButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        this.confirmButton.setPosition(this.x + 10, this.y + this.screenHeight - 26);
    }

    protected void onConfirm()
    {
        this.fileConsumer.accept(this.getListWidget().getCurrentDirectory());
        BaseScreen.openScreen(this.getParent());
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

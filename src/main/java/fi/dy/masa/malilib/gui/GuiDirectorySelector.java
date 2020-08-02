package fi.dy.masa.malilib.gui;

import java.io.File;
import java.util.function.Consumer;
import fi.dy.masa.malilib.gui.widget.WidgetDirectoryBrowser;
import fi.dy.masa.malilib.util.StringUtils;

public class GuiDirectorySelector extends GuiListBase<WidgetDirectoryBrowser>
{
    protected final File rootDirectory;
    protected final File currentDirectory;
    protected final Consumer<File> directoryConsumer;

    public GuiDirectorySelector(File currentDirectory, File rootDirectory, Consumer<File> directoryConsumer)
    {
        super(10, 30);

        this.title = StringUtils.translate("malilib.gui.title.directory_browser");
        this.currentDirectory = currentDirectory;
        this.rootDirectory = rootDirectory;
        this.directoryConsumer = directoryConsumer;
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        this.directoryConsumer.accept(this.getListWidget().getCurrentDirectory());
    }

    @Override
    protected int getBrowserWidth()
    {
        return this.width - 20;
    }

    @Override
    protected int getBrowserHeight()
    {
        return this.height - 40;
    }

    @Override
    protected WidgetDirectoryBrowser createListWidget(int listX, int listY)
    {
        WidgetDirectoryBrowser widget = new WidgetDirectoryBrowser(listX, listY,
                this.getBrowserWidth(), this.getBrowserHeight(), this.currentDirectory, this.rootDirectory);
        widget.setParentScreen(this.getParent());
        return widget;
    }
}

package fi.dy.masa.malilib.gui;

import java.io.File;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetDirectoryBrowser;
import fi.dy.masa.malilib.gui.widgets.WidgetDirectoryEntry;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase.DirectoryEntry;
import fi.dy.masa.malilib.util.StringUtils;

public class GuiDirectorySelector extends GuiListBase<DirectoryEntry, WidgetDirectoryEntry, WidgetDirectoryBrowser>
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
    @Nullable
    protected ISelectionListener<DirectoryEntry> getSelectionListener()
    {
        return null;
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
        widget.setParentGui(this.getParent());
        return widget;
    }
}

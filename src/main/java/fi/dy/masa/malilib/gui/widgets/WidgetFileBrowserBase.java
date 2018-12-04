package fi.dy.masa.malilib.gui.widgets;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryCache;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.gui.interfaces.IFileBrowserIconProvider;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase.DirectoryEntry;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.KeyCodes;

public abstract class WidgetFileBrowserBase extends WidgetListBase<DirectoryEntry, WidgetDirectoryEntry> implements IDirectoryNavigator
{
    protected static final FileFilter DIRECTORY_FILTER = new FileFilterDirectories();
    protected static final FileFilter SCHEMATIC_FILTER = new FileFilterSchematics();
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected final IDirectoryCache cache;
    protected File currentDirectory;
    protected final String browserContext;
    protected final IFileBrowserIconProvider iconProvider;

    @Nullable
    protected WidgetDirectoryNavigation directoryNavigationWidget;

    public WidgetFileBrowserBase(int x, int y, int width, int height,
            IDirectoryCache cache, String browserContext, File defaultDirectory,
            @Nullable ISelectionListener<DirectoryEntry> selectionListener, IFileBrowserIconProvider iconProvider)
    {
        super(x, y, width, height, selectionListener);

        this.cache = cache;
        this.browserContext = browserContext;
        this.currentDirectory = this.cache.getCurrentDirectoryForContext(this.browserContext);
        this.iconProvider = iconProvider;

        if (this.currentDirectory == null)
        {
            this.currentDirectory = defaultDirectory;
        }

        this.setSize(width, height);
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if ((keyCode == KeyCodes.KEY_BACK || keyCode == KeyCodes.KEY_LEFT) && this.currentDirectoryIsRoot() == false)
        {
            this.switchToParentDirectory();
            return true;
        }
        else if ((keyCode == KeyCodes.KEY_RIGHT || keyCode == KeyCodes.KEY_RETURN) &&
                  this.getSelectedEntry() != null && this.getSelectedEntry().getType() == DirectoryEntryType.DIRECTORY)
        {
            this.switchToDirectory(new File(this.getSelectedEntry().getDirectory(), this.getSelectedEntry().getName()));
            return true;
        }
        else
        {
            return super.onKeyTyped(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.directoryNavigationWidget != null && this.directoryNavigationWidget.onMouseClickedImpl(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawContents(int mouseX, int mouseY, float partialTicks)
    {
        // Draw an outline around the entire file browser
        RenderUtils.drawOutlinedBox(this.posX, this.posY, this.browserWidth, this.browserHeight, 0xB0000000, COLOR_HORIZONTAL_BAR);

        // Draw the root/up widget, is the current directory has that (ie. is not the root directory)
        if (this.directoryNavigationWidget != null)
        {
            this.directoryNavigationWidget.render(mouseX, mouseY, false);
        }

        this.drawAdditionalContents(mouseX, mouseY);

        super.drawContents(mouseX, mouseY, partialTicks);
    }

    protected abstract void drawAdditionalContents(int mouseX, int mouseY);

    @Override
    public void setSize(int width, int height)
    {
        super.setSize(width, height);

        this.browserWidth = this.getBrowserWidthForTotalWidth(width);
        this.browserEntryWidth = this.browserWidth - 14;
    }

    protected int getBrowserWidthForTotalWidth(int width)
    {
        return width - 6;
    }

    protected void updateDirectoryNavigationWidget()
    {
        if (this.currentDirectoryIsRoot() == false)
        {
            int x = this.posX + 2;
            int y = this.posY + 4;

            this.directoryNavigationWidget = new WidgetDirectoryNavigation(x, y, this.browserEntryWidth, 14, this.zLevel,
                    this.currentDirectory, this.getRootDirectory(), this.mc, this, this.iconProvider);
            this.browserEntriesOffsetY = this.directoryNavigationWidget.getHeight() + 2;
        }
        else
        {
            this.directoryNavigationWidget = null;
            this.browserEntriesOffsetY = 0;
        }
    }

    @Override
    public void refreshEntries()
    {
        this.listContents.clear();

        File dir = this.currentDirectory;

        if (dir.isDirectory() && dir.canRead())
        {
            List<DirectoryEntry> list = new ArrayList<>();

            this.addDirectoryEntriesToList(dir, list);
            this.listContents.addAll(list);
            list.clear();

            this.addFileEntriesToList(dir, list);
            this.listContents.addAll(list);
        }

        this.updateDirectoryNavigationWidget();
        this.reCreateListEntryWidgets();
    }

    protected abstract File getRootDirectory();

    protected void addDirectoryEntriesToList(File dir, List<DirectoryEntry> list)
    {
        // Show directories at the top
        for (File file : dir.listFiles(DIRECTORY_FILTER))
        {
            list.add(new DirectoryEntry(DirectoryEntryType.fromFile(file), dir, file.getName()));
        }

        Collections.sort(list);
    }

    protected abstract void addFileEntriesToList(File dir, List<DirectoryEntry> list);

    @Override
    protected WidgetDirectoryEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, DirectoryEntry entry)
    {
        return new WidgetDirectoryEntry(x, y, this.browserEntryWidth, this.getBrowserEntryHeightFor(entry),
                this.zLevel, isOdd, entry, this.mc, this, this.iconProvider);
    }

    protected boolean currentDirectoryIsRoot()
    {
        return this.currentDirectory.equals(this.getRootDirectory());
    }

    @Override
    public File getCurrentDirectory()
    {
        return this.currentDirectory;
    }

    @Override
    public void switchToDirectory(File dir)
    {
        this.clearSelection();

        this.currentDirectory = FileUtils.getCanonicalFileIfPossible(dir);
        this.cache.setCurrentDirectoryForContext(this.browserContext, dir);

        this.refreshEntries();
    }

    @Override
    public void switchToRootDirectory()
    {
        this.switchToDirectory(this.getRootDirectory());
    }

    @Override
    public void switchToParentDirectory()
    {
        File parent = this.currentDirectory.getParentFile();

        if (parent != null)
        {
            this.switchToDirectory(parent);
        }
        else
        {
            this.switchToRootDirectory();
        }
    }

    public static class DirectoryEntry implements Comparable<DirectoryEntry>
    {
        private final DirectoryEntryType type;
        private final File dir;
        private final String name;

        public DirectoryEntry(DirectoryEntryType type, File dir, String name)
        {
            this.type = type;
            this.dir = dir;
            this.name = name;
        }

        public DirectoryEntryType getType()
        {
            return this.type;
        }

        public File getDirectory()
        {
            return this.dir;
        }

        public String getName()
        {
            return this.name;
        }

        public File getFullPath()
        {
            return new File(this.dir, this.name);
        }

        @Override
        public int compareTo(DirectoryEntry other)
        {
            return this.name.toLowerCase(Locale.US).compareTo(other.getName().toLowerCase(Locale.US));
        }
    }

    public enum DirectoryEntryType
    {
        INVALID,
        DIRECTORY,
        FILE;

        public static DirectoryEntryType fromFile(File file)
        {
            if (file.exists() == false)
            {
                return INVALID;
            }
            else if (file.isDirectory())
            {
                return DIRECTORY;
            }
            else
            {
                return FILE;
            }
        }
    }

    public static class FileFilterDirectories implements FileFilter
    {
        @Override
        public boolean accept(File pathName)
        {
            return pathName.isDirectory();
        }
    }

    public static class FileFilterSchematics implements FileFilter
    {
        @Override
        public boolean accept(File pathName)
        {
            String name = pathName.getName();
            return  name.endsWith(".litematic") ||
                    name.endsWith(".schematic") ||
                    name.endsWith(".nbt");
        }
    }
}

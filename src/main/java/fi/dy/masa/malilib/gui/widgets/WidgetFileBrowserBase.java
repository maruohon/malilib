package fi.dy.masa.malilib.gui.widgets;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryCache;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.gui.interfaces.IFileBrowserIconProvider;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.util.FileBrowserIconProviderBase;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase.DirectoryEntry;
import fi.dy.masa.malilib.util.FileUtils;

public abstract class WidgetFileBrowserBase extends WidgetListBase<DirectoryEntry, WidgetDirectoryEntry> implements IDirectoryNavigator
{
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected final IDirectoryCache cache;
    protected final String browserContext;
    protected final IFileBrowserIconProvider iconProvider = new FileBrowserIconProviderBase();
    protected WidgetDirectoryNavigation widgetNavigation;
    protected File currentDirectory;

    public WidgetFileBrowserBase(int x, int y, int width, int height,
            IDirectoryCache cache, String browserContext, File defaultDirectory,
            @Nullable ISelectionListener<DirectoryEntry> selectionListener)
    {
        super(x, y, width, height, selectionListener);

        this.cache = cache;
        this.browserContext = browserContext;
        this.currentDirectory = this.cache.getCurrentDirectoryForContext(this.browserContext);
        this.allowKeyboardNavigation = true;
        this.shouldSortList = true;

        if (this.currentDirectory == null)
        {
            this.currentDirectory = defaultDirectory;
        }

        this.setBackgroundColor(0xB0000000);
        this.setBorderColor(GuiBase.COLOR_HORIZONTAL_BAR);
        this.setBackgroundEnabled(true);

        this.setSize(width, height);
        this.updateDirectoryNavigationWidget();
    }

    @Override
    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        if (super.onKeyTyped(typedChar, keyCode))
        {
            return true;
        }

        if ((keyCode == Keyboard.KEY_BACK || keyCode == Keyboard.KEY_LEFT) && this.currentDirectoryIsRoot() == false)
        {
            this.switchToParentDirectory();
            return true;
        }
        else if ((keyCode == Keyboard.KEY_RIGHT || keyCode == Keyboard.KEY_RETURN) &&
                  this.getLastSelectedEntry() != null && this.getLastSelectedEntry().getType() == DirectoryEntryType.DIRECTORY)
        {
            this.switchToDirectory(new File(this.getLastSelectedEntry().getDirectory(), this.getLastSelectedEntry().getName()));
            return true;
        }

        return false;
    }

    @Override
    protected int getBackgroundWidth()
    {
        return this.browserWidth;
    }

    @Override
    protected int getBackgroundHeight()
    {
        return this.browserHeight;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        super.render(mouseX, mouseY, isActiveGui, hovered);

        this.drawAdditionalContents(mouseX, mouseY);
    }

    public IFileBrowserIconProvider getIconProvider()
    {
        return this.iconProvider;
    }

    protected int getBrowserWidthForTotalWidth(int width)
    {
        return width;
    }

    protected void drawAdditionalContents(int mouseX, int mouseY)
    {
    }

    @Override
    public void setSize(int width, int height)
    {
        super.setSize(width, height);

        this.browserWidth = this.getBrowserWidthForTotalWidth(width);
        this.browserEntryWidth = this.browserWidth - 14;

        if (this.widgetNavigation != null)
        {
            this.widgetNavigation.setWidth(this.browserEntryWidth);
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.updateDirectoryNavigationWidget();
    }

    @Override
    public void refreshEntries()
    {
        this.refreshBrowserEntries();
    }

    @Override
    protected void refreshBrowserEntries()
    {
        this.listContents.clear();

        File dir = this.currentDirectory;

        if (dir.isDirectory() && dir.canRead())
        {
            if (this.hasFilter())
            {
                this.addFilteredContents(dir);
            }
            else
            {
                this.addNonFilteredContents(dir);
            }
        }

        this.reCreateListEntryWidgets();
    }

    @Override
    protected Comparator<DirectoryEntry> getComparator()
    {
        return (e1, e2) -> e1.compareTo(e2);
    }

    protected void updateDirectoryNavigationWidget()
    {
        // Remove the old widget, if any, from the sub widgets list
        if (this.widgetNavigation != null)
        {
            this.removeWidget(this.widgetNavigation);
        }

        this.widgetNavigation = new WidgetDirectoryNavigation(this.getX() + 2, this.getY() + 4, this.browserEntryWidth, 14,
                this.currentDirectory, this.getRootDirectory(), this, this.getIconProvider());
        this.addSearchBarWidget(this.widgetNavigation);

        this.updateScrollbarPosition();
    }

    protected void addNonFilteredContents(File dir)
    {
        List<DirectoryEntry> list = new ArrayList<>();

        // Show directories at the top
        this.addMatchingEntriesToList(this.getDirectoryFilter(), dir, list, null, null);
        Collections.sort(list);
        this.listContents.addAll(list);
        list.clear();

        this.addMatchingEntriesToList(this.getFileFilter(), dir, list, null, null);
        this.sortEntryList(list);
        this.listContents.addAll(list);
    }

    protected void addFilteredContents(File dir)
    {
        String filterText = this.getSearchBarWidget().getFilter().toLowerCase();
        List<DirectoryEntry> list = new ArrayList<>();
        this.addFilteredContents(dir, filterText, list, null);
        this.listContents.addAll(list);
    }

    protected void addFilteredContents(File dir, String filterText, List<DirectoryEntry> listOut, @Nullable String prefix)
    {
        List<DirectoryEntry> list = new ArrayList<>();
        this.addMatchingEntriesToList(this.getDirectoryFilter(), dir, list, filterText, prefix);
        Collections.sort(list);
        listOut.addAll(list);
        list.clear();

        for (File subDir : FileUtils.getSubDirectories(dir))
        {
            String pre;

            if (prefix != null)
            {
                pre = prefix + subDir.getName() + "/";
            }
            else
            {
                pre = subDir.getName() + "/";
            }

            this.addFilteredContents(subDir, filterText, list, pre);
            this.sortEntryList(list);
            listOut.addAll(list);
            list.clear();
        }

        this.addMatchingEntriesToList(this.getFileFilter(), dir, list, filterText, prefix);
        this.sortEntryList(list);
        listOut.addAll(list);
    }

    protected void addMatchingEntriesToList(FileFilter filter, File dir, List<DirectoryEntry> list, @Nullable String filterText, @Nullable String displayNamePrefix)
    {
        for (File file : dir.listFiles(filter))
        {
            DirectoryEntry entry = new DirectoryEntry(DirectoryEntryType.fromFile(file), dir, file.getName(), displayNamePrefix);

            if (filterText == null || this.matchesFilter(this.getEntryStringsForFilter(entry), filterText))
            {
                list.add(entry);
            }
        }
    }

    @Override
    protected List<String> getEntryStringsForFilter(DirectoryEntry entry)
    {
        return ImmutableList.of(FileUtils.getNameWithoutExtension(entry.getName().toLowerCase()));
    }

    protected abstract File getRootDirectory();

    protected FileFilter getDirectoryFilter()
    {
        return FileUtils.DIRECTORY_FILTER;
    }

    protected abstract FileFilter getFileFilter();

    @Override
    protected WidgetDirectoryEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, DirectoryEntry entry)
    {
        return new WidgetDirectoryEntry(x, y, this.browserEntryWidth, this.getBrowserEntryHeightFor(entry),
                isOdd, entry, listIndex, this, this.getIconProvider());
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
        this.updateDirectoryNavigationWidget();
        this.resetScrollbarPosition();
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

        if (this.currentDirectoryIsRoot() == false && parent != null &&
            this.currentDirectory.getAbsolutePath().contains(this.getRootDirectory().getAbsolutePath()))
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
        @Nullable private final String displaynamePrefix;

        public DirectoryEntry(DirectoryEntryType type, File dir, String name, @Nullable String displaynamePrefix)
        {
            this.type = type;
            this.dir = dir;
            this.name = name;
            this.displaynamePrefix = displaynamePrefix;
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

        @Nullable
        public String getDisplayNamePrefix()
        {
            return this.displaynamePrefix;
        }

        public String getDisplayName()
        {
            return this.displaynamePrefix != null ? this.displaynamePrefix + this.name : this.name;
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
}

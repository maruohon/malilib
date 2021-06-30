package fi.dy.masa.malilib.gui.widget.list;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.TextInputScreen;
import fi.dy.masa.malilib.gui.icon.DefaultFileBrowserIconProvider;
import fi.dy.masa.malilib.gui.icon.FileBrowserIconProvider;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.DirectoryNavigationWidget;
import fi.dy.masa.malilib.gui.widget.MenuEntryWidget;
import fi.dy.masa.malilib.gui.widget.MenuWidget;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget.DirectoryEntry;
import fi.dy.masa.malilib.gui.widget.list.entry.DirectoryEntryWidget;
import fi.dy.masa.malilib.gui.widget.util.DirectoryCache;
import fi.dy.masa.malilib.gui.widget.util.DirectoryNavigator;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.DirectoryCreator;
import fi.dy.masa.malilib.util.FileUtils;

public class BaseFileBrowserWidget extends DataListWidget<DirectoryEntry> implements DirectoryNavigator
{
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final FileFilter ALWAYS_FALSE_FILE_FILTER = (file) -> false;
    public static final FileFilter ALWAYS_TRUE_FILE_FILTER = File::isFile;

    protected final Map<Pair<File, FileFilter>, List<File>> directoryContentsCache = new HashMap<>();
    protected final Map<File, Integer> keyboardNavigationPositions = new HashMap<>();
    protected final DirectoryNavigationWidget navigationWidget;
    protected final File rootDirectory;
    @Nullable protected final DirectoryCache cache;
    @Nullable protected String rootDirectoryDisplayName;
    protected FileFilter directoryFilter = FileUtils.DIRECTORY_FILTER;
    protected FileFilter fileFilter = ALWAYS_FALSE_FILE_FILTER;
    protected String browserContext;
    protected File currentDirectory;
    protected boolean shouldStoreKeyboardNavigationPosition = true;
    protected boolean showFileSize;
    protected boolean showFileModificationTime;

    public BaseFileBrowserWidget(int x, int y, int width, int height, File defaultDirectory, File rootDirectory,
                                 @Nullable DirectoryCache cache, @Nullable String browserContext)
    {
        this(x, y, width, height, defaultDirectory, rootDirectory,
             new DefaultFileBrowserIconProvider(), cache, browserContext);
    }

    public BaseFileBrowserWidget(int x, int y, int width, int height, File defaultDirectory, File rootDirectory,
                                 FileBrowserIconProvider iconProvider,
                                 @Nullable DirectoryCache cache, @Nullable String browserContext)
    {
        super(x, y, width, height, Collections::emptyList);

        this.rootDirectory = rootDirectory;
        this.cache = cache;
        this.browserContext = browserContext != null ? browserContext : "";
        this.currentDirectory = cache != null ? cache.getCurrentDirectoryForContext(this.browserContext) : null;
        this.allowKeyboardNavigation = true;
        this.shouldSortList = true;
        this.entryWidgetFixedHeight = 14;

        if (this.currentDirectory == null)
        {
            this.currentDirectory = defaultDirectory;
        }

        this.navigationWidget = new DirectoryNavigationWidget(this.getX() + 2, this.getY() + 3, width, 14,
                                                              this.currentDirectory, rootDirectory, this, iconProvider,
                                                              this::onSearchBarChange,
                                                              this::refreshFilteredEntries,
                                                              this::getRootDirectoryDisplayName);
        this.searchBarWidget = this.navigationWidget;
        this.listSortComparator = Comparator.naturalOrder();
        this.entryFilterStringFactory = (entry) -> ImmutableList.of(FileUtils.getNameWithoutExtension(entry.getName().toLowerCase(Locale.ROOT)));

        this.setEntryWidgetFactory((wx, wy, ww, wh, li, oi, entry, lw) ->
                                    new DirectoryEntryWidget(wx, wy, ww, wh, li, oi, entry, this, iconProvider));

        this.setAllowSelection(true);
        this.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, 0xB0000000);
        this.getBorderRenderer().getNormalSettings().setBorderWidthAndColor(1, 0xFF999999);
        this.listPosition.setRight(3);
        this.listPosition.setBottom(1);
    }

    @Override
    protected void updateSearchBarPosition(int defaultX, int defaultY, int defaultWidth)
    {
        this.searchBarWidget.setPosition(defaultX, defaultY + 3);
        this.searchBarWidget.setWidth(defaultWidth);
    }

    public void setFileFilter(FileFilter filter)
    {
        this.fileFilter = filter;
    }

    public void setDirectoryFilter(FileFilter directoryFilter)
    {
        this.directoryFilter = directoryFilter;
    }

    public void setRootDirectoryDisplayName(@Nullable String rootDirectoryDisplayName)
    {
        this.rootDirectoryDisplayName = rootDirectoryDisplayName;
    }

    public boolean getShowFileSize()
    {
        return this.showFileSize;
    }

    public boolean getShowFileModificationTime()
    {
        return this.showFileModificationTime;
    }

    public void setShowFileSize(boolean showFileSize)
    {
        this.showFileSize = showFileSize;
        this.reInitializeWidgets();
    }

    public void setShowFileModificationTime(boolean showFileModificationTime)
    {
        this.showFileModificationTime = showFileModificationTime;
        this.reInitializeWidgets();
    }

    public void toggleShowFileSize()
    {
        this.setShowFileSize(! this.showFileSize);
    }

    public void toggleShowModificationTime()
    {
        this.setShowFileModificationTime(! this.showFileModificationTime);
    }

    @Nullable
    protected String getRootDirectoryDisplayName()
    {
        return this.rootDirectoryDisplayName;
    }

    protected void updateDirectoryNavigationWidget()
    {
        this.navigationWidget.setCurrentDirectory(this.currentDirectory);
    }

    @Override
    public List<DirectoryEntry> getFilteredEntries()
    {
        return this.filteredContents;
    }

    @Override
    protected void reAddFilteredEntries()
    {
        this.filteredContents.clear();

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
    }

    protected void addNonFilteredContents(File dir)
    {
        List<DirectoryEntry> list = new ArrayList<>();

        // Show directories at the top
        this.addMatchingEntriesToList(dir, list, this.getDirectoryFilter(), null, null);
        list.sort(Comparator.comparing(e -> e.name.toLowerCase(Locale.ROOT)));
        this.filteredContents.addAll(list);
        list.clear();

        this.addMatchingEntriesToList(dir, list, this.getFileFilter(), null, null);
        this.sortEntryList(list);
        this.filteredContents.addAll(list);
    }

    protected void addFilteredContents(File dir)
    {
        String filterText = this.getSearchBarWidget().getFilter().toLowerCase();
        List<DirectoryEntry> list = new ArrayList<>();
        this.addFilteredContents(dir, filterText, list, null);
        this.filteredContents.addAll(list);
    }

    protected void addFilteredContents(File dir, String filterText, List<DirectoryEntry> listOut, @Nullable String prefix)
    {
        List<DirectoryEntry> list = new ArrayList<>();
        this.addMatchingEntriesToList(dir, list, this.getDirectoryFilter(), filterText, prefix);
        list.sort(Comparator.comparing(e -> e.name.toLowerCase(Locale.ROOT)));
        listOut.addAll(list);
        list.clear();

        for (File subDir : this.getContents(dir, FileUtils.DIRECTORY_FILTER))
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

        this.addMatchingEntriesToList(dir, list, this.getFileFilter(), filterText, prefix);
        this.sortEntryList(list);
        listOut.addAll(list);
    }

    protected void addMatchingEntriesToList(final File dir, List<DirectoryEntry> outputList, final FileFilter filter,
                                            @Nullable String filterText, @Nullable String displayNamePrefix)
    {
        for (File file : this.getContents(dir, filter))
        {
            String fileName = file.getName();
            String searchTerm = FileUtils.getNameWithoutExtension(fileName.toLowerCase(Locale.ROOT));

            if (filterText == null || this.searchTermsMatchFilter(searchTerm, filterText))
            {
                DirectoryEntryType type = DirectoryEntryType.fromFile(file);
                outputList.add(new DirectoryEntry(type, dir, fileName, displayNamePrefix));
            }
        }
    }

    protected List<File> getContents(final File dir, final FileFilter filter)
    {
        Pair<File, FileFilter> cacheKey = Pair.of(dir, filter);
        return this.directoryContentsCache.computeIfAbsent(cacheKey, (k) -> Arrays.asList(dir.listFiles(filter)));
    }

    protected File getRootDirectory()
    {
        return this.rootDirectory;
    }

    protected FileFilter getDirectoryFilter()
    {
        return this.directoryFilter;
    }

    protected FileFilter getFileFilter()
    {
        return this.fileFilter;
    }

    @Override
    protected int getHeightForListEntryWidgetCreation(int listIndex)
    {
        if (listIndex >= 0 && listIndex < this.filteredContents.size())
        {
            DirectoryEntry entry = this.filteredContents.get(listIndex);
            return entry.getType() == DirectoryEntryType.DIRECTORY ? 14 : this.entryWidgetFixedHeight;
        }

        return this.entryWidgetFixedHeight;
    }

    protected boolean currentDirectoryIsRoot()
    {
        return this.currentDirectory.equals(this.getRootDirectory());
    }

    protected void storeKeyboardNavigationPosition(File dir)
    {
        int index = this.getKeyboardNavigationIndex();

        if (index != -1 && this.shouldStoreKeyboardNavigationPosition)
        {
            this.keyboardNavigationPositions.put(dir, index);
        }
    }

    protected void restoreKeyboardNavigationPosition(File dir)
    {
        if (this.shouldStoreKeyboardNavigationPosition &&
            this.keyboardNavigationPositions.containsKey(dir))
        {
            int index = this.keyboardNavigationPositions.computeIfAbsent(dir, (d) -> 0);
            this.setKeyboardNavigationIndex(index);
        }
        else if (this.getKeyboardNavigationIndex() > 0)
        {
            this.setKeyboardNavigationIndex(0);
        }
    }

    protected void openSettingsContextMenu(int mouseX, int mouseY)
    {
        MenuWidget menuWidget = new MenuWidget(mouseX + 4, mouseY, 10, 10);
        menuWidget.setMenuCloseHook(() -> this.removeWidget(menuWidget));

        String sizeKey = this.showFileSize ? "malilib.label.hide_file_size" : "malilib.label.show_file_size";
        String mTimeKey = this.showFileModificationTime ? "malilib.label.hide_file_mtime" : "malilib.label.show_file_mtime";
        StyledTextLine textShowSize = StyledTextLine.translate(sizeKey);
        StyledTextLine textShowDate = StyledTextLine.translate(mTimeKey);
        menuWidget.setMenuEntries(new MenuEntryWidget(textShowSize, this::toggleShowFileSize),
                                  new MenuEntryWidget(textShowDate, this::toggleShowModificationTime));

        this.addWidget(menuWidget);
        menuWidget.updateSubWidgetsToGeometryChanges();
        // Changing/raising the z-level needs to happen after adding the widget to the container
        menuWidget.setZ(this.getZ() + 40);
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
        this.storeKeyboardNavigationPosition(this.currentDirectory);

        this.currentDirectory = FileUtils.getCanonicalFileIfPossible(dir);

        if (this.cache != null)
        {
            this.cache.setCurrentDirectoryForContext(this.browserContext, dir);
        }

        this.refreshEntries();
        this.updateDirectoryNavigationWidget();
        this.resetScrollBarPosition();
        // The index needs to be restored after the entries have been refreshed
        this.restoreKeyboardNavigationPosition(this.currentDirectory);
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

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (super.onMouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        if (mouseButton == 1 && this.isMouseOverListArea(mouseX, mouseY) == false)
        {
            this.openSettingsContextMenu(mouseX, mouseY);
            return true;
        }

        return false;
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (super.onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        if ((keyCode == Keyboard.KEY_BACK || keyCode == Keyboard.KEY_LEFT) && this.currentDirectoryIsRoot() == false)
        {
            this.switchToParentDirectory();
            return true;
        }
        else if (keyCode == Keyboard.KEY_RIGHT || keyCode == Keyboard.KEY_RETURN)
        {
            DirectoryEntry entry = this.getKeyboardNavigationEntry();

            if (entry != null && entry.getType() == DirectoryEntryType.DIRECTORY)
            {
                this.switchToDirectory(new File(entry.getDirectory(), entry.getName()));
                return true;
            }
        }
        else if (keyCode == Keyboard.KEY_N && BaseScreen.isCtrlDown() && BaseScreen.isShiftDown())
        {
            DirectoryCreator creator = new DirectoryCreator(this.getCurrentDirectory(), this);
            TextInputScreen gui = new TextInputScreen("malilib.gui.title.create_directory", "", GuiUtils.getCurrentScreen(), creator);
            BaseScreen.openPopupScreen(gui);
            return true;
        }

        return false;
    }

    protected void drawAdditionalContents(int x, int y, float z, ScreenContext ctx)
    {
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        this.drawAdditionalContents(x, y, z, ctx);
    }

    public static class DirectoryEntry implements Comparable<DirectoryEntry>
    {
        protected final DirectoryEntryType type;
        protected final File dir;
        protected final String name;
        @Nullable protected final String displayNamePrefix;

        public DirectoryEntry(DirectoryEntryType type, File dir, String name, @Nullable String displayNamePrefix)
        {
            this.type = type;
            this.dir = dir;
            this.name = name;
            this.displayNamePrefix = displayNamePrefix;
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
            return this.displayNamePrefix;
        }

        public String getDisplayName()
        {
            return this.displayNamePrefix != null ? this.displayNamePrefix + this.name : this.name;
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

            if (file.isDirectory())
            {
                return DIRECTORY;
            }
            else if (file.isFile())
            {
                return FILE;
            }

            return INVALID;
        }
    }
}

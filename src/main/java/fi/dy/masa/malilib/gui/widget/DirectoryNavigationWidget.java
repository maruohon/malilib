package fi.dy.masa.malilib.gui.widget;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.OpenGlHelper;
import fi.dy.masa.malilib.config.value.HorizontalAlignment;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.TextInputScreen;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.FileBrowserIconProvider;
import fi.dy.masa.malilib.gui.icon.FileBrowserIconProvider.FileBrowserIconType;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.util.DirectoryNavigator;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.util.DirectoryCreator;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class DirectoryNavigationWidget extends SearchBarWidget
{
    protected final DirectoryNavigator navigator;
    protected final FileBrowserIconProvider iconProvider;
    protected final GenericButton buttonRoot;
    protected final GenericButton buttonUp;
    protected final GenericButton buttonCreateDir;
    protected final InfoIconWidget infoWidget;
    protected final File rootDir;
    protected final List<PathPart> pathParts = new ArrayList<>();
    @Nullable protected final Supplier<String> rootDirDisplayNameSupplier;
    protected File currentDir;
    protected int pathStartX;

    public DirectoryNavigationWidget(int width, int height,
                                     File currentDir, File rootDir, DirectoryNavigator navigator,
                                     FileBrowserIconProvider iconProvider,
                                     Consumer<String> textChangeListener,
                                     @Nullable EventListener openCloseListener)
    {
        this(width, height, currentDir, rootDir, navigator, iconProvider,
             textChangeListener, openCloseListener, null);
    }

    public DirectoryNavigationWidget(int width, int height,
                                     File currentDir, File rootDir, DirectoryNavigator navigator,
                                     FileBrowserIconProvider iconProvider,
                                     Consumer<String> textChangeListener,
                                     @Nullable EventListener openCloseListener,
                                     @Nullable Supplier<String> rootDirDisplayNameSupplier)
    {
        super(width, height, textChangeListener, openCloseListener, iconProvider.getIcon(FileBrowserIconType.SEARCH));

        this.currentDir = currentDir;
        this.rootDir = rootDir;
        this.navigator = navigator;
        this.iconProvider = iconProvider;
        this.rootDirDisplayNameSupplier = rootDirDisplayNameSupplier;
        this.setToggleButtonAlignment(HorizontalAlignment.RIGHT);

        this.buttonRoot = GenericButton.create(iconProvider.getIcon(FileBrowserIconType.ROOT));
        this.buttonRoot.translateAndAddHoverString("malilib.hover.button.directory_navigation_widget.root");
        this.buttonRoot.setPlayClickSound(false);
        this.buttonRoot.setActionListener(() -> { if (this.isSearchOpen == false) this.navigator.switchToRootDirectory(); });

        this.buttonUp = GenericButton.create(iconProvider.getIcon(FileBrowserIconType.UP));
        this.buttonUp.translateAndAddHoverString("malilib.hover.button.directory_navigation_widget.up");
        this.buttonUp.setPlayClickSound(false);
        this.buttonUp.setActionListener(() -> { if (this.isSearchOpen == false) this.navigator.switchToParentDirectory(); });

        this.buttonCreateDir = GenericButton.create(iconProvider.getIcon(FileBrowserIconType.CREATE_DIR));
        this.buttonCreateDir.translateAndAddHoverString("malilib.hover.button.directory_navigation_widget.create_directory");
        this.buttonCreateDir.setPlayClickSound(false);
        this.buttonCreateDir.setActionListener(() -> {
            if (this.isSearchOpen == false)
            {
                DirectoryCreator creator = new DirectoryCreator(this.getCurrentDirectory(), this.navigator);
                TextInputScreen gui = new TextInputScreen("malilib.title.screen.create_directory", "",
                                                          creator, GuiUtils.getCurrentScreen());
                BaseScreen.openPopupScreen(gui);
            }
        });

        String hoverKey = "malilib.hover.button.directory_navigation_widget.hold_shift_to_open_directory";
        this.infoWidget = new InfoIconWidget(DefaultIcons.INFO_ICON_11, hoverKey);

        this.generatePathParts(currentDir);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        if (this.isSearchOpen() == false)
        {
            this.addWidget(this.buttonRoot);
            this.addWidget(this.buttonUp);
            this.addWidget(this.buttonCreateDir);
            this.addWidget(this.infoWidget);

            this.placePathParts(this.pathStartX, this.getY());
        }
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getX() + 2;
        int y = this.getY();
        int height = this.getHeight();
        int middleY = y + height / 2;

        int by = middleY - this.buttonRoot.getHeight() / 2;
        this.buttonRoot.setPosition(x, by);

        x += this.buttonRoot.getWidth() + 2;
        this.buttonUp.setPosition(x, by);

        x += this.buttonUp.getWidth() + 2;
        this.buttonCreateDir.setPosition(x, by);

        int xRight = this.getRight();
        int iw = this.infoWidget.getWidth();
        int ih = this.infoWidget.getHeight();
        int tw = this.searchToggleButton.getWidth();

        this.searchToggleButton.setPosition(xRight - tw - iw - 4, by);
        this.infoWidget.setPosition(xRight - iw - 2, middleY - ih / 2 - 1);

        this.textField.setWidth(this.getWidth() - tw - iw - 8);
        this.pathStartX = this.buttonCreateDir.getRight() + 2;

        this.reAddSubWidgets();
    }

    public File getCurrentDirectory()
    {
        return this.currentDir;
    }

    public void setCurrentDirectory(File dir)
    {
        this.currentDir = dir;
        this.generatePathParts(dir);
        this.reAddSubWidgets();
    }

    public MultiIcon getNavBarIconRoot(boolean isOpen)
    {
        return this.iconProvider.getIcon(isOpen ? FileBrowserIconType.NAVBAR_ROOT_PATH_OPEN : FileBrowserIconType.NAVBAR_ROOT_PATH_CLOSED);
    }

    public MultiIcon getNavBarIconSubDirectories(boolean isOpen)
    {
        return this.iconProvider.getIcon(isOpen ? FileBrowserIconType.NAVBAR_SUBDIRS_OPEN : FileBrowserIconType.NAVBAR_SUBDIRS_CLOSED);
    }

    protected int getMaxPathBarWidth()
    {
        return this.getWidth() - (this.pathStartX - this.getX()) - 28;
    }

    protected String getDisplayNameForDirectory(File dir)
    {
        String name;

        if (this.rootDirDisplayNameSupplier != null && this.rootDir.equals(dir))
        {
            name = this.rootDirDisplayNameSupplier.get();
        }
        else
        {
            name = dir.getName();
        }

        // The partition root on Windows returns an empty string... ('C:\' -> '')
        if (name == null || name.length() == 0)
        {
            name = dir.toString();
        }

        return name;
    }

    protected int getFirstFittingPathPartIndex()
    {
        final int size = this.pathParts.size();
        int maxWidth = this.getMaxPathBarWidth();
        int rootElementWidth = 11;

        for (int i = 0; i < size; ++i)
        {
            if (this.pathParts.get(i).widthSumUntil < maxWidth)
            {
                return i;
            }

            // If the entire path did not fit, then the root element will get added,
            // so account for the width of that element.
            if (i == 0)
            {
                maxWidth -= rootElementWidth;
            }
        }

        return -1;
    }

    protected void placePathParts(int x, int y)
    {
        final int size = this.pathParts.size();
        int buttonHeight = 14;
        int rootElementWidth = 11;
        int bx = x + 2;
        int by = y + this.getHeight() / 2 - buttonHeight / 2;
        int index = this.getFirstFittingPathPartIndex();
        boolean addRootPathElements = index > 0;

        // No parts fit entirely
        if (index < 0 || index >= this.pathParts.size())
        {
            // Even the current directory would not fit, show a shrunk version of it
            if (this.pathParts.isEmpty() == false)
            {
                PathPart part = this.pathParts.get(this.pathParts.size() - 1);
                final int maxWidth = this.getMaxPathBarWidth();
                this.addShrunkPathPartElement(bx, by, buttonHeight, maxWidth, part);
            }

            return;
        }

        for (; index < size; ++index)
        {
            PathPart part = this.pathParts.get(index);

            if (addRootPathElements)
            {
                this.addRootPathElements(part, bx, by);
                bx += rootElementWidth;
            }

            addRootPathElements = false;
            this.addPathPartElements(bx, by, buttonHeight, part);
            bx += part.totalWidth;
        }
    }

    protected void generatePathParts(File currentDirectory)
    {
        this.pathParts.clear();

        final File root = this.rootDir;
        final int extraWidth = this.getNavBarIconSubDirectories(false).getWidth() + 6;
        File dir = currentDirectory;
        int widthSum = 0;

        while (dir != null)
        {
            String name = this.getDisplayNameForDirectory(dir);
            int nameWidth = this.getStringWidth(name);
            int totalWidth = nameWidth + extraWidth;
            widthSum += totalWidth;

            this.pathParts.add(new PathPart(dir, name, nameWidth, totalWidth, widthSum));

            if (root.equals(dir))
            {
                break;
            }

            dir = dir.getParentFile();
        }

        Collections.reverse(this.pathParts);
    }

    protected void addRootPathElements(PathPart part, int x, int y)
    {
        List<File> dirs = FileUtils.getDirsForRootPath(part.dir, this.rootDir);
        final DropDownListWidget<File> dropdown = new DropDownListWidget<>(-1, 12, 120, 10, dirs, this::getDisplayNameForDirectory);
        dropdown.setPosition(x, y + 16);
        dropdown.setNoBarWhenClosed(x, y + 3, () -> this.getNavBarIconRoot(dropdown.isOpen()));
        dropdown.setSelectionListener(this.navigator::switchToDirectory);
        this.addWidget(dropdown);
    }

    protected void addPathPartElements(int x, int y, int buttonHeight, PathPart part)
    {
        this.addPathPartElements(x, y, buttonHeight, part.dir, part.displayName, part.nameWidth, part.totalWidth);
    }

    protected void addPathPartElements(int x, int y, int buttonHeight, File dir, String displayName, int nameWidth, int totalWidth)
    {
        GenericButton button = this.createPathPartButton(dir, displayName, nameWidth + 6, buttonHeight);
        button.setPosition(x, y);

        this.addWidget(button);

        List<File> dirs = FileUtils.getSubDirectories(dir);

        if (dirs.isEmpty() == false)
        {
            DropDownListWidget<File> dropdown = new DropDownListWidget<>(-1, 12, 120, 10, dirs, this::getDisplayNameForDirectory);
            dropdown.setY(y + 16);
            dropdown.setRight(x + totalWidth);
            dropdown.setNoBarWhenClosed(x + totalWidth - 9, y + 3, () -> this.getNavBarIconSubDirectories(dropdown.isOpen()));
            dropdown.setSelectionListener(this.navigator::switchToDirectory);
            this.addWidget(dropdown);
        }
    }

    protected void addShrunkPathPartElement(int x, int y, int buttonHeight, int maxWidth, PathPart part)
    {
        final int adjDirsIconWidth = this.getNavBarIconSubDirectories(false).getWidth() + 4;
        String name = StringUtils.clampTextToRenderLength(part.displayName, maxWidth - adjDirsIconWidth, LeftRight.RIGHT, "...");
        int nameWidth = StringUtils.getStringWidth(name);
        this.addPathPartElements(x, y, buttonHeight, part.dir, name, nameWidth, nameWidth + adjDirsIconWidth + 4);
    }

    protected GenericButton createPathPartButton(final File dir, String displayName, int width, int height)
    {
        GenericButton button = GenericButton.create(width, height, displayName);

        button.setPlayClickSound(false);
        button.setRenderButtonBackgroundTexture(false);
        button.getBorderRenderer().getHoverSettings().setEnabled(true);
        button.getTextSettings().setTextShadowEnabled(false);
        button.setActionListener(() -> this.onDirectoryButtonClicked(dir));
        button.getPadding().setLeftRight(2);
        button.updateButtonState();

        return button;
    }

    protected void onDirectoryButtonClicked(File dir)
    {
        if (BaseScreen.isShiftDown())
        {
            OpenGlHelper.openFile(dir);
        }
        else
        {
            this.navigator.switchToDirectory(dir);
        }
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        if (this.isSearchOpen == false)
        {
            int rx = this.pathStartX;
            int width = this.getMaxPathBarWidth();
            ShapeRenderUtils.renderRectangle(rx, y, z, width, this.getHeight(), 0xFF202020);
        }

        super.renderAt(x, y, z, ctx);
    }

    public static class PathPart
    {
        public final File dir;
        public final String displayName;
        public final int nameWidth;
        public final int totalWidth;
        public final int widthSumUntil;

        public PathPart(File dir, String displayName, int nameWidth, int totalWidth, int widthSumUntil)
        {
            this.dir = dir;
            this.displayName = displayName;
            this.nameWidth = nameWidth;
            this.totalWidth = totalWidth;
            this.widthSumUntil = widthSumUntil;
        }
    }
}

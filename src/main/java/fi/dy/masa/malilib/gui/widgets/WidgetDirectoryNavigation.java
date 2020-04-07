package fi.dy.masa.malilib.gui.widgets;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.OpenGlHelper;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTextInputFeedback;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.gui.interfaces.IFileBrowserIconProvider;
import fi.dy.masa.malilib.gui.interfaces.IFileBrowserIconProvider.FileBrowserIconType;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.util.GuiIconBase;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.interfaces.IStringRetriever;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.DirectoryCreator;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.HorizontalAlignment;
import fi.dy.masa.malilib.util.LeftRight;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetDirectoryNavigation extends WidgetSearchBar
{
    protected final IDirectoryNavigator navigator;
    protected final IFileBrowserIconProvider iconProvider;
    protected final ButtonGeneric buttonRoot;
    protected final ButtonGeneric buttonUp;
    protected final ButtonGeneric buttonCreateDir;
    protected final WidgetInfoIcon infoWidget;
    protected final File rootDir;
    protected final int pathStartX;
    @Nullable protected final String rootDirDisplayName;
    protected File currentDir;

    public WidgetDirectoryNavigation(int x, int y, int width, int height,
            File currentDir, File rootDir, IDirectoryNavigator navigator, IFileBrowserIconProvider iconProvider)
    {
        this(x, y, width, height, currentDir, rootDir, navigator, iconProvider, null);
    }

    public WidgetDirectoryNavigation(int x, int y, int width, int height,
            File currentDir, File rootDir, IDirectoryNavigator navigator, IFileBrowserIconProvider iconProvider, @Nullable String rootDirDisplayName)
    {
        super(x, y, width, height, 0, iconProvider.getIcon(FileBrowserIconType.SEARCH), HorizontalAlignment.RIGHT);

        this.currentDir = currentDir;
        this.rootDir = rootDir;
        this.navigator = navigator;
        this.iconProvider = iconProvider;
        this.rootDirDisplayName = rootDirDisplayName;

        this.buttonRoot = ButtonGeneric.createIconOnly(x, y, iconProvider.getIcon(FileBrowserIconType.ROOT));
        this.buttonRoot.addHoverString("malilib.gui.button.hover.directory_widget.root");
        this.buttonRoot.setActionListener((btn, mbtn) -> { if (this.searchOpen == false) this.navigator.switchToRootDirectory(); });
        x += this.buttonRoot.getWidth() + 2;

        this.buttonUp = ButtonGeneric.createIconOnly(x, y, iconProvider.getIcon(FileBrowserIconType.UP));
        this.buttonUp.addHoverString("malilib.gui.button.hover.directory_widget.up");
        this.buttonUp.setActionListener((btn, mbtn) -> { if (this.searchOpen == false) this.navigator.switchToParentDirectory(); });
        x += this.buttonUp.getWidth() + 2;

        this.buttonCreateDir = ButtonGeneric.createIconOnly(x, y, iconProvider.getIcon(FileBrowserIconType.CREATE_DIR));
        this.buttonCreateDir.addHoverString("malilib.gui.button.hover.directory_widget.create_directory");
        this.buttonCreateDir.setActionListener((btn, mbtn) -> {
            if (this.searchOpen == false)
            {
                DirectoryCreator creator = new DirectoryCreator(this.getCurrentDirectory(), this.navigator);
                GuiTextInputFeedback gui = new GuiTextInputFeedback("malilib.gui.title.create_directory", "", GuiUtils.getCurrentScreen(), creator);
                GuiBase.openPopupGui(gui);
            }
        });
        this.pathStartX = this.buttonCreateDir.getX() + this.buttonCreateDir.getWidth() + 6;

        // Make room for the info widget
        x = this.buttonSearchToggle.getX();
        this.buttonSearchToggle.setX(x - 14);
        this.searchBox.setWidth(this.searchBox.getWidth() - 14);

        this.infoWidget = new WidgetInfoIcon(x + 2, y + 1, GuiIconBase.INFO_ICON_11, "malilib.gui.button.hover.directory_widget.hold_shift_to_open_directory");

        this.updateSubWidgets();
    }

    public File getCurrentDirectory()
    {
        return this.currentDir;
    }

    public IGuiIcon getNavBarIconRoot(boolean isOpen)
    {
        return this.iconProvider.getIcon(isOpen ? FileBrowserIconType.NAVBAR_ROOT_PATH_OPEN : FileBrowserIconType.NAVBAR_ROOT_PATH_CLOSED);
    }

    public IGuiIcon getNavBarIconSubdirs(boolean isOpen)
    {
        return this.iconProvider.getIcon(isOpen ? FileBrowserIconType.NAVBAR_SUBDIRS_OPEN : FileBrowserIconType.NAVBAR_SUBDIRS_CLOSED);
    }

    @Override
    protected void updateSubWidgets()
    {
        if (this.isSearchOpen() == false)
        {
            this.clearWidgets();

            this.addWidget(this.infoWidget);
            this.addWidget(this.buttonSearchToggle);
            this.addWidget(this.buttonRoot);
            this.addWidget(this.buttonUp);
            this.addWidget(this.buttonCreateDir);

            this.placePathElements(this.pathStartX, this.getY(), this.generatePathElements());
        }
        else
        {
            super.updateSubWidgets();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        if (this.searchOpen)
        {
            this.searchBox.render(mouseX, mouseY, isActiveGui, hoveredWidgetId);
        }
        else
        {
            // Draw the directory path text background
            RenderUtils.drawRect(this.pathStartX - 2, this.getY(), this.getWidth() - this.pathStartX - 12, this.getHeight(), 0xFF242424, this.getZLevel());
        }

        super.render(mouseX, mouseY, isActiveGui, hoveredWidgetId);
    }

    protected void placePathElements(int x, int y, List<PathElement> elements)
    {
        IStringRetriever<File> displayNameFactory;

        if (this.rootDirDisplayName != null)
        {
            final File rootDir = this.rootDir;
            final String overriddenName = this.rootDirDisplayName;
            displayNameFactory = (file) -> { return file.equals(rootDir) ? overriddenName : file.getName(); };
        }
        else
        {
            displayNameFactory = (file) -> file.getName();
        }

        for (final PathElement el : elements)
        {
            if (el.type == PathElement.Type.DIR)
            {
                ButtonGeneric button = new ButtonGeneric(x, y, el.nameWidth + 4, 14, el.displayName);
                button.setRenderDefaultBackground(false);
                button.setRenderOutline(true);
                button.setPlayClickSound(false);
                button.setUseTextShadow(false);
                button.setTextColorHovered(0xFFFFFF);

                this.addButton(button, (btn, mbtn) -> {
                    if (GuiBase.isShiftDown())
                    {
                        OpenGlHelper.openFile(el.dir);
                    }
                    else
                    {
                        this.navigator.switchToDirectory(el.dir);
                    }
                });

                List<File> dirs = FileUtils.getSubDirectories(el.dir);

                if (dirs.isEmpty() == false)
                {
                    final WidgetDirectorySelection dropdown = new WidgetDirectorySelection(x + el.totalWidth, y + 16, dirs, displayNameFactory);
                    dropdown.setNoBarWhenClosed(x + el.totalWidth - 12, y + 2, () -> {
                        return this.getNavBarIconSubdirs(dropdown.isOpen());
                    });
                    dropdown.setSelectionListener((dir) -> this.navigator.switchToDirectory(dir));
                    dropdown.setRightAlign(true, x + el.totalWidth);
                    this.addWidget(dropdown);
                }
            }
            else
            {
                List<File> dirs = FileUtils.getDirsForRootPath(el.dir, this.rootDir);
                final WidgetDirectorySelection dropdown = new WidgetDirectorySelection(x, y + 16, dirs, displayNameFactory);
                dropdown.setNoBarWhenClosed(x, y + 2, () -> {
                    return this.getNavBarIconRoot(dropdown.isOpen());
                });
                dropdown.setSelectionListener((dir) -> this.navigator.switchToDirectory(dir));
                this.addWidget(dropdown);
            }

            x += el.totalWidth;
        }
    }

    public static class WidgetDirectorySelection extends WidgetDropDownList<File>
    {
        public WidgetDirectorySelection(int x, int y, List<File> entries, IStringRetriever<File> displayNameFactory)
        {
            super(x, y, -1, 12, 120, 10, entries, displayNameFactory);
        }
    }

    protected List<PathElement> generatePathElements()
    {
        ArrayList<PathElement> list = new ArrayList<>();
        int maxWidth = this.getWidth() - 75;
        File root = this.rootDir;
        File dir = this.currentDir;
        int usedWidth = 0;
        int adjDirsIconWidth = this.getNavBarIconSubdirs(false).getWidth();

        while (dir != null)
        {
            String name = this.rootDirDisplayName != null && root.equals(dir) ? this.rootDirDisplayName : dir.getName();
            int nameWidth = this.getStringWidth(name);
            int entryWidth = nameWidth + adjDirsIconWidth + 8;

            // The current path element doesn't fit.
            // This thus also means that all the elements didn't fit, and they will be cut from the beginning.
            // This also means that the root paths dropdown widget should be shown.
            if (usedWidth + entryWidth > maxWidth)
            {
                // This is the current directory, show as much of it as possible
                if (usedWidth == 0)
                {
                    name = StringUtils.clampTextToRenderLength(name, maxWidth - adjDirsIconWidth, LeftRight.RIGHT, "...");
                    list.add(new PathElement(dir, PathElement.Type.DIR, name, nameWidth, entryWidth));

                    if (root.equals(dir) == false)
                    {
                        dir = dir.getParentFile();
                    }
                }

                if (dir != null)
                {
                    int w = this.getNavBarIconRoot(false).getWidth() + 4;
                    list.add(new PathElement(dir, PathElement.Type.ROOT_PATH, "", w, w));
                }

                break;
            }

            list.add(new PathElement(dir, PathElement.Type.DIR, name, nameWidth, entryWidth));

            usedWidth += entryWidth;

            if (root.equals(dir))
            {
                break;
            }

            dir = dir.getParentFile();
        }

        Collections.reverse(list);

        return list;
    }

    public static class PathElement
    {
        public final File dir;
        public final Type type;
        public final String displayName;
        public final int nameWidth;
        public final int totalWidth;

        public PathElement(File dir, Type type, String displayName, int nameWidth, int totalWidth)
        {
            this.dir = dir;
            this.type = type;
            this.displayName = displayName;
            this.nameWidth = nameWidth;
            this.totalWidth = totalWidth;
        }

        public enum Type
        {
            ROOT_PATH,
            DIR
        }
    }
}

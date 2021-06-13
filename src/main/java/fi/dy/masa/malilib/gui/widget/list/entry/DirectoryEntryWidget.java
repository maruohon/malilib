package fi.dy.masa.malilib.gui.widget.list.entry;

import java.io.File;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.icon.FileBrowserIconProvider;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget.DirectoryEntry;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget.DirectoryEntryType;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.util.DirectoryNavigator;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.FileUtils;

public class DirectoryEntryWidget extends BaseDataListEntryWidget<DirectoryEntry>
{
    protected final DirectoryNavigator navigator;
    protected final DirectoryEntry entry;

    public DirectoryEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                DirectoryEntry entry, DataListWidget<DirectoryEntry> listWidget,
                                DirectoryNavigator navigator, @Nullable FileBrowserIconProvider iconProvider)
    {
        super(x, y, width, height, listIndex, originalListIndex, entry, listWidget);

        this.entry = entry;
        this.navigator = navigator;
        this.getTextSettings().setTextShadowEnabled(false);

        this.setText(StyledTextLine.raw(this.getDisplayName()));
        this.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, this.isOdd ? 0xFF202020 : 0xFF303030);
        this.getBackgroundRenderer().getHoverSettings().setColor(0xFF404040);

        int textXOffset = 2;
        @Nullable MultiIcon icon = iconProvider != null ? iconProvider.getIconForEntry(entry) : null;

        if (icon != null)
        {
            textXOffset += iconProvider.getEntryIconWidth(entry) + 2;
            this.iconOffset.setXOffset(2);
            this.setIcon(icon);
        }

        this.textOffset.setXOffset(textXOffset);
    }

    public DirectoryEntry getDirectoryEntry()
    {
        return this.entry;
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.entry.getType() == DirectoryEntryType.DIRECTORY)
        {
            this.navigator.switchToDirectory(new File(this.entry.getDirectory(), this.entry.getName()));
        }
        else
        {
            return super.onMouseClicked(mouseX, mouseY, mouseButton);
        }

        return true;
    }

    protected String getDisplayName()
    {
        if (this.entry.getType() == DirectoryEntryType.DIRECTORY)
        {
            return this.entry.getDisplayName();
        }
        else
        {
            return FileUtils.getNameWithoutExtension(this.entry.getDisplayName());
        }
    }
}

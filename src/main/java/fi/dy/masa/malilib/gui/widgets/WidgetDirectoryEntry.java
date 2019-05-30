package fi.dy.masa.malilib.gui.widgets;

import java.io.File;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.gui.interfaces.IFileBrowserIconProvider;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase.DirectoryEntry;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase.DirectoryEntryType;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.FileUtils;
import net.minecraft.client.renderer.GlStateManager;

public class WidgetDirectoryEntry extends WidgetListEntryBase<DirectoryEntry>
{
    protected final IDirectoryNavigator navigator;
    protected final DirectoryEntry entry;
    protected final IFileBrowserIconProvider iconProvider;
    protected final boolean isOdd;

    public WidgetDirectoryEntry(int x, int y, int width, int height, boolean isOdd, DirectoryEntry entry,
            int listIndex, IDirectoryNavigator navigator, IFileBrowserIconProvider iconProvider)
    {
        super(x, y, width, height, entry, listIndex);

        this.isOdd = isOdd;
        this.entry = entry;
        this.navigator = navigator;
        this.iconProvider = iconProvider;
    }

    public DirectoryEntry getDirectoryEntry()
    {
        return this.entry;
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.entry.getType() == DirectoryEntryType.DIRECTORY)
        {
            this.navigator.switchToDirectory(new File(this.entry.getDirectory(), this.entry.getName()));
        }
        else
        {
            return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
        }

        return true;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        IGuiIcon icon = null;

        switch (this.entry.getType())
        {
            case DIRECTORY:
                icon = this.iconProvider.getIconDirectory();
                break;

            default:
                icon = this.iconProvider.getIconForFile(this.entry.getFullPath());
        }

        int iconWidth = icon != null ? icon.getWidth() : 0;
        int xOffset = iconWidth + 2;

        if (icon != null)
        {
            GlStateManager.color4f(1f, 1f, 1f, 1f);
            this.bindTexture(icon.getTexture());
            icon.renderAt(this.x, this.y + (this.height - icon.getHeight()) / 2, this.zLevel + 1, false, false);
        }

        // Draw a lighter background for the hovered and the selected entry
        if (selected || this.isMouseOver(mouseX, mouseY))
        {
            GuiBase.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x70FFFFFF);
        }
        else if (this.isOdd)
        {
            GuiBase.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x20FFFFFF);
        }
        // Draw a slightly lighter background for even entries
        else
        {
            GuiBase.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x38FFFFFF);
        }

        // Draw an outline if this is the currently selected entry
        if (selected)
        {
            RenderUtils.drawOutline(this.x, this.y, this.width, this.height, 0xEEEEEEEE);
        }

        int yOffset = (this.height - this.textRenderer.FONT_HEIGHT) / 2 + 1;
        this.drawString(this.getDisplayName(), this.x + xOffset + 2, this.y + yOffset, 0xFFFFFFFF);

        super.render(mouseX, mouseY, selected);
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

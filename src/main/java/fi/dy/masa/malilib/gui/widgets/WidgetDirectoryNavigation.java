package fi.dy.masa.malilib.gui.widgets;

import java.io.File;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.gui.interfaces.IFileBrowserIconProvider;
import fi.dy.masa.malilib.util.FileUtils;
import net.minecraft.client.Minecraft;

public class WidgetDirectoryNavigation extends WidgetSearchBar
{
    protected final File currentDir;
    protected final File rootDir;
    protected final Minecraft mc;
    protected final IDirectoryNavigator navigator;
    protected final WidgetIcon iconRoot;
    protected final WidgetIcon iconUp;

    public WidgetDirectoryNavigation(int x, int y, int width, int height, float zLevel,
            File currentDir, File rootDir, Minecraft mc, IDirectoryNavigator navigator, IFileBrowserIconProvider iconProvider)
    {
        super(x, y, width, height, zLevel, - iconProvider.getIconRoot().getWidth(), iconProvider.getIconSearch(), LeftRight.RIGHT, mc);

        this.currentDir = currentDir;
        this.rootDir = rootDir;
        this.mc = mc;
        this.navigator = navigator;
        this.iconRoot = new WidgetIcon(x, y + 1, zLevel, iconProvider.getIconRoot(), mc);
        this.iconUp = new WidgetIcon(x + iconProvider.getIconRoot().getWidth() + 2, y + 1, zLevel, iconProvider.getIconUp(), mc);
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.searchOpen == false)
        {
            WidgetIcon hoveredIcon = this.getHoveredIcon(mouseX, mouseY);

            if (hoveredIcon == this.iconRoot)
            {
                this.navigator.switchToRootDirectory();
                return true;
            }
            else if (hoveredIcon == this.iconUp)
            {
                this.navigator.switchToParentDirectory();
                return true;
            }
        }

        return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }

    @Nullable
    protected WidgetIcon getHoveredIcon(int mouseX, int mouseY)
    {
        if (this.searchOpen == false)
        {
            if (this.iconRoot.isMouseOver(mouseX, mouseY))
            {
                return this.iconRoot;
            }
            else if (this.iconUp.isMouseOver(mouseX, mouseY))
            {
                return this.iconUp;
            }
        }

        return null;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        super.render(mouseX, mouseY, selected);

        if (this.searchOpen == false)
        {
            WidgetIcon hoveredIcon = this.getHoveredIcon(mouseX, mouseY);

            this.iconRoot.render(false, hoveredIcon == this.iconRoot);
            this.iconUp.render(false, hoveredIcon == this.iconUp);

            final int widthUp = this.iconUp.getWidth();

            // Draw the directory path text background
            GuiBase.drawRect(this.iconUp.x + widthUp + 6, this.y, this.x + this.width, this.y + this.height, 0x20FFFFFF);

            int textColor = 0xC0C0C0C0;
            int maxLen = (this.width - 40) / this.mc.fontRenderer.getStringWidth("a") - 4; // FIXME
            String path = FileUtils.getJoinedTrailingPathElements(this.currentDir, this.rootDir, maxLen, " / ");
            this.mc.fontRenderer.drawString(path, this.iconUp.x + widthUp + 9, this.y + 3, textColor);
        }
    }
}

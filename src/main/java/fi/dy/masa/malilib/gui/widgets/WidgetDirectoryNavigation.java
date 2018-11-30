package fi.dy.masa.malilib.gui.widgets;

import java.io.File;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.interfaces.IDirectoryNavigator;
import fi.dy.masa.malilib.gui.interfaces.IFileBrowserIconProvider;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.FileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class WidgetDirectoryNavigation extends WidgetBase
{
    protected final File currentDir;
    protected final File rootDir;
    protected final Minecraft mc;
    protected final IDirectoryNavigator navigator;
    protected final IGuiIcon iconRoot;
    protected final IGuiIcon iconUp;

    public WidgetDirectoryNavigation(int x, int y, int width, int height, float zLevel,
            File currentDir, File rootDir, Minecraft mc, IDirectoryNavigator navigator, IFileBrowserIconProvider iconProvider)
    {
        super(x, y, width, height, zLevel);

        this.currentDir = currentDir;
        this.rootDir = rootDir;
        this.mc = mc;
        this.navigator = navigator;
        this.iconRoot = iconProvider.getIconRoot();
        this.iconUp = iconProvider.getIconUp();
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isHoveringIcon(mouseX, mouseY, 0))
        {
            this.navigator.switchToRootDirectory();
            return true;
        }
        else if (this.isHoveringIcon(mouseX, mouseY, 1))
        {
            this.navigator.switchToParentDirectory();
            return true;
        }

        return false;
    }

    protected boolean isHoveringIcon(int mouseX, int mouseY, int iconIndex)
    {
        final int iw = iconIndex == 0 ? this.iconRoot.getWidth() : this.iconUp.getWidth();
        return mouseY >= this.y + 1 && mouseY < this.y + this.height &&
            mouseX >= this.x + iconIndex * (iw + 2) && mouseX < this.x + iconIndex * (iw + 2) + iw;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        final int widthRoot = this.iconRoot.getWidth();
        final int widthUp = this.iconUp.getWidth();

        // Hovering the "to root directory" widget/icon
        if (this.isHoveringIcon(mouseX, mouseY, 0))
        {
            RenderUtils.drawOutlinedBox(this.x                , this.y + 1, widthRoot, widthRoot, 0x20C0C0C0, 0xE0FFFFFF);
        }
        else if (this.isHoveringIcon(mouseX, mouseY, 1))
        {
            RenderUtils.drawOutlinedBox(this.x + widthRoot + 2, this.y + 1, widthUp, widthUp, 0x20C0C0C0, 0xE0FFFFFF);
        }

        GlStateManager.color4f(1f, 1f, 1f, 1f);

        this.mc.getTextureManager().bindTexture(this.iconRoot.getTexture());
        this.iconRoot.renderAt(this.x                , this.y + 1, this.zLevel, false, false);

        this.mc.getTextureManager().bindTexture(this.iconUp.getTexture());
        this.iconUp  .renderAt(this.x + widthRoot + 2, this.y + 1, this.zLevel, false, false);

        // Draw the directory path text background
        GuiBase.drawRect(this.x + widthRoot + widthUp + 6, this.y, this.x + this.width, this.y + this.height, 0x20FFFFFF);

        int textColor = 0xC0C0C0C0;
        int maxLen = (this.width - 40) / this.mc.fontRenderer.getStringWidth("a") - 4; // FIXME
        String path = FileUtils.getJoinedTrailingPathElements(this.currentDir, this.rootDir, maxLen, " / ");
        this.mc.fontRenderer.drawString(path, this.x + widthRoot * 2 + 9, this.y + 3, textColor);
    }
}

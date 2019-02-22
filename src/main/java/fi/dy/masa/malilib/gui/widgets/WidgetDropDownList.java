package fi.dy.masa.malilib.gui.widgets;

import java.util.List;
import javax.annotation.Nullable;
import com.mumfrey.liteloader.client.gui.GuiSimpleScrollBar;
import fi.dy.masa.malilib.gui.MaLiLibIcons;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class WidgetDropDownList<T> extends WidgetBase
{
    protected final GuiSimpleScrollBar scrollBar = new GuiSimpleScrollBar();
    protected final List<T> entries;
    protected final int maxHeight;
    protected final int maxVisibleEntries;
    protected final int totalHeight;
    protected boolean isOpen;
    protected int selectedIndex;
    protected int scrollbarWidth = 10;

    public WidgetDropDownList(int x, int y, int width, int height, int maxHeight,
            int maxVisibleEntries, float zLevel, List<T> entries)
    {
        super(x, y, width, height, zLevel);

        this.maxHeight = maxHeight;
        this.entries = entries;

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int v = Math.min(maxVisibleEntries, entries.size());
        v = Math.min(v, maxHeight / height);
        v = Math.min(v, (sr.getScaledHeight() - y) / height);
        v = Math.max(v, 1);

        this.maxVisibleEntries = v;
        this.totalHeight = (v + 1) * height;
        this.scrollBar.setMaxValue(entries.size() - this.maxVisibleEntries);
    }

    public int getSelectedIndex()
    {
        return this.selectedIndex;
    }

    @Nullable
    public T getSelectedEntry()
    {
        if (this.selectedIndex >= 0 && this.selectedIndex < this.entries.size())
        {
            return this.entries.get(this.selectedIndex);
        }

        return null;
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY)
    {
        int maxY = this.isOpen ? this.y + this.totalHeight : this.y + this.height;
        return mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < maxY;
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isOpen && mouseY > this.y + this.height)
        {
            if (mouseX < this.x + this.width - this.scrollbarWidth)
            {
                int relIndex = (mouseY - this.y - this.height) / this.height;
                this.selectedIndex = this.scrollBar.getValue() + relIndex;
            }
            else
            {
                if (this.scrollBar.wasMouseOver() == false)
                {
                    int relY = mouseY - this.y - this.height;
                    int ddHeight = this.height * this.maxVisibleEntries;
                    int newPos = (int) (((double) relY / (double) ddHeight) * this.scrollBar.getMaxValue());

                    this.scrollBar.setValue(newPos);
                    this.scrollBar.handleDrag(mouseY, 123);
                }

                this.scrollBar.setDragging(true);
            }
        }

        if (this.isOpen == false || (mouseX < this.x + this.width - this.scrollbarWidth || mouseY < this.y + this.height))
        {
            this.isOpen = ! this.isOpen;
        }

        return true;
    }

    @Override
    public void onMouseReleasedImpl(int mouseX, int mouseY, int mouseButton)
    {
        this.scrollBar.setDragging(false);
    }

    @Override
    public boolean onMouseScrolledImpl(int mouseX, int mouseY, int mouseWheelDelta)
    {
        if (this.isOpen)
        {
            int amount = mouseWheelDelta < 0 ? 1 : -1;
            this.scrollBar.offsetValue(amount);
        }

        return false;
    }

    protected String getDisplayString(T entry)
    {
        return entry != null ? entry.toString() : "-";
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        GlStateManager.color(1f, 1f, 1f, 1f);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 1);
        int visibleEntries = Math.min(this.maxVisibleEntries, this.entries.size());

        RenderUtils.drawOutlinedBox(this.x + 1, this.y, this.width - 2, this.height - 1, 0xFF101010, 0xFFC0C0C0);

        String str = this.getDisplayString(this.getSelectedEntry());
        int fh = this.mc.fontRenderer.FONT_HEIGHT;
        int txtX = this.x + 4;
        int txtY = this.y + this.height / 2 - fh / 2;
        this.mc.fontRenderer.drawString(str, txtX, txtY, 0xFFE0E0E0);
        txtY += this.height + 1;
        int scrollWidth = 10;

        if (this.isOpen)
        {
            RenderUtils.drawOutline(this.x, this.y + this.height, this.width, visibleEntries * this.height + 2, 0xFFE0E0E0);

            int y = this.y + this.height + 1;
            int startIndex = Math.max(0, this.scrollBar.getValue());
            int max = Math.min(startIndex + this.maxVisibleEntries, this.entries.size());

            for (int i = startIndex; i < max; ++i)
            {
                int bg = (i & 0x1) != 0 ? 0x20FFFFFF : 0x30FFFFFF;
                RenderUtils.drawRect(this.x, y, this.width - scrollWidth, this.height, bg);
                str = this.getDisplayString(this.entries.get(i));
                this.mc.fontRenderer.drawString(str, txtX, txtY, 0xFFE0E0E0);
                y += this.height;
                txtY += this.height;
            }

            int x = this.x + this.width - this.scrollbarWidth - 1;
            y = this.y + this.height + 1;
            int h = visibleEntries * this.height;
            int totalHeight = Math.max(h, this.entries.size() * this.height);

            this.scrollBar.drawScrollBar(mouseX, mouseY, 0, x, y, this.scrollbarWidth, h, totalHeight);
        }
        else
        {
            this.mc.getTextureManager().bindTexture(MaLiLibIcons.TEXTURE);
            MaLiLibIcons i = MaLiLibIcons.ARROW_DOWN;
            RenderUtils.drawTexturedRect(this.x + this.width - 16, this.y + 2, i.getU() + i.getWidth(), i.getV(), i.getWidth(), i.getHeight());
        }

        GlStateManager.popMatrix();
    }
}

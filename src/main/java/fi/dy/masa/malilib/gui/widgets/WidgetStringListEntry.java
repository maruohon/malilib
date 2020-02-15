package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.render.RenderUtils;

public class WidgetStringListEntry extends WidgetListEntryBase<String>
{
    private final boolean isOdd;

    public WidgetStringListEntry(int x, int y, int width, int height, boolean isOdd, String entry, int listIndex)
    {
        super(x, y, width, height, entry, listIndex);

        this.isOdd = isOdd;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId, boolean selected)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        int x = this.getX();
        int y = this.getY();
        int z = this.getZLevel();
        int width = this.getWidth();
        int height = this.getHeight();

        // Draw a lighter background for the hovered entry
        if (selected || (isActiveGui && this.getId() == hoveredWidgetId))
        {
            RenderUtils.drawRect(x, y, width, height, 0xA0707070, z);
        }
        else if (this.isOdd)
        {
            RenderUtils.drawRect(x, y, width, height, 0xA0101010, z);
        }
        // Draw a slightly lighter background for even entries
        else
        {
            RenderUtils.drawRect(x, y, width, height, 0xA0303030, z);
        }

        this.drawStringWithShadow(x + 2, y + this.getCenteredTextOffsetY(), 0xFFFFFFFF, this.entry);

        super.render(mouseX, mouseY, isActiveGui, hoveredWidgetId, selected);
    }
}

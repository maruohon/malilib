package fi.dy.masa.malilib.gui.widget.list.entry;

import fi.dy.masa.malilib.render.RenderUtils;

public class StringListEntryWidget extends BaseDataListEntryWidget<String>
{
    public StringListEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex, String entry)
    {
        super(x, y, width, height, listIndex, originalListIndex, entry);
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId, boolean selected)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        int x = this.getX();
        int y = this.getY();
        float z = this.getZLevel();
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

        this.drawStringWithShadow(x + 2, y + this.getCenteredTextOffsetY(), 0xFFFFFFFF, this.data);

        super.render(mouseX, mouseY, isActiveGui, hoveredWidgetId, selected);
    }
}

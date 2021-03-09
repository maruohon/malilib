package fi.dy.masa.malilib.gui.widget.list.entry;

import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public class StringListEntryWidget extends BaseDataListEntryWidget<String>
{
    protected final StyledTextLine string;

    public StringListEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                 String entry, DataListWidget<String> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, entry, listWidget);

        this.string = StyledTextLine.raw(entry);
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId, boolean selected)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        int width = this.getWidth();
        int height = this.getHeight();

        // Draw a lighter background for the hovered entry
        if (selected || (isActiveGui && this.getId() == hoveredWidgetId))
        {
            RenderUtils.renderRectangle(x, y, width, height, 0xA0707070, z);
        }
        else if (this.isOdd)
        {
            RenderUtils.renderRectangle(x, y, width, height, 0xA0101010, z);
        }
        // Draw a slightly lighter background for even entries
        else
        {
            RenderUtils.renderRectangle(x, y, width, height, 0xA0303030, z);
        }

        this.renderTextLine(x + 2, y + this.getCenteredTextOffsetY(), z, 0xFFFFFFFF, true, this.string);

        super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hoveredWidgetId, selected);
    }
}

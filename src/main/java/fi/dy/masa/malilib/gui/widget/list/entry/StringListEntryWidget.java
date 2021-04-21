package fi.dy.masa.malilib.gui.widget.list.entry;

import fi.dy.masa.malilib.gui.widget.ScreenContext;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
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
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        int width = this.getWidth();
        int height = this.getHeight();

        // Draw a lighter background for the hovered entry
        if (this.isSelected() || (ctx.isActiveScreen && this.getId() == ctx.hoveredWidgetId))
        {
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, 0xA0707070);
        }
        else if (this.isOdd)
        {
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, 0xA0101010);
        }
        // Draw a slightly lighter background for even entries
        else
        {
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, 0xA0303030);
        }

        this.renderTextLine(x + 2, y + this.getCenteredTextOffsetY(), z, 0xFFFFFFFF, true, ctx, this.string);

        super.renderAt(x, y, z, ctx);
    }
}

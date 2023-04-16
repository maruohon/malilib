package malilib.gui.widget.list.header;

import java.util.List;
import java.util.Optional;

import malilib.config.value.HorizontalAlignment;
import malilib.config.value.SortDirection;
import malilib.gui.icon.Icon;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.list.DataListWidget;
import malilib.render.ShapeRenderUtils;
import malilib.render.text.StyledTextLine;

public class ColumnizedDataListHeaderWidget<DATATYPE> extends DataListHeaderWidget<DATATYPE>
{
    protected final List<DataColumn<DATATYPE>> columns;

    public ColumnizedDataListHeaderWidget(int width, int height,
                                          DataListWidget<DATATYPE> listWidget,
                                          List<DataColumn<DATATYPE>> columns)
    {
        super(width, height, listWidget);

        this.canReceiveMouseClicks = true;
        this.columns = columns;
        this.getBorderRenderer().getNormalSettings().setColor(0x50C0C0C0);
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            int x = this.getX();
            int y = this.getY();
            int height = this.getHeight();
            boolean hoveredY = mouseY >= y && mouseY < y + height;

            if (hoveredY)
            {
                for (DataColumn<DATATYPE> column : this.columns)
                {
                    if (this.isMouseOverColumnOnX(column, x, mouseX))
                    {
                        this.listWidget.toggleSortByColumn(column);
                        return true;
                    }
                }
            }
        }

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        int height = this.getHeight();
        int usableHeight = height - this.padding.getVerticalTotal();
        int ty = this.getTextPositionY(y, usableHeight, this.getLineHeight());
        boolean hoveredY = ctx.mouseY >= y && ctx.mouseY < y + height;
        boolean shadow = this.getTextSettings().getTextShadowEnabled();

        for (DataColumn<DATATYPE> column : this.columns)
        {
            int cx = x + column.getRelativeStartX();
            int tx = cx + 4;
            boolean hovered = this.isHoveredForRender(ctx) && hoveredY && this.isMouseOverColumnOnX(column, x, ctx.mouseX);
            int textColor = this.getTextColorForRender(hovered);
            int borderColor = hovered ? this.getBorderRenderer().getHoverSettings().getColor().getTop() :
                                        this.getBorderRenderer().getNormalSettings().getColor().getTop();

            if (column.getCanSortBy())
            {
                Optional<SortDirection> directionOptional = this.listWidget.getSortDirectionForColumn(column);

                if (directionOptional.isPresent())
                {
                    Optional<Icon> iconOptional = column.getSortIcon(directionOptional.get());

                    if (iconOptional.isPresent())
                    {
                        Icon icon = iconOptional.get();
                        HorizontalAlignment align = column.getIconPosition();
                        int ix = cx + align.getStartX(icon.getWidth(), column.getWidth(), 2);
                        int iy = this.getIconPositionY(y, usableHeight, icon.getHeight());

                        if (align == HorizontalAlignment.LEFT)
                        {
                            tx += icon.getWidth() + 4;
                        }

                        icon.renderAt(ix, iy, z + 0.0125f);
                    }

                    if (hovered == false)
                    {
                        borderColor |= 0xFF000000;
                    }
                }
            }

            ShapeRenderUtils.renderOutline(cx, y, z, column.getWidth(), height, 1, borderColor);

            Optional<StyledTextLine> nameOptional = column.getName();

            if (nameOptional.isPresent())
            {
                this.renderTextLine(tx, ty, z, textColor, shadow, nameOptional.get(), ctx);
            }
        }
    }

    protected boolean isMouseOverColumnOnX(DataColumn<DATATYPE> column, int x, int mouseX)
    {
        int startX = column.getRelativeStartX();
        return mouseX >= x + startX && mouseX < x + startX + column.getWidth();
    }
}

package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.render.RenderUtils;

public abstract class SortableListEntryWidget<TYPE> extends BaseDataListEntryWidget<TYPE>
{
    protected int columnCount = 2;

    public SortableListEntryWidget(int x, int y, int width, int height, int listIndex,
                                   int originalListIndex, @Nullable TYPE entry)
    {
        super(x, y, width, height, listIndex, originalListIndex, entry);
    }

    protected abstract int getColumnPosX(int column);

    protected abstract int getCurrentSortColumn();

    protected abstract boolean getSortInReverse();

    protected int getColumnCount()
    {
        return this.columnCount;
    }

    protected int getMouseOverColumn(int mouseX, int mouseY)
    {
        int numColumns = this.getColumnCount();
        int x1 = this.getColumnPosX(0);
        int xEnd = this.getColumnPosX(numColumns);
        int y = this.getY();
        int height = this.getHeight();

        if (mouseY >= y && mouseY <= y + height && mouseX >= x1 && mouseX < xEnd)
        {
            for (int column = 1; column <= numColumns; ++column)
            {
                if (mouseX < this.getColumnPosX(column))
                {
                    return column - 1;
                }
            }
        }

        return -1;
    }

    protected void renderColumnHeader(int mouseX, int mouseY, Icon iconNatural, Icon iconReverse)
    {
        int mouseOverColumn = this.getMouseOverColumn(mouseX, mouseY);
        int sortColumn = this.getCurrentSortColumn();
        boolean reverse = this.getSortInReverse();
        int iconX = this.getColumnPosX(sortColumn + 1) - 21; // align to the right edge
        int y = this.getY();
        int height = this.getHeight();

        Icon icon = reverse ? iconReverse : iconNatural;
        icon.renderAt(iconX, y + 3, this.getZLevel(), true, sortColumn == mouseOverColumn);

        for (int i = 0; i < this.getColumnCount(); ++i)
        {
            int outlineColor = mouseOverColumn == i ? 0xFFFFFFFF : 0xC0707070;
            int xStart = this.getColumnPosX(i);
            int xEnd = this.getColumnPosX(i + 1);

            RenderUtils.drawOutline(xStart - 3, y + 1, xEnd - xStart - 2, height - 2, 1, outlineColor, this.getZLevel());
        }
    }
}

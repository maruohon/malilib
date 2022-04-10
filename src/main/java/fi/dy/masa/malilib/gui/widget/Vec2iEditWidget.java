package fi.dy.masa.malilib.gui.widget;

import java.util.function.Consumer;
import fi.dy.masa.malilib.util.position.Coordinate;
import fi.dy.masa.malilib.util.position.Vec2i;

public class Vec2iEditWidget extends BaseDualNumberEditWidget<Vec2i, IntegerEditWidget>
{
    public Vec2iEditWidget(int width, int height,
                           int gap, boolean vertical,
                           Vec2i initialValue,
                           Consumer<Vec2i> valueConsumer)
    {
        super(width, height, gap, vertical, initialValue, valueConsumer);
    }

    @Override
    public void updateWidgetState()
    {
        this.xCoordinateWidget.setIntegerValue(this.pos.getX());
        this.yCoordinateWidget.setIntegerValue(this.pos.getY());
    }

    @Override
    protected IntegerEditWidget createNumberEditWidget(int width, int height, Vec2i initialPos, Coordinate coord)
    {
        return new IntegerEditWidget(width, height, initialPos.getX(), (val) -> this.setValue(val, coord));
    }

    public void setValidRange(int minX, int minY, int maxX, int maxY)
    {
        this.xCoordinateWidget.setValidRange(minX, maxX);
        this.yCoordinateWidget.setValidRange(minY, maxY);
    }

    protected void setValue(int newVal, Coordinate coordinate)
    {
        this.setValue(coordinate.modifyVec2i(newVal, this.pos));
    }
}

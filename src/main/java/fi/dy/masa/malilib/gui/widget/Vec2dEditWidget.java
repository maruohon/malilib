package fi.dy.masa.malilib.gui.widget;

import java.util.function.Consumer;
import fi.dy.masa.malilib.util.position.Coordinate;
import fi.dy.masa.malilib.util.position.Vec2d;

public class Vec2dEditWidget extends BaseDualNumberEditWidget<Vec2d, DoubleEditWidget>
{
    public Vec2dEditWidget(int width, int height,
                           int gap, boolean vertical,
                           Vec2d initialValue,
                           Consumer<Vec2d> valueConsumer)
    {
        super(width, height, gap, vertical, initialValue, valueConsumer);
    }

    @Override
    public void updateWidgetState()
    {
        this.xCoordinateWidget.setDoubleValue(this.pos.x);
        this.yCoordinateWidget.setDoubleValue(this.pos.y);
    }

    @Override
    protected DoubleEditWidget createNumberEditWidget(int width, int height, Vec2d initialPos, Coordinate coord)
    {
        return new DoubleEditWidget(width, height,
                                    coord.asDouble(initialPos),
                                    -31000000.0, 31000000.0,
                                    (val) -> this.setValue(val, coord));
    }

    public void setValidRange(double minX, double minY, double maxX, double maxY)
    {
        this.xCoordinateWidget.setValidRange(minX, maxX);
        this.yCoordinateWidget.setValidRange(minY, maxY);
    }

    protected void setValue(double newVal, Coordinate coordinate)
    {
        this.setValue(coordinate.modifyVec2d(newVal, this.pos));
    }
}

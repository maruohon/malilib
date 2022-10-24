package malilib.gui.widget;

import java.util.function.Consumer;

import net.minecraft.util.math.Vec3i;

import malilib.util.game.wrap.EntityWrap;
import malilib.util.position.Coordinate;

public class Vec3iEditWidget extends BaseTripleNumberEditWidget<Vec3i, IntegerEditWidget>
{
    public Vec3iEditWidget(int width, int height, int gap,
                           boolean addMoveToPlayerButton,
                           Vec3i initialPos,
                           Consumer<Vec3i> posConsumer)
    {
        super(width, height, gap, addMoveToPlayerButton, initialPos, posConsumer);
    }

    @Override
    public void updateWidgetState()
    {
        Vec3i pos = this.pos;
        this.xCoordinateWidget.setIntegerValue(pos.getX());
        this.yCoordinateWidget.setIntegerValue(pos.getY());
        this.zCoordinateWidget.setIntegerValue(pos.getZ());
    }

    public void setValidRange(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
    {
        this.xCoordinateWidget.setValidRange(minX, maxX);
        this.yCoordinateWidget.setValidRange(minY, maxY);
        this.zCoordinateWidget.setValidRange(minZ, maxZ);
    }

    @Override
    protected IntegerEditWidget createNumberEditWidget(int width, int height, Vec3i initialPos, Coordinate coord)
    {
        return new IntegerEditWidget(width, height, coord.asInt(initialPos), (val) -> this.setPos(val, coord));
    }

    @Override
    protected Vec3i getPositionFromPlayer()
    {
        return EntityWrap.getCameraEntityBlockPos();
    }

    protected void setPos(int newVal, Coordinate coordinate)
    {
        this.setPos(coordinate.modifyBlockPos(newVal, this.pos));
    }
}

package malilib.gui.widget;

import java.util.function.Consumer;

import net.minecraft.world.phys.Vec3;

import malilib.util.game.wrap.EntityWrap;
import malilib.util.position.Coordinate;

public class Vec3dEditWidget extends BaseTripleNumberEditWidget<Vec3, DoubleEditWidget>
{
    public Vec3dEditWidget(int width, int height, int gap,
                           boolean addMoveToPlayerButton,
                           Vec3 initialPos,
                           Consumer<Vec3> posConsumer)
    {
        super(width, height, gap, addMoveToPlayerButton, initialPos, posConsumer);
    }

    @Override
    public void updateWidgetState()
    {
        Vec3 pos = this.pos;
        this.xCoordinateWidget.setDoubleValue(pos.x);
        this.yCoordinateWidget.setDoubleValue(pos.y);
        this.zCoordinateWidget.setDoubleValue(pos.z);
    }

    public void setValidRange(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        this.xCoordinateWidget.setValidRange(minX, maxX);
        this.yCoordinateWidget.setValidRange(minY, maxY);
        this.zCoordinateWidget.setValidRange(minZ, maxZ);
    }

    @Override
    protected DoubleEditWidget createNumberEditWidget(int width, int height, Vec3 initialPos, Coordinate coord)
    {
        return new DoubleEditWidget(width, height,
                                    coord.asDouble(initialPos),
                                    -31000000.0, 31000000.0,
                                    (val) -> this.setPos(val, coord));
    }

    @Override
    protected Vec3 getPositionFromPlayer()
    {
        return EntityWrap.getCameraEntityPosition();
    }

    protected void setPos(double newVal, Coordinate coordinate)
    {
        this.setPos(coordinate.modifyVec3d(newVal, this.pos));
    }
}

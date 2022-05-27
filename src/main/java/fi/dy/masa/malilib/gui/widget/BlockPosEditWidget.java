package fi.dy.masa.malilib.gui.widget;

import java.util.function.Consumer;
import net.minecraft.util.math.BlockPos;
import fi.dy.masa.malilib.util.game.wrap.EntityWrap;
import fi.dy.masa.malilib.util.position.Coordinate;

public class BlockPosEditWidget extends BaseTripleNumberEditWidget<BlockPos, IntegerEditWidget>
{
    public BlockPosEditWidget(int width, int height, int gap,
                              boolean addMoveToPlayerButton,
                              BlockPos initialPos,
                              Consumer<BlockPos> posConsumer)
    {
        super(width, height, gap, addMoveToPlayerButton, initialPos, posConsumer);
    }

    @Override
    public void updateWidgetState()
    {
        BlockPos pos = this.pos;
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
    protected IntegerEditWidget createNumberEditWidget(int width, int height, BlockPos initialPos, Coordinate coord)
    {
        return new IntegerEditWidget(width, height, coord.asInt(initialPos), (val) -> this.setPos(val, coord));
    }

    @Override
    protected BlockPos getPositionFromPlayer()
    {
        return EntityWrap.getCameraEntityBlockPos();
    }

    protected void setPos(int newVal, Coordinate coordinate)
    {
        this.setPos(coordinate.modifyBlockPos(newVal, this.pos));
    }
}

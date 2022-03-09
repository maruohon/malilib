package fi.dy.masa.malilib.gui.widget;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.position.Coordinate;

public class BlockPosEditWidget extends ContainerWidget
{
    protected final Consumer<BlockPos> posConsumer;
    protected final IntegerEditWidget xCoordinateWidget;
    protected final IntegerEditWidget yCoordinateWidget;
    protected final IntegerEditWidget zCoordinateWidget;
    protected final GenericButton moveToPlayerButton;
    protected Vec3i pos;
    protected boolean addMoveToPlayerButton;
    protected int gap;

    public BlockPosEditWidget(int width, int height, int gap,
                              boolean addMoveToPlayerButton,
                              Vec3i initialPos,
                              Consumer<BlockPos> posConsumer)
    {
        super(width, height);

        this.posConsumer = posConsumer;
        this.pos = initialPos;
        this.gap = gap;
        this.addMoveToPlayerButton = addMoveToPlayerButton;

        int h = addMoveToPlayerButton ? (height - gap * 3 - 18) / 3 : (height - gap * 2) / 3;
        this.moveToPlayerButton = GenericButton.create(18, "malilib.button.render_layers.set_to_player");
        this.moveToPlayerButton.setActionListener(this::moveToPlayer);

        this.xCoordinateWidget = new IntegerEditWidget(width, h, initialPos.getX(), (val) -> this.setPos(val, Coordinate.X));
        this.yCoordinateWidget = new IntegerEditWidget(width, h, initialPos.getY(), (val) -> this.setPos(val, Coordinate.Y));
        this.zCoordinateWidget = new IntegerEditWidget(width, h, initialPos.getZ(), (val) -> this.setPos(val, Coordinate.Z));

        this.xCoordinateWidget.setLabelText("malilib.label.misc.coordinate.x_colon");
        this.yCoordinateWidget.setLabelText("malilib.label.misc.coordinate.y_colon");
        this.zCoordinateWidget.setLabelText("malilib.label.misc.coordinate.z_colon");

        BooleanSupplier enabledSupplier = this::isEnabled;
        this.moveToPlayerButton.setEnabledStatusSupplier(enabledSupplier);
        this.xCoordinateWidget.setEnabledStatusSupplier(enabledSupplier);
        this.yCoordinateWidget.setEnabledStatusSupplier(enabledSupplier);
        this.zCoordinateWidget.setEnabledStatusSupplier(enabledSupplier);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.xCoordinateWidget);
        this.addWidget(this.yCoordinateWidget);
        this.addWidget(this.zCoordinateWidget);
        this.addWidgetIf(this.moveToPlayerButton, this.addMoveToPlayerButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getX();

        this.xCoordinateWidget.setPosition(x, this.getY());
        this.yCoordinateWidget.setPosition(x, this.xCoordinateWidget.getBottom() + this.gap);
        this.zCoordinateWidget.setPosition(x, this.yCoordinateWidget.getBottom() + this.gap);
        this.moveToPlayerButton.setPosition(this.zCoordinateWidget.getTextFieldWidgetX(),
                                            this.zCoordinateWidget.getBottom() + this.gap);
    }

    @Override
    public void updateWidgetState()
    {
        Vec3i pos = this.pos;
        this.xCoordinateWidget.setIntegerValue(pos.getX());
        this.yCoordinateWidget.setIntegerValue(pos.getY());
        this.zCoordinateWidget.setIntegerValue(pos.getZ());
    }

    public int getTextFieldStartX()
    {
        return this.xCoordinateWidget.getTextFieldWidgetX();
    }

    public void setAddMoveToPlayerButton(boolean addMoveToPlayerButton)
    {
        this.addMoveToPlayerButton = addMoveToPlayerButton;
    }

    public void setValidRange(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
    {
        this.xCoordinateWidget.setValidRange(minX, maxX);
        this.yCoordinateWidget.setValidRange(minY, maxY);
        this.zCoordinateWidget.setValidRange(minZ, maxZ);
    }

    public void setLabels(String xTranslationKey, String yTranslationKey, String zTranslationKey)
    {
        this.xCoordinateWidget.setLabelText(xTranslationKey);
        this.yCoordinateWidget.setLabelText(yTranslationKey);
        this.zCoordinateWidget.setLabelText(zTranslationKey);
    }

    protected void moveToPlayer()
    {
        this.setPos(EntityUtils.getCameraEntityBlockPos());
        this.updateWidgetState();
    }

    protected void setPos(int newVal, Coordinate coordinate)
    {
        this.setPos(coordinate.modifyBlockPos(newVal, this.pos));
    }

    protected void setPos(BlockPos pos)
    {
        this.pos = pos;
        this.posConsumer.accept(pos);
    }

    public void setPosAndUpdate(Vec3i pos)
    {
        this.setPos(new BlockPos(pos));
        this.updateWidgetState();
    }
}

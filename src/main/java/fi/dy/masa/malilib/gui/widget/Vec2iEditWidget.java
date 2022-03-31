package fi.dy.masa.malilib.gui.widget;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import fi.dy.masa.malilib.util.position.Coordinate;
import fi.dy.masa.malilib.util.position.Vec2i;

public class Vec2iEditWidget extends ContainerWidget
{
    protected final Consumer<Vec2i> posConsumer;
    protected final IntegerEditWidget xCoordinateWidget;
    protected final IntegerEditWidget yCoordinateWidget;
    protected final boolean vertical;
    protected final int gap;
    protected Vec2i pos;

    public Vec2iEditWidget(int width, int height,
                           int gap, boolean vertical,
                           Vec2i initialValue,
                           Consumer<Vec2i> valueConsumer)
    {
        super(width, height);

        this.posConsumer = valueConsumer;
        this.pos = initialValue;
        this.gap = gap;
        this.vertical = vertical;

        int w = vertical ? width : (width - gap) / 2;
        int h = vertical ? (height - gap) / 2 : height;
        this.xCoordinateWidget = new IntegerEditWidget(w, h, initialValue.getX(), (val) -> this.setValue(val, Coordinate.X));
        this.yCoordinateWidget = new IntegerEditWidget(w, h, initialValue.getY(), (val) -> this.setValue(val, Coordinate.Y));

        this.xCoordinateWidget.setLabelText("malilib.label.misc.coordinate.x_colon");
        this.yCoordinateWidget.setLabelText("malilib.label.misc.coordinate.y_colon");

        BooleanSupplier enabledSupplier = this::isEnabled;
        this.xCoordinateWidget.setEnabledStatusSupplier(enabledSupplier);
        this.yCoordinateWidget.setEnabledStatusSupplier(enabledSupplier);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.xCoordinateWidget);
        this.addWidget(this.yCoordinateWidget);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getX();
        int y = this.getY();

        int width = this.getWidth();
        int height = this.getHeight();
        int w = this.vertical ? width : (width - this.gap) / 2;
        int h = this.vertical ? (height - this.gap) / 2 : height;
        this.xCoordinateWidget.setSize(w, h);
        this.yCoordinateWidget.setSize(w, h);

        this.xCoordinateWidget.setPosition(x, y);

        if (this.vertical)
        {
            this.yCoordinateWidget.setPosition(x, this.xCoordinateWidget.getBottom() + this.gap);
        }
        else
        {
            this.yCoordinateWidget.setPosition(this.xCoordinateWidget.getRight() + this.gap, y);
        }
    }

    @Override
    public void updateWidgetState()
    {
        this.xCoordinateWidget.setIntegerValue(this.pos.getX());
        this.yCoordinateWidget.setIntegerValue(this.pos.getY());
    }

    public void setValidRange(int minX, int minY, int maxX, int maxY)
    {
        this.xCoordinateWidget.setValidRange(minX, maxX);
        this.yCoordinateWidget.setValidRange(minY, maxY);
    }

    public void setLabels(String xTranslationKey, String yTranslationKey)
    {
        this.xCoordinateWidget.setLabelText(xTranslationKey);
        this.yCoordinateWidget.setLabelText(yTranslationKey);
    }

    public void setHoverTexts(String xTranslationKey, String yTranslationKey)
    {
        this.xCoordinateWidget.getTextField().translateAndAddHoverString(xTranslationKey);
        this.yCoordinateWidget.getTextField().translateAndAddHoverString(yTranslationKey);
    }

    protected void setValue(int newVal, Coordinate coordinate)
    {
        this.setValue(coordinate.modifyVec2i(newVal, this.pos));
    }

    protected void setValue(Vec2i pos)
    {
        this.pos = pos;
        this.posConsumer.accept(pos);
    }

    public void setValueAndUpdate(Vec2i pos)
    {
        this.setValue(pos);
        this.updateWidgetState();
    }
}

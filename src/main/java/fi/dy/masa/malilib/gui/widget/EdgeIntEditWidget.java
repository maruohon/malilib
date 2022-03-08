package fi.dy.masa.malilib.gui.widget;

import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.data.EdgeInt;

public class EdgeIntEditWidget extends ContainerWidget
{
    @Nullable protected final StyledTextLine centerText;
    protected final EdgeInt value;
    protected final InteractableWidget topEdgeEditorWidget;
    protected final InteractableWidget rightEdgeEditorWidget;
    protected final InteractableWidget bottomEdgeEditorWidget;
    protected final InteractableWidget leftEdgeEditorWidget;
    protected final int widgetWidth;
    protected final int widgetHeight;

    public EdgeIntEditWidget(int width, int height, EdgeInt value, boolean isColor, @Nullable String centerText)
    {
        super(width, height);

        this.value = value;
        this.centerText = centerText != null ? StyledTextLine.translate(centerText) : null;
        this.widgetWidth = 80;
        this.widgetHeight = 16;

        int ww = this.widgetWidth;
        int wh = this.widgetHeight;

        if (isColor)
        {
            this.topEdgeEditorWidget    = new ColorEditorWidget(ww, wh, value::getTop,    value::setTop);
            this.rightEdgeEditorWidget  = new ColorEditorWidget(ww, wh, value::getRight,  value::setRight);
            this.bottomEdgeEditorWidget = new ColorEditorWidget(ww, wh, value::getBottom, value::setBottom);
            this.leftEdgeEditorWidget   = new ColorEditorWidget(ww, wh, value::getLeft,   value::setLeft);
        }
        else
        {
            int min = 0;
            int max = 4096;
            this.topEdgeEditorWidget    = new IntegerEditWidget(ww, wh, value.getTop(),    min, max, value::setTop);
            this.rightEdgeEditorWidget  = new IntegerEditWidget(ww, wh, value.getRight(),  min, max, value::setRight);
            this.bottomEdgeEditorWidget = new IntegerEditWidget(ww, wh, value.getBottom(), min, max, value::setBottom);
            this.leftEdgeEditorWidget   = new IntegerEditWidget(ww, wh, value.getLeft(),   min, max, value::setLeft);
        }

        BooleanSupplier enabledSupplier = this::isEnabled;
        this.bottomEdgeEditorWidget.setEnabledStatusSupplier(enabledSupplier);
        this.leftEdgeEditorWidget.setEnabledStatusSupplier(enabledSupplier);
        this.rightEdgeEditorWidget.setEnabledStatusSupplier(enabledSupplier);
        this.topEdgeEditorWidget.setEnabledStatusSupplier(enabledSupplier);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.topEdgeEditorWidget);
        this.addWidget(this.rightEdgeEditorWidget);
        this.addWidget(this.bottomEdgeEditorWidget);
        this.addWidget(this.leftEdgeEditorWidget);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getX();
        int y = this.getY();
        int width = this.getWidth();
        int height = this.getHeight();
        int ww = this.widgetWidth;
        int wh = this.widgetHeight;

        this.topEdgeEditorWidget.setPosition(x + width / 2 - ww / 2, y);
        this.bottomEdgeEditorWidget.setPosition(x + width / 2 - ww / 2, y + height - wh);
        this.leftEdgeEditorWidget.setPosition(x, y + height / 2 - wh / 2);
        this.rightEdgeEditorWidget.setPosition(x + width - ww, y + height / 2 - wh / 2);
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        int width = this.getWidth();
        int height = this.getHeight();
        int w = width - this.widgetWidth * 2 - 10;
        int h = height - this.widgetHeight * 2 - 6;

        ShapeRenderUtils.renderOutline(x + this.widgetWidth + 5, y + this.widgetHeight + 3, z, w, h, 1, 0xFFFFFFFF);

        if (this.centerText != null)
        {
            int tx = x + width / 2 - this.centerText.renderWidth / 2;
            int ty = y + height / 2 - this.getFontHeight() / 2;
            this.renderTextLine(tx, ty, z, 0xFFFFFFFF, true, this.centerText, ctx);
        }
    }
}

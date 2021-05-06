package fi.dy.masa.malilib.gui.action;

import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.position.EdgeInt;
import fi.dy.masa.malilib.gui.widget.ScreenContext;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.data.Vec2i;
import fi.dy.masa.malilib.util.position.SectorEdge;

public class RadialActionExecutionWidget extends BaseActionExecutionWidget
{
    protected Vec2i center = Vec2i.ZERO;
    @Nullable SectorEdge draggedEdge;
    protected double outerRadius;
    protected double innerRadius;
    protected double startAngle;
    protected double endAngle;

    public RadialActionExecutionWidget()
    {
        super();

        this.setInnerRadius(30.0);
        this.setOuterRadius(60.0);
        this.setStartAngle(0.5);
        this.setEndAngle(1.2);

        this.setNormalBorderWidth(3);
        this.setHoveredBorderWidth(5);
    }

    @Override
    protected Type getType()
    {
        return Type.RADIAL;
    }

    public void setCenter(Vec2i center)
    {
        this.center = center;
    }

    @Override
    public void onAdded(BaseScreen screen)
    {
        int x = screen.getX() + screen.getScreenWidth() / 2;
        int y = screen.getY() + screen.getScreenHeight() / 2;

        this.setCenter(new Vec2i(x, y));
        this.updatePosition();
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY)
    {
        double distance = this.center.getDistance(mouseX, mouseY);

        if (distance >= this.innerRadius && distance <= this.outerRadius)
        {
            double mouseAngle = this.getMouseAngle(mouseX, mouseY, false);
            double start = this.startAngle;
            double end = this.endAngle;

            if (end > start)
            {
                return mouseAngle >= start && mouseAngle <= end;
            }
            else
            {
                return mouseAngle >= start || mouseAngle <= end;
            }
        }

        return false;
    }

    @Override
    public boolean intersects(EdgeInt rectangle)
    {
        int x = this.getX();
        int y = this.getY();

        return x >= rectangle.getLeft() &&
               x <= rectangle.getRight() &&
               y >= rectangle.getTop() &&
               y <= rectangle.getBottom();
    }

    @Override
    protected void startResize(int mouseX, int mouseY)
    {
        this.draggedEdge = SectorEdge.getClosestEdge(mouseX, mouseY, this.center.x, this.center.y,
                                                     this.innerRadius, this.outerRadius,
                                                     this.startAngle, this.endAngle);
        this.resizing = true;
        this.notifyChange();
    }

    @Override
    public void moveWidget(int mouseX, int mouseY)
    {
        double mouseAngle = this.getMouseAngle(mouseX, mouseY, true);
        double middleAngle = this.getMiddleAngle();

        // Change the old values by an offset, to avoid re-calculating
        // both values directly, as that could mis-align or collapse the range
        // or otherwise mess up the edges in some cases.
        double change = mouseAngle - middleAngle;

        this.setStartAngle(this.startAngle + change);
        this.setEndAngle(this.endAngle + change);
    }

    @Override
    protected void resizeWidget(int mouseX, int mouseY)
    {
        if (this.draggedEdge == null)
        {
            return;
        }

        if (this.draggedEdge.isRadius())
        {
            int gridSize = this.getGridSize();
            double centerX = this.center.x;
            double centerY = this.center.y;
            double distMouse = Math.sqrt((mouseX - centerX) * (mouseX - centerX) +
                                         (mouseY - centerY) * (mouseY - centerY));

            if (gridSize > 1)
            {
                distMouse = MathUtils.roundUp(distMouse, gridSize);
            }

            if (this.draggedEdge == SectorEdge.INNER_RING)
            {
                this.setInnerRadius(distMouse);
            }
            else
            {
                this.setOuterRadius(distMouse);
            }
        }
        else
        {
            double mouseAngle = this.getMouseAngle(mouseX, mouseY, true);

            if (this.draggedEdge == SectorEdge.START_ANGLE)
            {
                this.setStartAngle(mouseAngle);
            }
            else if (this.draggedEdge == SectorEdge.END_ANGLE)
            {
                this.setEndAngle(mouseAngle);
            }
        }
    }

    public void setInnerRadius(double innerRadius)
    {
        this.innerRadius = Math.max(innerRadius, 1);
        this.outerRadius = Math.max(this.innerRadius + 4, this.outerRadius);
        this.updatePosition();
    }

    public void setOuterRadius(double outerRadius)
    {
        this.outerRadius = Math.max(outerRadius, 2);
        this.innerRadius = Math.max(Math.min(this.outerRadius - 4, this.innerRadius), 1);
        this.updatePosition();
    }

    public void setStartAngle(double startAngle)
    {
        this.startAngle = MathUtils.wrapRadianAngle(startAngle);
        this.updatePosition();
    }

    public void setEndAngle(double endAngle)
    {
        this.endAngle = MathUtils.wrapRadianAngle(endAngle);
        this.updatePosition();
    }

    protected double getMouseAngle(int mouseX, int mouseY, boolean gridSnap)
    {
        // The zero angle will be on the middle right, because the coordinates are flipped here
        double diffX = this.center.x - mouseX;
        double diffY = this.center.y - mouseY;
        // Change from -pi .. pi to 0 .. 2 * pi.
        double angle = Math.atan2(diffY, diffX) + Math.PI;
        int gridSize = this.getGridSize();

        if (gridSnap && gridSize > 1)
        {
            angle = MathUtils.roundUp(angle, gridSize / 90.0);
        }

        return angle;
    }

    protected double getMiddleAngle()
    {
        double start = this.startAngle;
        double end = this.endAngle;
        double twoPi = 2 * Math.PI;
        double angle;

        if (end > start)
        {
            angle = (start + end) / 2.0;
        }
        else
        {
            angle = start + (twoPi - (start - end)) / 2.0;
        }

        return MathUtils.wrapRadianAngle(angle);
    }

    protected void updatePosition()
    {
        double r = (this.innerRadius + this.outerRadius) / 2.0;
        double angle = this.getMiddleAngle();

        this.setX(this.center.x + (int) (Math.cos(angle) * r));
        this.setY(this.center.y + (int) (Math.sin(angle) * r));
    }

    @Override
    protected void renderBorder(int x, int y, float z, int width, int height, int borderWidth,
                                boolean hovered, EdgeInt color, ScreenContext ctx)
    {
        double centerX = this.center.x;
        double centerY = this.center.y;
        int borderColor = color.getTop();

        if (hovered)
        {
            z += 0.0125f;
        }

        ShapeRenderUtils.renderSectorOutline(centerX, centerY, z, this.innerRadius, this.outerRadius,
                                             this.startAngle, this.endAngle, borderWidth, borderColor);
    }

    @Override
    protected void renderBackground(int x, int y, float z, int width, int height, int borderWidth,
                                    boolean hovered, int color, ScreenContext ctx)
    {
        double centerX = this.center.x;
        double centerY = this.center.y;

        if (hovered)
        {
            z += 0.0125f;
        }

        ShapeRenderUtils.renderSectorFill(centerX, centerY, z, this.innerRadius, this.outerRadius,
                                          this.startAngle, this.endAngle, color);
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();

        obj.addProperty("min_r", this.innerRadius);
        obj.addProperty("max_r", this.outerRadius);
        obj.addProperty("start_angle", this.startAngle);
        obj.addProperty("end_angle", this.endAngle);

        return obj;
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);

        this.setInnerRadius(JsonUtils.getDoubleOrDefault(obj, "min_r", this.innerRadius));
        this.setOuterRadius(JsonUtils.getDoubleOrDefault(obj, "max_r", this.outerRadius));
        this.setStartAngle(JsonUtils.getDoubleOrDefault(obj, "start_angle", this.startAngle));
        this.setEndAngle(JsonUtils.getDoubleOrDefault(obj, "end_angle", this.endAngle));
    }
}

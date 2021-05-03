package fi.dy.masa.malilib.gui.action;

import java.util.Locale;
import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.action.ActionContext;
import fi.dy.masa.malilib.action.ActionRegistry;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.position.EdgeInt;
import fi.dy.masa.malilib.gui.util.DraggedCorner;
import fi.dy.masa.malilib.gui.widget.ContainerWidget;
import fi.dy.masa.malilib.gui.widget.ScreenContext;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.data.Vec2i;

public class ActionExecutionWidget extends ContainerWidget
{
    @Nullable protected NamedAction action;
    @Nullable EventListener dirtyListener;
    protected Type type = Type.RECTANGULAR;
    protected DraggedCorner draggedCorner = DraggedCorner.BOTTOM_RIGHT;
    protected Vec2i dragStartOffset = Vec2i.ZERO;
    protected String name = "?";
    protected EdgeInt editedBorderColor = new EdgeInt(0xFFFF8000);
    protected boolean dragging;
    protected boolean editMode;
    protected boolean resizing;
    protected boolean selected;
    protected int gridSize = -1;
    protected int maxRadius;
    protected int minRadius;
    protected int sectorIndex;
    protected int sectorRing;

    public ActionExecutionWidget()
    {
        super(0, 0, 40, 20);

        this.centerTextHorizontally = true;
        this.textOffsetX = 0;

        this.setNormalBorderWidth(1);
        this.setHoveredBorderWidth(2);
        this.setNormalBorderColor(0xFFFFFFFF);
        this.setHoveredBorderColor(0xFFE0E020);
        this.setNormalBackgroundColor(0x00000000);
        this.setHoveredBackgroundColor(0x00000000);

        this.setRenderNormalBorder(true);
        this.setRenderNormalBackground(true);
        this.setRenderHoverBackground(true);
    }

    public Type getType()
    {
        return this.type;
    }

    public void setAction(@Nullable NamedAction action)
    {
        this.action = action;

        if (action != null)
        {
            this.getHoverInfoFactory().setTextLineProvider("action_info", action::getHoverInfo);
        }
        else
        {
            this.getHoverInfoFactory().removeTextLineProvider("action_info");
        }
    }

    public void setDirtyListener(@Nullable EventListener dirtyListener)
    {
        this.dirtyListener = dirtyListener;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public void setName(String name)
    {
        this.name = name;
        this.setText(StyledTextLine.of(name));
        this.setWidth(this.text.renderWidth + 10);
    }

    public void setGridSize(int gridSize)
    {
        this.gridSize = gridSize;
    }

    public void setMinRadius(int minRadius)
    {
        this.minRadius = minRadius;
    }

    public void setMaxRadius(int maxRadius)
    {
        this.maxRadius = maxRadius;
    }

    public void setSectorIndex(int sectorIndex)
    {
        this.sectorIndex = sectorIndex;
    }

    public void setSectorRing(int sectorRing)
    {
        this.sectorRing = sectorRing;
    }

    public void setEditMode(boolean editMode)
    {
        this.editMode = editMode;
    }

    public boolean isSelected()
    {
        return this.selected;
    }

    public void toggleSelected()
    {
        this.setSelected(! this.selected);
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;

        if (this.selected == false)
        {
            this.dragging = false;
        }
    }

    protected void notifyChange()
    {
        if (this.dirtyListener != null)
        {
            this.dirtyListener.onEvent();
        }
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.editMode)
        {
            if (mouseButton == 0)
            {
                this.startDragging(mouseX, mouseY);
            }
            else if (mouseButton == 1)
            {
                if (BaseScreen.isShiftDown())
                {
                    this.startResize(mouseX, mouseY);
                }

                return true;
            }

            return false;
        }

        if (this.action != null)
        {
            // Close the current screen first, in case the action opens another screen
            BaseScreen.openScreen(null);
            this.action.getAction().execute(new ActionContext());
        }

        return true;
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        this.dragging = false;
        this.resizing = false;
        super.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseMoved(int mouseX, int mouseY)
    {
        if (this.dragging)
        {
            this.moveWidget(mouseX, mouseY);
        }
        else if (this.resizing)
        {
            this.resizeWidget(mouseX, mouseY);
        }

        return false;
    }

    @Override
    public void setSize(int width, int height)
    {
        width = Math.max(width, this.text.renderWidth + 6);
        height = Math.max(height, 10);
        super.setSize(width, height);
    }

    @Override
    public void setWidth(int width)
    {
        width = Math.max(width, this.text.renderWidth + 6);
        super.setWidth(width);
    }

    @Override
    public void setHeight(int height)
    {
        height = Math.max(height, 10);
        super.setHeight(height);
    }

    public void startDragging(int mouseX, int mouseY)
    {
        this.dragStartOffset = new Vec2i(mouseX - this.getX(),  mouseY - this.getY());
        this.dragging = true;
        this.notifyChange();
    }

    protected void startResize(int mouseX, int mouseY)
    {
        if (this.getType() == ActionExecutionWidget.Type.RECTANGULAR)
        {
            this.draggedCorner = DraggedCorner.getFor(mouseX, mouseY, this);
            this.resizing = true;
            this.notifyChange();
        }
    }

    public void moveWidget(int mouseX, int mouseY)
    {
        if (this.getType() == ActionExecutionWidget.Type.RECTANGULAR)
        {
            int x = mouseX - this.dragStartOffset.x;
            int y = mouseY - this.dragStartOffset.y;
            x = MathUtils.roundDown(x, this.gridSize);
            y = MathUtils.roundDown(y, this.gridSize);
            this.setPosition(x, y);
        }
    }

    protected void resizeWidget(int mouseX, int mouseY)
    {
        if (this.getType() == ActionExecutionWidget.Type.RECTANGULAR)
        {
            this.draggedCorner.updateWidgetSize(mouseX, mouseY, this.gridSize, this);
        }
    }

    @Override
    protected EdgeInt getNormalBorderColorForRender()
    {
        if (this.dragging || this.resizing || this.selected)
        {
            return this.editedBorderColor;
        }

        return super.getNormalBorderColorForRender();
    }

    @Override
    protected EdgeInt getHoveredBorderColorForRender()
    {
        if (this.dragging || this.resizing || this.selected)
        {
            return this.editedBorderColor;
        }

        return super.getHoveredBorderColorForRender();
    }

    @Override
    public boolean shouldRenderHoverInfo(ScreenContext ctx)
    {
        if (this.editMode == false || this.dragging || this.resizing ||
            BaseScreen.isShiftDown() || BaseScreen.isCtrlDown())
        {
            return false;
        }

        return super.shouldRenderHoverInfo(ctx);
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        if (this.resizing || this.selected)
        {
            StyledTextLine line = StyledTextLine.raw(String.format("%d x %d", this.getWidth(), this.getHeight()));
            this.renderTextLine(x, y - 11, z, 0xFFFFFFFF, true, ctx, line);
        }

        super.renderAt(x, y, z, ctx);
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("type", this.type.name().toLowerCase(Locale.ROOT));
        obj.addProperty("name", this.name);
        obj.addProperty("name_color", this.defaultTextColor);
        obj.addProperty("bg_color", this.normalBackgroundColor);
        obj.addProperty("bg_color_hover", this.hoveredBackgroundColor);
        obj.add("border_color", this.borderColorNormal.toJson());
        obj.add("border_color_hover", this.borderColorHovered.toJson());

        if (this.action != null)
        {
            obj.addProperty("action_name", this.action.getRegistryName());

            JsonObject actionData = this.action.toJson();

            if (actionData != null)
            {
                obj.add("action_data", actionData);
            }
        }

        if (this.type == Type.RECTANGULAR)
        {
            obj.addProperty("x", this.getX());
            obj.addProperty("y", this.getY());
            obj.addProperty("width", this.getWidth());
            obj.addProperty("height", this.getHeight());
        }
        else
        {
            obj.addProperty("sector_index", this.sectorIndex);
            obj.addProperty("sector_ring", this.sectorRing);
        }

        return obj;
    }

    @Nullable
    public static ActionExecutionWidget fromJson(JsonObject obj)
    {
        ActionExecutionWidget widget = new ActionExecutionWidget();

        widget.setName(JsonUtils.getStringOrDefault(obj, "name", "?"));
        widget.defaultTextColor = JsonUtils.getIntegerOrDefault(obj, "name_color", widget.defaultTextColor);
        widget.normalBackgroundColor = JsonUtils.getIntegerOrDefault(obj, "bg_color", widget.normalBackgroundColor);
        widget.hoveredBackgroundColor = JsonUtils.getIntegerOrDefault(obj, "bg_color_hover", widget.hoveredBackgroundColor);

        if (JsonUtils.hasArray(obj, "border_color"))
        {
            widget.normalBorderColor.fromJson(obj.get("border_color").getAsJsonArray());
        }

        if (JsonUtils.hasArray(obj, "border_color_hover"))
        {
            widget.hoveredBorderColor.fromJson(obj.get("border_color_hover").getAsJsonArray());
        }

        // FIXME
        NamedAction action = ActionRegistry.INSTANCE.getAction(JsonUtils.getStringOrDefault(obj, "action_name", "?"));

        if (action != null)
        {
            if (JsonUtils.hasObject(obj, "action_data"))
            {
                action = action.fromJson(obj.get("action_data").getAsJsonObject());
            }

            widget.setAction(action);
        }

        if (JsonUtils.getStringOrDefault(obj, "type", "?").equals("sector"))
        {
            widget.setType(Type.SECTOR);
            widget.sectorIndex = JsonUtils.getIntegerOrDefault(obj, "sector_index", widget.sectorIndex);
            widget.sectorRing = JsonUtils.getIntegerOrDefault(obj, "sector_ring", widget.sectorRing);
        }
        else
        {
            int x = JsonUtils.getIntegerOrDefault(obj, "x", 0);
            int y = JsonUtils.getIntegerOrDefault(obj, "y", 0);
            widget.setType(Type.RECTANGULAR);
            widget.setPosition(x, y);
            widget.setWidth(JsonUtils.getIntegerOrDefault(obj, "width", widget.getWidth()));
            widget.setHeight(JsonUtils.getIntegerOrDefault(obj, "height", widget.getHeight()));
        }

        return widget;
    }

    public enum Type
    {
        RECTANGULAR,
        SECTOR
    }
}

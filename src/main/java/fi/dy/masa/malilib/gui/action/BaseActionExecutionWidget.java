package fi.dy.masa.malilib.gui.action;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.action.ActionContext;
import fi.dy.masa.malilib.action.ActionRegistry;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.position.EdgeInt;
import fi.dy.masa.malilib.gui.widget.ContainerWidget;
import fi.dy.masa.malilib.gui.widget.ScreenContext;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class BaseActionExecutionWidget extends ContainerWidget
{
    @Nullable protected NamedAction action;
    @Nullable protected EventListener dirtyListener;
    @Nullable protected String hoverText = "";
    protected BooleanSupplier editModeSupplier = ActionWidgetScreen.ALWAYS_FALSE;
    protected IntSupplier gridSizeSupplier = ActionWidgetScreen.NO_GRID;
    protected String name = "?";
    protected EdgeInt editedBorderColor = new EdgeInt(0xFFFF8000);
    protected boolean dragging;
    protected boolean resizing;
    protected boolean selected;

    public BaseActionExecutionWidget()
    {
        super(0, 0, 40, 20);

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

    public void setManagementData(@Nullable EventListener dirtyListener,
                                  BooleanSupplier editModeSupplier,
                                  IntSupplier gridSizeSupplier)
    {
        this.dirtyListener = dirtyListener;
        this.editModeSupplier = editModeSupplier;
        this.gridSizeSupplier = gridSizeSupplier;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
        this.setText(StyledTextLine.of(name));

        int width = this.text.renderWidth + 10;

        if (width > this.getWidth())
        {
            this.setWidth(width);
        }
    }

    protected boolean isEditMode()
    {
        return this.editModeSupplier.getAsBoolean();
    }

    protected int getGridSize()
    {
        return this.gridSizeSupplier.getAsInt();
    }

    @Nullable
    public String getActionWidgetHoverTextString()
    {
        return this.hoverText;
    }

    protected List<StyledTextLine> getActionWidgetHoverTextLines()
    {
        if (this.hoverText != null &&
            (this.isEditMode() == false || (BaseScreen.isCtrlDown() == false && BaseScreen.isShiftDown() == false)))
        {
            return ImmutableList.of(StyledTextLine.translate(this.hoverText));
        }

        return Collections.emptyList();
    }

    public void setActionWidgetHoverText(@Nullable String hoverText)
    {
        if (org.apache.commons.lang3.StringUtils.isBlank(hoverText))
        {
            hoverText = null;
            this.getHoverInfoFactory().removeTextLineProvider("widget_hover_tip");
        }
        else
        {
            this.getHoverInfoFactory().setTextLineProvider("widget_hover_tip", this::getActionWidgetHoverTextLines, 99);
        }

        this.hoverText = hoverText;
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

    public void onAdded(BaseScreen screen)
    {
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isEditMode())
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
        else if (mouseButton == 0 && this.action != null)
        {
            // Close the current screen first, in case the action opens another screen
            if (MaLiLibConfigs.Generic.ACTION_SCREEN_CLOSE_ON_EXECUTE.getBooleanValue())
            {
                BaseScreen.openScreen(null);
            }

            this.executeAction();
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
        if (this.text != null)
        {
            width = Math.max(width, this.text.renderWidth + 6);
            height = Math.max(height, 10);
        }

        super.setSize(width, height);
    }

    @Override
    public void setWidth(int width)
    {
        if (this.text != null)
        {
            width = Math.max(width, this.text.renderWidth + 6);
        }

        super.setWidth(width);
    }

    @Override
    public void setHeight(int height)
    {
        height = Math.max(height, 10);
        super.setHeight(height);
    }

    protected abstract Type getType();

    public void executeAction()
    {
        if (this.action != null)
        {
            this.action.getAction().execute(new ActionContext());
        }
    }

    public void startDragging(int mouseX, int mouseY)
    {
        this.dragging = true;
        this.notifyChange();
    }

    protected abstract void startResize(int mouseX, int mouseY);

    public abstract void moveWidget(int mouseX, int mouseY);

    protected abstract void resizeWidget(int mouseX, int mouseY);

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
        if (this.dragging || this.resizing || BaseScreen.isShiftDown() || BaseScreen.isCtrlDown())
        {
            return false;
        }

        return super.shouldRenderHoverInfo(ctx);
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("type", this.getType().name().toLowerCase(Locale.ROOT));
        obj.addProperty("name", this.name);
        obj.addProperty("name_color", this.defaultTextColor);
        obj.addProperty("bg_color", this.normalBackgroundColor);
        obj.addProperty("bg_color_hover", this.hoveredBackgroundColor);
        obj.addProperty("name_centered_x", this.centerTextHorizontally);
        obj.addProperty("name_centered_y", this.centerTextVertically);
        obj.addProperty("name_x_offset", this.textOffsetX);
        obj.addProperty("name_y_offset", this.textOffsetY);
        obj.add("border_color", this.normalBorderColor.toJson());
        obj.add("border_color_hover", this.hoveredBorderColor.toJson());

        if (this.action != null)
        {
            obj.addProperty("action_name", this.action.getRegistryName());

            JsonObject actionData = this.action.toJson();

            if (actionData != null)
            {
                obj.add("action_data", actionData);
            }
        }

        if (org.apache.commons.lang3.StringUtils.isBlank(this.hoverText) == false)
        {
            obj.addProperty("hover_text", this.hoverText);
        }

        return obj;
    }

    protected void fromJson(JsonObject obj)
    {
        this.setName(JsonUtils.getStringOrDefault(obj, "name", "?"));
        this.setActionWidgetHoverText(JsonUtils.getString(obj, "hover_text"));
        this.defaultTextColor = JsonUtils.getIntegerOrDefault(obj, "name_color", this.defaultTextColor);
        this.normalBackgroundColor = JsonUtils.getIntegerOrDefault(obj, "bg_color", this.normalBackgroundColor);
        this.hoveredBackgroundColor = JsonUtils.getIntegerOrDefault(obj, "bg_color_hover", this.hoveredBackgroundColor);
        this.centerTextHorizontally = JsonUtils.getBooleanOrDefault(obj, "name_centered_x", this.centerTextHorizontally);
        this.centerTextVertically = JsonUtils.getBooleanOrDefault(obj, "name_centered_y", this.centerTextVertically);
        this.textOffsetX = JsonUtils.getIntegerOrDefault(obj, "name_x_offset", this.textOffsetX);
        this.textOffsetY = JsonUtils.getIntegerOrDefault(obj, "name_y_offset", this.textOffsetY);

        if (JsonUtils.hasArray(obj, "border_color"))
        {
            this.normalBorderColor.fromJson(obj.get("border_color").getAsJsonArray());
        }

        if (JsonUtils.hasArray(obj, "border_color_hover"))
        {
            this.hoveredBorderColor.fromJson(obj.get("border_color_hover").getAsJsonArray());
        }

        // FIXME
        NamedAction action = ActionRegistry.INSTANCE.getAction(JsonUtils.getStringOrDefault(obj, "action_name", "?"));

        if (action != null)
        {
            if (JsonUtils.hasObject(obj, "action_data"))
            {
                action = action.fromJson(obj.get("action_data").getAsJsonObject());
            }

            this.setAction(action);
        }
    }

    @Nullable
    public static BaseActionExecutionWidget createFromJson(JsonObject obj)
    {
        Type type = JsonUtils.getStringOrDefault(obj, "type", "").equals("radial") ? Type.RADIAL : Type.RECTANGULAR;
        BaseActionExecutionWidget widget = type.create();
        widget.fromJson(obj);
        return widget;
    }

    public enum Type
    {
        RECTANGULAR ("malilib.label.action_execution_widget.type.rectangular",  RectangularActionExecutionWidget::new),
        RADIAL      ("malilib.label.action_execution_widget.type.radial",       RadialActionExecutionWidget::new);

        public static final ImmutableList<Type> VALUES = ImmutableList.copyOf(values());

        private final Supplier<BaseActionExecutionWidget> factory;
        private final String translationKey;

        Type(String translationKey, Supplier<BaseActionExecutionWidget> factory)
        {
            this.translationKey = translationKey;
            this.factory = factory;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }

        public BaseActionExecutionWidget create()
        {
            return this.factory.get();
        }
    }
}

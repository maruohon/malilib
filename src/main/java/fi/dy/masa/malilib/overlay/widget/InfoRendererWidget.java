package fi.dy.masa.malilib.overlay.widget;

import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.overlay.InfoWidgetManager;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.JsonUtils;

public abstract class InfoRendererWidget extends BaseWidget
{
    protected ScreenLocation location = ScreenLocation.TOP_LEFT;
    @Nullable protected EventListener geometryChangeListener;
    @Nullable protected EventListener enabledChangeListener;
    protected String name = "?";
    @Nullable protected StyledTextLine styledName;
    protected boolean enabled = true;
    protected boolean isOverlay;
    protected boolean renderBackground;
    protected boolean oddEvenBackground;
    protected boolean renderName;
    protected boolean shouldSerialize;
    protected long previousGeometryUpdateTime = -1;
    protected long geometryShrinkDelay = (long) (5 * 1E9); // 5 seconds
    protected int backgroundColor = 0x30A0A0A0;
    protected int backgroundColorOdd = 0x40A0A0A0;
    protected int containerWidth;
    protected int containerHeight;
    protected int geometryShrinkThresholdX = 40;
    protected int geometryShrinkThresholdY = 10;
    protected int previousUpdatedWidth;
    protected int previousUpdatedHeight;
    protected int sortIndex = 100;

    public InfoRendererWidget()
    {
        super(0, 0, 0, 0);
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    /**
     * A widget that says it's an overlay will not get moved on the y direction
     * by other widgets, but instead it will sit on top of the other widgets
     * at the base location of the InfoArea.
     */
    public boolean isOverlay()
    {
        return this.isOverlay;
    }

    /**
     * Returns whether or not this widget should get saved and loaded
     * automatically. This should generally only return true for
     * widgets that are created by the user via some configuration menu,
     * and are thus handled via the InfoWidgetManager.
     */
    public boolean getShouldSerialize()
    {
        return this.shouldSerialize;
    }

    public int getSortIndex()
    {
        return this.sortIndex;
    }

    public boolean isBackgroundEnabled()
    {
        return this.renderBackground;
    }

    public boolean isOddEvenBackgroundEnabled()
    {
        return this.oddEvenBackground;
    }

    public boolean getRenderName()
    {
        return this.renderName;
    }

    public String getName()
    {
        return this.name != null ? this.name : this.location.getDisplayName();
    }

    public ScreenLocation getScreenLocation()
    {
        return this.location;
    }

    public int getBackgroundColor()
    {
        return this.backgroundColor;
    }

    public int getOddBackgroundColor()
    {
        return this.backgroundColorOdd;
    }

    public void toggleEnabled()
    {
        this.setEnabled(! this.isEnabled());
    }

    public void setEnabled(boolean enabled)
    {
        if (enabled != this.enabled && this.enabledChangeListener != null)
        {
            this.enabled = enabled;
            this.enabledChangeListener.onEvent();
        }
    }

    public void toggleBackgroundEnabled()
    {
        this.renderBackground = ! this.renderBackground;
    }

    public void toggleOddEvenBackgroundEnabled()
    {
        this.oddEvenBackground = ! this.oddEvenBackground;
    }

    public void toggleRenderName()
    {
        this.renderName = ! this.renderName;
        this.updateSize();
        this.notifyContainerOfChanges(true);
    }

    /**
     * Sets the sort index of this widget. Lower values come first (higher up).
     */
    public void setSortIndex(int index)
    {
        this.sortIndex = index;
    }

    public void setBackgroundColor(int color)
    {
        this.backgroundColor = color;
    }

    public void setOddBackgroundColor(int color)
    {
        this.backgroundColorOdd = color;
    }

    /**
     * Sets a listener that should be notified if the dimensions of this widget get changed,
     * such as the widget height or width changing due to changes in the displayed contents.
     */
    public void setGeometryChangeListener(@Nullable EventListener listener)
    {
        this.geometryChangeListener = listener;
    }

    /**
     * Sets a listener that should be notified if the dimensions of this widget get changed,
     * such as the widget height or width changing due to changes in the displayed contents.
     */
    public void setEnabledChangeListener(@Nullable EventListener listener)
    {
        this.enabledChangeListener = listener;
    }

    public void setContainerDimensions(int width, int height)
    {
        this.containerWidth = width;
        this.containerHeight = height;
    }

    public void setLocation(ScreenLocation location)
    {
        this.location = location;

        if (StringUtils.isBlank(this.name))
        {
            this.setName(location.getDisplayName());
        }
    }

    public void setName(String name)
    {
        this.name = name;
        this.styledName = StyledTextLine.of(name);
    }

    /**
     * Requests the container to re-layout all the info widgets due to
     * this widget's dimensions changing.
     */
    protected void notifyContainerOfChanges(boolean force)
    {
        if (this.geometryChangeListener != null && (force || this.needsGeometryUpdate()))
        {
            this.geometryChangeListener.onEvent();
            this.previousUpdatedWidth = this.getWidth();
            this.previousUpdatedHeight = this.getHeight();
            this.previousGeometryUpdateTime = System.nanoTime();
        }
    }

    protected boolean needsGeometryUpdate()
    {
        int height = this.getHeight();
        int width = this.getWidth();

        if (width > this.previousUpdatedWidth || height > this.previousUpdatedHeight)
        {
            return true;
        }

        if (width < (this.previousUpdatedWidth - this.geometryShrinkThresholdX) ||
            height < (this.previousUpdatedHeight - this.geometryShrinkThresholdY))
        {
            return System.nanoTime() - this.previousGeometryUpdateTime > this.geometryShrinkDelay;
        }

        return false;
    }

    /**
     * 
     * Called to allow the widget to update its state before all the enabled widgets are rendered.
     */
    public void updateState(Minecraft mc)
    {
    }

    public void render()
    {
        if (this.isEnabled())
        {
            int x = this.getX();
            int y = this.getY();
            this.renderAt(x, y, this.getZLevel());

            if (MaLiLibConfigs.Debug.INFO_OVERLAY_DEBUG.getBooleanValue())
            {
                this.renderDebug(0, 0, false, true, MaLiLibConfigs.Debug.GUI_DEBUG_INFO_ALWAYS.getBooleanValue());
            }
        }
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("type", this.getClass().getName());
        obj.addProperty("name", this.getName());
        obj.addProperty("enabled", this.isEnabled());
        obj.addProperty("screen_location", this.getScreenLocation().getName());
        obj.addProperty("render_name", this.renderName);
        obj.addProperty("bg_enabled", this.renderBackground);
        obj.addProperty("bg_odd_even", this.oddEvenBackground);
        obj.addProperty("bg_color", this.backgroundColor);
        obj.addProperty("bg_color_odd", this.backgroundColorOdd);
        obj.addProperty("sort_index", this.getSortIndex());
        obj.add("padding", this.padding.toJson());
        obj.add("margin", this.margin.toJson());

        return obj;
    }

    public void fromJson(JsonObject obj)
    {
        this.enabled = JsonUtils.getBooleanOrDefault(obj, "enabled", true);
        this.renderBackground = JsonUtils.getBooleanOrDefault(obj, "bg_enabled", false);
        this.oddEvenBackground = JsonUtils.getBooleanOrDefault(obj, "bg_odd_even", false);
        this.backgroundColor = JsonUtils.getIntegerOrDefault(obj, "bg_color", 0x30A0A0A0);
        this.backgroundColorOdd = JsonUtils.getIntegerOrDefault(obj, "bg_color_odd", 0x40A0A0A0);
        this.renderName = JsonUtils.getBooleanOrDefault(obj, "render_name", false);
        this.setName(JsonUtils.getStringOrDefault(obj, "name", this.name));
        this.setSortIndex(JsonUtils.getIntegerOrDefault(obj, "sort_index", 100));

        if (JsonUtils.hasString(obj, "screen_location"))
        {
            ScreenLocation location = ScreenLocation.findValueByName(obj.get("screen_location").getAsString(), ScreenLocation.VALUES);
            this.setLocation(location);
        }

        if (JsonUtils.hasArray(obj, "padding"))
        {
            this.padding.fromJson(obj.get("padding").getAsJsonArray());
        }

        if (JsonUtils.hasArray(obj, "margin"))
        {
            this.margin.fromJson(obj.get("margin").getAsJsonArray());
        }
    }

    protected void renderBackground(int x, int y, float z)
    {
        if (this.renderBackground)
        {
            int width = this.getWidth();
            int height = this.getHeight();

            ShapeRenderUtils.renderRectangle(x, y, z, width, height, this.backgroundColor);
        }
    }

    public void renderAt(int x, int y, float z)
    {
        this.renderBackground(x, y, z);

        if (this.renderName && this.styledName != null)
        {
            y += this.padding.getTop();
            this.renderTextLine(x + this.padding.getLeft(), y, z, 0xFFFFFFFF, true, this.styledName);
            y += this.lineHeight;
        }

        this.renderContents(x, y, z);
    }

    protected void renderContents(int x, int y, float z)
    {
    }

    @Nullable
    public static InfoRendererWidget createFromJson(JsonObject obj)
    {
        if (JsonUtils.hasString(obj, "type"))
        {
            String type = obj.get("type").getAsString();
            InfoWidgetManager.InfoWidgetFactory factory = InfoWidgetManager.getWidgetFactory(type);

            if (factory != null)
            {
                InfoRendererWidget widget = factory.create();
                widget.fromJson(obj);
                return widget;
            }
        }

        return null;
    }
}

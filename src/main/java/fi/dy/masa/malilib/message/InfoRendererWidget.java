package fi.dy.masa.malilib.message;

import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.overlay.InfoWidgetManager;
import fi.dy.masa.malilib.util.JsonUtils;

public abstract class InfoRendererWidget extends BaseWidget
{
    protected ScreenLocation location = ScreenLocation.TOP_LEFT;
    @Nullable protected EventListener geometryChangeListener;
    protected String name;
    protected boolean enabled = true;
    protected boolean shouldSerialize;
    protected long previousGeometryUpdateTime = -1;
    protected long geometryShrinkDelay = (long) (5 * 1E9); // 5 seconds
    protected int sortIndex = 100;
    protected int containerWidth;
    protected int containerHeight;
    protected int geometryShrinkThresholdX = 40;
    protected int geometryShrinkThresholdY = 10;
    protected int previousUpdatedWidth;
    protected int previousUpdatedHeight;

    public InfoRendererWidget()
    {
        super(0, 0, 0, 0);
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public boolean getShouldSerialize()
    {
        return this.shouldSerialize;
    }

    public int getSortIndex()
    {
        return this.sortIndex;
    }

    public String getName()
    {
        return this.name != null ? this.name : this.location.getDisplayName();
    }

    public ScreenLocation getScreenLocation()
    {
        return this.location;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Sets the sort index of this widget. Lower values come first (higher up).
     * @param index
     */
    public void setSortIndex(int index)
    {
        this.sortIndex = index;
    }

    /**
     * Sets a listener that should be notified if the dimensions of this widget get changed,
     * such as the widget height or width changing due to changes in the displayed contents.
     * @param listener
     */
    public void setGeometryChangeListener(@Nullable EventListener listener)
    {
        this.geometryChangeListener = listener;
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
            this.name = location.getDisplayName();
        }
    }

    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Requests the container to re-layout all the info widgets due to
     * this widget's dimensions changing.
     */
    protected void notifyContainerOfChanges()
    {
        if (this.geometryChangeListener != null && this.needsGeometryUpdate())
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
            int x = this.getContentStartX();
            int y = this.getContentStartY();
            this.renderAt(x, y, this.getZLevel());

            if (MaLiLibConfigs.Debug.INFO_OVERLAY_DEBUG.getBooleanValue())
            {
                this.renderDebug(0, 0, false, true, true);
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
        obj.addProperty("sort_index", this.getSortIndex());

        return obj;
    }

    public void fromJson(JsonObject obj)
    {
        if (JsonUtils.hasString(obj, "name"))
        {
            this.setName(obj.get("name").getAsString());
        }

        if (JsonUtils.hasBoolean(obj, "enabled"))
        {
            this.setEnabled(obj.get("enabled").getAsBoolean());
        }

        if (JsonUtils.hasString(obj, "screen_location"))
        {
            ScreenLocation location = ScreenLocation.findValueByName(obj.get("screen_location").getAsString(), ScreenLocation.VALUES);
            this.setLocation(location);
        }

        if (JsonUtils.hasInteger(obj, "sort_index"))
        {
            this.setSortIndex(obj.get("sort_index").getAsInt());
        }
    }

    public abstract void renderAt(int x, int y, float z);

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

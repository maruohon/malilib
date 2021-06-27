package fi.dy.masa.malilib.overlay.widget;

import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.gui.position.GeometryResizeNotifier;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.data.MarkerManager;

public class BaseOverlayWidget extends BaseWidget
{
    protected final MarkerManager<String> markerManager = new MarkerManager<>(JsonPrimitive::new, JsonElement::getAsString);
    protected final GeometryResizeNotifier geometryResizeNotifier;
    @Nullable protected EventListener enabledChangeListener;
    protected boolean forceNotifyGeometryChangeListener;
    protected boolean needsReLayout;
    protected boolean enabled = true;

    public BaseOverlayWidget()
    {
        super(0, 0, 0, 0);

        this.geometryResizeNotifier = new GeometryResizeNotifier(this::getWidth, this::getHeight);
        this.margin.setChangeListener(this::requestUnconditionalReLayout);
        this.padding.setChangeListener(this::requestUnconditionalReLayout);
    }

    /**
     * Sets a listener that should be notified if the dimensions of this widget get changed,
     * such as the widget height or width changing due to changes in the displayed contents.
     */
    public void setGeometryChangeListener(@Nullable EventListener listener)
    {
        this.geometryResizeNotifier.setGeometryChangeListener(listener);
    }

    /**
     * Sets a listener that should be notified if the dimensions of this widget get changed,
     * such as the widget height or width changing due to changes in the displayed contents.
     */
    public void setEnabledChangeListener(@Nullable EventListener listener)
    {
        this.enabledChangeListener = listener;
    }

    public MarkerManager<String> getMarkerManager()
    {
        return this.markerManager;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public void toggleEnabled()
    {
        this.setEnabled(! this.isEnabled());
    }

    public void setEnabled(boolean enabled)
    {
        boolean wasEnabled = this.enabled;
        this.enabled = enabled;

        if (enabled != wasEnabled && this.enabledChangeListener != null)
        {
            this.enabledChangeListener.onEvent();
        }
    }

    protected void requestConditionalReLayout()
    {
        this.needsReLayout = true;
    }

    protected void requestUnconditionalReLayout()
    {
        this.needsReLayout = true;
        this.forceNotifyGeometryChangeListener = true;
    }

    protected void reLayoutWidgets(boolean forceNotify)
    {
        this.updateSize();
        this.updateSubWidgetPositions();
        this.geometryResizeNotifier.checkAndNotifyContainerOfChanges(forceNotify);

        this.needsReLayout = false;
        this.forceNotifyGeometryChangeListener = false;
    }

    /**
     * Called to allow the widget to update its state before all the enabled widgets are rendered.
     */
    public void updateState()
    {
        if (this.needsReLayout)
        {
            this.reLayoutWidgets(this.forceNotifyGeometryChangeListener);
        }

        this.geometryResizeNotifier.updateState();
    }

    @Override
    protected void onPositionChanged(int oldX, int oldY)
    {
        this.updateSubWidgetPositions();
    }

    public void updateSubWidgetPositions()
    {
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("type", this.getClass().getName());
        obj.addProperty("enabled", this.isEnabled());
        obj.addProperty("z", this.getZ());

        obj.add("text_settings", this.getTextSettings().toJson());

        if (this.margin.isEmpty() == false)
        {
            obj.add("margin", this.margin.toJson());
        }

        if (this.padding.isEmpty() == false)
        {
            obj.add("padding", this.padding.toJson());
        }

        obj.add("markers", this.markerManager.toJson());

        return obj;
    }

    public void fromJson(JsonObject obj)
    {
        this.enabled = JsonUtils.getBooleanOrDefault(obj, "enabled", true);
        this.setZ(JsonUtils.getFloatOrDefault(obj, "z", this.getZ()));

        JsonUtils.readArrayIfPresent(obj, "padding", this.padding::fromJson);
        JsonUtils.readArrayIfPresent(obj, "margin", this.margin::fromJson);
        JsonUtils.readObjectIfPresent(obj, "text_settings", this.getTextSettings()::fromJson);

        if (obj.has("markers"))
        {
            this.markerManager.fromJson(obj.get("markers"));
        }
    }
}

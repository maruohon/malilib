package fi.dy.masa.malilib.overlay.widget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.GlStateManager;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.gui.widget.ScreenContext;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.overlay.InfoOverlay;
import fi.dy.masa.malilib.overlay.InfoWidgetManager;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextRenderSettings;
import fi.dy.masa.malilib.util.JsonUtils;

public abstract class InfoRendererWidget extends BaseWidget
{
    protected final List<Consumer<ScreenLocation>> locationChangeListeners = new ArrayList<>();
    protected final Set<String> markers = new HashSet<>();
    protected TextRenderSettings textSettings = new TextRenderSettings();
    protected ScreenLocation location = ScreenLocation.TOP_LEFT;
    protected String name = "?";
    protected IntSupplier viewportWidthSupplier = GuiUtils::getScaledWindowWidth;
    protected IntSupplier viewportHeightSupplier = GuiUtils::getScaledWindowHeight;
    protected InfoOverlay.RenderContext visibleInContext = InfoOverlay.RenderContext.BOTH;
    @Nullable protected EventListener geometryChangeListener;
    @Nullable protected EventListener enabledChangeListener;
    @Nullable protected StyledTextLine styledName;
    protected boolean enabled = true;
    protected boolean delayedGeometryUpdate;
    protected boolean forceNotifyGeometryChangeListener;
    protected boolean isOverlay;
    protected boolean needsReLayout;
    protected boolean renderAboveScreen;
    protected boolean renderName;
    protected boolean shouldSerialize;
    protected boolean renderBackground;
    protected double scale = 1.0;
    protected long previousGeometryUpdateTime = -1;
    protected long geometryShrinkDelay = (long) (2 * 1E9); // 2 seconds
    protected int backgroundColor = 0xC0000000;
    protected int borderColor = 0xFFC0C0C0;
    protected int geometryShrinkThresholdX = 40;
    protected int geometryShrinkThresholdY = 10;
    protected int previousUpdatedWidth;
    protected int previousUpdatedHeight;
    protected int sortIndex = 100;

    public InfoRendererWidget()
    {
        super(0, 0, 0, 0);

        this.margin.setChangeListener(this::requestUnconditionalReLayout);
        this.padding.setChangeListener(this::requestUnconditionalReLayout);
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

    public TextRenderSettings getTextSettings()
    {
        return this.textSettings;
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

    public void toggleRenderName()
    {
        this.renderName = ! this.renderName;
        this.requestUnconditionalReLayout();
    }

    public double getScale()
    {
        return this.scale;
    }

    public void setScale(double scale)
    {
        this.scale = scale;
        this.requestConditionalReLayout();
    }

    public boolean getRenderBackground()
    {
        return this.renderBackground;
    }

    public void setRenderBackground(boolean renderBackground)
    {
        this.renderBackground = renderBackground;
    }

    public int getBackgroundColor()
    {
        return this.backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public int getBorderColor()
    {
        return this.borderColor;
    }

    public void setBorderColor(int borderColor)
    {
        this.borderColor = borderColor;
    }

    /**
     * Sets the sort index of this widget. Lower values come first (higher up) within the InfoArea.
     * The default sort index is 100.
     */
    public void setSortIndex(int index)
    {
        this.sortIndex = index;
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

    /**
     * Adds a listener that gets notified when the ScreenLocation of this widget gets changed.
     */
    public void addLocationChangeListener(Consumer<ScreenLocation> listener)
    {
        if (this.locationChangeListeners.contains(listener) == false)
        {
            this.locationChangeListeners.add(listener);
        }
    }

    public void removeLocationChangeListener(Consumer<ScreenLocation> listener)
    {
        this.locationChangeListeners.remove(listener);
    }

    public void setViewportSizeSuppliers(IntSupplier viewportWidthSupplier, IntSupplier viewportHeightSupplier)
    {
        this.viewportWidthSupplier = viewportWidthSupplier;
        this.viewportHeightSupplier = viewportHeightSupplier;
    }

    /**
     * Adds a marker that a mod can use to recognize which of the possibly several
     * info widgets of the same type in the same InfoArea/location it has been using.
     * This is mostly useful after game restarts or world re-logs, when the
     * InfoWidgetManager reloads the saved widgets, and a mod wants to re-attach to the
     * "same" widget it was using before, instead of creating new ones every time.
     */
    public void addMarker(String marker)
    {
        this.markers.add(marker);
    }

    public void removeMarker(String marker)
    {
        this.markers.remove(marker);
    }

    public boolean hasMarker(String marker)
    {
        return this.markers.contains(marker);
    }

    public void setLocation(ScreenLocation location)
    {
        this.location = location;

        if (StringUtils.isBlank(this.name))
        {
            this.setName(location.getDisplayName());
        }

        for (Consumer<ScreenLocation> listener : this.locationChangeListeners)
        {
            listener.accept(location);
        }
    }

    public void setVisibleInContext(InfoOverlay.RenderContext context)
    {
        this.visibleInContext = context;
    }

    /**
     * Sets whether or not widgets that are visible in RenderContext.BOTH will render
     * below or above the screen.
     */
    public void setRenderAboveScreen(boolean renderBelowScreen)
    {
        this.renderAboveScreen = renderBelowScreen;
    }

    public void setName(String name)
    {
        this.name = name;
        this.styledName = StyledTextLine.of(name);
    }

    public void openEditScreen()
    {
    }

    /**
     * Updates this widget's position, if this is an "overlay widget"
     * that is not positioned automatically by the InfoArea.
     */
    protected void updateWidgetPosition()
    {
        if (this.isOverlay)
        {
            int viewportWidth = this.viewportWidthSupplier.getAsInt();
            int viewportHeight = this.viewportHeightSupplier.getAsInt();
            int width = (int) Math.ceil(this.getWidth() * this.getScale());
            int height = (int) Math.ceil(this.getHeight() * this.getScale());
            int marginX = this.location.horizontalLocation.getMargin(this.getMargin());
            int marginY = this.location.verticalLocation.getMargin(this.getMargin());
            int x = this.location.getStartX(width, viewportWidth, marginX);
            int y = this.location.getStartY(height, viewportHeight, marginY);

            this.setPosition(x, y);
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
        this.notifyContainerOfChanges(forceNotify);

        this.needsReLayout = false;
        this.forceNotifyGeometryChangeListener = false;
    }

    @Override
    protected void onPositionChanged(int oldX, int oldY)
    {
        this.updateSubWidgetPositions();
    }

    public void updateSubWidgetPositions()
    {
    }

    /**
     * Called after the widget has been properly initialized and added to the InfoArea
     */
    public void onAdded()
    {
    }

    /**
     * Requests the container to re-layout all the info widgets due to
     * this widget's dimensions changing.
     */
    protected void notifyContainerOfChanges(boolean forceNotify)
    {
        if (this.geometryChangeListener != null && (forceNotify || this.needsGeometryUpdate()))
        {
            this.geometryChangeListener.onEvent();
            this.previousUpdatedWidth = this.getWidth();
            this.previousUpdatedHeight = this.getHeight();
            this.previousGeometryUpdateTime = System.nanoTime();
            this.delayedGeometryUpdate = false;
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
            this.delayedGeometryUpdate = true;
            return System.nanoTime() - this.previousGeometryUpdateTime > this.geometryShrinkDelay;
        }

        return false;
    }

    /**
     * 
     * Called to allow the widget to update its state before all the enabled widgets are rendered.
     */
    public void updateState()
    {
        if (this.needsReLayout)
        {
            this.reLayoutWidgets(this.forceNotifyGeometryChangeListener);
        }

        // Keep checking for geometry updates until the delay time runs out,
        // if the contents are set to shrink after a delay
        if (this.delayedGeometryUpdate)
        {
            this.notifyContainerOfChanges(false);
        }
    }

    /**
     * This method determines which "instance" or rather render call of the InfoOverlay
     * should **potentially** handle rendering this widget.
     * If the widget should always be visible and on top, then this should return BOTH.
     * The other two values INGAME and GUI make the widget only render from one or the other,
     * which means that if it's also visible INGAME then it can't be on top when a GUI is open,
     * and if it renders only in a GUI context then it's obviously not rendered when in-game.
     * <br>
     * Note that the other {@link #shouldRenderFromContext(InfoOverlay.RenderContext, boolean)}
     * method is used to check whether or not the widget will actually
     * render, based on the current status of having a GUI open or not.
     * So basically that method will prevent a widget from actually rendering twice if
     * it's set to be visible in BOTH contexts.
     */
    public boolean isVisibleInContext(InfoOverlay.RenderContext context)
    {
        return this.visibleInContext == context || this.visibleInContext == InfoOverlay.RenderContext.BOTH;
    }

    public boolean shouldRenderFromContext(InfoOverlay.RenderContext context, boolean isScreenOpen)
    {
        // Note that widgets that have visibleInContext = INGAME will never be called with the argument GUI here,
        // or vice versa widgets with visibleInContext = GUI will never be called with the argument INGAME.
        // Only widgets with visibleInContext = BOTH will be called with both arguments while a screen is open.

        // The isScreenOpen == false condition allows all widgets that are in the INGAME list to render,
        // since if there is no GUI open then they would not get rendered a second time from the GUI context.

        // If a screen is open however, then widgets that are visible in both contexts
        // can decide whether or not they want to render below or on top of the screen,
        // by setting the value of the renderAboveScreen field.

        return isScreenOpen == false || (this.renderAboveScreen == (context == InfoOverlay.RenderContext.GUI));
    }

    public void render(ScreenContext ctx)
    {
        if (this.isEnabled())
        {
            int x = this.getX();
            int y = this.getY();
            float z = this.getZLevel();

            this.renderAt(x, y, z, ctx);
        }
    }

    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        boolean scaled = this.scale != 1.0;

        if (scaled)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.scale(this.scale, this.scale, 1);

            x = 0;
            y = 0;
            z = 0f;
        }

        this.renderBackground(x, y, z, ctx);
        y += this.renderName(x, y, z, ctx);
        this.renderContents(x, y, z, ctx);

        if (scaled)
        {
            GlStateManager.popMatrix();
        }

        if (MaLiLibConfigs.Debug.INFO_OVERLAY_DEBUG.getBooleanValue())
        {
            this.renderDebug(x, y, z, false, ctx);
        }
    }

    protected int renderName(int x, int y, float z, ScreenContext ctx)
    {
        if (this.renderName && this.styledName != null)
        {
            int paddingTop = this.padding.getTop();
            y += paddingTop;
            this.renderTextLine(x + this.padding.getLeft(), y, z, 0xFFFFFFFF, true, ctx, this.styledName);

            return this.lineHeight + paddingTop;
        }

        return 0;
    }

    protected void renderBackground(int x, int y, float z, ScreenContext ctx)
    {
        TextRenderSettings settings = this.getTextSettings();

        if (settings.getUseBackground())
        {
            if (settings.getUseOddEvenBackground())
            {
                this.renderOddEvenLineBackgrounds(x, y, z, ctx);
            }
            else
            {
                this.renderSingleBackground(x, y, z, ctx);
            }
        }
    }

    protected void renderSingleBackground(int x, int y, float z, ScreenContext ctx)
    {
        int width = this.getWidth();
        int height = this.getHeight();
        ShapeRenderUtils.renderRectangle(x, y, z, width, height, this.getTextSettings().getBackgroundColor());
    }

    protected void renderOddEvenLineBackgrounds(int x, int y, float z, ScreenContext ctx)
    {
    }

    protected void renderContents(int x, int y, float z, ScreenContext ctx)
    {
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("type", this.getClass().getName());
        obj.addProperty("name", this.getName());
        obj.addProperty("enabled", this.isEnabled());
        obj.addProperty("screen_location", this.getScreenLocation().getName());
        obj.addProperty("scale", this.scale);
        obj.addProperty("render_name", this.renderName);
        obj.addProperty("sort_index", this.getSortIndex());
        obj.addProperty("bg_enabled", this.renderBackground);
        obj.addProperty("bg_color", this.backgroundColor);
        obj.addProperty("border_color", this.borderColor);
        obj.addProperty("z", this.getZLevel());

        obj.add("text_settings", this.getTextSettings().toJson());

        if (this.margin.isEmpty() == false)
        {
            obj.add("margin", this.margin.toJson());
        }

        if (this.padding.isEmpty() == false)
        {
            obj.add("padding", this.padding.toJson());
        }

        if (this.markers.isEmpty() == false)
        {
            JsonArray arr = new JsonArray();

            for (String marker : this.markers)
            {
                arr.add(marker);
            }

            obj.add("markers", arr);
        }

        return obj;
    }

    public void fromJson(JsonObject obj)
    {
        this.enabled = JsonUtils.getBooleanOrDefault(obj, "enabled", true);
        this.renderName = JsonUtils.getBooleanOrDefault(obj, "render_name", false);
        this.scale = JsonUtils.getDoubleOrDefault(obj, "scale", 1.0);
        this.setName(JsonUtils.getStringOrDefault(obj, "name", this.name));
        this.setSortIndex(JsonUtils.getIntegerOrDefault(obj, "sort_index", 100));
        this.renderBackground = JsonUtils.getBooleanOrDefault(obj, "bg_enabled", this.renderBackground);
        this.backgroundColor = JsonUtils.getIntegerOrDefault(obj, "bg_color", this.backgroundColor);
        this.borderColor = JsonUtils.getIntegerOrDefault(obj, "border_color", this.borderColor);
        this.setZLevel(JsonUtils.getFloatOrDefault(obj, "z", this.getZLevel()));

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

        if (JsonUtils.hasObject(obj, "text_settings"))
        {
            this.getTextSettings().fromJson(obj.get("text_settings").getAsJsonObject());
        }

        this.markers.clear();

        if (JsonUtils.hasArray(obj, "markers"))
        {
            JsonArray arr = obj.get("markers").getAsJsonArray();
            int size = arr.size();

            for (int i = 0; i < size; ++i)
            {
                this.markers.add(arr.get(i).getAsString());
            }
        }
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

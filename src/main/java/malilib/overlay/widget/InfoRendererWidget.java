package malilib.overlay.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.renderer.GlStateManager;

import malilib.MaLiLibConfigs;
import malilib.config.value.ScreenLocation;
import malilib.gui.util.BackgroundRenderer;
import malilib.gui.util.BackgroundSettings;
import malilib.gui.util.BorderRenderer;
import malilib.gui.util.BorderSettings;
import malilib.gui.util.GuiUtils;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.list.entry.BaseInfoRendererWidgetEntryWidget;
import malilib.overlay.InfoOverlay;
import malilib.overlay.InfoOverlay.OverlayRenderContext;
import malilib.overlay.InfoWidgetRegistry.InfoWidgetFactory;
import malilib.registry.Registry;
import malilib.render.RenderUtils;
import malilib.render.ShapeRenderUtils;
import malilib.render.text.MultiLineTextRenderSettings;
import malilib.render.text.StyledTextLine;
import malilib.util.data.EdgeInt;
import malilib.util.data.json.JsonUtils;

public abstract class InfoRendererWidget extends BaseOverlayWidget
{
    protected final List<Consumer<ScreenLocation>> locationChangeListeners = new ArrayList<>();
    protected final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    protected final BorderRenderer borderRenderer = new BorderRenderer();
    protected MultiLineTextRenderSettings textSettings = new MultiLineTextRenderSettings();
    protected OverlayRenderContext visibleInContext = InfoOverlay.OverlayRenderContext.BOTH;
    protected ScreenLocation location = ScreenLocation.TOP_LEFT;
    protected IntSupplier viewportHeightSupplier = GuiUtils::getScaledWindowHeight;
    protected IntSupplier viewportWidthSupplier = GuiUtils::getScaledWindowWidth;
    protected String name = "?";
    @Nullable protected StyledTextLine styledName;
    protected boolean renderAboveScreen;
    protected boolean renderName;
    protected boolean shouldSerialize;
    protected boolean valid = true;
    protected double scale = 1.0;
    protected int sortIndex = 100;

    public InfoRendererWidget()
    {
        super();
    }

    /**
     * A widget that is "fixed position" will not get moved on the y direction
     * by other widgets, but instead it will sit on top of the other widgets
     * at the base location of the InfoArea.
     */
    public boolean isFixedPosition()
    {
        return false;
    }

    /**
     * Returns whether this widget should get saved and loaded
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
        return this.name;
    }

    public ScreenLocation getScreenLocation()
    {
        return this.location;
    }

    @Override
    public MultiLineTextRenderSettings getTextSettings()
    {
        return this.textSettings;
    }

    public BackgroundSettings getBackgroundSettings()
    {
        return this.backgroundRenderer.getNormalSettings();
    }

    public BorderSettings getBorderSettings()
    {
        return this.borderRenderer.getNormalSettings();
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
        this.requestUnconditionalReLayout();
    }

    public boolean isValid()
    {
        return this.valid;
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

    public void setVisibleInContext(InfoOverlay.OverlayRenderContext context)
    {
        this.visibleInContext = context;
    }

    public boolean getRenderAboveScreen()
    {
        return this.renderAboveScreen;
    }

    /**
     * Sets whether widgets that are visible in {@code OverlayRenderContext.BOTH} will render
     * below or above the screen.
     */
    public void setRenderAboveScreen(boolean renderAboveScreen)
    {
        this.renderAboveScreen = renderAboveScreen;
    }

    public void setName(String name)
    {
        this.name = name;
        this.styledName = StyledTextLine.parseFirstLine(name);
    }

    /**
     * Called when the widget is removed from the InfoWidgetManager
     */
    public void invalidate()
    {
        this.valid = false;
    }

    public void openEditScreen()
    {
    }

    public void initListEntryWidget(BaseInfoRendererWidgetEntryWidget widget)
    {
    }

    /**
     * Updates this widget's position, if this is an "overlay widget"
     * that is not positioned automatically by the InfoArea.
     */
    protected void updateWidgetPosition()
    {
        if (this.isFixedPosition())
        {
            this.updateFixedPositionWidgetPosition();
        }
    }

    protected void updateFixedPositionWidgetPosition()
    {
        int viewportWidth = this.viewportWidthSupplier.getAsInt();
        int viewportHeight = this.viewportHeightSupplier.getAsInt();
        double scale = this.getScale();
        int width = (int) Math.ceil(this.getWidth() * scale);
        int height = (int) Math.ceil(this.getHeight() * scale);

        ScreenLocation location = this.getScreenLocation();
        EdgeInt margin = this.getMargin();
        int marginX = location.getMarginX(margin);
        int marginY = location.getMarginY(margin);
        int x = location.getStartX(width, viewportWidth, marginX);
        int y = location.getStartY(height, viewportHeight, marginY);

        this.setPosition(x, y);
    }

    /**
     * Called after the widget has been properly initialized and added to the InfoArea
     */
    public void onAdded()
    {
    }

    /**
     * This method determines which "instance" or rather render call of the InfoOverlay
     * should **potentially** handle rendering this widget.
     * If the widget should always be visible and on top, then this should return BOTH.
     * The other two values INGAME and GUI make the widget only render from one or the other,
     * which means that if it's also visible INGAME then it can't be on top when a GUI is open,
     * and if it renders only in a GUI context then it's obviously not rendered when in-game.
     * <br>
     * Note that the other {@link #shouldRenderFromContext(malilib.overlay.InfoOverlay.OverlayRenderContext, boolean)}
     * method is used to check whether the widget will actually render,
     * based on whether a Screen is currently open or not.
     * So basically that method will prevent a widget from actually rendering twice if
     * it's set to be visible in BOTH contexts.
     */
    public boolean isVisibleInContext(InfoOverlay.OverlayRenderContext context)
    {
        return this.visibleInContext == context || this.visibleInContext == InfoOverlay.OverlayRenderContext.BOTH;
    }

    public boolean shouldRenderFromContext(InfoOverlay.OverlayRenderContext context, boolean isScreenOpen)
    {
        // Note that widgets that have visibleInContext = INGAME will never be called with the argument GUI here,
        // or vice versa widgets with visibleInContext = GUI will never be called with the argument INGAME.
        // Only widgets with visibleInContext = BOTH will be called with both arguments while a screen is open.

        // The isScreenOpen == false condition allows all widgets that are in the INGAME list to render,
        // since if there is no GUI open then they would not get rendered a second time from the GUI context.

        // If a screen is open however, then widgets that are visible in both contexts
        // can decide whether they want to render below or on top of the screen,
        // by setting the value of the renderAboveScreen field.

        return isScreenOpen == false || (this.renderAboveScreen == (context == InfoOverlay.OverlayRenderContext.GUI));
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        if (this.isEnabled() == false)
        {
            return;
        }

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.setupBlend();

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

        this.renderWidgetBackground(x, y, z, ctx);
        this.renderWidgetBorder(x, y, z, ctx);
        this.renderTextBackground(x, y, z, ctx);
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
            x += this.padding.getLeft();
            y += paddingTop;
            this.renderTextLine(x, y, z, 0xFFFFFFFF, true, this.styledName, ctx);

            return this.getLineHeight() + paddingTop;
        }

        return 0;
    }

    protected void renderWidgetBackground(int x, int y, float z, ScreenContext ctx)
    {
        BackgroundSettings settings = this.backgroundRenderer.getNormalSettings();
        this.backgroundRenderer.renderBackgroundIfEnabled(x, y, z, this.getWidth(), this.getHeight(), settings, ctx);
    }

    protected void renderWidgetBorder(int x, int y, float z, ScreenContext ctx)
    {
        BorderSettings settings = this.borderRenderer.getNormalSettings();
        this.borderRenderer.renderBorderIfEnabled(x, y, z, this.getWidth(), this.getHeight(), settings, ctx);
    }

    protected void renderTextBackground(int x, int y, float z, ScreenContext ctx)
    {
        MultiLineTextRenderSettings settings = this.getTextSettings();

        if (settings.getBackgroundEnabled())
        {
            if (settings.getOddEvenBackgroundEnabled())
            {
                this.renderOddEvenTextLineBackgrounds(x, y, z, ctx);
            }
            else
            {
                this.renderSingleTextBackground(x, y, z, ctx);
            }
        }
    }

    protected void renderSingleTextBackground(int x, int y, float z, ScreenContext ctx)
    {
        int width = this.getWidth();
        int height = this.getHeight();
        ShapeRenderUtils.renderRectangle(x, y, z, width, height, this.getTextSettings().getBackgroundColor());
    }

    protected void renderOddEvenTextLineBackgrounds(int x, int y, float z, ScreenContext ctx)
    {
    }

    protected void renderContents(int x, int y, float z, ScreenContext ctx)
    {
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();

        obj.addProperty("name", this.getName());
        obj.addProperty("screen_location", this.getScreenLocation().getName());

        if (this.renderName) { obj.addProperty("render_name", this.renderName); }
        if (this.getRenderAboveScreen()) { obj.addProperty("above_screen", this.getRenderAboveScreen()); }
        JsonUtils.addIfNotEqual(obj, "z", this.getZ(), 0.0F);
        JsonUtils.addIfNotEqual(obj, "scale", this.scale, 1.0);
        JsonUtils.addIfNotEqual(obj, "sort_index", this.sortIndex, 100);
        this.getTextSettings().writeToJsonIfModified(obj, "text_settings");
        this.backgroundRenderer.getNormalSettings().writeToJsonIfModified(obj, "bg");
        this.borderRenderer.getNormalSettings().writeToJsonIfModified(obj, "border");
        this.markerManager.writeToJsonIfHasData(obj, "markers");

        return obj;
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);

        this.renderName = JsonUtils.getBooleanOrDefault(obj, "render_name", false);
        this.setName(JsonUtils.getStringOrDefault(obj, "name", this.name));
        this.setRenderAboveScreen(JsonUtils.getBooleanOrDefault(obj, "above_screen", false));
        this.scale = JsonUtils.getDoubleOrDefault(obj, "scale", 1.0);
        this.setSortIndex(JsonUtils.getIntegerOrDefault(obj, "sort_index", 100));
        this.setZ(JsonUtils.getFloatOrDefault(obj, "z", this.getZ()));
        JsonUtils.getObjectIfExists(obj, "text_settings", this.getTextSettings()::fromJson);
        JsonUtils.getObjectIfExists(obj, "bg", this.backgroundRenderer.getNormalSettings()::fromJson);
        JsonUtils.getObjectIfExists(obj, "border", this.borderRenderer.getNormalSettings()::fromJson);
        JsonUtils.getArrayIfExists(obj, "markers", this.getMarkerManager()::fromJson);

        if (JsonUtils.hasString(obj, "screen_location"))
        {
            ScreenLocation location = ScreenLocation.findValueByName(obj.get("screen_location").getAsString(), ScreenLocation.VALUES);
            this.setLocation(location);
        }
    }

    @Nullable
    public static InfoRendererWidget createFromJson(JsonElement el)
    {
        if (el.isJsonObject() == false)
        {
            return null;
        }

        JsonObject obj = el.getAsJsonObject();

        if (JsonUtils.hasString(obj, "type"))
        {
            String type = obj.get("type").getAsString();
            InfoWidgetFactory factory = Registry.INFO_WIDGET.getWidgetFactory(type);

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

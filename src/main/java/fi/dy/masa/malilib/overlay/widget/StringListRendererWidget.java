package fi.dy.masa.malilib.overlay.widget;

import java.util.List;
import java.util.function.Supplier;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.text.OrderedStringListFactory;
import fi.dy.masa.malilib.render.text.StringListRenderer;

public class StringListRendererWidget extends InfoRendererWidget
{
    protected final OrderedStringListFactory stringListFactory = new OrderedStringListFactory();
    protected final StringListRenderer stringListRenderer = new StringListRenderer();
    protected boolean dirty;

    public StringListRendererWidget()
    {
        super();

        this.textSettings = this.stringListRenderer.getNormalTextSettings();
        this.padding.setChangeListener(this::onPaddingChanged);
        this.padding.setAll(1, 2, 0, 2);
        this.shouldSerialize = true;
    }

    /**
     * Adds the provided lines, by using the provided key.
     * The key can be used to remove just these lines later.
     */
    public void setLines(String key, List<String> lines, int priority)
    {
        this.stringListFactory.setStringListProvider(key, () -> lines, priority);
        this.markDirty();
    }

    /**
     * Adds the provided line supplier, by using the provided key.
     * The key can be used to remove just these lines later.
     * The priority is the sort order of all the line suppliers,
     * they are sorted by their numerical priority (so smaller priority value comes first).
     */
    public void setStringListProvider(String key, Supplier<List<String>> supplier, int priority)
    {
        this.stringListFactory.setStringListProvider(key, supplier, priority);
        this.markDirty();
    }

    public void removeStringListProvider(String key)
    {
        this.stringListFactory.removeStringListProvider(key);
        this.markDirty();
    }

    @Override
    public void setLocation(ScreenLocation location)
    {
        super.setLocation(location);
        this.stringListRenderer.setHorizontalAlignment(this.location.horizontalLocation);
        this.stringListRenderer.setVerticalAlignment(this.location.verticalLocation);
    }

    protected void onPaddingChanged()
    {
        this.stringListRenderer.getPadding().setFrom(this.padding);

        if (this.renderName && this.styledName != null)
        {
            // Compensate/remove the extra padding that the StringListRenderer would
            // add if the name row was already rendered with the top padding
            this.stringListRenderer.getPadding().setTop(0);
        }

        this.requestUnconditionalReLayout();
    }

    @Override
    public void toggleRenderName()
    {
        super.toggleRenderName();
        this.onPaddingChanged();
    }

    @Override
    public void setLineHeight(int lineHeight)
    {
        super.setLineHeight(lineHeight);

        this.stringListRenderer.setLineHeight(lineHeight);
    }

    /**
     * Call this method to indicate that the string list needs to be re-built.
     */
    public void markDirty()
    {
        //System.out.print("StringListRendererWidget#markDirty()\n");
        this.dirty = true;
    }

    protected void updateLines()
    {
        boolean isEnabled = this.isEnabled();

        if (isEnabled)
        {
            this.stringListFactory.markDirty();
            this.stringListRenderer.setStyledTextLines(this.stringListFactory.getStyledLines());

            this.requestConditionalReLayout();
        }
    }

    @Override
    public void updateSize()
    {
        int width = this.stringListRenderer.getTotalRenderWidth();
        int height = this.stringListRenderer.getTotalRenderHeight() + (this.renderName ? this.lineHeight : 0);

        this.setWidth(width);
        this.setHeight(height);
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);
        this.updateSize();
    }

    @Override
    public void updateState()
    {
        if (this.dirty)
        {
            this.updateLines();
            this.dirty = false;
        }

        super.updateState();
    }

    @Override
    protected void renderSingleBackground(int x, int y, float z)
    {
        // Render the background for the title row
        if (this.getTextSettings().getUseBackground() && this.renderName && this.styledName != null)
        {
            int width = this.getWidth();
            int height = this.lineHeight + this.padding.getTop();
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, this.getTextSettings().getBackgroundColor());
        }
    }

    @Override
    protected void renderOddEvenLineBackgrounds(int x, int y, float z)
    {
        // Render the background for the title row
        if (this.getTextSettings().getUseOddEvenBackground() && this.renderName && this.styledName != null)
        {
            int width = this.getWidth();
            int height = this.lineHeight + this.padding.getTop();
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, this.getTextSettings().getBackgroundColor());
        }
    }

    @Override
    protected void renderContents(int x, int y, float z)
    {
        this.stringListRenderer.renderAt(x, y, z, false);
    }
}

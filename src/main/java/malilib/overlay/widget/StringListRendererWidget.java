package malilib.overlay.widget;

import java.util.List;
import java.util.function.Supplier;
import com.google.gson.JsonObject;

import malilib.MaLiLibReference;
import malilib.config.value.ScreenLocation;
import malilib.gui.BaseScreen;
import malilib.gui.edit.overlay.StringListRendererWidgetEditScreen;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.list.entry.BaseInfoRendererWidgetEntryWidget;
import malilib.render.ShapeRenderUtils;
import malilib.render.text.OrderedStringListFactory;
import malilib.render.text.StringListRenderer;
import malilib.util.data.EdgeInt;

public class StringListRendererWidget extends InfoRendererWidget
{
    protected final OrderedStringListFactory stringListFactory;
    protected final StringListRenderer stringListRenderer = new StringListRenderer();
    protected boolean stringListModified;

    public StringListRendererWidget()
    {
        super();

        this.stringListFactory = new OrderedStringListFactory(8192);
        this.textSettings = this.stringListRenderer.getNormalTextSettings();
        this.padding.setChangeListener(this::onPaddingChanged);
        this.padding.setAll(1, 2, 0, 2);
        this.shouldSerialize = true;
    }

    @Override
    public String getWidgetTypeId()
    {
        return MaLiLibReference.MOD_ID + ":string_list_renderer";
    }

    @Override
    public void initListEntryWidget(BaseInfoRendererWidgetEntryWidget widget)
    {
        widget.setCanConfigure(true);
        widget.setCanRemove(true);
    }

    public void setMaxRenderWidth(int maxRenderWidth)
    {
        this.stringListFactory.setMaxTextRenderWidth(maxRenderWidth);
    }

    /**
     * Adds the provided lines, by using the provided key.
     * The key can be used to remove just these lines later.
     */
    public void setLines(String key, List<String> lines, int priority)
    {
        this.stringListFactory.setStringListProvider(key, () -> lines, priority);
        this.notifyStringListChanged();
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
        this.notifyStringListChanged();
    }

    public void removeStringListProvider(String key)
    {
        this.stringListFactory.removeTextLineProvider(key);
        this.notifyStringListChanged();
    }

    @Override
    public void setLocation(ScreenLocation location)
    {
        super.setLocation(location);
        this.stringListRenderer.setHorizontalAlignment(this.getScreenLocation().horizontalLocation);
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

    @Override
    public void openEditScreen()
    {
        StringListRendererWidgetEditScreen screen = new StringListRendererWidgetEditScreen(this);
        BaseScreen.openPopupScreenWithCurrentScreenAsParent(screen, false);
        screen.setupAsPopup();
    }

    /**
     * Call this method to indicate that the string list needs to be re-built.
     */
    public void notifyStringListChanged()
    {
        //System.out.print("StringListRendererWidget#markDirty()\n");
        this.stringListModified = true;
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
        int lineHeight = this.getLineHeight();
        int height = this.stringListRenderer.getTotalRenderHeight() + (this.renderName ? lineHeight : 0);

        EdgeInt padding = this.padding;
        width += padding.getHorizontalTotal();
        height += padding.getVerticalTotal();

        this.setSizeNoUpdate(width, height);
    }

    @Override
    public void updateState()
    {
        if (this.stringListModified)
        {
            this.updateLines();
            this.stringListModified = false;
        }

        super.updateState();
    }

    @Override
    protected void renderSingleTextBackground(int x, int y, float z, ScreenContext ctx)
    {
        // Render the background for the title row
        if (this.getTextSettings().getBackgroundEnabled() && this.renderName && this.styledName != null)
        {
            int width = this.getWidth();
            int height = this.getLineHeight() + this.padding.getTop();
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, this.getTextSettings().getBackgroundColor(), ctx);
        }
    }

    @Override
    protected void renderOddEvenTextLineBackgrounds(int x, int y, float z, ScreenContext ctx)
    {
        // Render the background for the title row
        if (this.getTextSettings().getOddEvenBackgroundEnabled() && this.renderName && this.styledName != null)
        {
            int width = this.getWidth();
            int height = this.getLineHeight() + this.padding.getTop();
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, this.getTextSettings().getBackgroundColor(), ctx);
        }
    }

    @Override
    protected void renderContents(int x, int y, float z, ScreenContext ctx)
    {
        this.stringListRenderer.renderAt(x, y, z, false, ctx);
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);
        this.updateSize();
    }
}

package fi.dy.masa.malilib.message;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.RenderUtils;

public class StringListRendererWidget extends InfoRendererWidget
{
    protected final OrderedStringListFactory stringListFactory = new OrderedStringListFactory();
    protected final StringListRenderer stringListRenderer = new StringListRenderer();
    @Nullable protected EventListener changeListener;
    protected boolean dirty;

    public StringListRendererWidget()
    {
        this.stringListRenderer.setNormalTextColor(0xFFFFFFFF);
        this.stringListRenderer.setHoverTextColor(0xFFFFFFFF);
    }

    /**
     * Adds the provided lines, by using the provided key.
     * The key can be used to remove just these lines later.
     */
    public void setLines(String key, List<String> lines, int priority)
    {
        this.stringListFactory.setStringListProvider(key, () -> lines, priority);
        this.dirty = true;
    }

    /**
     * Adds the provided line supplier, by using the provided key.
     * The key can be used to remove just these lines later.
     * The priority is the sort order of all the line suppliers,
     * they are sorted by their numerical priority (so smaller priority value comes first).
     */
    public void setStringListProvider(String key, Supplier<List<String>> supplier, int priority)
    {
        this.stringListFactory.setStringListProvider(key, (lines) -> supplier.get(), priority);
        this.dirty = true;
    }

    public void removeStringListProvider(String key)
    {
        this.stringListFactory.removeStringListProvider(key);
        this.dirty = true;
    }

    public void setChangeListener(@Nullable EventListener changeListener)
    {
        this.changeListener = changeListener;
    }

    @Override
    public void setLocation(ScreenLocation location)
    {
        super.setLocation(location);
        this.stringListRenderer.setHorizontalAlignment(this.location.horizontalLocation);
    }

    /**
     * Call this method to indicate that the string list needs to be re-built.
     */
    public void markDirty()
    {
        this.stringListFactory.markDirty();
        this.dirty = true;
    }

    protected void updateLines()
    {
        boolean wasEnabled = this.isEnabled();
        this.setEnabled(this.stringListFactory.isEmpty() == false);
        boolean isEnabled = this.isEnabled();

        if (isEnabled)
        {
            this.stringListRenderer.setText(this.stringListFactory.getLines());
            this.setWidth(this.stringListRenderer.getTotalTextWidth());
            this.setHeight(this.stringListRenderer.getClampedHeight());
        }

        if (isEnabled || wasEnabled)
        {
            // TODO - only update when something changes
            this.updateContainerLayout();
        }

        if (this.changeListener != null)
        {
            this.changeListener.onEvent();
        }
    }

    @Override
    public void updateState()
    {
        if (this.dirty)
        {
            this.updateLines();
            this.dirty = false;
        }
    }

    @Override
    public void renderAt(int x, int y, float z)
    {
        this.stringListRenderer.renderAt(x, y, z, false);
        RenderUtils.color(1f, 1f, 1f, 1f);
    }
}

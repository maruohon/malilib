package fi.dy.masa.malilib.message;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.text.TextRenderSettings;

public class StringListRendererWidget extends InfoRendererWidget
{
    protected final OrderedStringListFactory stringListFactory = new OrderedStringListFactory();
    protected final StringListRenderer stringListRenderer = new StringListRenderer();
    protected double scale = 1.0;
    protected boolean dirty;

    public StringListRendererWidget()
    {
        this.stringListRenderer.setNormalTextColor(0xFFFFFFFF);
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

    @Override
    public void setLocation(ScreenLocation location)
    {
        super.setLocation(location);
        this.stringListRenderer.setHorizontalAlignment(this.location.horizontalLocation);
    }

    public void setScale(double scale)
    {
        this.scale = scale;
        this.markDirty();
    }

    public void setTextSettings(TextRenderSettings settings)
    {
        this.stringListRenderer.setNormalTextSettings(settings);
    }

    /**
     * Call this method to indicate that the string list needs to be re-built.
     */
    public void markDirty()
    {
        this.dirty = true;
    }

    protected void updateLines()
    {
        boolean wasEnabled = this.isEnabled();
        this.setEnabled(this.stringListFactory.isEmpty() == false);
        boolean isEnabled = this.isEnabled();

        if (isEnabled)
        {
            this.stringListFactory.markDirty();
            this.stringListRenderer.setText(this.stringListFactory.getLines());
            int width = (int) Math.ceil(this.stringListRenderer.getTotalTextWidth() * this.scale);
            int height = (int) Math.ceil(this.stringListRenderer.getClampedHeight() * this.scale);
            this.setWidth(width);
            this.setHeight(height);
        }

        if (isEnabled || wasEnabled)
        {
            this.updateContainerLayout();
        }
    }

    @Override
    public void updateState(Minecraft mc)
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
        if (this.scale != 1.0)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.scale(this.scale, this.scale, 1);
            this.stringListRenderer.renderAt(0, 0, 0, false);
            GlStateManager.popMatrix();
        }
        else
        {
            this.stringListRenderer.renderAt(x, y, z, false);
        }

        RenderUtils.color(1f, 1f, 1f, 1f);
    }
}

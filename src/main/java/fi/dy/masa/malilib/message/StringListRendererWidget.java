package fi.dy.masa.malilib.message;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.gui.position.VerticalAlignment;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.text.StringListRenderer;
import fi.dy.masa.malilib.render.text.TextRenderSettings;

public class StringListRendererWidget extends InfoRendererWidget
{
    protected final OrderedStringListFactory stringListFactory = new OrderedStringListFactory();
    protected final StringListRenderer stringListRenderer = new StringListRenderer();
    protected double scale = 1.0;
    protected boolean dirty;
    protected int stringListPosX;
    protected int stringListPosY;

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

    public void setScale(double scale)
    {
        this.scale = scale;
        this.markDirty();
    }

    public void setTextSettings(TextRenderSettings settings)
    {
        this.stringListRenderer.setNormalTextSettings(settings);
    }

    @Override
    public void updateWidth()
    {
        int width = (int) Math.ceil(this.stringListRenderer.getTotalRenderWidth() * this.scale);
        this.setWidth(width + this.padding.getHorizontalTotalPadding());
    }

    @Override
    public void updateHeight()
    {
        int height = (int) Math.ceil(this.stringListRenderer.getTotalRenderHeight() * this.scale);
        this.setHeight(height + this.padding.getVerticalTotalPadding());
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
        this.setEnabled(this.stringListFactory.hasNoProviders() == false);
        boolean isEnabled = this.isEnabled();

        if (isEnabled)
        {
            this.stringListFactory.markDirty();
            this.stringListRenderer.setStyledTextLines(this.stringListFactory.getStyledLines());

            this.updateSize();
        }

        if (isEnabled || wasEnabled)
        {
            this.notifyContainerOfChanges();
        }
    }

    @Override
    protected void onPositionChanged(int oldX, int oldY)
    {
        super.onPositionChanged(oldX, oldY);
        this.updateStringListRendererPosition();
    }

    protected void updateStringListRendererPosition()
    {
        HorizontalAlignment ha = this.location.horizontalLocation;
        VerticalAlignment va = this.location.verticalLocation;

        if (ha == HorizontalAlignment.LEFT)
        {
            this.stringListPosX = this.getX() + this.padding.getLeft();
        }
        else if (ha == HorizontalAlignment.RIGHT)
        {
            this.stringListPosX = this.getX() + this.getWidth() - 1 - this.padding.getRight();
        }
        else if (ha == HorizontalAlignment.CENTER)
        {
            this.stringListPosX = this.getX() + this.getWidth() / 2;
        }

        if (va == VerticalAlignment.TOP)
        {
            this.stringListPosY = this.getY() + this.padding.getTop();
        }
        else if (va == VerticalAlignment.BOTTOM)
        {
            this.stringListPosY = this.getY() + this.getHeight() - this.padding.getBottom();
        }
        else if (va == VerticalAlignment.CENTER)
        {
            this.stringListPosY = this.getY() + this.getHeight() / 2;
        }
    }

    @Override
    protected int getContentStartX()
    {
        return this.stringListPosX;
    }

    @Override
    protected int getContentStartY()
    {
        return this.stringListPosY;
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

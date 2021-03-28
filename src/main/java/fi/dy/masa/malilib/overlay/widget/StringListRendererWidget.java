package fi.dy.masa.malilib.overlay.widget;

import java.util.List;
import java.util.function.Supplier;
import org.lwjgl.opengl.GL11;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.gui.position.VerticalAlignment;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.text.OrderedStringListFactory;
import fi.dy.masa.malilib.render.text.StringListRenderer;
import fi.dy.masa.malilib.util.JsonUtils;

public class StringListRendererWidget extends InfoRendererWidget
{
    protected final OrderedStringListFactory stringListFactory = new OrderedStringListFactory();
    protected final StringListRenderer stringListRenderer = new StringListRenderer();
    protected double textScale = 1.0;
    protected boolean dirty;
    protected int textColor = 0xFFFFFFFF;
    protected int stringListPosX;
    protected int stringListPosY;

    public StringListRendererWidget()
    {
        super();
        this.shouldSerialize = true;
        this.stringListRenderer.setNormalTextColor(this.textColor);
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

    public double getTextScale()
    {
        return this.textScale;
    }

    public void setTextScale(double scale)
    {
        this.textScale = scale;
        this.markDirty();
    }

    public int getTextColor()
    {
        return this.textColor;
    }

    public void setTextColor(int color)
    {
        this.textColor = color;
        this.stringListRenderer.setNormalTextColor(color);
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
        //System.out.print("StringListRendererWidget#updateLines()\n");
        //boolean wasEnabled = this.isEnabled();
        //this.setEnabled(this.stringListFactory.hasNoProviders() == false);
        boolean isEnabled = this.isEnabled();

        if (isEnabled)
        {
            this.stringListFactory.markDirty();
            this.stringListRenderer.setStyledTextLines(this.stringListFactory.getStyledLines());

            this.requestConditionalReLayout();
        }

        /*
        if (isEnabled || wasEnabled)
        {
            this.notifyContainerOfChanges(false);
        }
        */
    }

    @Override
    protected void onPositionChanged(int oldX, int oldY)
    {
        //System.out.print("StringListRendererWidget#onPositionChanged()\n");
        super.onPositionChanged(oldX, oldY);
        this.updateStringListRendererPosition();
    }

    @Override
    public void updateSize()
    {
        int height = this.renderName ? this.lineHeight : 0;
        int width = (int) Math.ceil(this.stringListRenderer.getTotalRenderWidth() * this.textScale) + 10 + this.padding.getHorizontalTotal();
        height += (int) Math.ceil(this.stringListRenderer.getTotalRenderHeight() * this.textScale) + this.padding.getVerticalTotal();

        this.setWidth(width);
        this.setHeight(height);
    }

    protected void updateStringListRendererPosition()
    {
        //System.out.print("StringListRendererWidget#updateStringListRendererPosition()\n");
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
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();

        obj.addProperty("text_scale", this.textScale);
        obj.addProperty("text_color", this.textColor);

        return obj;
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);

        this.textScale = JsonUtils.getDoubleOrDefault(obj, "text_scale", 1.0);
        this.setTextColor(JsonUtils.getIntegerOrDefault(obj, "text_color", 0xFFFFFFFF));

        this.updateSize();
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
    protected void renderOddEvenLineBackgrounds(int x, int y, float z)
    {
        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR, false);

        int width = this.getWidth();
        int size = this.stringListRenderer.getTotalLineCount();
        int i = 0;

        if (this.renderName && this.styledName != null)
        {
            int height = this.lineHeight + this.padding.getTop();

            if (size > 0)
            {
                height += this.lineHeight;
            }
            else
            {
                height += this.padding.getBottom();
            }

            ShapeRenderUtils.renderRectangle(x, y, z, width, height, this.backgroundColor, buffer);
            y += height;
            i = 1;
        }

        for (; i < size; ++i)
        {
            int height = this.lineHeight;

            if (i == 0)
            {
                height += this.padding.getTop();
            }

            if (i == size - 1)
            {
                height += this.padding.getBottom();
            }

            int color = (i & 0x1) != 0 ? this.backgroundColorOdd : this.backgroundColor;
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, color, buffer);
            y += height;
        }

        RenderUtils.drawBuffer();
    }

    @Override
    protected void renderContents(int x, int y, float z)
    {
        x += this.padding.getLeft();
        y += this.padding.getTop();

        if (this.textScale != 1.0)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.scale(this.textScale, this.textScale, 1);

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

package fi.dy.masa.malilib.render.overlay;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.data.ModInfo;
import fi.dy.masa.malilib.util.data.json.JsonUtils;

public abstract class BaseOverlayRenderer
{
    protected static final BufferBuilder BUFFER_1 = new BufferBuilder(2097152);
    protected static final BufferBuilder BUFFER_2 = new BufferBuilder(2097152);

    protected final List<BaseRenderObject> renderObjects = new ArrayList<>();
    protected BlockPos lastUpdatePos = BlockPos.ORIGIN;
    private Vec3d updateCameraPos = Vec3d.ZERO;
    protected boolean needsUpdate;
    protected boolean renderThrough;
    protected float lineWidth = 1f;

    public void setNeedsUpdate()
    {
        this.needsUpdate = true;
    }

    public void setLastUpdatePos(BlockPos lastUpdatePos)
    {
        this.lastUpdatePos = lastUpdatePos;
    }

    /**
     * @return the camera position where the renderer was last updated
     */
    public final Vec3d getUpdatePosition()
    {
        return this.updateCameraPos;
    }

    /**
     * Sets the camera position where the renderer was last updated
     */
    public final void setUpdatePosition(Vec3d cameraPosition)
    {
        this.updateCameraPos = cameraPosition;
    }

    public void setRenderThrough(boolean renderThrough)
    {
        this.renderThrough = renderThrough;
    }

    /**
     * Optional code to run when the overlay is enabled.
     * Note that both implementation and calling this method are left for the
     * mod to handle, this is just a "convenience place to run the code related to the overlay"
     */
    public void onEnabled()
    {
    }

    public boolean isEnabled()
    {
        return true;
    }

    public abstract ModInfo getModInfo();

    @Nullable
    public abstract Path getSaveFile(boolean isDimensionChangeOnly);

    /**
     * Should this renderer draw anything at the moment, ie. is it enabled for example
     */
    public abstract boolean shouldRender(MinecraftClient mc);

    /**
     * @return true, if this renderer should get re-drawn/updated
     */
    public abstract boolean needsUpdate(Entity entity, MinecraftClient mc);

    /**
     * Update the renderer (draw again to the buffers). This method is called
     * when {@link BaseOverlayRenderer#needsUpdate(Entity, Minecraft)} returns true.
     * 
     * @param cameraPos The position of the camera when the method is called.
     *                  The camera position should be subtracted from any world coordinates for the vertex positions.
     *                  During the draw() call the MatrixStack will be translated by the camera position,
     *                  minus the difference between the camera position during the update() call,
     *                  and the camera position during the draw() call.
     * @param entity The current camera entity
     */
    public abstract void update(Vec3d cameraPos, Entity entity, MinecraftClient mc);

    protected void preRender()
    {
        GlStateManager.glLineWidth(this.lineWidth);

        if (this.renderThrough)
        {
            GlStateManager.disableDepth();
            //GlStateManager.depthMask(false);
        }
    }

    protected void postRender()
    {
        if (this.renderThrough)
        {
            GlStateManager.enableDepth();
            //GlStateManager.depthMask(true);
        }
    }

    /**
     * Draws all the buffers to screen
     */
    public void draw()
    {
        this.preRender();

        for (BaseRenderObject obj : this.renderObjects)
        {
            obj.draw();
        }

        this.postRender();
    }

    /**
     * Allocates the OpenGL resources according to the current Video settings
     */
    public void allocateGlResources()
    {
        this.allocateBuffer(GL11.GL_QUADS);
        this.allocateBuffer(GL11.GL_LINES);
    }

    /**
     * Frees the allocated OpenGL buffers etc.
     */
    public void deleteGlResources()
    {
        for (BaseRenderObject obj : this.renderObjects)
        {
            obj.deleteGlResources();
        }

        this.renderObjects.clear();
    }

    /**
     * Allocates a new VBO or display list, adds it to the list, and returns it
     */
    protected BaseRenderObject allocateBuffer(int glMode)
    {
        return this.allocateBuffer(glMode, DefaultVertexFormats.POSITION_COLOR, VboRenderObject::setupArrayPointersPosColor);
    }

    /**
     * Allocates a new VBO or display list, adds it to the list, and returns it
     * @param func the function to set up the array pointers according to the used vertex format
     */
    protected BaseRenderObject allocateBuffer(int glMode, VertexFormat vertexFormat, EventListener func)
    {
        BaseRenderObject obj;

        if (OpenGlHelper.useVbo())
        {
            obj = new VboRenderObject(glMode, vertexFormat, func);
        }
        else
        {
            obj = new DisplayListRenderObject(glMode, vertexFormat);
        }

        this.renderObjects.add(obj);

        return obj;
    }

    public String getSaveId()
    {
        return this.getClass().getName();
    }

    @Nullable
    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("line_width", this.lineWidth);
        obj.addProperty("render_through", this.renderThrough);
        return obj;
    }

    public void fromJson(JsonObject obj)
    {
        this.lineWidth = JsonUtils.getFloatOrDefault(obj, "line_width", 1f);
        this.renderThrough = JsonUtils.getBooleanOrDefault(obj, "render_through", false);
    }
}

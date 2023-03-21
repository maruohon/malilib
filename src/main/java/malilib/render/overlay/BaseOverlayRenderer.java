package malilib.render.overlay;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import malilib.util.data.ModInfo;
import malilib.util.data.json.JsonUtils;

public abstract class BaseOverlayRenderer
{
    protected static final BufferBuilder BUFFER_1 = new BufferBuilder(2097152);
    protected static final BufferBuilder BUFFER_2 = new BufferBuilder(2097152);

    protected final List<BaseRenderObject> renderObjects = new ArrayList<>();
    protected BlockPos lastUpdatePos = BlockPos.ZERO;
    private Vec3 updateCameraPos = Vec3.ZERO;
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
    public final Vec3 getUpdatePosition()
    {
        return this.updateCameraPos;
    }

    /**
     * Sets the camera position where the renderer was last updated
     */
    public final void setUpdatePosition(Vec3 cameraPosition)
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
     * Should this renderer draw anything at the moment, i.e. is it enabled for example
     */
    public abstract boolean shouldRender();

    /**
     * @return true, if this renderer should get re-drawn/updated
     */
    public abstract boolean needsUpdate(Entity entity);

    /**
     * Update the renderer (draw again to the buffers). This method is called
     * when {@link BaseOverlayRenderer#needsUpdate(Entity)} returns true.
     * 
     * @param cameraPos The position of the camera when the method is called.
     *                  The camera position should be subtracted from any world coordinates for the vertex positions.
     *                  During the draw() call the MatrixStack will be translated by the camera position,
     *                  minus the difference between the camera position during the update() call,
     *                  and the camera position during the draw() call.
     * @param entity The current camera entity
     */
    public abstract void update(Vec3 cameraPos, Entity entity);

    protected void preRender()
    {
        RenderSystem.lineWidth(this.lineWidth);

        if (this.renderThrough)
        {
            RenderSystem.disableDepthTest();
            //GlStateManager.depthMask(false);
        }
    }

    protected void postRender()
    {
        if (this.renderThrough)
        {
            RenderSystem.enableDepthTest();
            //GlStateManager.depthMask(true);
        }
    }

    /**
     * Draws all the buffers to screen
     */
    public void draw(PoseStack matrixStack, Matrix4f projMatrix)
    {
        this.preRender();

        for (BaseRenderObject obj : this.renderObjects)
        {
            obj.draw(matrixStack, projMatrix);
        }

        this.postRender();
    }

    /**
     * Allocates the OpenGL resources according to the current Video settings
     */
    public void allocateGlResources()
    {
        this.allocateBuffer(VertexFormat.Mode.QUADS);
        this.allocateBuffer(VertexFormat.Mode.DEBUG_LINES);
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
    protected BaseRenderObject allocateBuffer(VertexFormat.Mode drawMode)
    {
        return this.allocateBuffer(drawMode, GameRenderer::getPositionColorShader, false);
    }

    /**
     * Allocates a new VBO or display list, adds it to the list, and returns it
     */
    protected BaseRenderObject allocateBuffer(VertexFormat.Mode drawMode, Supplier<ShaderInstance> shader, boolean hasTexture)
    {
        BaseRenderObject obj = new VboRenderObject(drawMode, shader, hasTexture);
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

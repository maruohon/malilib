package fi.dy.masa.malilib.render.overlay;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ArrayListMultimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import fi.dy.masa.malilib.util.BackupUtils;
import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.data.json.JsonUtils;

public class OverlayRendererContainer
{
    public static final OverlayRendererContainer INSTANCE = new OverlayRendererContainer();

    protected final List<BaseOverlayRenderer> renderers = new ArrayList<>();
    protected final List<BaseOverlayRenderer> enabledRenderers = new ArrayList<>();
    protected boolean resourcesAllocated;
    protected boolean useVbo;
    protected int countActive;

    private boolean canRender;
    private boolean enabledRenderersNeedUpdate;
    private long loginTime;

    public void addRenderer(BaseOverlayRenderer renderer)
    {
        if (this.resourcesAllocated)
        {
            renderer.allocateGlResources();
        }

        this.renderers.add(renderer);
        this.setEnabledRenderersNeedUpdate();
    }

    public void removeRenderer(BaseOverlayRenderer renderer)
    {
        this.renderers.remove(renderer);
        this.setEnabledRenderersNeedUpdate();

        if (this.resourcesAllocated)
        {
            renderer.deleteGlResources();
        }
    }

    public void setEnabledRenderersNeedUpdate()
    {
        this.enabledRenderersNeedUpdate = true;
    }

    protected Vec3d getCameraPos(Entity cameraEntity, float partialTicks)
    {
        double x = cameraEntity.lastTickPosX + (cameraEntity.posX - cameraEntity.lastTickPosX) * partialTicks;
        double y = cameraEntity.lastTickPosY + (cameraEntity.posY - cameraEntity.lastTickPosY) * partialTicks;
        double z = cameraEntity.lastTickPosZ + (cameraEntity.posZ - cameraEntity.lastTickPosZ) * partialTicks;

        return new Vec3d(x, y, z);
    }

    public void resetRenderTimeout()
    {
        this.canRender = false;
        this.loginTime = System.nanoTime();
    }

    protected void updateEnabledRenderersList()
    {
        this.enabledRenderers.clear();

        for (BaseOverlayRenderer renderer : this.renderers)
        {
            if (renderer.isEnabled())
            {
                this.enabledRenderers.add(renderer);
            }
        }

        this.enabledRenderersNeedUpdate = false;
    }

    public void render(MinecraftClient mc, float partialTicks)
    {
        Entity cameraEntity = EntityUtils.getCameraEntity();

        if (cameraEntity == null)
        {
            return;
        }

        if (this.canRender == false)
        {
            // Don't render before the player has been placed in the actual proper position,
            // otherwise some of the renderers mess up.
            // The magic 8.5, 65, 8.5 comes from the ClientWorld constructor
            if (System.nanoTime() - this.loginTime >= 5000000000L ||
                cameraEntity.posX != 8.5 || cameraEntity.posY != 65 || cameraEntity.posZ != 8.5)
            {
                this.canRender = true;
            }
            else
            {
                return;
            }
        }

        Vec3d cameraPos = this.getCameraPos(cameraEntity, partialTicks);

        mc.profiler.startSection("update");
        this.update(cameraPos, cameraEntity, mc);
        mc.profiler.endSection();

        mc.profiler.startSection("draw");
        this.draw(cameraPos, mc);
        mc.profiler.endSection();
    }

    protected void update(Vec3d cameraPos, Entity entity, MinecraftClient mc)
    {
        if (this.enabledRenderersNeedUpdate)
        {
            this.updateEnabledRenderersList();
        }

        this.checkVideoSettings();
        this.countActive = 0;

        for (BaseOverlayRenderer renderer : this.enabledRenderers)
        {
            mc.profiler.func_194340_a(() -> renderer.getClass().getName());

            if (renderer.shouldRender(mc))
            {
                if (renderer.needsUpdate(entity, mc))
                {
                    renderer.setLastUpdatePos(new BlockPos(entity));
                    renderer.setUpdatePosition(cameraPos);
                    renderer.update(cameraPos, entity, mc);
                }

                ++this.countActive;
            }

            mc.profiler.endSection();
        }
    }

    protected void draw(Vec3d cameraPos, MinecraftClient mc)
    {
        if (this.resourcesAllocated && this.countActive > 0)
        {
            GlStateManager.pushMatrix();

            GlStateManager.disableTexture2D();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01F);
            GlStateManager.disableCull();
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.doPolygonOffset(-3f, -3f);
            GlStateManager.enablePolygonOffset();

            fi.dy.masa.malilib.render.RenderUtils.setupBlend();
            fi.dy.masa.malilib.render.RenderUtils.color(1f, 1f, 1f, 1f);

            if (OpenGlHelper.useVbo())
            {
                GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                GlStateManager.glEnableClientState(GL11.GL_COLOR_ARRAY);
            }

            double cx = cameraPos.x;
            double cy = cameraPos.y;
            double cz = cameraPos.z;

            for (BaseOverlayRenderer renderer : this.enabledRenderers)
            {
                mc.profiler.func_194340_a(() -> renderer.getClass().getName());

                if (renderer.shouldRender(mc))
                {
                    Vec3d updatePos = renderer.getUpdatePosition();
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(updatePos.x - cx, updatePos.y - cy, updatePos.z - cz);

                    renderer.draw();

                    GlStateManager.popMatrix();
                }

                mc.profiler.endSection();
            }

            if (OpenGlHelper.useVbo())
            {
                OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
                GlStateManager.resetColor();

                GlStateManager.glDisableClientState(GL11.GL_VERTEX_ARRAY);
                GlStateManager.glDisableClientState(GL11.GL_COLOR_ARRAY);
            }

            fi.dy.masa.malilib.render.RenderUtils.color(1f, 1f, 1f, 1f);

            GlStateManager.doPolygonOffset(0f, 0f);
            GlStateManager.disablePolygonOffset();
            GlStateManager.disableBlend();
            GlStateManager.enableDepth();
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();

            GlStateManager.popMatrix();
        }
    }

    protected void checkVideoSettings()
    {
        boolean vboLast = this.useVbo;
        this.useVbo = OpenGlHelper.useVbo();

        if (vboLast != this.useVbo || this.resourcesAllocated == false)
        {
            this.deleteGlResources();
            this.allocateGlResources();
        }
    }

    protected void allocateGlResources()
    {
        if (this.resourcesAllocated == false)
        {
            for (BaseOverlayRenderer renderer : this.renderers)
            {
                renderer.allocateGlResources();
            }

            this.resourcesAllocated = true;
        }
    }

    protected void deleteGlResources()
    {
        if (this.resourcesAllocated)
        {
            for (BaseOverlayRenderer renderer : this.renderers)
            {
                renderer.deleteGlResources();
            }

            this.resourcesAllocated = false;
        }
    }

    protected ArrayListMultimap<File, BaseOverlayRenderer> getModGroupedRenderersForSerialization(boolean isDimensionChangeOnly)
    {
        ArrayListMultimap<File, BaseOverlayRenderer> map = ArrayListMultimap.create();

        for (BaseOverlayRenderer renderer : this.renderers)
        {
            String id = renderer.getSaveId();
            File file = renderer.getSaveFile(isDimensionChangeOnly);

            if (file != null && StringUtils.isBlank(id) == false)
            {
                map.put(file, renderer);
            }
        }

        return map;
    }

    public void saveToFile(boolean isDimensionChangeOnly)
    {
        ArrayListMultimap<File, BaseOverlayRenderer> map = this.getModGroupedRenderersForSerialization(isDimensionChangeOnly);

        for (File file : map.keySet())
        {
            File dir = file.getParentFile();
            JsonObject obj = new JsonObject();

            for (BaseOverlayRenderer renderer : map.get(file))
            {
                obj.add(renderer.getSaveId(), renderer.toJson());
            }

            if (BackupUtils.createRegularBackup(file, new File(dir, "backups")))
            {
                JsonUtils.writeJsonToFile(obj, file);
            }
        }
    }

    public void loadFromFile(boolean isDimensionChangeOnly)
    {
        ArrayListMultimap<File, BaseOverlayRenderer> map = this.getModGroupedRenderersForSerialization(isDimensionChangeOnly);

        for (File file : map.keySet())
        {
            if (file.isFile() == false || file.canRead() == false)
            {
                continue;
            }

            JsonElement element = JsonUtils.parseJsonFile(file);

            if (element == null || element.isJsonObject() == false)
            {
                continue;
            }

            JsonObject obj = element.getAsJsonObject();

            for (BaseOverlayRenderer renderer : map.get(file))
            {
                String id = renderer.getSaveId();

                if (StringUtils.isBlank(id) == false && JsonUtils.hasObject(obj, id))
                {
                    renderer.fromJson(obj.get(id).getAsJsonObject());
                }
            }
        }
    }
}

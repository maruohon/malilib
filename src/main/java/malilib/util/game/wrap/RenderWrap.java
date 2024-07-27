package malilib.util.game.wrap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;

import malilib.gui.util.GuiUtils;
import malilib.render.RenderContext;
import malilib.util.data.Identifier;

public class RenderWrap
{
    public static final int GL_ARRAY_BUFFER = GL15.GL_ARRAY_BUFFER;

    public static final int DEFAULT_TEX_UNIT = GL13.GL_TEXTURE0;
    public static final int LIGHTMAP_TEX_UNIT = GL13.GL_TEXTURE1;

    /*
    private static final FloatBuffer COLOR_BUFFER = createDirectFloatBuffer(4);
    private static final Vec3f LIGHT0_POS = Vec3f.normalized( 0.2F, 1.0F, -0.7F);
    private static final Vec3f LIGHT1_POS = Vec3f.normalized(-0.2F, 1.0F,  0.7F);

    //private static int activeShadeModel = GL11.GL_SMOOTH;
    private static final boolean arbMultiTexture;
    private static final boolean arbVbo;
    private static final boolean blendFuncSeparate;
    private static final boolean extBlendFuncSeparate;
    */

    public static void bindTexture(Identifier texture)
    {
        GameWrap.getClient().getTextureManager().bindTexture(texture);
    }

    public static void setupBlendSimple()
    {
        enableBlend();
        blendFunc(BlendSourceFactor.SRC_ALPHA, BlendDestFactor.ONE_MINUS_SRC_ALPHA);
    }

    public static void setupBlendSeparate()
    {
        enableBlend();
        tryBlendFuncSeparate(BlendSourceFactor.SRC_ALPHA,
                             BlendDestFactor.ONE_MINUS_SRC_ALPHA,
                             BlendSourceFactor.ONE,
                             BlendDestFactor.ZERO);
    }

    public static void setupScaledScreenRendering(double scaleFactor, RenderContext ctx)
    {
        double width = GuiUtils.getDisplayWidth() / scaleFactor;
        double height = GuiUtils.getDisplayHeight() / scaleFactor;

        setupScaledScreenRendering(width, height, ctx);
    }

    public static void setupScaledScreenRendering(double width, double height, RenderContext ctx)
    {
        GlStateManager.clear(256);
        matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, width, height, 0.0D, 1000.0D, 3000.0D);
        matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        translate(0.0F, 0.0F, -2000.0F, ctx);
    }

    public static void enableAlpha()
    {
        GlStateManager.enableAlpha();
    }

    public static void enableBlend()
    {
        GlStateManager.enableBlend();
    }

    public static void enableColorMaterial()
    {
        GlStateManager.enableColorMaterial();
    }

    public static void enableCull()
    {
        GlStateManager.enableCull();
    }

    public static void enableDepthTest()
    {
        GlStateManager.enableDepth();
    }

    public static void enableFog()
    {
        GlStateManager.enableFog();
    }

    public static void enableLighting()
    {
        GlStateManager.enableLighting();
    }

    public static void enablePolygonOffset()
    {
        GlStateManager.enablePolygonOffset();
    }

    public static void enableRescaleNormal()
    {
        GlStateManager.enableRescaleNormal();
    }

    public static void enableTexture2D()
    {
        GlStateManager.enableTexture2D();
    }

    public static void disableAlpha()
    {
        GlStateManager.disableAlpha();
    }

    public static void disableBlend()
    {
        GlStateManager.disableBlend();
    }

    public static void disableColorMaterial()
    {
        GlStateManager.disableColorMaterial();
    }

    public static void disableCull()
    {
        GlStateManager.disableCull();
    }

    public static void disableDepthTest()
    {
        GlStateManager.disableDepth();
    }

    public static void disableFog()
    {
        GlStateManager.disableFog();
    }

    public static void disableLighting()
    {
        GlStateManager.disableLighting();
    }

    public static void disablePolygonOffset()
    {
        GlStateManager.disablePolygonOffset();
    }

    public static void disableRescaleNormal()
    {
        GlStateManager.disableRescaleNormal();
    }

    public static void disableTexture2D()
    {
        GlStateManager.disableTexture2D();
    }

    public static void enableClientState(int capability)
    {
        GlStateManager.glEnableClientState(capability);
    }

    public static void disableClientState(int capability)
    {
        GlStateManager.glDisableClientState(capability);
    }

    public static void enableLight(int light)
    {
        GlStateManager.enableLight(light);
    }

    public static void disableLight(int light)
    {
        GlStateManager.disableLight(light);
    }

    public static void alphaFunc(int func, float ref)
    {
        GlStateManager.alphaFunc(func, ref);
    }

    public static void bindBuffer(int target, int buffer)
    {
        OpenGlHelper.glBindBuffer(target, buffer);
    }

    public static void colorMaterial(int face, int mode)
    {
        GlStateManager.colorMaterial(face, mode);
    }

    public static void depthMask(boolean enabled)
    {
        GlStateManager.depthMask(enabled);
    }

    public static void color(float r, float g, float b, float a)
    {
        GlStateManager.color(r, g, b, a);
    }

    public static void light(int light, int pname, FloatBuffer params)
    {
        GlStateManager.glLight(light, pname, params);
    }

    public static void lightModel(int pname, FloatBuffer params)
    {
        GlStateManager.glLightModel(pname, params);
    }

    public static void lineWidth(float lineWidth)
    {
        GlStateManager.glLineWidth(lineWidth);
    }

    public static void matrixMode(int mode)
    {
        GlStateManager.matrixMode(mode);
    }

    public static void normal(float nx, float ny, float nz)
    {
        GlStateManager.glNormal3f(nx, ny, nz);
    }

    public static void polygonMode(int face, int mode)
    {
        GlStateManager.glPolygonMode(face, mode);
    }

    public static void polygonOffset(float factor, float units)
    {
        GlStateManager.doPolygonOffset(factor, units);
    }

    public static void pushMatrix(RenderContext ctx)
    {
        GlStateManager.pushMatrix();
    }

    public static void popMatrix(RenderContext ctx)
    {
        GlStateManager.popMatrix();
    }

    public static void resetColor()
    {
        GlStateManager.resetColor();
    }

    public static void rotate(float angle, float x, float y, float z, RenderContext ctx)
    {
        GlStateManager.rotate(angle, x, y, z);
    }

    public static void scale(double x, double y, double z, RenderContext ctx)
    {
        GlStateManager.scale(x, y, z);
    }

    public static void setClientActiveTexture(int texture)
    {
        OpenGlHelper.setClientActiveTexture(texture);
    }

    public static void setFogDensity(float param)
    {
        GlStateManager.setFogDensity(param);
    }

    public static void shadeModel(int mode)
    {
        GlStateManager.shadeModel(mode);
    }

    public static void translate(float x, float y, float z, RenderContext ctx)
    {
        GlStateManager.translate(x, y, z);
    }

    public static void translate(double x, double y, double z, RenderContext ctx)
    {
        GlStateManager.translate(x, y, z);
    }

    public static boolean useVbo()
    {
        return OpenGlHelper.useVbo();
    }

    public static void colorPointer(int size, int type, int stride, int bufferOffset)
    {
        GlStateManager.glColorPointer(size, type, stride, bufferOffset);
    }

    public static void colorPointer(int size, int type, int stride, ByteBuffer buffer)
    {
        GlStateManager.glColorPointer(size, type, stride, buffer);
    }

    public static void normalPointer(int type, int stride, long bufferOffset)
    {
        GL11.glNormalPointer(type, stride, bufferOffset);
    }

    public static void normalPointer(int type, int stride, ByteBuffer buffer)
    {
        GlStateManager.glNormalPointer(type, stride, buffer);
    }

    public static void texCoordPointer(int size, int type, int stride, int bufferOffset)
    {
        GlStateManager.glTexCoordPointer(size, type, stride, bufferOffset);
    }

    public static void texCoordPointer(int size, int type, int stride, ByteBuffer buffer)
    {
        GlStateManager.glTexCoordPointer(size, type, stride, buffer);
    }

    public static void vertexPointer(int size, int type, int stride, int bufferOffset)
    {
        GlStateManager.glVertexPointer(size, type, stride, bufferOffset);
    }

    public static void vertexPointer(int size, int type, int stride, ByteBuffer buffer)
    {
        GlStateManager.glVertexPointer(size, type, stride, buffer);
    }

    public static void blendFunc(BlendSourceFactor srcFactor, BlendDestFactor dstFactor)
    {
        blendFunc(srcFactor.factor, dstFactor.factor);
    }

    public static void blendFunc(int srcFactor, int dstFactor)
    {
        GlStateManager.blendFunc(srcFactor, dstFactor);
    }

    public static void tryBlendFuncSeparate(BlendSourceFactor srcFactor,
                                            BlendDestFactor dstFactor,
                                            BlendSourceFactor srcFactorAlpha,
                                            BlendDestFactor dstFactorAlpha
    )
    {
        tryBlendFuncSeparate(srcFactor.factor, dstFactor.factor, srcFactorAlpha.factor, dstFactorAlpha.factor);
    }

    public static void tryBlendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha)
    {
        GlStateManager.tryBlendFuncSeparate(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
    }

    /*
    private static FloatBuffer setColorBuffer(float f, float g, float h, float i)
    {
        COLOR_BUFFER.clear();
        COLOR_BUFFER.put(f).put(g).put(h).put(i);
        COLOR_BUFFER.flip();
        return COLOR_BUFFER;
    }
    */

    public static void disableItemLighting()
    {
        RenderHelper.disableStandardItemLighting();
        /*
        disableLighting();
        disableLight(0);
        disableLight(1);
        disableColorMaterial();
        */
    }

    public static void enableItemLighting()
    {
        RenderHelper.enableStandardItemLighting();
        /*
        enableLighting();
        enableLight(0);
        enableLight(1);
        enableColorMaterial();
        colorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);

        light(GL11.GL_LIGHT0, GL11.GL_POSITION, setColorBuffer(LIGHT0_POS.x, LIGHT0_POS.y, LIGHT0_POS.z, 0.0F));
        light(GL11.GL_LIGHT0, GL11.GL_DIFFUSE,  setColorBuffer(0.6F, 0.6F, 0.6F, 1.0F));
        light(GL11.GL_LIGHT0, GL11.GL_AMBIENT,  setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        light(GL11.GL_LIGHT0, GL11.GL_SPECULAR, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));

        light(GL11.GL_LIGHT1, GL11.GL_POSITION, setColorBuffer(LIGHT1_POS.x, LIGHT1_POS.y, LIGHT1_POS.z, 0.0F));
        light(GL11.GL_LIGHT1, GL11.GL_DIFFUSE,  setColorBuffer(0.6F, 0.6F, 0.6F, 1.0F));
        light(GL11.GL_LIGHT1, GL11.GL_AMBIENT,  setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        light(GL11.GL_LIGHT1, GL11.GL_SPECULAR, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));

        shadeModel(GL11.GL_FLAT);
        lightModel(GL11.GL_LIGHT_MODEL_AMBIENT, setColorBuffer(0.4F, 0.4F, 0.4F, 1.0F));
        */
    }

    public static void enableGuiItemLighting(RenderContext ctx)
    {
        RenderHelper.enableGUIStandardItemLighting();
        /*
        pushMatrix();
        rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        rotate(165.0F, 1.0F, 0.0F, 0.0F);
        enableItemLighting();
        popMatrix();
        */
    }

    public static void bufferData(int target, ByteBuffer data, int usage)
    {
        OpenGlHelper.glBufferData(target, data, usage);
    }

    public static void glDeleteBuffers(int buffer)
    {
        OpenGlHelper.glDeleteBuffers(buffer);
    }

    public static void glDrawArrays(int mode, int first, int count)
    {
        GlStateManager.glDrawArrays(mode, first, count);
    }

    public static int glGenBuffers()
    {
        return OpenGlHelper.glGenBuffers();
    }

    private static synchronized ByteBuffer createDirectByteBuffer(int capacity)
    {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    private static FloatBuffer createDirectFloatBuffer(int capacity)
    {
        return createDirectByteBuffer(capacity << 2).asFloatBuffer();
    }

    /*
    static
    {
        ContextCapabilities contextCapabilities = GLContext.getCapabilities();
        arbMultiTexture = contextCapabilities.OpenGL13 == false && contextCapabilities.GL_ARB_multitexture;
        arbVbo = contextCapabilities.OpenGL15 == false && contextCapabilities.GL_ARB_vertex_buffer_object;
        blendFuncSeparate = contextCapabilities.OpenGL14 || contextCapabilities.GL_EXT_blend_func_separate;
        extBlendFuncSeparate = contextCapabilities.OpenGL14 == false && contextCapabilities.GL_EXT_blend_func_separate;
    }
    */

    public enum BlendSourceFactor {
        CONSTANT_ALPHA(GL11.GL_CONSTANT_ALPHA),
        CONSTANT_COLOR(GL11.GL_CONSTANT_COLOR),
        DST_ALPHA(GL11.GL_DST_ALPHA),
        DST_COLOR(GL11.GL_DST_COLOR),
        ONE(GL11.GL_ONE),
        ONE_MINUS_CONSTANT_ALPHA(GL11.GL_ONE_MINUS_CONSTANT_ALPHA),
        ONE_MINUS_CONSTANT_COLOR(GL11.GL_ONE_MINUS_CONSTANT_COLOR),
        ONE_MINUS_DST_ALPHA(GL11.GL_ONE_MINUS_DST_ALPHA),
        ONE_MINUS_DST_COLOR(GL11.GL_ONE_MINUS_DST_COLOR),
        ONE_MINUS_SRC_ALPHA(GL11.GL_ONE_MINUS_SRC_ALPHA),
        ONE_MINUS_SRC_COLOR(GL11.GL_ONE_MINUS_SRC_COLOR),
        SRC_ALPHA(GL11.GL_SRC_ALPHA),
        SRC_ALPHA_SATURATE(GL11.GL_SRC_ALPHA_SATURATE),
        SRC_COLOR(GL11.GL_SRC_COLOR),
        ZERO(GL11.GL_ZERO);

        public final int factor;

        BlendSourceFactor(int j)
        {
            this.factor = j;
        }
    }

    public enum BlendDestFactor
    {
        CONSTANT_ALPHA(GL11.GL_CONSTANT_ALPHA),
        CONSTANT_COLOR(GL11.GL_CONSTANT_COLOR),
        DST_ALPHA(GL11.GL_DST_ALPHA),
        DST_COLOR(GL11.GL_DST_COLOR),
        ONE(GL11.GL_ONE),
        ONE_MINUS_CONSTANT_ALPHA(GL11.GL_ONE_MINUS_CONSTANT_ALPHA),
        ONE_MINUS_CONSTANT_COLOR(GL11.GL_ONE_MINUS_CONSTANT_COLOR),
        ONE_MINUS_DST_ALPHA(GL11.GL_ONE_MINUS_DST_ALPHA),
        ONE_MINUS_DST_COLOR(GL11.GL_ONE_MINUS_DST_COLOR),
        ONE_MINUS_SRC_ALPHA(GL11.GL_ONE_MINUS_SRC_ALPHA),
        ONE_MINUS_SRC_COLOR(GL11.GL_ONE_MINUS_SRC_COLOR),
        SRC_ALPHA(GL11.GL_SRC_ALPHA),
        SRC_COLOR(GL11.GL_SRC_COLOR),
        ZERO(GL11.GL_ZERO);

        public final int factor;

        BlendDestFactor(int j)
        {
            this.factor = j;
        }
    }
}

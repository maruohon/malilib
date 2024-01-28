package malilib.util.game.wrap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTBlendFuncSeparate;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GLContext;

import net.minecraft.client.render.texture.TextureManager;

import malilib.util.data.Identifier;
import malilib.util.position.Vec3f;

public class RenderWrap
{
    public static final int GL_ARRAY_BUFFER = GL15.GL_ARRAY_BUFFER;

    public static final int DEFAULT_TEX_UNIT = GL13.GL_TEXTURE0;

    private static final FloatBuffer COLOR_BUFFER = createDirectFloatBuffer(4);
    private static final Vec3f LIGHT0_POS = Vec3f.normalized( 0.2F, 1.0F, -0.7F);
    private static final Vec3f LIGHT1_POS = Vec3f.normalized(-0.2F, 1.0F,  0.7F);

    //private static int activeShadeModel = GL11.GL_SMOOTH;
    private static final boolean arbMultiTexture;
    private static final boolean arbVbo;
    private static final boolean blendFuncSeparate;
    private static final boolean extBlendFuncSeparate;

    public static void bindTexture(Identifier texture)
    {
        String path = String.format("/assets/%s/%s", texture.getNamespace(), texture.getPath());
        TextureManager manager = GameUtils.getClient().textureManager;
        manager.bind(manager.load(path));
    }

    public static void setupBlendSimple()
    {
        enableBlend();
        blendFunc(BlendSourceFactor.SRC_ALPHA, BlendDestFactor.ONE_MINUS_SRC_ALPHA);
    }

    public static void setupBlend()
    {
        enableBlend();
        tryBlendFuncSeparate(BlendSourceFactor.SRC_ALPHA,
                             BlendDestFactor.ONE_MINUS_SRC_ALPHA,
                             BlendSourceFactor.ONE,
                             BlendDestFactor.ZERO);
    }

    public static void enableAlpha()
    {
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    public static void enableBlend()
    {
        GL11.glEnable(GL11.GL_BLEND);
    }

    public static void enableColorMaterial()
    {
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
    }

    public static void enableCull()
    {
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    public static void enableDepthTest()
    {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public static void enableLighting()
    {
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    public static void enablePolygonOffset()
    {
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
    }

    public static void enableRescaleNormal()
    {
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }

    public static void enableTexture2D()
    {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void disableAlpha()
    {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

    public static void disableBlend()
    {
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void disableColorMaterial()
    {
        GL11.glDisable(GL11.GL_COLOR_MATERIAL);
    }

    public static void disableCull()
    {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public static void disableDepthTest()
    {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    public static void disableLighting()
    {
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public static void disablePolygonOffset()
    {
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
    }

    public static void disableRescaleNormal()
    {
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    public static void disableTexture2D()
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public static void enableClientState(int capability)
    {
        GL11.glEnableClientState(capability);
    }

    public static void disableClientState(int capability)
    {
        GL11.glDisableClientState(capability);
    }

    public static void enableLight(int light)
    {
        if (light >= 0 && light < 8)
        {
            GL11.glEnable(GL11.GL_LIGHT0 + light);
        }
    }

    public static void disableLight(int light)
    {
        if (light >= 0 && light < 8)
        {
            GL11.glDisable(GL11.GL_LIGHT0 + light);
        }
    }

    public static void alphaFunc(int func, float ref)
    {
        GL11.glAlphaFunc(func, ref);
    }

    public static void bindBuffer(int target, int buffer)
    {
        if (arbVbo)
        {
            ARBVertexBufferObject.glBindBufferARB(target, buffer);
        }
        else
        {
            GL15.glBindBuffer(target, buffer);
        }
    }

    public static void colorMaterial(int face, int mode)
    {
        GL11.glColorMaterial(face, mode);
    }

    public static void depthMask(boolean enabled)
    {
        GL11.glDepthMask(enabled);
    }

    public static void color(float r, float g, float b, float a)
    {
        GL11.glColor4f(r, g, b, a);
    }

    public static void light(int light, int pname, FloatBuffer params)
    {
        GL11.glLight(light, pname, params);
    }

    public static void lightModel(int pname, FloatBuffer params)
    {
        GL11.glLightModel(pname, params);
    }

    public static void lineWidth(float lineWidth)
    {
        GL11.glLineWidth(lineWidth);
    }

    public static void normal(float nx, float ny, float nz)
    {
        GL11.glNormal3f(nx, ny, nz);
    }

    public static void polygonOffset(float factor, float units)
    {
        GL11.glPolygonOffset(factor, units);
    }

    public static void pushMatrix()
    {
        GL11.glPushMatrix();
    }

    public static void popMatrix()
    {
        GL11.glPopMatrix();
    }

    public static void rotate(float angle, float x, float y, float z)
    {
        GL11.glRotatef(angle, x, y, z);
    }

    public static void scale(double x, double y, double z)
    {
        GL11.glScaled(x, y, z);
    }

    public static void setClientActiveTexture(int texture)
    {
        if (arbMultiTexture)
        {
            ARBMultitexture.glClientActiveTextureARB(texture);
        }
        else
        {
            GL13.glClientActiveTexture(texture);
        }
    }

    public static void shadeModel(int mode)
    {
        //if (mode != activeShadeModel)
        {
            //activeShadeModel = mode;
            GL11.glShadeModel(mode);
        }
    }

    public static void translate(float x, float y, float z)
    {
        GL11.glTranslatef(x, y, z);
    }

    public static void translate(double x, double y, double z)
    {
        GL11.glTranslated(x, y, z);
    }

    public static void colorPointer(int size, int type, int stride, long bufferOffset)
    {
        GL11.glColorPointer(size, type, stride, bufferOffset);
    }

    public static void colorPointer(int size, int type, int stride, ByteBuffer buffer)
    {
        GL11.glColorPointer(size, type, stride, buffer);
    }

    public static void normalPointer(int type, int stride, ByteBuffer buffer)
    {
        GL11.glNormalPointer(type, stride, buffer);
    }

    public static void texCoordPointer(int size, int type, int stride, long bufferOffset)
    {
        GL11.glTexCoordPointer(size, type, stride, bufferOffset);
    }

    public static void texCoordPointer(int size, int type, int stride, ByteBuffer buffer)
    {
        GL11.glTexCoordPointer(size, type, stride, buffer);
    }

    public static void vertexPointer(int size, int type, int stride, long bufferOffset)
    {
        GL11.glVertexPointer(size, type, stride, bufferOffset);
    }

    public static void vertexPointer(int size, int type, int stride, ByteBuffer buffer)
    {
        GL11.glVertexPointer(size, type, stride, buffer);
    }

    public static void blendFunc(BlendSourceFactor srcFactor, BlendDestFactor dstFactor)
    {
        blendFunc(srcFactor.factor, dstFactor.factor);
    }

    public static void blendFunc(int srcFactor, int dstFactor)
    {
        GL11.glBlendFunc(srcFactor, dstFactor);
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
        if (blendFuncSeparate)
        {
            if (extBlendFuncSeparate)
            {
                EXTBlendFuncSeparate.glBlendFuncSeparateEXT(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
            }
            else
            {
                GL14.glBlendFuncSeparate(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
            }
        }
        else
        {
            GL11.glBlendFunc(srcFactor, dstFactor);
        }
    }

    private static FloatBuffer setColorBuffer(float f, float g, float h, float i)
    {
        COLOR_BUFFER.clear();
        COLOR_BUFFER.put(f).put(g).put(h).put(i);
        COLOR_BUFFER.flip();
        return COLOR_BUFFER;
    }

    public static void disableItemLighting()
    {
        disableLighting();
        disableLight(0);
        disableLight(1);
        disableColorMaterial();
    }

    public static void enableItemLighting()
    {
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
    }

    public static void enableGuiItemLighting()
    {
        RenderWrap.pushMatrix();
        RenderWrap.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        RenderWrap.rotate(165.0F, 1.0F, 0.0F, 0.0F);
        RenderWrap.enableItemLighting();
        RenderWrap.popMatrix();
    }

    public static void glBindBuffer(int target, int buffer)
    {
        if (arbVbo)
        {
            ARBVertexBufferObject.glBindBufferARB(target, buffer);
        }
        else
        {
            GL15.glBindBuffer(target, buffer);
        }
    }

    public static void glBufferData(int target, ByteBuffer data, int usage)
    {
        if (arbVbo)
        {
            ARBVertexBufferObject.glBufferDataARB(target, data, usage);
        }
        else
        {
            GL15.glBufferData(target, data, usage);
        }
    }

    public static void glDeleteBuffers(int buffer)
    {
        if (arbVbo)
        {
            ARBVertexBufferObject.glDeleteBuffersARB(buffer);
        }
        else
        {
            GL15.glDeleteBuffers(buffer);
        }
    }

    public static void glDrawArrays(int mode, int first, int count)
    {
        GL11.glDrawArrays(mode, first, count);
    }

    public static int glGenBuffers()
    {
        return arbVbo ? ARBVertexBufferObject.glGenBuffersARB() : GL15.glGenBuffers();
    }

    private static synchronized ByteBuffer createDirectByteBuffer(int capacity)
    {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    private static FloatBuffer createDirectFloatBuffer(int capacity)
    {
        return createDirectByteBuffer(capacity << 2).asFloatBuffer();
    }

    static
    {
        ContextCapabilities contextCapabilities = GLContext.getCapabilities();
        arbMultiTexture = contextCapabilities.OpenGL13 == false && contextCapabilities.GL_ARB_multitexture;
        arbVbo = contextCapabilities.OpenGL15 == false && contextCapabilities.GL_ARB_vertex_buffer_object;
        blendFuncSeparate = contextCapabilities.OpenGL14 || contextCapabilities.GL_EXT_blend_func_separate;
        extBlendFuncSeparate = contextCapabilities.OpenGL14 == false && contextCapabilities.GL_EXT_blend_func_separate;
    }

    public enum BlendSourceFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_ALPHA_SATURATE(776),
        SRC_COLOR(768),
        ZERO(0);

        public final int factor;

        BlendSourceFactor(int j)
        {
            this.factor = j;
        }
    }

    public enum BlendDestFactor
    {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_COLOR(768),
        ZERO(0);

        public final int factor;

        BlendDestFactor(int j)
        {
            this.factor = j;
        }
    }
}

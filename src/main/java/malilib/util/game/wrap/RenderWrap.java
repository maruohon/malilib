package malilib.util.game.wrap;

import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GLContext;

public class RenderWrap
{
    public static final int GL_ARRAY_BUFFER = GL15.GL_ARRAY_BUFFER;

    //private static int activeShadeModel = GL11.GL_SMOOTH;
    private static boolean arbVbo;

    public static void enableAlpha()
    {
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    public static void enableBlend()
    {
        GL11.glEnable(GL11.GL_BLEND);
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
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    public static void enableTexture2D()
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public static void disableAlpha()
    {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

    public static void disableBlend()
    {
        GL11.glDisable(GL11.GL_BLEND);
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

    public static void depthMask(boolean enabled)
    {
        GL11.glDepthMask(enabled);
    }

    public static void enableClientState(int capability)
    {
        GL11.glEnableClientState(capability);
    }

    public static void disableClientState(int capability)
    {
        GL11.glDisableClientState(capability);
    }

    public static void color(float r, float g, float b, float a)
    {
        GL11.glColor4f(r, g, b, a);
    }

    public static void normal(float nx, float ny, float nz)
    {
        GL11.glNormal3f(nx, ny, nz);
    }

    public static void polygonOffset(float factor, float units)
    {
        GL11.glPolygonOffset(factor, units);
    }

    public static void lineWidth(float lineWidth)
    {
        GL11.glLineWidth(lineWidth);
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

    public static void translate(float x, float y, float z)
    {
        GL11.glTranslatef(x, y, z);
    }

    public static void translate(double x, double y, double z)
    {
        GL11.glTranslated(x, y, z);
    }

    public static void shadeModel(int mode)
    {
        //if (mode != activeShadeModel)
        {
            //activeShadeModel = mode;
            GL11.glShadeModel(mode);
        }
    }

    static
    {
        ContextCapabilities contextCapabilities = GLContext.getCapabilities();
        arbVbo = contextCapabilities.OpenGL15 == false && contextCapabilities.GL_ARB_vertex_buffer_object;
    }
}

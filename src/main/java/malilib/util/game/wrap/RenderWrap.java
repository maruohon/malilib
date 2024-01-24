package malilib.util.game.wrap;

import org.lwjgl.opengl.GL11;

public class RenderWrap
{
    //private static int activeShadeModel = GL11.GL_SMOOTH;

    public static void shadeModel(int mode)
    {
        //if (mode != activeShadeModel)
        {
            //activeShadeModel = mode;
            GL11.glShadeModel(mode);
        }
    }
}

package fi.dy.masa.malilib.render.shader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import fi.dy.masa.malilib.MaLiLib;

/**
 * This class has been directly taken from Schematica by Lunatrius & contributors
 * https://github.com/Lunatrius/Schematica/blob/master/src/main/java/com/github/lunatrius/schematica/client/renderer/shader/ShaderProgram.java
 */
public class ShaderProgram
{
    private final MinecraftClient mc;
    private int program;

    public ShaderProgram(final String domain, final String vertShaderFilename, final String fragShaderFilename)
    {
        this.mc = MinecraftClient.getInstance();

        try
        {
            this.init(domain, vertShaderFilename, fragShaderFilename);
        }
        catch (final Exception e)
        {
            MaLiLib.logger.error("Could not initialize shader program!", e);
            this.program = 0;
        }
    }

    private void init(final String domain, final String vertShaderFilename, final String fragShaderFilename)
    {
        this.program = GL20.glCreateProgram();

        final int vertShader = this.loadAndCompileShader(domain, vertShaderFilename, GL20.GL_VERTEX_SHADER);
        final int fragShader = this.loadAndCompileShader(domain, fragShaderFilename, GL20.GL_FRAGMENT_SHADER);

        if (vertShader != 0)
        {
            GL20.glAttachShader(this.program, vertShader);
        }

        if (fragShader != 0)
        {
            GL20.glAttachShader(this.program, fragShader);
        }

        GL20.glLinkProgram(this.program);

        if (GL20.glGetProgrami(this.program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE)
        {
            MaLiLib.logger.error("Could not link shader: {}", GL20.glGetProgramInfoLog(this.program, 1024));
            GL20.glDeleteProgram(this.program);
            this.program = 0;
            return;
        }

        GL20.glValidateProgram(this.program);

        if (GL20.glGetProgrami(this.program, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE)
        {
            MaLiLib.logger.error("Could not validate shader: {}", GL20.glGetProgramInfoLog(this.program, 1024));
            GL20.glDeleteProgram(this.program);
            this.program = 0;
        }
    }

    private int loadAndCompileShader(final String domain, final String filename, final int shaderType)
    {
        if (filename == null)
        {
            return 0;
        }

        final int handle = GL20.glCreateShader(shaderType);

        if (handle == 0)
        {
            MaLiLib.logger.error("Could not create shader of type {} for {}: {}", shaderType, filename, GL20.glGetProgramInfoLog(this.program, 1024));
            return 0;
        }

        final String code = loadFile(new Identifier(domain, filename));

        if (code == null)
        {
            GL20.glDeleteShader(handle);
            return 0;
        }

        GL20.glShaderSource(handle, code);
        GL20.glCompileShader(handle);

        if (GL20.glGetShaderi(handle, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
        {
            MaLiLib.logger.error("Could not compile shader {}: {}", filename, GL20.glGetShaderInfoLog(this.program, 1024));
            GL20.glDeleteShader(handle);
            return 0;
        }

        return handle;
    }

    private String loadFile(final Identifier resourceLocation)
    {
        try
        {
            final StringBuilder code = new StringBuilder();
            final InputStream inputStream = this.mc.getResourceManager().getResource(resourceLocation).get().getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null)
            {
                code.append(line);
                code.append('\n');
            }

            reader.close();

            return code.toString();
        }
        catch (final Exception e)
        {
            MaLiLib.logger.error("Could not load shader file!", e);
        }

        return null;
    }

    public int getProgram()
    {
        return this.program;
    }
}

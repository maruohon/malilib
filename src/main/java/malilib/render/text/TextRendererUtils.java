package malilib.render.text;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.function.IntConsumer;

import javax.imageio.ImageIO;

import malilib.MaLiLib;
import malilib.util.FileUtils;
import malilib.util.data.Identifier;

public class TextRendererUtils
{
    public static void setColorCodes(int[] colorCodes, boolean anaglyph)
    {
        for (int i = 0; i < 32; ++i)
        {
            int j = (i >> 3 & 1) * 85;
            int r = (i >> 2 & 1) * 170 + j;
            int g = (i >> 1 & 1) * 170 + j;
            int b = (i      & 1) * 170 + j;

            if (i == 6)
            {
                r += 85;
            }

            if (anaglyph)
            {
                r = (r * 30 + g * 59 + b * 11) / 100;
                g = (r * 30 + g * 70         ) / 100;
                b = (r * 30          + b * 70) / 100;
            }

            if (i >= 16)
            {
                r /= 4;
                g /= 4;
                b /= 4;
            }

            colorCodes[i] = (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
        }
    }

    public static void readGlyphSizes(byte[] glyphWidth)
    {
        /* TODO b1.7.3
        try (IResource resource = GameUtils.getClient().getResourceManager().getResource(new Identifier("font/glyph_sizes.bin")))
        {
            if (resource.getInputStream().read(glyphWidth) <= 0)
            {
                MaLiLib.LOGGER.warn("Failed to read glyph sizes from 'font/glyph_sizes.bin'");
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        */
    }

    public static void readCharacterWidthsFromFontTexture(Identifier texture, int[] charWidthArray,
                                                          IntConsumer glyphWidthListener, IntConsumer glyphHeightListener)
    {
        BufferedImage bufferedImage = null;

        try
        {
            InputStream is;

            if ("minecraft".equals(texture.getNamespace()))
            {
                is = FileUtils.openVanillaResource(texture.getPath());
            }
            else
            {
                is = FileUtils.openModResource(texture.getNamespace(), texture.getPath());
            }

            if (is != null)
            {
                bufferedImage = ImageIO.read(is);
                is.close();
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read font texture from '{}'", texture, e);
        }

        if (bufferedImage == null)
        {
            MaLiLib.LOGGER.warn("Failed to read font texture from '{}' (failed to open image)", texture);
            return;
        }

        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        int[] pixelData = new int[imageWidth * imageHeight];

        bufferedImage.getRGB(0, 0, imageWidth, imageHeight, pixelData, 0, imageWidth);

        int charWidth = imageWidth / 16;
        int charHeight = imageHeight / 16;
        float pixelWidthScale = 8.0F / (float) charWidth;

        glyphWidthListener.accept(charWidth);
        glyphHeightListener.accept(charHeight);

        for (int charIndex = 0; charIndex < 256; ++charIndex)
        {
            int charIndexX = charIndex % 16;
            int charIndexY = charIndex / 16;
            int pixelX;

            for (pixelX = charWidth - 1; pixelX >= 0; --pixelX)
            {
                int imageX = charIndexX * charWidth + pixelX;
                boolean fullyTransparent = true;

                for (int pixelY = 0; pixelY < charHeight; ++pixelY)
                {
                    // In vanilla this was: int startIndex = (charIndexY * charWidth + pixelY) * imageWidth;
                    int startIndex = (charIndexY * charHeight + pixelY) * imageWidth;

                    if ((pixelData[startIndex + imageX] >> 24 & 0xFF) != 0)
                    {
                        fullyTransparent = false;
                        break;
                    }
                }

                if (fullyTransparent == false)
                {
                    break;
                }
            }

            ++pixelX;
            charWidthArray[charIndex] = (int) (((float) pixelX * pixelWidthScale) + 0.5F) + 1;
        }

        charWidthArray[32] = 4; // space
    }

    /*
    public static String bidiReorder(String text)
    {
        try
        {
            Bidi bidi = new Bidi((new ArabicShaping(8)).shape(text), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        }
        catch (ArabicShapingException var3)
        {
            return text;
        }
    }
    */
}

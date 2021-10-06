package fi.dy.masa.malilib.render.text;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import org.apache.commons.io.IOUtils;
import com.google.common.collect.ImmutableList;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.GameUtils;

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
        IResource resource = null;

        try
        {
            resource = GameUtils.getClient().getResourceManager().getResource(new ResourceLocation("font/glyph_sizes.bin"));

            if (resource.getInputStream().read(glyphWidth) <= 0)
            {
                MaLiLib.LOGGER.warn("Failed to read glyph sizes from 'font/glyph_sizes.bin'");
            }
        }
        catch (IOException ioexception)
        {
            throw new RuntimeException(ioexception);
        }
        finally
        {
            IOUtils.closeQuietly(resource);
        }
    }

    public static void readCharacterWidthsFromFontTexture(ResourceLocation texture, int[] charWidthArray,
                                                          IntConsumer glyphWidthListener, IntConsumer glyphHeightListener)
    {
        IResource resource = null;
        BufferedImage bufferedImage;

        try
        {
            resource = GameUtils.getClient().getResourceManager().getResource(texture);
            bufferedImage = TextureUtil.readBufferedImage(resource.getInputStream());
        }
        catch (IOException ioexception)
        {
            throw new RuntimeException(ioexception);
        }
        finally
        {
            IOUtils.closeQuietly(resource);
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

    public static void generatePerFontTextureSegmentsFor(String displayString, String originalString, TextStyle style,
                                                         Consumer<StyledTextSegment> consumer, GlyphSource glyphSource)
    {
        List<Glyph> glyphs = new ArrayList<>();
        ResourceLocation texture = null;
        final int len = displayString.length();
        int displayStringStart = 0;
        int originalStringStart = 0;
        int stylePrefixLength = originalString.length() - displayString.length();
        int segmentLength = 0;

        for (int i = 0; i < len; ++i, ++segmentLength)
        {
            Glyph glyph = glyphSource.getGlyphFor(displayString.charAt(i));

            // font sheet change, add the segment
            if (texture != null && glyph.texture != texture)
            {
                int endIndex = originalStringStart + stylePrefixLength + segmentLength;
                String originalStringSegment = originalString.substring(originalStringStart, endIndex);
                String displayStringSegment = displayString.substring(displayStringStart, i);

                consumer.accept(new StyledTextSegment(texture, style, ImmutableList.copyOf(glyphs), displayStringSegment, originalStringSegment));

                displayStringStart += segmentLength;
                originalStringStart += segmentLength + stylePrefixLength;
                stylePrefixLength = 0;
                segmentLength = 0;
                glyphs.clear();
            }

            glyphs.add(glyph);
            texture = glyph.texture;
        }

        String displayStringSegment = displayString.substring(displayStringStart, len);
        String originalStringSegment = originalString.substring(originalStringStart);

        consumer.accept(new StyledTextSegment(texture, style, ImmutableList.copyOf(glyphs), displayStringSegment, originalStringSegment));
    }

    public interface GlyphSource
    {
        Glyph getGlyphFor(char character);
    }
}

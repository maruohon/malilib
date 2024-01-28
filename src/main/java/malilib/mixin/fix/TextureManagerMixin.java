package malilib.mixin.fix;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.texture.TextureManager;

import malilib.render.text.TextRenderer;

@Mixin(TextureManager.class)
public class TextureManagerMixin
{
    @Shadow private BufferedImage image;

    @Inject(method = "readImage", at = @At("HEAD"), cancellable = true)
    private void malilib_preventCrashes(InputStream is, CallbackInfoReturnable<BufferedImage> cir)
    {
        if (is == null)
        {
            cir.setReturnValue(this.image);
        }

        try
        {
            BufferedImage img = ImageIO.read(is);
            is.close();
            cir.setReturnValue(img);
        }
        catch (Exception e)
        {
            cir.setReturnValue(this.image);
        }
    }

        @Inject(method = "reload", at = @At("TAIL"))
    private void malilib_onPostReload(CallbackInfo ci)
    {
        TextRenderer.INSTANCE.onResourceManagerReload();
    }
}

package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.value.HudAlignment;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.message.ToastRenderer;
import fi.dy.masa.malilib.util.StringUtils;

public class ToastWidget extends BaseWidget
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(MaLiLibReference.MOD_ID, "textures/gui/toasts.png");

    protected final List<String> text;
    protected long expireTime;
    protected int lifeTime;
    protected boolean active;

    public ToastWidget(int x, int y, int width, int height, List<String> text, int lifeTime)
    {
        super(x, y, width, height);

        this.text = new ArrayList<>();
        this.setText(text, lifeTime);
    }

    public boolean hasExpired()
    {
        return System.currentTimeMillis() > this.expireTime;
    }

    public void setText(List<String> text, int lifeTime)
    {
        this.text.clear();
        this.text.addAll(text);

        this.lifeTime = lifeTime;
        this.setWidth(StringUtils.getMaxStringRenderWidth(text) + 20);
        this.setHeight((text.size() * (this.fontHeight + 2)) + 12);

        this.resetLifeTime();
    }

    public void resetLifeTime()
    {
        this.expireTime = System.currentTimeMillis() + (long) this.lifeTime;
    }

    public void render(int x, int y)
    {
        if (this.active == false)
        {
            this.resetLifeTime();
            this.active = true;
        }

        RenderUtils.color(1f, 1f, 1f, 1f);
        this.bindTexture(TEXTURE);

        RenderUtils.draw9SplicedTexture(x, y, 0, 0, this.getWidth(), this.getHeight(), 256, 32, 6, this.getZLevel());

        RenderUtils.renderText(x + 10, y + 8, 0xFFFFFFFF, this.text);
    }

    public static void updateOrAddToast(HudAlignment alignment, List<String> text, int lifeTime)
    {
        ToastWidget toast = ToastRenderer.INSTANCE.getToast(alignment, ToastWidget.class);

        if (toast != null)
        {
            toast.setText(text, lifeTime);
        }
        else
        {
            addToast(alignment, text, lifeTime);
        }
    }

    public static void addToast(HudAlignment alignment, List<String> text, int lifeTime)
    {
        // The position and dimensions get update in the constructor and before rendering
        ToastRenderer.INSTANCE.addToast(alignment, new ToastWidget(0, 0, 32, 32, text, lifeTime));
    }
}

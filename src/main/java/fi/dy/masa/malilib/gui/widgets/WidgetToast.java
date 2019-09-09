package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.values.HudAlignment;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ToastRenderer;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetToast extends WidgetBase
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(MaLiLibReference.MOD_ID, "textures/gui/toasts.png");

    protected final List<String> text;
    protected long expireTime;
    protected int lifeTime;
    protected boolean active;

    public WidgetToast(int x, int y, int width, int height, List<String> text, int lifeTime)
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
        this.width = StringUtils.getMaxStringRenderWidth(text) + 20;
        this.height = (text.size() * (StringUtils.getFontHeight() + 2)) + 12;

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

        RenderUtils.draw9SplicedTexture(x, y, 0, 0, this.width, this.height, 256, 32, 6, this.zLevel);

        RenderUtils.renderText(x + 10, y + 8, 0xFFFFFFFF, this.text);
    }

    public static void updateOrAddToast(HudAlignment alignment, List<String> text, int lifeTime)
    {
        WidgetToast toast = ToastRenderer.INSTANCE.getToast(alignment, WidgetToast.class);

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
        ToastRenderer.INSTANCE.addToast(alignment, new WidgetToast(0, 0, 32, 32, text, lifeTime));
    }
}

package fi.dy.masa.malilib.overlay.message;

import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Queues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.config.value.HudAlignment;
import fi.dy.masa.malilib.gui.widget.ToastWidget;

public class ToastRenderer
{
    public static final ToastRenderer INSTANCE = new ToastRenderer();

    protected final Minecraft mc;
    protected final ArrayListMultimap<HudAlignment, ToastInstance> activeToasts = ArrayListMultimap.create();
    protected final Map<HudAlignment, Deque<ToastWidget>> toastQueue = new HashMap<>();
    protected int maxToasts = 5;

    protected ToastRenderer()
    {
        this.mc = Minecraft.getMinecraft();
    }

    public void addToast(HudAlignment alignment, ToastWidget toast)
    {
        this.toastQueue.computeIfAbsent(alignment, (a) -> Queues.newArrayDeque()).add(toast);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends ToastWidget> T getToast(HudAlignment alignment, Class <? extends T> clazz)
    {
        for (ToastInstance instance : this.activeToasts.get(alignment))
        {
            if (clazz.isAssignableFrom(instance.getToast().getClass()))
            {
                return (T) instance.getToast();
            }
        }

        for (Deque<ToastWidget> deque : this.toastQueue.values())
        {
            for (ToastWidget toast : deque)
            {
                if (clazz.isAssignableFrom(toast.getClass()))
                {
                    return (T) toast;
                }
            }
        }

        return null;
    }

    public void render()
    {
        if (this.mc.gameSettings.hideGUI == false)
        {
            ScaledResolution res = new ScaledResolution(this.mc);
            RenderHelper.disableStandardItemLighting();

            for (HudAlignment alignment : HudAlignment.VALUES)
            {
                List<ToastInstance> list = this.activeToasts.get(alignment);

                if (list.isEmpty() == false)
                {
                    int y = this.getBaseY(alignment, res, list);

                    for (int i = 0; i < list.size(); ++i)
                    {
                        ToastInstance instance = list.get(i);
                        y += instance.render(res, y);

                        if (instance.hasExpired())
                        {
                            list.remove(i--);
                        }
                    }
                }

                Deque<ToastWidget> deque = this.toastQueue.computeIfAbsent(alignment, (a) -> Queues.newArrayDeque());

                if (deque.isEmpty() == false)
                {
                    int countToAdd = Math.min(deque.size(), this.maxToasts - list.size());

                    for (int i = 0; i < countToAdd; ++i)
                    {
                        list.add(new ToastInstance(alignment, deque.removeFirst()));
                    }
                }
            }
        }
    }

    protected int getBaseY(HudAlignment alignment, ScaledResolution res, List<ToastInstance> toasts)
    {
        if (alignment == HudAlignment.BOTTOM_LEFT || alignment == HudAlignment.BOTTOM_RIGHT)
        {
            return res.getScaledHeight() - this.getTotalToastHeight(toasts);
        }
        else if (alignment == HudAlignment.CENTER)
        {
            return res.getScaledHeight() / 2 - this.getTotalToastHeight(toasts) / 2;
        }

        return 0;
    }

    protected int getTotalToastHeight(List<ToastInstance> list)
    {
        int height = 0;

        for (ToastInstance toastInstance : list)
        {
            height += toastInstance.getHeight();
        }

        return height;
    }

    public static class ToastInstance
    {
        protected final ToastWidget toast;
        protected final HudAlignment alignment;
        protected long animationStartTime;
        protected long fullyVisibleStartTime;
        protected boolean expired;
        protected long animationTime;
        protected long currentTime;

        protected ToastInstance(HudAlignment alignment, ToastWidget toast)
        {
            this.alignment = alignment;
            this.toast = toast;
            this.animationTime = 300L;
            this.animationStartTime = -1L;
            this.fullyVisibleStartTime = -1L;
            this.expired = false;
        }

        public ToastWidget getToast()
        {
            return this.toast;
        }

        protected float getVisibility(long currentTime)
        {
            float f = MathHelper.clamp((float) (currentTime - this.animationStartTime) / (float) this.animationTime, 0.0F, 1.0F);
            f = f * f;

            return this.expired ? 1.0F - f : f;
        }

        public int getHeight()
        {
            return this.toast.getHeight();
        }

        /**
         * 
         * @param res
         * @param y
         * @return the height of the rendered toast, which is used to calculate the next toast's position
         */
        public int render(ScaledResolution res, int y)
        {
            int x = 0;
            int width = this.toast.getWidth();
            int height = this.toast.getHeight();
            int animationOffset = width;

            if (this.alignment == HudAlignment.TOP_RIGHT || this.alignment == HudAlignment.BOTTOM_RIGHT)
            {
                x = res.getScaledWidth() - this.toast.getWidth();
            }
            else if (this.alignment == HudAlignment.TOP_LEFT || this.alignment == HudAlignment.BOTTOM_LEFT)
            {
                animationOffset = -animationOffset;
            }
            else if (this.alignment == HudAlignment.CENTER)
            {
                x = res.getScaledWidth() / 2 - this.toast.getWidth() / 2;
            }

            long currentTime = Minecraft.getSystemTime();
            this.currentTime = currentTime;

            if (this.animationStartTime == -1L)
            {
                this.animationStartTime = currentTime;
            }

            if (this.expired == false && currentTime - this.animationStartTime <= this.animationTime)
            {
                this.fullyVisibleStartTime = currentTime;
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate((float) animationOffset * (1.0F - this.getVisibility(currentTime)), 0, 500F);

            this.toast.setPosition(x, y);
            this.toast.render(x, y);

            GlStateManager.popMatrix();

            boolean expired = this.toast.hasExpired();

            if (expired != this.expired)
            {
                this.animationStartTime = currentTime - (long) ((1.0F - this.getVisibility(currentTime)) * (float) this.animationTime);
                this.expired = expired;
            }

            return height;
        }

        public boolean hasExpired()
        {
            return this.expired && this.currentTime - this.animationStartTime > this.animationTime;
        }
    }
}

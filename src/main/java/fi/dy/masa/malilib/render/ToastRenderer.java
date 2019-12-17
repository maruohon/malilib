package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.Deque;
import javax.annotation.Nullable;
import com.google.common.collect.Queues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.config.values.HudAlignment;
import fi.dy.masa.malilib.gui.widgets.WidgetToast;

public class ToastRenderer
{
    public static final ToastRenderer INSTANCE = new ToastRenderer();

    protected final Minecraft mc;
    protected final ArrayList<ArrayList<ToastInstance>> activeToasts = new ArrayList<>();
    protected final ArrayList<Deque<WidgetToast>> toastQueue = new ArrayList<>();
    protected int maxToasts = 5;

    protected ToastRenderer()
    {
        this.mc = Minecraft.getMinecraft();

        for (int i = 0; i < HudAlignment.values().length; ++i)
        {
            this.activeToasts.add(new ArrayList<>());
            this.toastQueue.add(Queues.newArrayDeque());
        }
    }

    public void addToast(HudAlignment alignment, WidgetToast toast)
    {
        this.toastQueue.get(alignment.ordinal()).add(toast);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends WidgetToast> T getToast(HudAlignment alignment, Class <? extends T> clazz)
    {
        for (ToastInstance instance : this.activeToasts.get(alignment.ordinal()))
        {
            if (clazz.isAssignableFrom(instance.getToast().getClass()))
            {
                return (T) instance.getToast();
            }
        }

        for (Deque<WidgetToast> deque : this.toastQueue)
        {
            for (WidgetToast toast : deque)
            {
                if (clazz.isAssignableFrom(toast.getClass()))
                {
                    return (T) toast;
                }
            }
        }

        return (T) null;
    }

    public void render()
    {
        if (this.mc.gameSettings.hideGUI == false)
        {
            ScaledResolution res = new ScaledResolution(this.mc);
            RenderHelper.disableStandardItemLighting();

            for (HudAlignment alignment : HudAlignment.values())
            {
                ArrayList<ToastInstance> list = this.activeToasts.get(alignment.ordinal());

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

                Deque<WidgetToast> deque = this.toastQueue.get(alignment.ordinal());

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

    protected int getBaseY(HudAlignment alignment, ScaledResolution res, ArrayList<ToastInstance> toasts)
    {
        switch (alignment)
        {
            case TOP_LEFT:
            case TOP_RIGHT:
                return 0;
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                return res.getScaledHeight() - this.getTotalToastHeight(toasts);
            case CENTER:
                return res.getScaledHeight() / 2 - this.getTotalToastHeight(toasts) / 2;
            default:
        }

        return 0;
    }

    protected int getTotalToastHeight(ArrayList<ToastInstance> list)
    {
        int height = 0;

        for (int i = 0; i < list.size(); ++i)
        {
            height += list.get(i).getHeight();
        }

        return height;
    }

    public static class ToastInstance
    {
        protected final WidgetToast toast;
        protected final HudAlignment alignment;
        protected long animationStartTime;
        protected long fullyVisibleStartTime;
        protected boolean expired;
        protected long animationTime;
        protected long currentTime;

        protected ToastInstance(HudAlignment alignment, WidgetToast toast)
        {
            this.alignment = alignment;
            this.toast = toast;
            this.animationTime = 300L;
            this.animationStartTime = -1L;
            this.fullyVisibleStartTime = -1L;
            this.expired = false;
        }

        public WidgetToast getToast()
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

            switch (this.alignment)
            {
                case TOP_RIGHT:
                case BOTTOM_RIGHT:
                    x = res.getScaledWidth() - this.toast.getWidth();
                    break;
                case CENTER:
                    x = res.getScaledWidth() / 2 - this.toast.getWidth() / 2;
                    break;
                case TOP_LEFT:
                case BOTTOM_LEFT:
                    animationOffset = -animationOffset;
                    break;
                default:
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

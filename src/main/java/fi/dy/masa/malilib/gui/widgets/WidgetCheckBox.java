package fi.dy.masa.malilib.gui.widgets;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class WidgetCheckBox extends WidgetBase
{
    protected final Minecraft mc;
    protected final String displayText;
    protected final IGuiIcon widgetUnchecked;
    protected final IGuiIcon widgetChecked;
    protected final List<String> hoverInfo;
    protected final int textWidth;
    protected boolean checked;
    @Nullable
    protected ISelectionListener<WidgetCheckBox> listener;

    public WidgetCheckBox(int x, int y, float zLevel, IGuiIcon widgetUnchecked,
            IGuiIcon widgetChecked, String text, Minecraft mc)
    {
        this(x, y, zLevel, widgetUnchecked, widgetChecked, text, mc, null);
    }

    public WidgetCheckBox(int x, int y, float zLevel, IGuiIcon widgetUnchecked,
            IGuiIcon widgetChecked, String text, Minecraft mc, @Nullable String hoverInfo)
    {
        super(x, y, widgetUnchecked.getWidth() + 3 + mc.fontRenderer.getStringWidth(text),
                Math.max(mc.fontRenderer.FONT_HEIGHT, widgetChecked.getHeight()), zLevel);

        this.mc = mc;
        this.displayText = text;
        this.textWidth = mc.fontRenderer.getStringWidth(text);
        this.widgetUnchecked = widgetUnchecked;
        this.widgetChecked = widgetChecked;

        if (hoverInfo != null)
        {
            //hoverInfo = I18n.format(hoverInfo);
            String[] parts = hoverInfo.split("\\n");
            this.hoverInfo = ImmutableList.copyOf(parts);
        }
        else
        {
            this.hoverInfo = ImmutableList.of();
        }
    }

    public void setListener(@Nullable ISelectionListener<WidgetCheckBox> listener)
    {
        this.listener = listener;
    }

    public boolean isChecked()
    {
        return this.checked;
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;

        if (this.listener != null)
        {
            this.listener.onSelectionChange(this);
        }
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        this.setChecked(! this.checked);
        return true;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        IGuiIcon icon = this.checked ? this.widgetChecked : this.widgetUnchecked;

        GlStateManager.color(1f, 1f, 1f);
        this.mc.getTextureManager().bindTexture(icon.getTexture());
        icon.renderAt(this.x, this.y, this.zLevel, false, false);

        int iw = icon.getWidth();
        int y = this.y + (this.height - this.mc.fontRenderer.FONT_HEIGHT) / 2;
        int textColor = this.checked ? 0xFFFFFFFF : 0xB0B0B0B0;

        this.mc.fontRenderer.drawString(this.displayText, this.x + iw + 3, y, textColor);
    }

    @Override
    public void postRenderHovered(int mouseX, int mouseY, boolean selected)
    {
        if (this.hoverInfo.isEmpty() == false)
        {
            this.mc.currentScreen.drawHoveringText(this.hoverInfo, mouseX, mouseY);
        }
    }
}

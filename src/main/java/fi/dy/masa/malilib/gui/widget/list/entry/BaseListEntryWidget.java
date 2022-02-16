package fi.dy.masa.malilib.gui.widget.list.entry;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.gui.util.BackgroundSettings;
import fi.dy.masa.malilib.gui.util.BorderSettings;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.ContainerWidget;
import fi.dy.masa.malilib.render.ShapeRenderUtils;

public class BaseListEntryWidget extends ContainerWidget
{
    protected final BackgroundSettings selectedBgSettings = new BackgroundSettings(0x50FFFFFF);
    protected final BorderSettings selectedBorderSettings = new BorderSettings();
    protected final int listIndex;
    protected final int originalListIndex;
    protected boolean isOdd;
    protected int keyboardNavigationHighlightColor = 0xFFFF5000;
    protected int selectedBackgroundColor = 0x50FFFFFF;

    public BaseListEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex)
    {
        super(x, y, width, height);

        this.listIndex = listIndex;
        this.originalListIndex = originalListIndex;

        this.selectedBgSettings.setEnabled(true);
        this.selectedBorderSettings.setEnabled(true);
        this.selectedBorderSettings.setColor(MaLiLibConfigs.Generic.SELECTED_LIST_ENTRY_COLOR.getIntegerValue());

        int hoverColor = MaLiLibConfigs.Generic.HOVERED_LIST_ENTRY_COLOR.getIntegerValue();
        this.getBackgroundRenderer().getHoverSettings().setEnabledAndColor(true, hoverColor);
        this.getBackgroundRenderer().getNormalSettings().setColor(this.isOdd ? 0x20FFFFFF : 0x30FFFFFF);
        this.setIsOdd((listIndex & 0x1) != 0);
    }

    public void setIsOdd(boolean isOdd)
    {
        this.isOdd = isOdd;
    }

    public void setSelectedBackgroundColor(int selectedBackgroundColor)
    {
        this.selectedBackgroundColor = selectedBackgroundColor;
    }

    public int getListIndex()
    {
        return this.listIndex;
    }

    /**
     * This gets called from BaseListWidget before the widgets
     * are cleared before being re-created. This allows for example
     * config widgets to save their changes before being destroyed.
     */
    public void onAboutToDestroy()
    {
    }

    /**
     * Focuses this widget.
     * <br><br>
     * What this means is defined by the implementation.
     * In most cases it would be for example focusing a text field
     * in a newly created entry widget.
     */
    public void focusWidget()
    {
    }

    /**
     * Returns true if this widget can be selected by clicking at the given point
     */
    public boolean canSelectAt(int mouseX, int mouseY, int mouseButton)
    {
        return mouseButton == 0;
    }

    protected boolean isSelected()
    {
        return false;
    }

    protected boolean isKeyboardNavigationSelected()
    {
        return false;
    }

    @Override
    protected BackgroundSettings getActiveBackgroundSettings(ScreenContext ctx)
    {
        if (this.isSelected())
        {
            return this.selectedBgSettings;
        }

        return super.getActiveBackgroundSettings(ctx);
    }

    @Override
    protected BorderSettings getActiveBorderSettings(ScreenContext ctx)
    {
        if (this.isSelected())
        {
            return this.selectedBorderSettings;
        }

        return super.getActiveBorderSettings(ctx);
    }

    protected void renderKeyboardNavigationHighlight(int x, int y, float z, int width, int height, ScreenContext ctx)
    {
        if (this.isKeyboardNavigationSelected())
        {
            ShapeRenderUtils.renderRectangle(x            , y, z, 2, height, this.keyboardNavigationHighlightColor);
            ShapeRenderUtils.renderRectangle(x + width - 2, y, z, 2, height, this.keyboardNavigationHighlightColor);
        }
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        this.renderKeyboardNavigationHighlight(x, y, z, this.getWidth(), this.getHeight(), ctx);
    }
}

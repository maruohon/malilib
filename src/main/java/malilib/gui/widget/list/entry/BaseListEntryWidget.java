package malilib.gui.widget.list.entry;

import malilib.MaLiLibConfigs;
import malilib.gui.util.BackgroundSettings;
import malilib.gui.util.BorderSettings;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.ContainerWidget;
import malilib.render.ShapeRenderUtils;

public class BaseListEntryWidget extends ContainerWidget
{
    protected final BackgroundSettings selectedBgSettings = new BackgroundSettings(0x50FFFFFF);
    protected final BorderSettings selectedBorderSettings = new BorderSettings();
    protected final int listIndex;
    protected final int originalListIndex;
    protected boolean isOdd;
    protected int keyboardNavigationHighlightColor = 0xFFFF5000;

    public BaseListEntryWidget(DataListEntryWidgetData constructData)
    {
        super(constructData.x,
              constructData.y,
              constructData.width,
              constructData.height);

        this.listIndex = constructData.listIndex;
        this.originalListIndex = constructData.originalListIndex;

        this.selectedBgSettings.setEnabled(true);
        this.selectedBorderSettings.setEnabled(true);
        this.selectedBorderSettings.setColor(MaLiLibConfigs.Generic.SELECTED_LIST_ENTRY_COLOR.getIntegerValue());

        int hoverColor = MaLiLibConfigs.Generic.HOVERED_LIST_ENTRY_COLOR.getIntegerValue();
        this.setIsOdd((this.listIndex & 0x1) != 0);
        this.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, this.isOdd ? 0xC0101010 : 0xC0202020);
        this.getBackgroundRenderer().getHoverSettings().setEnabledAndColor(true, hoverColor);
    }

    public void setIsOdd(boolean isOdd)
    {
        this.isOdd = isOdd;
    }

    /**
     * @return the list index of the data entry this widget corresponds to, in the backing data list.
     * This can be -1 if the widget does not correspond to a data entry.
     */
    public int getDataListIndex()
    {
        return this.listIndex;
    }

    /**
     * This gets called from BaseListWidget before the widgets
     * are cleared before being re-created. This allows for example
     * config widgets to save their changes before being destroyed
     * when the list is scrolled after editing a value.
     */
    public void onAboutToDestroy()
    {
    }

    /**
     * Focuses this widget.
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
            ShapeRenderUtils.renderRectangle(x            , y, z, 2, height, this.keyboardNavigationHighlightColor, ctx);
            ShapeRenderUtils.renderRectangle(x + width - 2, y, z, 2, height, this.keyboardNavigationHighlightColor, ctx);
        }
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        this.renderKeyboardNavigationHighlight(x, y, z, this.getWidth(), this.getHeight(), ctx);
    }
}

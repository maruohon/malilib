package fi.dy.masa.malilib.gui.widget.list.entry;

import fi.dy.masa.malilib.gui.widget.ScreenContext;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.overlay.widget.ConfigStatusIndicatorContainerWidget;
import fi.dy.masa.malilib.overlay.widget.sub.BaseConfigStatusIndicatorWidget;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;

public class ConfigStatusIndicatorEntryWidget extends BaseOrderableListEditEntryWidget<BaseConfigStatusIndicatorWidget<?>>
{
    protected final ConfigStatusIndicatorContainerWidget containerWidget;
    protected final GenericButton configureButton;
    protected final GenericButton removeButton;

    public ConfigStatusIndicatorEntryWidget(int x, int y, int width, int height,
                                            int listIndex, int originalListIndex,
                                            BaseConfigStatusIndicatorWidget<?> data,
                                            DataListWidget<BaseConfigStatusIndicatorWidget<?>> listWidget,
                                            ConfigStatusIndicatorContainerWidget containerWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        this.useAddButton = false;
        this.useRemoveButton = false;

        this.containerWidget = containerWidget;
        this.configureButton = new GenericButton(0, 0, -1, 16, "malilib.gui.button.label.configure");
        this.configureButton.setActionListener(this::openEditScreen);

        this.removeButton = new GenericButton(0, 0, -1, 16, "malilib.gui.button.label.remove");
        this.removeButton.setActionListener(this::removeInfoRendererWidget);
    }

    public void removeInfoRendererWidget()
    {
        this.containerWidget.removeWidget(this.data);
        this.listWidget.refreshEntries();
    }

    public void openEditScreen()
    {
        this.getData().openEditScreen();
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.configureButton);
        this.addWidget(this.removeButton);
    }

    @Override
    protected void updateSubWidgetsToGeometryChangesPre(int x, int y)
    {
        super.updateSubWidgetsToGeometryChangesPre(x, y);

        x = this.getX();
        y = this.getY() + this.getHeight() / 2 - this.configureButton.getHeight() / 2;
        int rightX = x + this.getWidth();

        rightX -= this.removeButton.getWidth() + 2;
        this.removeButton.setPosition(rightX, y);

        rightX -= this.configureButton.getWidth() + 2;
        this.configureButton.setPosition(rightX, y);

        this.nextWidgetX = this.configureButton.getX() - 36;
        this.draggableRegionEndX = this.nextWidgetX - 1;
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        int width = this.getWidth();
        int height = this.getHeight();

        // Draw a lighter background for the hovered and the selected entry
        if (this.isHoveredForRender(ctx))
        {
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, 0x60FFFFFF);
        }
        else if (this.isOdd)
        {
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, 0x20FFFFFF);
        }
        // Draw a slightly lighter background for even entries
        else
        {
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, 0x40FFFFFF);
        }

        super.renderAt(x, y, z, ctx);

        int ly = y + height / 2 - this.fontHeight / 2;
        this.renderTextLine(x + 4, ly, z, 0xFFFFFFFF, true, ctx, this.data.getStyledName());
    }

    @Override
    public void postRenderHovered(ScreenContext ctx)
    {
        super.postRenderHovered(ctx);

        BaseConfigStatusIndicatorWidget<?> widget = this.data;
        int width = widget.getWidth();
        int height = widget.getHeight();
        float z = this.getZLevel();
        int x = ctx.mouseX + 10;
        int y = ctx.mouseY - 5;

        ShapeRenderUtils.renderRectangle(x - 2, y - 2, z + 10f, width + 4, height + 4, 0xC0000000);
        widget.renderAt(x, y, z + 15f, ctx);
    }
}

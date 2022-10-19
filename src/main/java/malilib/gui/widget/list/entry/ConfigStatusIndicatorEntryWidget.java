package malilib.gui.widget.list.entry;

import malilib.gui.util.ScreenContext;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.button.OnOffButton;
import malilib.overlay.widget.ConfigStatusIndicatorContainerWidget;
import malilib.overlay.widget.sub.BaseConfigStatusIndicatorWidget;
import malilib.render.ShapeRenderUtils;

public class ConfigStatusIndicatorEntryWidget extends BaseOrderableListEditEntryWidget<BaseConfigStatusIndicatorWidget<?>>
{
    protected final ConfigStatusIndicatorContainerWidget containerWidget;
    protected final GenericButton toggleButton;
    protected final GenericButton configureButton;
    protected final GenericButton removeButton;

    public ConfigStatusIndicatorEntryWidget(BaseConfigStatusIndicatorWidget<?> data,
                                            DataListEntryWidgetData constructData,
                                            ConfigStatusIndicatorContainerWidget containerWidget)
    {
        super(data, constructData);

        this.useAddButton = false;
        this.useRemoveButton = false;
        this.useMoveButtons = false;
        this.containerWidget = containerWidget;

        this.toggleButton = OnOffButton.simpleSlider(16, this.data::isEnabled, this.data::toggleEnabled);
        this.configureButton = GenericButton.create(16, "malilib.button.misc.configure", this::openEditScreen);
        this.removeButton = GenericButton.create(16, "malilib.button.misc.remove", this::removeInfoRendererWidget);

        this.setText(data.getStyledName());
        this.getBackgroundRenderer().getNormalSettings().setEnabled(true);
        this.getBackgroundRenderer().getNormalSettings().setColor(this.isOdd ? 0x30707070 : 0x50707070);
        this.getBackgroundRenderer().getHoverSettings().setColor(0x50909090);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.toggleButton);
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

        rightX -= this.toggleButton.getWidth() + 2;
        this.toggleButton.setPosition(rightX, y);

        this.nextWidgetX = this.toggleButton.getX() - 36;
        this.draggableRegionEndX = this.nextWidgetX - 1;
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
    public void postRenderHovered(ScreenContext ctx)
    {
        super.postRenderHovered(ctx);

        BaseConfigStatusIndicatorWidget<?> widget = this.data;
        int width = widget.getWidth();
        int height = widget.getHeight();
        float z = this.getZ();
        int x = ctx.mouseX + 10;
        int y = ctx.mouseY - 5;

        ShapeRenderUtils.renderRectangle(x - 2, y - 2, z + 10f, width + 4, height + 4, 0xC0000000);
        widget.renderAt(x, y, z + 15f, ctx);
    }
}

package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.indicator.ConfigStatusIndicatorGroupEditScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.button.OnOffButton;
import fi.dy.masa.malilib.gui.widget.button.OnOffStyle;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.overlay.InfoWidgetManager;
import fi.dy.masa.malilib.overlay.widget.ConfigStatusIndicatorContainerWidget;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;

public class ConfigStatusIndicatorContainerEntryWidget extends BaseDataListEntryWidget<ConfigStatusIndicatorContainerWidget>
{
    protected final LabelWidget nameLabelWidget;
    protected final GenericButton toggleButton;
    protected final GenericButton configureButton;
    protected final GenericButton removeButton;

    public ConfigStatusIndicatorContainerEntryWidget(int x, int y, int width, int height,
                                                     int listIndex, int originalListIndex,
                                                     ConfigStatusIndicatorContainerWidget data,
                                                     @Nullable DataListWidget<? extends ConfigStatusIndicatorContainerWidget> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        this.nameLabelWidget = new LabelWidget(0, 0, -1, -1, 0xFFFFFFFF, data.getName());

        this.toggleButton = new OnOffButton(0, 0, -1, 20, OnOffStyle.SLIDER_ON_OFF, data::isEnabled, null);
        this.toggleButton.setActionListener(data::toggleEnabled);

        this.configureButton = new GenericButton(0, 0, -1, 20, "malilib.gui.button.label.configure");
        this.configureButton.setActionListener(this::openEditScreen);

        this.removeButton = new GenericButton(0, 0, -1, 20, "malilib.gui.button.label.remove");
        this.removeButton.setActionListener(this::removeInfoRendererWidget);
    }

    public void removeInfoRendererWidget()
    {
        InfoWidgetManager.INSTANCE.removeWidget(this.data);
        this.listWidget.refreshEntries();
    }

    public void openEditScreen()
    {
        ConfigStatusIndicatorGroupEditScreen screen = new ConfigStatusIndicatorGroupEditScreen(this.data);
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();
        
        this.addWidget(this.toggleButton);
        this.addWidget(this.nameLabelWidget);
        this.addWidget(this.configureButton);
        this.addWidget(this.removeButton);
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int x = this.getX();
        int y = this.getY();
        int rightX = x + this.getWidth();
        int height = this.getHeight();

        int tmpY = y + height / 2 - this.nameLabelWidget.getHeight() / 2;
        this.nameLabelWidget.setPosition(x + 2, tmpY);

        tmpY = y + height / 2 - this.removeButton.getHeight() / 2;
        rightX -= this.removeButton.getWidth() + 2;
        this.removeButton.setPosition(rightX, tmpY);

        rightX -= this.configureButton.getWidth() + 2;
        this.configureButton.setPosition(rightX, tmpY);

        rightX -= this.toggleButton.getWidth() + 2;
        this.toggleButton.setPosition(rightX, tmpY);
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        int width = this.getWidth();
        int height = this.getHeight();

        // Draw a lighter background for the hovered and the selected entry
        if (hovered)
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

        super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hovered);
    }
}

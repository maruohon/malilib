package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.button.OnOffButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.overlay.widget.InfoRendererWidget;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public class BaseInfoRendererWidgetEntryWidget extends BaseDataListEntryWidget<InfoRendererWidget>
{
    protected final GenericButton toggleButton;
    protected final GenericButton configureButton;
    protected final GenericButton removeButton;
    protected boolean canConfigure;
    protected boolean canRemove;
    protected boolean canToggle;

    public BaseInfoRendererWidgetEntryWidget(int x, int y, int width, int height,
                                             int listIndex, int originalListIndex,
                                             InfoRendererWidget data,
                                             @Nullable DataListWidget<? extends InfoRendererWidget> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        this.toggleButton = OnOffButton.simpleSlider(20, data::isEnabled, data::toggleEnabled);
        this.configureButton = GenericButton.create("malilib.button.misc.configure", data::openEditScreen);
        this.removeButton = GenericButton.create("malilib.button.misc.remove", this::removeInfoRendererWidget);

        this.setText(StyledTextLine.of(data.getName()));
        this.getBackgroundRenderer().getNormalSettings().setEnabled(true);

        this.data.initListEntryWidget(this);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        if (this.canToggle)
        {
            this.addWidget(this.toggleButton);
        }

        if (this.canConfigure)
        {
            this.addWidget(this.configureButton);
        }

        if (this.canRemove)
        {
            this.addWidget(this.removeButton);
        }
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int x = this.getX();
        int y = this.getY();
        int rightX = x + this.getWidth();
        int height = this.getHeight();
        int tmpY;

        if (this.canRemove)
        {
            tmpY = y + height / 2 - this.removeButton.getHeight() / 2;
            rightX -= this.removeButton.getWidth() + 2;
            this.removeButton.setPosition(rightX, tmpY);
        }

        if (this.canConfigure)
        {
            tmpY = y + height / 2 - this.configureButton.getHeight() / 2;
            rightX -= this.configureButton.getWidth() + 2;
            this.configureButton.setPosition(rightX, tmpY);
        }

        if (this.canToggle)
        {
            tmpY = y + height / 2 - this.toggleButton.getHeight() / 2;
            rightX -= this.toggleButton.getWidth() + 2;
            this.toggleButton.setPosition(rightX, tmpY);
        }
    }

    public void setCanConfigure(boolean canConfigure)
    {
        this.canConfigure = canConfigure;
    }

    public void setCanRemove(boolean canRemove)
    {
        this.canRemove = canRemove;
    }

    public void setCanToggle(boolean canToggle)
    {
        this.canToggle = canToggle;
    }

    public void removeInfoRendererWidget()
    {
        this.scheduleTask(() -> {
            Registry.INFO_WIDGET_MANAGER.removeWidget(this.data);
            this.listWidget.refreshEntries();
        });
    }
}

package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.button.OnOffButton;
import fi.dy.masa.malilib.gui.widget.button.OnOffStyle;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.overlay.InfoWidgetManager;
import fi.dy.masa.malilib.overlay.widget.InfoRendererWidget;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public abstract class BaseInfoRendererWidgetEntryWidget<TYPE extends InfoRendererWidget> extends BaseDataListEntryWidget<TYPE>
{
    protected final GenericButton toggleButton;
    protected final GenericButton configureButton;
    protected final GenericButton removeButton;
    protected boolean canConfigure;
    protected boolean canRemove;
    protected boolean canToggle;

    public BaseInfoRendererWidgetEntryWidget(int x, int y, int width, int height,
                                             int listIndex, int originalListIndex,
                                             TYPE data,
                                             @Nullable DataListWidget<? extends TYPE> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        this.toggleButton = new OnOffButton(0, 0, -1, 20, OnOffStyle.SLIDER_ON_OFF, data::isEnabled, null);
        this.toggleButton.setActionListener(data::toggleEnabled);

        this.configureButton = new GenericButton(0, 0, -1, 20, "malilib.gui.button.label.configure");
        this.configureButton.setActionListener(data::openEditScreen);

        this.removeButton = new GenericButton(0, 0, -1, 20, "malilib.gui.button.label.remove");
        this.removeButton.setActionListener(this::removeInfoRendererWidget);

        this.setText(StyledTextLine.of(data.getName()));
        this.setRenderNormalBackground(true);
    }

    public void removeInfoRendererWidget()
    {
        InfoWidgetManager.INSTANCE.removeWidget(this.data);
        this.listWidget.refreshEntries();
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
}

package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.CustomIconEditScreen;
import fi.dy.masa.malilib.gui.config.CustomIconListScreen;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.icon.IconRegistry;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public class IconEntryWidget extends BaseDataListEntryWidget<Icon>
{
    protected final GenericButton editButton;
    protected final GenericButton removeButton;

    public IconEntryWidget(int x, int y, int width, int height,
                           int listIndex, int originalListIndex,
                           @Nullable Icon data,
                           @Nullable DataListWidget<? extends Icon> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        this.editButton = new GenericButton(20, "malilib.gui.button.edit");
        this.editButton.setActionListener(this::openEditScreen);

        this.removeButton = new GenericButton(20, "malilib.gui.button.remove");
        this.removeButton.setActionListener(this::removeIcon);

        int w = data.getWidth();
        int h = data.getHeight();
        int u = data.getU();
        int v = data.getV();
        String texture = data.getTexture().toString();

        this.iconOffset.setXOffset(4);
        this.textOffset.setXOffset(28);
        this.icon = data;
        this.setText(StyledTextLine.of(String.format("%d x %d @ [ %d, %d ] @ %s", w, h, u, v, texture)));
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.editButton);
        this.addWidget(this.removeButton);
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int x = this.getRight() - this.removeButton.getWidth() - 2;
        int y = this.getY() + 1;

        this.removeButton.setPosition(x, y);

        x = this.removeButton.getX() - this.editButton.getWidth() - 2;
        this.editButton.setPosition(x, y);
    }

    @Override
    protected void renderIcon(int x, int y, float z, boolean enabled, boolean hovered, ScreenContext ctx)
    {
        if (this.icon != null)
        {
            int width = this.icon.getWidth();
            int height = this.icon.getHeight();
            int maxSize = this.getHeight() - 2;

            if (width > maxSize || height > maxSize)
            {
                double scale = (double) maxSize / (double) Math.max(width, height);
                width = (int) Math.floor(scale * width);
                height = (int) Math.floor(scale * height);
            }

            x = this.getIconPositionX(x, width);
            y = this.getIconPositionY(y, height);

            this.icon.renderScaledAt(x, y, z + 0.025f, width, height);
        }
    }

    protected void openEditScreen()
    {
        CustomIconListScreen screen = GuiUtils.getCurrentScreenIfMatches(CustomIconListScreen.class);

        if (screen != null)
        {
            CustomIconEditScreen editScreen = new CustomIconEditScreen(this.data, this::replaceIcon);
            editScreen.setParent(screen);
            BaseScreen.openPopupScreen(editScreen);
        }
    }

    protected void replaceIcon(Icon icon)
    {
        this.scheduleTask(() -> {
            IconRegistry.INSTANCE.unregisterUserIcon(this.data);
            IconRegistry.INSTANCE.registerUserIcon(icon);
            this.listWidget.refreshEntries();
        });
    }

    protected void removeIcon()
    {
        this.scheduleTask(() -> {
            IconRegistry.INSTANCE.unregisterUserIcon(this.data);
            this.listWidget.refreshEntries();
        });
    }
}

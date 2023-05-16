package malilib.gui.widget.list.entry;

import com.google.common.collect.ImmutableList;

import malilib.gui.BaseScreen;
import malilib.gui.edit.CustomIconEditScreen;
import malilib.gui.edit.CustomIconListScreen;
import malilib.gui.icon.Icon;
import malilib.gui.icon.NamedIcon;
import malilib.gui.util.GuiUtils;
import malilib.gui.widget.button.GenericButton;
import malilib.registry.Registry;
import malilib.render.text.StyledTextLine;

public class IconEntryWidget extends BaseDataListEntryWidget<NamedIcon>
{
    protected final GenericButton editButton;
    protected final GenericButton removeButton;

    public IconEntryWidget(NamedIcon data, DataListEntryWidgetData constructData)
    {
        super(data, constructData);

        this.editButton = GenericButton.create(20, "malilib.button.misc.edit", this::openEditScreen);
        this.removeButton = GenericButton.create(20, "malilib.button.misc.remove", this::removeIcon);

        this.downScaleIcon = true;
        this.iconOffset.setXOffset(4);
        this.textOffset.setXOffset(28);
        this.setIcon(data);
        this.setText(StyledTextLine.unParsed(data.getName()));
        this.getHoverInfoFactory().addTextLines(getHoverInfoForIcon(data));

        this.getBackgroundRenderer().getNormalSettings().setEnabled(true);
        this.getBackgroundRenderer().getNormalSettings().setColor(this.isOdd ? 0x30707070 : 0x50707070);
        this.getBackgroundRenderer().getHoverSettings().setColor(0x50909090);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.editButton);
        this.addWidget(this.removeButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int y = this.getY() + 1;

        this.removeButton.setRight(this.getRight() - 2);
        this.removeButton.setY(y);

        this.editButton.setRight(this.removeButton.getX() - 2);
        this.editButton.setY(y);
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

    protected void replaceIcon(NamedIcon icon)
    {
        this.scheduleTask(() -> {
            Registry.ICON.unregisterUserIcon(this.data);
            Registry.ICON.registerUserIcon(icon);
            this.listWidget.refreshEntries();
        });
    }

    protected void removeIcon()
    {
        this.scheduleTask(() -> {
            Registry.ICON.unregisterUserIcon(this.data);
            this.listWidget.refreshEntries();
        });
    }

    public static ImmutableList<StyledTextLine> getHoverInfoForIcon(Icon icon)
    {
        int w = icon.getWidth();
        int h = icon.getHeight();
        int u = icon.getU();
        int v = icon.getV();
        int sw = icon.getTextureSheetWidth();
        int sh = icon.getTextureSheetHeight();
        String texture = icon.getTexture().toString();

        return StyledTextLine.translate("malilib.hover.custom_icon.info", u, v, w, h, sw, sh, texture);
    }
}

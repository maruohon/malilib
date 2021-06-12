package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.action.ActionRegistry;
import fi.dy.masa.malilib.action.AliasAction;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StyledTextUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class AliasActionEntryWidget extends BaseDataListEntryWidget<AliasAction>
{
    protected final GenericButton removeButton;

    public AliasActionEntryWidget(int x, int y, int width, int height, int listIndex,
                                  int originalListIndex, @Nullable AliasAction data,
                                  @Nullable DataListWidget<? extends AliasAction> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        StyledTextLine nameText = data.getWidgetDisplayName();
        this.setText(StyledTextUtils.clampStyledTextToMaxWidth(nameText, width - 20, LeftRight.RIGHT, " ..."));

        this.removeButton = GenericButton.createIconOnly(DefaultIcons.LIST_REMOVE_MINUS_9);
        this.removeButton.translateAndAddHoverStrings("malilib.gui.button.hover.list.remove");
        this.removeButton.setActionListener(this::removeAlias);

        this.getBackgroundRenderer().getHoverSettings().setEnabled(false);
        this.getBorderRenderer().getHoverSettings().setBorderWidthAndColor(1, 0xFF00FF60);

        this.getHoverInfoFactory().setTextLineProvider("action_info", data::getHoverInfo);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.removeButton);
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        this.removeButton.setPosition(this.getRight() - 12, this.getY() + 2);
    }

    protected void removeAlias()
    {
        ActionRegistry.INSTANCE.removeAlias(this.data.getRegistryName());
        this.listWidget.refreshEntries();
    }
}

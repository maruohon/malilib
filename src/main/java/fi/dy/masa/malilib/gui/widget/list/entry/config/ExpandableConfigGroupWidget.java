package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.config.ExpandableConfigGroup;
import fi.dy.masa.malilib.gui.icon.BaseIcon;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.widget.IconWidget;

public class ExpandableConfigGroupWidget extends BaseConfigOptionWidget<ExpandableConfigGroup>
{
    protected final ExpandableConfigGroup config;
    protected final IconWidget plusMinusIconWidget;
    protected final IconWidget arrowIconWidget;

    public ExpandableConfigGroupWidget(int x, int y, int width, int height, int listIndex,
                                       int originalListIndex, ExpandableConfigGroup config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.config = config;

        this.setBorderColor(0xFFFFFFFF);
        this.setHoveredBorderWidth(1);

        this.arrowIconWidget = new IconWidget(0, 0, this.getArrowIcon());
        this.arrowIconWidget.setEnabled(true);
        this.arrowIconWidget.setDoHighlight(true);
        this.arrowIconWidget.setRenderHoverChecker(this::isHoveredForRender);

        this.plusMinusIconWidget = new IconWidget(0, 0, this.getPlusMinusIcon());
        this.plusMinusIconWidget.setEnabled(true);
        this.plusMinusIconWidget.setDoHighlight(true);
        this.plusMinusIconWidget.setRenderHoverChecker(this::isHoveredForRender);
    }

    protected Icon getArrowIcon()
    {
        return this.config.isExpanded() ? BaseIcon.ARROW_DOWN : BaseIcon.ARROW_RIGHT;
    }

    protected Icon getPlusMinusIcon()
    {
        return this.config.isExpanded() ? BaseIcon.GROUP_COLLAPSE_MINUS : BaseIcon.GROUP_EXPAND_PLUS;
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.plusMinusIconWidget);
        this.addWidget(this.arrowIconWidget);
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int x = this.getX();
        int y = this.getY();
        int height = this.getHeight();

        this.plusMinusIconWidget.setIcon(this.getPlusMinusIcon());
        this.plusMinusIconWidget.setPosition(x + 2, y + (height - this.plusMinusIconWidget.getHeight()) / 2);
        this.arrowIconWidget.setIcon(this.getArrowIcon());
        this.arrowIconWidget.setPosition(this.getElementsStartPosition(), y + (height - this.arrowIconWidget.getHeight()) / 2);

        int tx = this.plusMinusIconWidget.getRight();
        this.configNameLabelWidget.setX(tx);
        this.configOwnerLabelWidget.setX(tx);

        int labelLeftPadding = this.getNestingOffset(this.ctx.getNestingLevel()) + 2;
        this.configNameLabelWidget.setPaddingLeft(labelLeftPadding);
        this.configOwnerLabelWidget.setPaddingLeft(labelLeftPadding);
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        this.config.toggleIsExpanded();
        int sb = this.ctx.getListWidget().getScrollbar().getValue();
        this.ctx.getListWidget().refreshEntries();
        this.ctx.getListWidget().getScrollbar().setValue(sb);
        return true;
    }
}

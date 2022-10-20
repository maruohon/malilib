package malilib.gui.widget.list.entry.config;

import java.util.ArrayList;
import java.util.List;
import malilib.config.group.ExpandableConfigGroup;
import malilib.config.option.ConfigInfo;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.icon.DefaultIcons;
import malilib.gui.icon.MultiIcon;
import malilib.gui.widget.IconWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.render.text.StyledTextLine;

public class ExpandableConfigGroupWidget extends BaseConfigWidget<ExpandableConfigGroup>
{
    protected final ExpandableConfigGroup config;
    protected final IconWidget plusMinusIconWidget;
    protected final IconWidget arrowIconWidget;

    public ExpandableConfigGroupWidget(ExpandableConfigGroup config,
                                       DataListEntryWidgetData constructData,
                                       ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.config = config;

        this.getBorderRenderer().getNormalSettings().setColor(0xFFFFFFFF);
        this.getBorderRenderer().getHoverSettings().setBorderWidth(1);
        this.getHoverInfoFactory().setTextLineProvider("config_list", this::getContainedConfigsHoverInfo);
        this.getHoverInfoFactory().setDynamic(false);

        this.arrowIconWidget = new IconWidget(this.getArrowIcon());
        this.arrowIconWidget.setUseEnabledVariant(true);
        this.arrowIconWidget.setDoHighlight(true);
        this.arrowIconWidget.setRenderHoverChecker(this::isHoveredForRender);

        this.plusMinusIconWidget = new IconWidget(this.getPlusMinusIcon());
        this.plusMinusIconWidget.setUseEnabledVariant(true);
        this.plusMinusIconWidget.setDoHighlight(true);
        this.plusMinusIconWidget.setRenderHoverChecker(this::isHoveredForRender);
    }

    protected MultiIcon getArrowIcon()
    {
        return this.config.isExpanded() ? DefaultIcons.ARROW_DOWN : DefaultIcons.ARROW_RIGHT;
    }

    protected MultiIcon getPlusMinusIcon()
    {
        return this.config.isExpanded() ? DefaultIcons.GROUP_COLLAPSE_MINUS : DefaultIcons.GROUP_EXPAND_PLUS;
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.plusMinusIconWidget);
        this.addWidget(this.arrowIconWidget);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getX();
        int y = this.getY();
        int height = this.getHeight();

        this.plusMinusIconWidget.setIcon(this.getPlusMinusIcon());
        this.plusMinusIconWidget.setPosition(x + 4, y + (height - this.plusMinusIconWidget.getHeight()) / 2);
        this.arrowIconWidget.setIcon(this.getArrowIcon());
        this.arrowIconWidget.setPosition(this.getElementsStartPosition(), y + (height - this.arrowIconWidget.getHeight()) / 2);

        int tx = this.plusMinusIconWidget.getRight();
        this.configOwnerAndNameLabelWidget.setX(tx);

        int labelLeftPadding = this.getNestingOffset(this.ctx.getNestingLevel()) + 5;
        this.configOwnerAndNameLabelWidget.getPadding().setLeft(labelLeftPadding);
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.config.toggleIsExpanded();

        // Clear the cache so that the new expanded or unexpanded set of configs will actually show up
        this.ctx.getListWidget().clearConfigSearchCache();
        this.ctx.getListWidget().refreshEntries();

        return true;
    }

    protected List<StyledTextLine> getContainedConfigsHoverInfo()
    {
        return getContainedConfigsHoverInfo(this.config.getConfigs());
    }

    public static List<StyledTextLine> getContainedConfigsHoverInfo(List<ConfigInfo> configs)
    {
        List<StyledTextLine> lines = new ArrayList<>();

        String titleKey = "malilibdev.hover.config.expandable_config_group.contained_configs";
        String entryKey = "malilibdev.hover.config.expandable_config_group.config_entry";
        final int size = configs.size();
        final int maxEntriesShown = 10;
        int count = Math.min(size, maxEntriesShown);

        if (maxEntriesShown == size - 1)
        {
            count = size;
        }

        lines.add(StyledTextLine.translate(titleKey, size));

        for (int i = 0; i < count; ++i)
        {
            ConfigInfo config = configs.get(i);
            String name = config.getDisplayName();
            String className = config.getClass().getSimpleName();
            lines.add(StyledTextLine.translate(entryKey, name, className));
        }

        if (size > count)
        {
            String footerKey = "malilibdev.hover.config.expandable_config_group.more";
            lines.add(StyledTextLine.translate(footerKey, size - count));
        }

        return lines;
    }
}

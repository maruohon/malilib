package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseDataListEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class BaseConfigWidget<CFG extends ConfigInfo> extends BaseDataListEntryWidget<CFG>
{
    protected final CFG config;
    protected final ConfigWidgetContext ctx;
    protected final GenericButton resetButton;
    protected final LabelWidget configOwnerAndNameLabelWidget;
    protected final StyledTextLine nameText;
    protected final StyledTextLine internalNameText;
    @Nullable protected final StyledTextLine categoryText;

    public BaseConfigWidget(CFG config,
                            DataListEntryWidgetData constructData,
                            ConfigWidgetContext ctx)
    {
        super(config, constructData);

        this.config = config;
        this.ctx = ctx;

        @Nullable String ownerLabel = this.getOwnerText(this.originalListIndex);
        this.categoryText = ownerLabel != null ? StyledTextLine.of(ownerLabel) : null;
        this.nameText = StyledTextLine.translate("malilib.label.config.config_display_name", config.getDisplayName());
        this.internalNameText = StyledTextLine.translate("malilib.label.config.config_internal_name", config.getName());
        this.configOwnerAndNameLabelWidget = new LabelWidget(this.getMaxLabelWidth(), this.getHeight(), 0xFFFFFFFF);
        this.resetButton = GenericButton.create("malilib.button.misc.reset.caps", this::onResetButtonClicked);
        this.resetButton.setEnabledStatusSupplier(this::isResetEnabled);

        EventListener clickHandler = config.getLabelClickHandler();
        List<String> comments = new ArrayList<>();
        Optional<String> o = Optional.empty(); o.ifPresent(comments::add);

        if (clickHandler != null)
        {
            comments.add(StringUtils.translate("malilib.hover.config.click_for_more_information"));
            this.configOwnerAndNameLabelWidget.setClickListener(clickHandler);
            this.configOwnerAndNameLabelWidget.getBorderRenderer()
                    .getHoverSettings().setBorderWidthAndColor(1, 0xFF15D6F0);
        }

        config.getComment().ifPresent(comments::add);

        this.configOwnerAndNameLabelWidget.getHoverInfoFactory().addStrings(comments);

        boolean bgEnabled = MaLiLibConfigs.Generic.CONFIG_WIDGET_BACKGROUND.getBooleanValue();
        this.getBackgroundRenderer().getNormalSettings()
                .setEnabledAndColor(bgEnabled, this.isOdd ? 0x70606060 : 0x70909090);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.configOwnerAndNameLabelWidget);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int nesting = this.getNestingOffset(this.ctx.getNestingLevel());
        boolean showCategory = this.ctx.getListWidget().isShowingOptionsFromOtherCategories();

        this.configOwnerAndNameLabelWidget.setPosition(this.getX(), this.getY());
        this.configOwnerAndNameLabelWidget.getPadding().setLeft(nesting + 4);

        if (showCategory && this.categoryText != null)
        {
            this.configOwnerAndNameLabelWidget.getPadding().setTop(2);
            this.configOwnerAndNameLabelWidget.setLabelStyledTextLines(this.nameText, this.categoryText);
        }
        else if (this.shouldShowInternalName())
        {
            this.configOwnerAndNameLabelWidget.getPadding().setTop(2);
            this.configOwnerAndNameLabelWidget.setLabelStyledTextLines(this.nameText, this.internalNameText);
        }
        else
        {
            this.configOwnerAndNameLabelWidget.getPadding().setTop(7);
            this.configOwnerAndNameLabelWidget.setLabelStyledTextLines(this.nameText);
        }
    }

    protected void onResetButtonClicked()
    {
        this.config.resetToDefault();
        this.updateWidgetState();
    }

    @Override
    public void updateWidgetState()
    {
        this.resetButton.updateWidgetState();
    }

    protected boolean isResetEnabled()
    {
        return this.config.isModified();
    }

    protected boolean shouldShowInternalName()
    {
        return this.ctx.getListWidget().getShowInternalConfigName();
    }

    @Nullable
    protected String getOwnerText(int originalListIndex)
    {
        return this.ctx.getListWidget().getModNameAndCategory(originalListIndex, this.shouldShowInternalName());
    }

    protected int getElementWidth()
    {
        return this.ctx.getListWidget().getElementWidth();
    }

    public boolean wasModified()
    {
        return false;
    }

    public int getMaxLabelWidth()
    {
        return this.ctx.getListWidget().getMaxLabelWidth();
    }

    public int getNestingOffset(int nestingLevel)
    {
        return nestingLevel * 13;
    }

    protected int getElementsStartPosition()
    {
        int nestingLevel = this.ctx.getNestingLevel();
        int offset = this.getNestingOffset(nestingLevel);
        return this.getX() + this.getMaxLabelWidth() + offset + 10;
    }
}

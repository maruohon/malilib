package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.FileConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.FileSelectorScreen;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseDataListEntryWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.FileUtils;
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

    public BaseConfigWidget(int x, int y, int width, int height, int listIndex,
                            int originalListIndex, CFG config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, null);

        this.config = config;
        this.ctx = ctx;

        @Nullable String ownerLabel = this.getOwnerText(originalListIndex);
        this.categoryText = ownerLabel != null ? StyledTextLine.of(ownerLabel) : null;
        this.nameText = StyledTextLine.translate("malilib.label.config.config_display_name", config.getDisplayName());
        this.internalNameText = StyledTextLine.translate("malilib.label.config.config_internal_name", config.getName());
        this.configOwnerAndNameLabelWidget = new LabelWidget(this.getMaxLabelWidth(), height, 0xFFFFFFFF);

        EventListener clickHandler = config.getLabelClickHandler();
        List<String> comments = new ArrayList<>();
        Optional<String> o = Optional.empty(); o.ifPresent(comments::add);

        if (clickHandler != null)
        {
            comments.add(StringUtils.translate("malilib.gui.label.config.hover.click_for_more_information"));
            this.configOwnerAndNameLabelWidget.setClickListener(clickHandler);
            this.configOwnerAndNameLabelWidget.getBorderRenderer().getHoverSettings().setBorderWidthAndColor(1, 0xFF15D6F0);
        }

        config.getComment().ifPresent(comments::add);

        this.configOwnerAndNameLabelWidget.addHoverStrings(comments);
        this.resetButton = new GenericButton("malilib.gui.button.reset.caps");

        boolean bgEnabled = MaLiLibConfigs.Generic.CONFIG_WIDGET_BACKGROUND.getBooleanValue();
        this.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(bgEnabled, this.isOdd ? 0x70606060 : 0x70909090);
    }

    @Override
    public void reAddSubWidgets()
    {
        this.clearWidgets();

        this.addWidget(this.configOwnerAndNameLabelWidget);
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int nesting = this.getNestingOffset(this.ctx.getNestingLevel());
        boolean showCategory = this.ctx.getListWidget().isShowingOptionsFromOtherCategories();

        this.configOwnerAndNameLabelWidget.setPosition(this.getX(), this.getY());
        this.configOwnerAndNameLabelWidget.getPadding().setLeft(nesting + 4);

        if (showCategory && this.categoryText != null)
        {
            this.configOwnerAndNameLabelWidget.getPadding().setTop(2);
            this.configOwnerAndNameLabelWidget.setStyledTextLines(this.nameText, this.categoryText);
        }
        else if (this.shouldShowInternalName())
        {
            this.configOwnerAndNameLabelWidget.getPadding().setTop(2);
            this.configOwnerAndNameLabelWidget.setStyledTextLines(this.nameText, this.internalNameText);
        }
        else
        {
            this.configOwnerAndNameLabelWidget.getPadding().setTop(7);
            this.configOwnerAndNameLabelWidget.setStyledTextLines(this.nameText);
        }
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

    protected void updateResetButton(int x, int y)
    {
        this.resetButton.setPosition(x, y);
        this.updateResetButtonState();
    }

    protected void updateResetButtonState()
    {
        this.resetButton.setEnabled(this.config.isModified());
    }

    public boolean wasModified()
    {
        return false;
    }

    protected GenericButton createFileSelectorWidgets(int y, final FileConfig config,
                                                      final FileSelectorScreenFactory screenFactory,
                                                      String buttonText, String hoverTextKey)
    {
        int x = this.getElementsStartPosition();
        int elementWidth = this.getElementWidth();
        File file = FileUtils.getCanonicalFileIfPossible(config.getValue());

        ArrayList<String> lines = new ArrayList<>();
        StringUtils.splitTextToLines(lines, StringUtils.translate(hoverTextKey, file.getAbsolutePath()), 280);

        GenericButton button = new GenericButton(elementWidth, 20, buttonText);
        button.setPosition(x, y + 1);
        button.setHoverStringProvider("path", () -> lines, 100);
        button.setHoverStringProvider("locked", config::getLockAndOverrideMessages, 101);
        button.setEnabled(config.isLocked() == false);
        button.setActionListener(() -> {
            FileSelectorScreen browserScreen = screenFactory.create();
            browserScreen.setParent(GuiUtils.getCurrentScreen());
            BaseScreen.openScreen(browserScreen);
        });

        this.resetButton.setActionListener(() -> {
            config.resetToDefault();
            this.reAddSubWidgets();
        });

        this.updateResetButton(x + elementWidth + 4, y + 1);
        this.addWidget(button);
        this.addWidget(this.resetButton);

        return button;
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

    public interface FileSelectorScreenFactory
    {
        FileSelectorScreen create();
    }
}

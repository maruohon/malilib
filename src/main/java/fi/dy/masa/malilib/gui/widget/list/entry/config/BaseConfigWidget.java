package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.FileConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.DirectorySelectorScreen;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseDataListEntryWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextStyle;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class BaseConfigWidget<CFG extends ConfigInfo> extends BaseDataListEntryWidget<CFG>
{
    protected final CFG config;
    protected final ConfigWidgetContext ctx;
    protected final GenericButton resetButton;
    protected final LabelWidget configOwnerAndNameLabelWidget;
    protected final StyledTextLine nameText;
    @Nullable protected final StyledTextLine ownerText;

    public BaseConfigWidget(int x, int y, int width, int height, int listIndex,
                            int originalListIndex, CFG config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, null);

        this.config = config;
        this.ctx = ctx;

        String nameLabel = config.getDisplayName();
        @Nullable String ownerLabel = this.ctx.getListWidget().getModNameAndCategoryPrefix(originalListIndex);

        this.ownerText = ownerLabel != null ? StyledTextLine.rawWithStyle(ownerLabel, TextStyle.normal(0xFF686868)) : null;
        this.nameText = StyledTextLine.of(nameLabel);
        this.configOwnerAndNameLabelWidget = new LabelWidget(this.getMaxLabelWidth(), height, 0xFFF0F0F0);

        EventListener clickHandler = config.getLabelClickHandler();
        List<String> comments = new ArrayList<>();
        String comment = config.getComment();

        if (clickHandler != null)
        {
            comments.add(StringUtils.translate("malilib.gui.label.config.hover.click_for_more_information"));
            this.configOwnerAndNameLabelWidget.setClickListener(clickHandler);
            this.configOwnerAndNameLabelWidget.setHoveredBorderColor(0xFF15D6F0);
            this.configOwnerAndNameLabelWidget.setHoveredBorderWidth(1);
        }

        if (comment != null)
        {
            comments.add(comment);
        }

        this.configOwnerAndNameLabelWidget.addHoverStrings(comments);
        this.resetButton = new GenericButton("malilib.gui.button.reset.caps");

        this.setNormalBackgroundColor(this.isOdd ? 0x70606060 : 0x70909090);
        this.setRenderNormalBackground(MaLiLibConfigs.Generic.CONFIG_WIDGET_BACKGROUND.getBooleanValue());
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
        boolean showOwner = this.ctx.getListWidget().isShowingOptionsFromOtherCategories();

        this.configOwnerAndNameLabelWidget.setPosition(this.getX(), this.getY());
        this.configOwnerAndNameLabelWidget.getPadding().setLeft(nesting + 4);

        if (showOwner && this.ownerText != null)
        {
            this.configOwnerAndNameLabelWidget.getPadding().setTop(2);
            this.configOwnerAndNameLabelWidget.setStyledTextLines(this.ownerText, this.nameText);
        }
        else
        {
            this.configOwnerAndNameLabelWidget.getPadding().setTop(7);
            this.configOwnerAndNameLabelWidget.setStyledTextLines(this.nameText);
        }
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
            DirectorySelectorScreen browserScreen = screenFactory.create();
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
        DirectorySelectorScreen create();
    }
}

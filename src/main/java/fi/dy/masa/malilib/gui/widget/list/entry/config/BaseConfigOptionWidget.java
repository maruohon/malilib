package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class BaseConfigOptionWidget<C extends ConfigInfo> extends BaseDataListEntryWidget<C>
{
    protected final ConfigWidgetContext ctx;
    protected final GenericButton resetButton;
    protected final LabelWidget configNameLabelWidget;
    protected final LabelWidget configOwnerLabelWidget;

    public BaseConfigOptionWidget(int x, int y, int width, int height, int listIndex,
                                  int originalListIndex, C config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config);

        this.ctx = ctx;

        String nameLabel = this.data.getDisplayName();
        String ownerLabel = this.ctx.getListWidget().getModNameAndCategoryPrefix(originalListIndex);
        boolean showOwner = this.ctx.getListWidget().isShowingOptionsFromOtherCategories();
        this.configOwnerLabelWidget = new LabelWidget(x + 2, y + 2, 0xFF707070, ownerLabel != null ? ownerLabel : "");
        this.configOwnerLabelWidget.setPaddingLeft(4);

        int lw = this.getMaxLabelWidth();

        this.configNameLabelWidget = new LabelWidget(x, y, lw, 22, 0xFFFFFFFF, nameLabel);
        this.configNameLabelWidget.setPaddingTop(showOwner ? 12 : 7);
        this.configNameLabelWidget.setPaddingLeft(4);

        EventListener clickHandler = config.getLabelClickHandler();
        String comment = config.getComment();
        List<String> comments = new ArrayList<>();

        if (clickHandler != null)
        {
            comments.add(StringUtils.translate("malilib.gui.label.config.hover.click_for_more_information"));
            this.configNameLabelWidget.setClickListener(clickHandler);
            this.configNameLabelWidget.setHoveredBorderColor(0xFF15D6F0);
            this.configNameLabelWidget.setHoveredBorderWidth(1);
        }

        if (comment != null)
        {
            comments.add(comment);
        }

        this.configNameLabelWidget.addHoverStrings(comments);
        this.resetButton = new GenericButton(x, y, -1, 20, StringUtils.translate("malilib.gui.button.reset.caps"));

        this.setBackgroundColor(this.isOdd ? 0x70606060 : 0x70909090);
        this.setBackgroundEnabled(true);
    }

    @Override
    public void reAddSubWidgets()
    {
        this.clearWidgets();

        if (this.ctx.getListWidget().isShowingOptionsFromOtherCategories())
        {
            this.addWidget(this.configOwnerLabelWidget);
        }

        this.addWidget(this.configNameLabelWidget);
    }

    protected int getElementWidth()
    {
        return this.ctx.getListWidget().getElementWidth();
    }

    protected void updateResetButton(int x, int y, ConfigInfo config)
    {
        this.resetButton.setPosition(x, y);
        this.resetButton.setEnabled(config.isModified());
    }

    protected GenericButton createFileSelectorWidgets(int x, int y, final FileConfig config,
                                                      final FileSelectorScreenFactory screenFactory, String buttonText, String hoverTextKey)
    {
        x += this.getMaxLabelWidth() + 10;
        int elementWidth = this.getElementWidth();
        File file = FileUtils.getCanonicalFileIfPossible(config.getFile());

        ArrayList<String> lines = new ArrayList<>();
        StringUtils.splitTextToLines(lines, StringUtils.translate(hoverTextKey, file.getAbsolutePath()), 280);

        GenericButton button = new GenericButton(x, y + 1, elementWidth, 20, buttonText);
        button.addHoverStrings(lines);

        this.addButton(button, (btn, mbtn) -> {
            DirectorySelectorScreen browserScreen = screenFactory.create();
            browserScreen.setParent(GuiUtils.getCurrentScreen());
            BaseScreen.openGui(browserScreen);
        });

        this.resetButton.setPosition(x + elementWidth + 4, y + 1);
        this.resetButton.setEnabled(config.isModified());

        this.addButton(this.resetButton, (btn, mbtn) -> {
            config.resetToDefault();
            this.reAddSubWidgets();
        });

        return button;
    }

    public int getMaxLabelWidth()
    {
        return this.ctx.getListWidget().getMaxLabelWidth();
    }

    public boolean wasModified()
    {
        return false;
    }

    public interface FileSelectorScreenFactory
    {
        DirectorySelectorScreen create();
    }
}

package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.io.File;
import java.util.ArrayList;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.config.option.FileConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.DirectorySelectorScreen;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.WidgetLabel;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseDataListEntryWidget;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class BaseConfigOptionWidget<C extends ConfigInfo> extends BaseDataListEntryWidget<C>
{
    protected final BaseConfigScreen gui;
    protected final ButtonGeneric resetButton;
    protected final WidgetLabel labelWidget;

    public BaseConfigOptionWidget(int x, int y, int width, int height, int listIndex, C config, BaseConfigScreen gui)
    {
        super(x, y, width, height, listIndex, config);

        this.gui = gui;

        this.labelWidget = new WidgetLabel(x + 2, y + 6, 0xFFFFFFFF, this.data.getDisplayName());
        this.labelWidget.addHoverStrings(this.data.getComment());

        this.resetButton = new ButtonGeneric(x, y, -1, 20, StringUtils.translate("malilib.gui.button.reset.caps"));
    }

    @Override
    public void reAddSubWidgets()
    {
        this.clearWidgets();
        this.addWidget(this.labelWidget);
    }

    protected void updateResetButton(int x, int y, ConfigOption<?> config)
    {
        this.resetButton.setPosition(x, y);
        this.resetButton.setEnabled(config.isModified());
    }

    protected ButtonGeneric createFileSelectorWidgets(int x, int y, final FileConfig config,
                                             final FileSelectorScreenFactory screenFactory, String buttonText, String hoverTextKey)
    {
        x += this.getMaxLabelWidth() + 10;
        int elementWidth = this.gui.getConfigElementsWidth();
        File file = FileUtils.getCanonicalFileIfPossible(config.getFile());

        ArrayList<String> lines = new ArrayList<>();
        StringUtils.splitTextToLines(lines, StringUtils.translate(hoverTextKey, file.getAbsolutePath()), 280);

        ButtonGeneric button = new ButtonGeneric(x, y + 1, elementWidth, 20, buttonText);
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
        return this.gui.getMaxLabelWidth();
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

package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.io.File;
import java.util.Collections;
import java.util.List;
import fi.dy.masa.malilib.config.option.FileConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.FileSelectorScreen;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class FileConfigWidget extends BaseConfigOptionWidget<File, FileConfig>
{
    protected final GenericButton openBrowserButton;

    public FileConfigWidget(FileConfig config,
                            DataListEntryWidgetData constructData,
                            ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.openBrowserButton = GenericButton.create(60, 20, this.getButtonLabelKey());
        this.openBrowserButton.setEnabledStatusSupplier(() -> this.config.isLocked() == false);
        this.openBrowserButton.setActionListener(this::openScreen);
        this.openBrowserButton.getHoverInfoFactory().setStringListProvider("path", this::getButtonHoverText, 100);
        this.openBrowserButton.getHoverInfoFactory().setStringListProvider("locked", config::getLockAndOverrideMessages, 101);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.openBrowserButton);
        this.addWidget(this.resetButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getElementsStartPosition();
        int elementWidth = this.getElementWidth();

        this.openBrowserButton.setX(x);
        this.openBrowserButton.centerVerticallyInside(this);
        this.openBrowserButton.setWidth(elementWidth);
        this.resetButton.setX(x + elementWidth + 4);
        this.resetButton.centerVerticallyInside(this);
    }

    @Override
    public void updateWidgetState()
    {
        super.updateWidgetState();
        this.openBrowserButton.getHoverInfoFactory().updateList();
    }

    protected BaseScreen createScreen(File currentDir, File rootDir)
    {
        return new FileSelectorScreen(currentDir, rootDir, this::onPathSelected);
    }

    protected String getButtonLabelKey()
    {
        return "malilib.button.config.select_file";
    }

    protected String getButtonHoverTextKey()
    {
        return "malilib.hover.button.config.selected_file";
    }

    protected void openScreen()
    {
        File rootDir = FileUtils.getRootDirectory();
        File dir = this.getDirectoryFromConfig(rootDir);

        BaseScreen browserScreen = this.createScreen(dir, rootDir);
        browserScreen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(browserScreen);
    }

    protected boolean onPathSelected(File file)
    {
        this.config.setValueFromString(file.getAbsolutePath());
        this.reAddSubWidgets();
        return true;
    }

    protected File getDirectoryFromConfig(File rootDir)
    {
        File file = FileUtils.getCanonicalFileIfPossible(this.config.getValue().getAbsoluteFile());

        if (file == null)
        {
            return rootDir;
        }

        if (file.isDirectory() == false)
        {
            return file.getParentFile();
        }

        return file;
    }

    protected List<String> getButtonHoverText()
    {
        File file = FileUtils.getCanonicalFileIfPossible(this.config.getValue());
        String text = StringUtils.translate(this.getButtonHoverTextKey(), file.getAbsolutePath());
        return Collections.singletonList(text);
    }
}

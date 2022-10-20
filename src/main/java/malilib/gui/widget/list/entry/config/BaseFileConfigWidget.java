package malilib.gui.widget.list.entry.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import malilib.config.option.BaseGenericConfig;
import malilib.gui.BaseScreen;
import malilib.gui.DirectorySelectorScreen;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.util.GuiUtils;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.util.FileUtils;
import malilib.util.StringUtils;

public abstract class BaseFileConfigWidget<T, CFG extends BaseGenericConfig<T>> extends BaseGenericConfigWidget<T, CFG>
{
    protected final GenericButton openBrowserButton;

    public BaseFileConfigWidget(CFG config,
                                DataListEntryWidgetData constructData,
                                ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.openBrowserButton = GenericButton.create(60, 20, this.getButtonLabelKey());
        this.openBrowserButton.setEnabledStatusSupplier(() -> this.config.isLocked() == false);
        this.openBrowserButton.setActionListener(this::openScreen);
        this.openBrowserButton.getHoverInfoFactory().setStringListProvider("path", this::getFileButtonHoverText, 100);
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

    protected abstract Path getFileFromConfig();

    protected abstract void setFileToConfig(Path file);

    protected String getButtonLabelKey()
    {
        return "malilibdev.button.config.select_directory";
    }

    protected String getButtonHoverTextKey()
    {
        return "malilibdev.hover.button.config.selected_directory";
    }

    protected BaseScreen createScreen(Path currentDir, Path rootDir)
    {
        return new DirectorySelectorScreen(currentDir, rootDir, this::onPathSelected);
    }

    protected void openScreen()
    {
        Path rootDir = FileUtils.getRootDirectory();
        Path dir = this.getDirectoryFromConfig(rootDir);

        BaseScreen browserScreen = this.createScreen(dir, rootDir);
        browserScreen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(browserScreen);
    }

    protected boolean onPathSelected(Path file)
    {
        this.setFileToConfig(file);
        this.reAddSubWidgets();
        return true;
    }

    protected Path getDirectoryFromConfig(Path rootDir)
    {
        Path file = this.getFileFromConfig().toAbsolutePath();

        if (file == null)
        {
            return rootDir;
        }

        if (Files.isDirectory(file) == false)
        {
            return file.getParent();
        }

        return file;
    }

    protected List<String> getFileButtonHoverText()
    {
        Path file = this.getFileFromConfig().toAbsolutePath();
        String text = StringUtils.translate(this.getButtonHoverTextKey(), file.toString());
        return Collections.singletonList(text);
    }
}

package fi.dy.masa.malilib.gui;

import java.io.File;
import java.io.FileFilter;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.BaseFileBrowserWidget;
import fi.dy.masa.malilib.util.FileNameUtils;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ToBooleanFunction;

public class FileSelectorScreen extends BaseListScreen<BaseFileBrowserWidget>
{
    protected final GenericButton confirmButton;
    protected final BaseTextFieldWidget fileNameTextField;
    protected final File rootDirectory;
    protected final File currentDirectory;
    protected final ToBooleanFunction<File> fileConsumer;
    protected FileFilter fileFilter = FileUtils.ANY_FILE_FILEFILTER;
    protected String fileNameExtension = "json";
    protected boolean allowCreatingFiles;

    public FileSelectorScreen(File currentDirectory, File rootDirectory, ToBooleanFunction<File> fileConsumer)
    {
        super(10, 28, 20, 58);

        this.currentDirectory = currentDirectory;
        this.rootDirectory = rootDirectory;
        this.fileConsumer = fileConsumer;
        this.confirmButton = GenericButton.create(16, this.getButtonLabel());
        this.confirmButton.setActionListener(this::onConfirm);

        this.fileNameTextField = new BaseTextFieldWidget(300, 16);

        this.setTitle("malilib.title.screen.file_browser");
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.confirmButton);

        if (this.allowCreatingFiles)
        {
            this.addWidget(this.fileNameTextField);
        }
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.getListX();

        if (this.allowCreatingFiles)
        {
            int y = 40;
            this.setListPosition(this.getListX(), y);
            this.fileNameTextField.setPosition(x, y - 20);
            this.fileNameTextField.setFocused(true);
            this.confirmButton.setPosition(this.fileNameTextField.getRight() + 4, y - 20);
        }
        else
        {
            this.confirmButton.setPosition(x, this.y + this.screenHeight - 26);
        }
    }

    protected FileFilter getFileFilter()
    {
        return this.fileFilter;
    }

    public void setFileFilter(FileFilter fileFilter)
    {
        this.fileFilter = fileFilter;
        //this.getListWidget().setFileFilter(fileFilter);
    }

    public void setAllowCreatingFilesWithExtension(String fileNameExtension)
    {
        this.allowCreatingFiles = true;
        this.fileNameExtension = fileNameExtension;
        this.getListWidget().getEntrySelectionHandler().setSelectionListener(this::onSelectionChange);
    }

    public void setConfirmButtonLabel(String translationKey)
    {
        this.confirmButton.setAutomaticWidth(true);
        this.confirmButton.setDisplayString(StringUtils.translate(translationKey));
    }

    protected String getButtonLabel()
    {
        return "malilib.button.config.use_selected_file";
    }

    protected void onConfirm()
    {
        BaseFileBrowserWidget.DirectoryEntry entry = this.getListWidget().getEntrySelectionHandler().getLastSelectedEntry();

        if (this.allowCreatingFiles)
        {
            String name = this.fileNameTextField.getText();

            if (org.apache.commons.lang3.StringUtils.isBlank(name))
            {
                return;
            }

            File dir = this.getListWidget().getCurrentDirectory();

            if (name.endsWith("." + this.fileNameExtension) == false)
            {
                name += "." + this.fileNameExtension;
            }

            if (this.fileConsumer.applyAsBoolean(new File(dir, name)))
            {
                BaseScreen.openScreen(this.getParent());
            }
        }
        else if (entry != null && entry.getType() == BaseFileBrowserWidget.DirectoryEntryType.FILE)
        {
            if (this.fileConsumer.applyAsBoolean(entry.getFullPath()))
            {
                BaseScreen.openScreen(this.getParent());
            }
        }
    }

    public void onSelectionChange(@Nullable BaseFileBrowserWidget.DirectoryEntry entry)
    {
        if (this.allowCreatingFiles && entry != null &&
            entry.getType() == BaseFileBrowserWidget.DirectoryEntryType.FILE)
        {
            this.fileNameTextField.setText(FileNameUtils.getFileNameWithoutExtension(entry.getName()));
        }
    }

    @Override
    protected BaseFileBrowserWidget createListWidget(int listX, int listY, int listWidth, int listHeight)
    {
        BaseFileBrowserWidget widget = new BaseFileBrowserWidget(listX, listY, listWidth, listHeight,
                                                                 this.currentDirectory, this.rootDirectory, null, null);
        widget.setParentScreen(this.getParent());
        widget.setFileFilter(this.getFileFilter());

        return widget;
    }
}

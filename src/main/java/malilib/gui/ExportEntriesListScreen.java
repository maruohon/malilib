package malilib.gui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;

import malilib.gui.widget.BaseTextFieldWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.overlay.message.MessageDispatcher;
import malilib.util.FileNameUtils;
import malilib.util.FileUtils;
import malilib.util.data.json.JsonUtils;

public class ExportEntriesListScreen<T> extends BaseImportExportEntriesListScreen<T>
{
    protected final Function<T, JsonElement> entrySerializer;

    protected final GenericButton exportToClipboardButton;
    protected final GenericButton exportToFileButton;
    protected final GenericButton selectOutputFileButton;
    protected final BaseTextFieldWidget exportFileTextField;

    public ExportEntriesListScreen(List<T> entries,
                                   Function<T, String> entryNameFunction,
                                   Function<T, JsonElement> entrySerializer)
    {
        super(10, 62, 20, 70, entries, entryNameFunction);

        this.entrySerializer = entrySerializer;

        this.exportToClipboardButton = GenericButton.create(16, "malilib.button.export_entries.export_to_clipboard", this::exportSelectedEntriesToClipboard);
        this.exportToFileButton      = GenericButton.create(16, "malilib.button.export_entries.export_to_file", this::exportSelectedEntriesToFile);
        this.selectOutputFileButton  = GenericButton.create(16, "malilib.button.export_entries.select_output_file", this::openOutputFileSelectionScreen);

        this.exportFileTextField = new BaseTextFieldWidget(300, 16);
        this.exportFileTextField.setEmptyValueDisplayString(malilib.util.StringUtils.translate("malilib.label.export_entries.empty_text_field_output_file"));

        this.setTitle("malilib.title.screen.export_entries");
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.exportFileTextField);
        this.addWidget(this.selectOutputFileButton);
        this.addWidget(this.exportToClipboardButton);
        this.addWidget(this.exportToFileButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + this.getListX();
        int y = this.getListWidget().getY() - 38;

        this.exportFileTextField.setPosition(x, y);
        this.selectOutputFileButton.setPosition(this.exportFileTextField.getRight() + 2, this.exportFileTextField.getY());
        y += 20;

        this.exportToFileButton.setRight(this.getListWidget().getRight());
        this.exportToFileButton.setY(y);
        this.exportToClipboardButton.setRight(this.exportToFileButton.getX() - 2);
        this.exportToClipboardButton.setY(y);
    }

    protected void openOutputFileSelectionScreen()
    {
        Path currentDir = FileUtils.getMinecraftDirectory();
        Path rootDir = FileUtils.getRootDirectory();
        FileSelectorScreen screen = new FileSelectorScreen(currentDir, rootDir, this::setOutputFile);

        screen.setFileFilter(FileUtils.JSON_FILEFILTER);
        // Don't include the extension in the name field, it will be automatically added (if missing)
        screen.setAllowCreatingFilesWithExtension("");

        BaseScreen.openScreenWithParent(screen);
    }

    protected boolean setOutputFile(Path file)
    {
        this.exportFileTextField.setText(file.toString());
        return true;
    }

    @Nullable
    protected JsonArray exportSelectedEntriesAsJson()
    {
        if (this.getListWidget().getEntrySelectionHandler().getSelectedEntryCount() == 0)
        {
            MessageDispatcher.error("malilib.message.error.no_entries_selected");
            return null;
        }

        JsonArray arr = new JsonArray();

        for (T entry : this.getListWidget().getEntrySelectionHandler().getSelectedEntries())
        {
            arr.add(this.entrySerializer.apply(entry));
        }

        return arr;
    }

    protected void exportSelectedEntriesToClipboard()
    {
        JsonArray arr = this.exportSelectedEntriesAsJson();

        if (arr == null)
        {
            return;
        }

        setStringToClipboard(arr.toString());
        MessageDispatcher.success("malilib.message.info.export.copied_to_clipboard");
    }

    protected void exportSelectedEntriesToFile()
    {
        JsonArray arr = this.exportSelectedEntriesAsJson();

        if (arr == null)
        {
            return;
        }

        String outputFile = this.exportFileTextField.getText();

        if (StringUtils.isBlank(outputFile))
        {
            MessageDispatcher.error("malilib.message.error.no_file_selected");
            return;
        }

        if (FileNameUtils.getFileNameExtension(outputFile).equals("json") == false)
        {
            outputFile += ".json";
        }

        Path file = Paths.get(outputFile);

        if (isShiftDown() == false && Files.exists(file))
        {
            MessageDispatcher.error("malilib.message.error.file_already_exists_hold_shift_to_override");
            return;
        }

        if (JsonUtils.writeJsonToFile(arr, file))
        {
            MessageDispatcher.success("malilib.message.info.exported_data_to_file", file.getFileName());
        }
        else
        {
            MessageDispatcher.error("malilib.message.error.failed_to_write_data_to_file", file.getFileName());
        }
    }
}

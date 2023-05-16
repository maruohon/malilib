package malilib.gui;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import malilib.gui.widget.BaseTextFieldWidget;
import malilib.gui.widget.RadioButtonWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.overlay.message.MessageDispatcher;
import malilib.util.FileUtils;
import malilib.util.data.AppendOverwrite;
import malilib.util.data.json.JsonUtils;

public class ImportEntriesListScreen<T> extends BaseImportExportEntriesListScreen<T>
{
    protected final Function<JsonElement, T> entryDeSerializer;
    protected final BiConsumer<List<T>, AppendOverwrite> entryConsumer;

    protected final GenericButton importSelectedEntriesButton;
    protected final GenericButton pasteFromClipboardButton;
    protected final GenericButton readFromFileButton;
    protected final BaseTextFieldWidget contentsTextField;
    protected final RadioButtonWidget<AppendOverwrite> appendOverwriteRadioWidget;

    public ImportEntriesListScreen(Function<T, String> entryNameFunction,
                                   Function<JsonElement, T> entryDeSerializer,
                                   BiConsumer<List<T>, AppendOverwrite> entryConsumer)
    {
        super(10, 98, 20, 105, new ArrayList<>(), entryNameFunction);

        this.entryDeSerializer = entryDeSerializer;
        this.entryConsumer = entryConsumer;

        this.pasteFromClipboardButton    = GenericButton.create(16, "malilib.button.import_entries.paste_from_clipboard", this::pasteFromClipboard);
        this.readFromFileButton          = GenericButton.create(16, "malilib.button.import_entries.read_from_file", this::openImportFileSelectorScreen);
        this.importSelectedEntriesButton = GenericButton.create(16, "malilib.button.import_entries.import_selected_entries", this::importSelectedEntries);

        this.contentsTextField = new BaseTextFieldWidget(300, 16);
        this.contentsTextField.setListener(this::parseStringToEntries);
        this.contentsTextField.setUpdateListenerFromTextSet(true);
        this.contentsTextField.setEmptyValueDisplayString(malilib.util.StringUtils.translate("malilib.label.import_entries.empty_text_field_import_contents"));

        this.appendOverwriteRadioWidget = new RadioButtonWidget<>(AppendOverwrite.VALUES, AppendOverwrite::getDisplayName);
        this.appendOverwriteRadioWidget.setSelection(AppendOverwrite.APPEND, false);

        this.setTitle("malilib.title.screen.import_entries");
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.contentsTextField);
        this.addWidget(this.pasteFromClipboardButton);
        this.addWidget(this.readFromFileButton);
        this.addWidget(this.appendOverwriteRadioWidget);
        this.addWidget(this.importSelectedEntriesButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + this.getListX();
        int y = this.getListWidget().getY() - 74;

        this.contentsTextField.setPosition(x, y);
        this.pasteFromClipboardButton.setPosition(x, this.contentsTextField.getBottom() + 1);
        this.readFromFileButton.setPosition(x, this.pasteFromClipboardButton.getBottom() + 1);

        this.importSelectedEntriesButton.setRight(this.getListWidget().getRight());
        this.importSelectedEntriesButton.setBottom(this.getListWidget().getY() - 2);
        this.appendOverwriteRadioWidget.setX(this.importSelectedEntriesButton.getX());
        this.appendOverwriteRadioWidget.setBottom(this.importSelectedEntriesButton.getY() - 2);
    }

    public void setRadioWidgetHoverText(String translationKey)
    {
        this.appendOverwriteRadioWidget.getHoverInfoFactory().removeAll();
        this.appendOverwriteRadioWidget.translateAndAddHoverString(translationKey);
    }

    protected void importSelectedEntries()
    {
        if (this.getListWidget().getEntrySelectionHandler().getSelectedEntryCount() == 0)
        {
            MessageDispatcher.error("malilib.message.error.no_entries_selected");
            return;
        }

        this.entryConsumer.accept(this.getListWidget().getEntrySelectionHandler().getSelectedEntries(),
                                  this.appendOverwriteRadioWidget.getSelection());
        this.closeScreenOrShowParent();
    }

    protected boolean readFileContents(Path file)
    {
        this.contentsTextField.setText(FileUtils.readFileAsString(file, 8 * 1024 * 1024));
        this.contentsTextField.setCursorToStart();
        this.contentsTextField.setFocused(true);
        return true;
    }

    protected void pasteFromClipboard()
    {
        this.contentsTextField.setText(getStringFromClipboard());
        this.contentsTextField.setCursorToStart();
        this.contentsTextField.setFocused(true);
    }

    protected void openImportFileSelectorScreen()
    {
        Path currentDir = FileUtils.getMinecraftDirectory();
        Path rootDir = FileUtils.getRootDirectory();
        FileSelectorScreen screen = new FileSelectorScreen(currentDir, rootDir, this::readFileContents);
        screen.setFileFilter(FileUtils.JSON_FILEFILTER);

        BaseScreen.openScreenWithParent(screen);
    }

    protected void parseStringToEntries(String str)
    {
        this.entries.clear();

        JsonElement el = JsonUtils.parseJsonFromString(str);

        if (el != null)
        {
            if (el.isJsonArray())
            {
                JsonArray arr = el.getAsJsonArray();
                JsonUtils.getArrayElements(arr, this::createAndAddEntryFrom);
            }
        }
        else
        {
            MessageDispatcher.error("malilib.message.error.failed_to_parse_string_as_json");
        }

        this.getListWidget().refreshEntries();
    }

    protected void createAndAddEntryFrom(JsonElement el)
    {
        T entry = this.entryDeSerializer.apply(el);

        if (entry != null)
        {
            this.entries.add(entry);
        }
    }
}

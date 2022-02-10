package fi.dy.masa.malilib.gui;

import java.io.File;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.widget.RadioButtonWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ResultingStringConsumer;
import fi.dy.masa.malilib.util.data.ToBooleanFunction;

public class SettingsExportImportScreen extends TextInputScreen
{
    protected final GenericButton copyToClipboardButton;
    protected final GenericButton pasteFromClipboardButton;
    protected final GenericButton readFromFileButton;
    protected final GenericButton writeToFileButton;
    protected final RadioButtonWidget<AppendOverwrite> appendOverwriteRadioWidget;
    @Nullable protected ResultingStringConsumer appendStringConsumer;
    protected boolean addAppendOverwriteSelection;
    protected String copyToClipboardMessage = "malilib.message.success.setting_string_copied_to_clipboard";
    protected String pasteFromClipboardMessage = "malilib.message.success.setting_string_pasted_from_clipboard";
    protected int messageDisplayTime = 2000;

    public SettingsExportImportScreen(String titleKey,
                                      String exportString,
                                      ResultingStringConsumer overwriteStringConsumer,
                                      @Nullable GuiScreen parent)
    {
        this(titleKey, exportString, overwriteStringConsumer);

        this.setParent(parent);
    }

    public SettingsExportImportScreen(String titleKey,
                                      String exportString,
                                      ResultingStringConsumer overwriteStringConsumer)
    {
        super(titleKey, exportString, overwriteStringConsumer);

        this.copyToClipboardButton = GenericButton.simple(14, "malilib.label.button.export_import_screen.copy_to_clipboard", this::copyToClipboard);
        this.copyToClipboardButton.translateAndAddHoverString("malilib.hover.button.export_import_screen.copy_to_clipboard");
        this.copyToClipboardButton.getTextOffset().setCenterHorizontally(false).setXOffset(5);

        this.pasteFromClipboardButton = GenericButton.simple(14, "malilib.label.button.export_import_screen.paste_from_clipboard", this::pasteFromClipboard);
        this.pasteFromClipboardButton.translateAndAddHoverString("malilib.hover.button.export_import_screen.paste_from_clipboard");
        this.pasteFromClipboardButton.getTextOffset().setCenterHorizontally(false).setXOffset(5);

        this.readFromFileButton = GenericButton.simple(14, "malilib.label.button.export_import_screen.read_from_file", this::readFromFile);
        this.readFromFileButton.translateAndAddHoverString("malilib.hover.button.export_import_screen.read_from_file");
        this.readFromFileButton.getTextOffset().setCenterHorizontally(false).setXOffset(5);

        this.writeToFileButton = GenericButton.simple(14, "malilib.label.button.export_import_screen.write_to_file", this::writeToFile);
        this.writeToFileButton.translateAndAddHoverString("malilib.hover.button.export_import_screen.write_to_file");
        this.writeToFileButton.getTextOffset().setCenterHorizontally(false).setXOffset(5);

        this.appendOverwriteRadioWidget = new RadioButtonWidget<>(AppendOverwrite.VALUES, AppendOverwrite::getDisplayName);
        this.appendOverwriteRadioWidget.setSelection(AppendOverwrite.APPEND, false);

        this.copyToClipboardButton.setAutomaticWidth(false);
        this.pasteFromClipboardButton.setAutomaticWidth(false);
        this.readFromFileButton.setAutomaticWidth(false);
        this.writeToFileButton.setAutomaticWidth(false);

        int w = Math.max(this.copyToClipboardButton.getWidth(), this.pasteFromClipboardButton.getWidth()) + 4;
        this.copyToClipboardButton.setWidth(w);
        this.pasteFromClipboardButton.setWidth(w);

        w = Math.max(this.readFromFileButton.getWidth(), this.writeToFileButton.getWidth()) + 4;
        this.readFromFileButton.setWidth(w);
        this.writeToFileButton.setWidth(w);

        this.baseHeight = 144;
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.copyToClipboardButton);
        this.addWidget(this.pasteFromClipboardButton);
        this.addWidget(this.readFromFileButton);
        this.addWidget(this.writeToFileButton);

        if (this.addAppendOverwriteSelection)
        {
            this.addWidget(this.appendOverwriteRadioWidget);
        }
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.textField.getX();
        int y = this.textField.getBottom() + 4;

        this.copyToClipboardButton.setPosition(x, y);
        y += 14;
        this.pasteFromClipboardButton.setPosition(x, y);
        y += 18;

        this.readFromFileButton.setPosition(x, y);
        y += 14;
        this.writeToFileButton.setPosition(x, y);
        y += 18;

        if (this.addAppendOverwriteSelection)
        {
            this.appendOverwriteRadioWidget.setPosition(x, y);
            y += 24;
        }

        this.okButton.setY(y);
        this.resetButton.setY(y);
        this.cancelButton.setY(y);
    }

    public void setAppendStringConsumer(@Nullable ResultingStringConsumer appendStringConsumer)
    {
        this.appendStringConsumer = appendStringConsumer;
        this.addAppendOverwriteSelection = appendStringConsumer != null;
        this.baseHeight = this.addAppendOverwriteSelection ? 164 : 144;
        this.updateHeight();
    }

    @Override
    protected boolean applyValue()
    {
        if (this.appendStringConsumer != null &&
            this.appendOverwriteRadioWidget.getSelection() == AppendOverwrite.APPEND)
        {
            return this.appendStringConsumer.consumeString(this.textField.getText());
        }

        return super.applyValue();
    }

    public void setMessageDisplayTime(int messageDisplayTime)
    {
        this.messageDisplayTime = messageDisplayTime;
    }

    public void setCopyToClipboardMessage(String copyToClipboardMessage)
    {
        this.copyToClipboardMessage = copyToClipboardMessage;
    }

    public void setPasteFromClipboardMessage(String pasteFromClipboardMessage)
    {
        this.pasteFromClipboardMessage = pasteFromClipboardMessage;
    }

    public void setRadioWidgetHoverText(String translationKey)
    {
        this.appendOverwriteRadioWidget.getHoverInfoFactory().removeAll();
        this.appendOverwriteRadioWidget.translateAndAddHoverString(translationKey);
    }

    protected void copyToClipboard()
    {
        GuiScreen.setClipboardString(this.textField.getText());
        MessageDispatcher.success().time(this.messageDisplayTime).translate(this.copyToClipboardMessage);
    }

    protected void pasteFromClipboard()
    {
        this.textField.setText(GuiScreen.getClipboardString());
        this.textField.setCursorToStart();
        this.textField.setFocused(true);
        MessageDispatcher.success().time(this.messageDisplayTime).translate(this.pasteFromClipboardMessage);
    }

    protected boolean setStringFromFile(File file)
    {
        String str = FileUtils.readFileAsString(file, 1048576);

        if (str != null)
        {
            this.textField.setText(str);
            this.textField.setCursorToStart();
            this.textField.setFocused(true);

            MessageDispatcher.success("malilib.message.success.settings_read_from_file_to_text_field");

            return true;
        }

        MessageDispatcher.error("malilib.message.error.setting_string_failed_to_read_from_file");

        return false;
    }

    protected boolean writeStringToFile(File file)
    {
        boolean override = BaseScreen.isShiftDown();

        if (file.exists() == false || override)
        {
            if (FileUtils.writeStringToFile(this.textField.getText(), file, override))
            {
                MessageDispatcher.success("malilib.message.success.settings_written_to_file");
                return true;
            }

            MessageDispatcher.error("malilib.message.error.failed_to_write_string_to_file", file.getAbsolutePath());

            return false;
        }

        MessageDispatcher.error("malilib.message.error.file_already_exists_hold_shift_to_override");

        return false;
    }

    protected void readFromFile()
    {
        FileSelectorScreen screen = this.createFileSelectorScreen(this::setStringFromFile);
        BaseScreen.openScreen(screen);
    }

    protected void writeToFile()
    {
        FileSelectorScreen screen = this.createFileSelectorScreen(this::writeStringToFile);
        screen.setAllowCreatingFilesWithExtension("json");
        screen.setConfirmButtonLabel("malilib.label.button.save_to_file");
        BaseScreen.openScreen(screen);
    }

    protected FileSelectorScreen createFileSelectorScreen(ToBooleanFunction<File> consumer)
    {
        File currentDir = FileUtils.getCanonicalFileIfPossible(new File("."));
        File rootDir = FileUtils.getRootDirectory();
        FileSelectorScreen screen = new FileSelectorScreen(currentDir, rootDir, consumer);
        screen.setFileFilter(FileUtils.JSON_FILEFILTER);
        screen.setParent(this);
        return screen;
    }

    protected enum AppendOverwrite
    {
        APPEND    ("malilib.label.name.append"),
        OVERWRITE ("malilib.label.name.overwrite");

        public static final ImmutableList<AppendOverwrite> VALUES = ImmutableList.copyOf(values());

        private final String translationKey;

        AppendOverwrite(String translationKey)
        {
            this.translationKey = translationKey;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }
    }
}

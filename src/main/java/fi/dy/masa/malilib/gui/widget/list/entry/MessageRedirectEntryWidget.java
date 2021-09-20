package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import fi.dy.masa.malilib.overlay.message.MessageRedirectManager.MessageRedirect;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class MessageRedirectEntryWidget extends BaseDataListEntryWidget<MessageRedirect>
{
    protected final GenericButton removeButton;
    protected final DropDownListWidget<MessageOutput> outputDropdown;

    public MessageRedirectEntryWidget(int x, int y, int width, int height,
                                      int listIndex, int originalListIndex,
                                      @Nullable MessageRedirect data,
                                      @Nullable DataListWidget<? extends MessageRedirect> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        this.removeButton = new GenericButton(14, "malilib.gui.button.remove");
        this.removeButton.setActionListener(this::removeRedirect);

        this.outputDropdown = new DropDownListWidget<>(-1, 14, 160, 10, MessageOutput.getValues(), MessageOutput::getDisplayName);
        this.outputDropdown.setSelectedEntry(data.getOutput());
        this.outputDropdown.setSelectionListener(this::replaceRedirect);

        int textWidth = width - this.removeButton.getWidth() - this.outputDropdown.getWidth() - 20;
        String key = StringUtils.clampTextToRenderLength(data.getTranslationKey(), textWidth, LeftRight.RIGHT, "...");

        this.setText(StyledTextLine.of(key));
        this.addHoverStrings(data.getTranslationKey());
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.outputDropdown);
        this.addWidget(this.removeButton);
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int x = this.getRight() - this.removeButton.getWidth() - 2;
        int y = this.getY() + 1;

        this.removeButton.setPosition(x, y);

        x = this.removeButton.getX() - this.outputDropdown.getWidth() - 2;
        this.outputDropdown.setPosition(x, y);
    }

    protected void replaceRedirect(MessageOutput output)
    {
        this.scheduleTask(() -> {
            String translationKey = this.data.getTranslationKey();
            MessageRedirect redirect = new MessageRedirect(translationKey, output);
            Registry.MESSAGE_REDIRECT_MANAGER.removeRedirect(translationKey);
            Registry.MESSAGE_REDIRECT_MANAGER.addRedirect(translationKey, redirect);
            this.listWidget.refreshEntries();
        });
    }

    protected void removeRedirect()
    {
        this.scheduleTask(() -> {
            Registry.MESSAGE_REDIRECT_MANAGER.removeRedirect(this.data.getTranslationKey());
            this.listWidget.refreshEntries();
        });
    }
}

package malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import malilib.gui.widget.DropDownListWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.overlay.message.MessageOutput;
import malilib.overlay.message.MessageRedirectManager.MessageRedirect;
import malilib.registry.Registry;
import malilib.render.text.StyledTextLine;
import malilib.util.StringUtils;
import malilib.util.data.LeftRight;

public class MessageRedirectEntryWidget extends BaseDataListEntryWidget<MessageRedirect>
{
    protected final GenericButton removeButton;
    protected final DropDownListWidget<MessageOutput> outputDropdown;

    public MessageRedirectEntryWidget(MessageRedirect data, DataListEntryWidgetData constructData)
    {
        super(data, constructData);

        this.removeButton = GenericButton.create(14, "malilib.button.misc.remove", this::removeRedirect);

        this.outputDropdown = new DropDownListWidget<>(14, 12, MessageOutput.getValues(), MessageOutput::getDisplayName);
        this.outputDropdown.setSelectedEntry(data.getOutput());
        this.outputDropdown.setSelectionListener(this::replaceRedirect);

        int textWidth = this.getWidth() - this.removeButton.getWidth() - this.outputDropdown.getWidth() - 20;
        String key = StringUtils.clampTextToRenderLength(data.getMessageTranslationKey(), textWidth, LeftRight.RIGHT, "...");

        this.setText(StyledTextLine.of(key));
        this.addHoverStrings(data.getMessageTranslationKey());
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.outputDropdown);
        this.addWidget(this.removeButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int y = this.getY() + 1;

        this.removeButton.setRight(this.getRight() - 2);
        this.removeButton.setY(y);

        this.outputDropdown.setRight(this.removeButton.getX() - 2);
        this.outputDropdown.setY(y);
    }

    protected void replaceRedirect(@Nullable MessageOutput output)
    {
        if (output != null)
        {
            this.scheduleTask(() -> {
                String translationKey = this.data.getMessageTranslationKey();
                Registry.MESSAGE_REDIRECT_MANAGER.removeRedirect(translationKey);
                MessageRedirect redirect = new MessageRedirect(translationKey, output);
                Registry.MESSAGE_REDIRECT_MANAGER.addRedirect(translationKey, redirect);
                this.listWidget.refreshEntries();
            });
        }
    }

    protected void removeRedirect()
    {
        this.scheduleTask(() -> {
            Registry.MESSAGE_REDIRECT_MANAGER.removeRedirect(this.data.getMessageTranslationKey());
            this.listWidget.refreshEntries();
        });
    }
}

package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.GuiTextFieldWrapper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class WidgetListConfigOptions extends WidgetListBase<IConfigValue, WidgetConfigOption>
{
    protected final GuiConfigsBase parent;
    protected final List<GuiTextFieldWrapper> textFields = new ArrayList<>();
    protected boolean configsModified;
    protected int maxLabelWidth;
    protected int configWidth;

    public WidgetListConfigOptions(int x, int y, int width, int height, int configWidth, GuiConfigsBase parent)
    {
        super(x, y, width, height, null);

        this.parent = parent;
        this.configWidth = configWidth;
        this.browserEntryHeight = 22;
    }

    @Override
    public void refreshEntries()
    {
        this.listContents.clear();
        this.listContents.addAll(this.parent.getConfigs());
        this.maxLabelWidth = this.parent.getMaxLabelWidth(this.listContents);

        this.reCreateListEntryWidgets();
    }

    @Override
    protected void reCreateListEntryWidgets()
    {
        this.applyPendingModifications();

        // Check for modifications before re-creating the widgets.
        // This is needed for the keybind settings, as re-creating
        // those widgets wipes the cached initial settings value.
        if (this.configsModified == false)
        {
            this.wereConfigsModified();
        }

        this.textFields.clear();
        super.reCreateListEntryWidgets();
    }

    @Override
    protected WidgetConfigOption createListEntryWidget(int x, int y, boolean isOdd, IConfigValue config)
    {
        return new WidgetConfigOption(x, y, this.browserEntryWidth, this.browserEntryHeight, this.zLevel,
                this.maxLabelWidth, this.configWidth, config, this.parent, this.mc, this);
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.clearTextFieldFocus();

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_TAB)
        {
            this.applyPendingModifications();
            return this.changeTextFieldFocus(GuiScreen.isShiftKeyDown());
        }
        else
        {
            for (WidgetConfigOption widget : this.listWidgets)
            {
                if (widget.onKeyTyped(typedChar, keyCode))
                {
                    return true;
                }
            }

            return super.onKeyTyped(typedChar, keyCode);
        }
    }

    public void addTextField(GuiTextFieldWrapper wrapper)
    {
        this.textFields.add(wrapper);
    }

    protected boolean changeTextFieldFocus(boolean reverse)
    {
        final int size = this.textFields.size();

        if (size > 1)
        {
            int currentIndex = -1;

            for (int i = 0; i < size; ++i)
            {
                GuiTextField textField = this.textFields.get(i).getTextField();

                if (textField.isFocused())
                {
                    currentIndex = i;
                    textField.setFocused(false);
                    break;
                }
            }

            if (currentIndex != -1)
            {
                int newIndex = currentIndex + (reverse ? -1 : 1);

                if (newIndex >= size)
                {
                    newIndex = 0;
                }
                else if (newIndex < 0)
                {
                    newIndex = size - 1;
                }

                this.textFields.get(newIndex).getTextField().setFocused(true);
                this.applyPendingModifications();

                return true;
            }
        }

        return false;
    }

    protected void clearTextFieldFocus()
    {
        for (int i = 0; i < this.textFields.size(); ++i)
        {
            GuiTextField textField = this.textFields.get(i).getTextField();

            if (textField.isFocused())
            {
                textField.setFocused(false);
                break;
            }
        }
    }

    public boolean wereConfigsModified()
    {
        // First check the cached value, this gets updated when scrolling
        // the list and the widgets get re-created.
        if (this.configsModified)
        {
            return true;
        }

        for (WidgetConfigOption widget : this.listWidgets)
        {
            if (widget.wasConfigModified())
            {
                this.configsModified = true;
                return true;
            }
        }

        return false;
    }

    public void applyPendingModifications()
    {
        for (WidgetConfigOption widget : this.listWidgets)
        {
            if (widget.hasPendingModifications())
            {
                widget.applyNewValueToConfig();
                // Cache the modified status before scrolling etc. and thus re-creating the widgets
                this.configsModified = true;
            }
        }
    }

    public void clearModifiedStatus()
    {
        this.configsModified = false;
    }
}

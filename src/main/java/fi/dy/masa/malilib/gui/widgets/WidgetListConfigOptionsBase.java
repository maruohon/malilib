package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.gui.GuiTextFieldWrapper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public abstract class WidgetListConfigOptionsBase<TYPE, WIDGET extends WidgetConfigOptionBase> extends WidgetListBase<TYPE, WIDGET>
{
    protected final List<GuiTextFieldWrapper> textFields = new ArrayList<>();
    protected boolean configsModified;
    protected int maxLabelWidth;
    protected int configWidth;

    public WidgetListConfigOptionsBase(int x, int y, int width, int height, int configWidth)
    {
        super(x, y, width, height, null);

        this.configWidth = configWidth;
        this.browserEntryHeight = 22;
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
            for (WIDGET widget : this.listWidgets)
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
        System.out.printf("%s - wereConfigsModified()\n", this.getClass().getSimpleName());
        // First check the cached value, this gets updated when scrolling
        // the list and the widgets get re-created.
        if (this.configsModified)
        {
            return true;
        }

        for (WidgetConfigOptionBase widget : this.listWidgets)
        {
            if (widget.wasConfigModified())
            {
                this.configsModified = true;
                return true;
            }
        }

        System.out.printf("%s - wereConfigsModified - FALSE\n", this.getClass().getSimpleName());
        return false;
    }

    public void applyPendingModifications()
    {
        System.out.printf("%s - applyPendingModifications()\n", this.getClass().getSimpleName());
        for (WidgetConfigOptionBase widget : this.listWidgets)
        {
            System.out.printf("%s - loop\n", this.getClass().getSimpleName());
            if (widget.hasPendingModifications())
            {
                System.out.printf("%s - applyPendingModifications - APPLY\n", this.getClass().getSimpleName());
                widget.applyNewValueToConfig();
                // Cache the modified status before scrolling etc. and thus re-creating the widgets
                this.configsModified = true;
            }
        }
    }
}

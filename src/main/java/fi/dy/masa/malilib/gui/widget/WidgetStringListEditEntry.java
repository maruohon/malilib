package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.List;
import fi.dy.masa.malilib.config.option.StringListConfig;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.util.BaseGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class WidgetStringListEditEntry extends WidgetConfigOptionBase<String>
{
    protected final WidgetListStringListEdit parent;
    protected final String defaultValue;
    protected final int listIndex;
    protected final boolean isOdd;

    public WidgetStringListEditEntry(int x, int y, int width, int height,
            int listIndex, boolean isOdd, String initialValue, String defaultValue, WidgetListStringListEdit parent)
    {
        super(x, y, width, height, parent, initialValue, listIndex);

        this.listIndex = listIndex;
        this.isOdd = isOdd;
        this.defaultValue = defaultValue;
        this.lastAppliedValue = initialValue;
        this.initialStringValue = initialValue;
        this.parent = parent;

        int textFieldX = x + 20;
        int textFieldWidth = width - 160;
        int resetX = textFieldX + textFieldWidth + 2;
        int by = y + 4;
        int bx = textFieldX;
        int bOff = 18;

        if (this.isDummy() == false)
        {
            this.addLabel(x + 2, y + 7, 0xC0C0C0C0, String.format("%3d:", listIndex + 1));
            bx = this.addTextField(textFieldX, y + 1, resetX, textFieldWidth, 20, initialValue);

            this.addListActionButton(bx, by, ButtonType.ADD);
            bx += bOff;

            this.addListActionButton(bx, by, ButtonType.REMOVE);
            bx += bOff;

            if (this.canBeMoved(true))
            {
                this.addListActionButton(bx, by, ButtonType.MOVE_DOWN);
            }

            bx += bOff;

            if (this.canBeMoved(false))
            {
                this.addListActionButton(bx, by, ButtonType.MOVE_UP);
            }
        }
        else
        {
            this.addListActionButton(bx, by, ButtonType.ADD);
        }
    }

    protected boolean isDummy()
    {
        return this.listIndex < 0;
    }

    protected void addListActionButton(int x, int y, ButtonType type)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, type.getIcon(), type.getHoverKey());
        button.setRenderOutline(true);
        this.addButton(button, new ListenerListActions(type, this));
    }

    protected int addTextField(int x, int y, int resetX, int configWidth, int configHeight, String initialValue)
    {
        ButtonGeneric resetButton = this.createResetButton(resetX, y, initialValue);
        this.textField = new WidgetTextFieldBase(x + 2, y + 1, configWidth - 4, configHeight - 3, initialValue);
        this.textField.setUpdateListenerAlways(true);

        this.addTextField(this.textField, (newText) -> resetButton.setEnabled(this.textField.getText().equals(this.defaultValue) == false));

        this.addButton(resetButton, (btn, mbtn) -> {
            this.textField.setText(this.defaultValue);
            resetButton.setEnabled(this.textField.getText().equals(this.defaultValue) == false);
        });

        return resetButton.getX() + resetButton.getWidth() + 4;
    }

    protected ButtonGeneric createResetButton(int x, int y, String initialValue)
    {
        String labelReset = StringUtils.translate("malilib.gui.button.reset.caps");
        ButtonGeneric resetButton = new ButtonGeneric(x, y, -1, 20, labelReset);
        resetButton.setEnabled(initialValue.equals(this.defaultValue) == false);

        return resetButton;
    }

    @Override
    public boolean wasConfigModified()
    {
        return this.isDummy() == false && this.textField.getText().equals(this.initialStringValue) == false;
    }

    @Override
    public void applyNewValueToConfig()
    {
        if (this.isDummy() == false)
        {
            StringListConfig config = this.parent.getParentGui().getConfig();
            List<String> list = new ArrayList<>(config.getStrings());
            String value = this.textField.getText();

            if (this.listIndex < list.size())
            {
                list.set(this.listIndex, value);
                config.setStrings(list);
                this.lastAppliedValue = value;
            }
        }
    }

    protected void insertEntry(boolean before)
    {
        StringListConfig config = this.parent.getParentGui().getConfig();
        List<String> list = config.getStrings();
        int index = this.getInsertionIndex(list, before);

        // Adding a new empty entry purposefully does not update the config by setting a new list,
        // so that the empty value does not get applied until it has been set to something valid.
        list.add(index, "");

        this.parent.refreshEntries();
        this.parent.markConfigsModified();
    }

    protected int getInsertionIndex(List<String> list, boolean before)
    {
        final int size = list.size();
        int index = this.listIndex < 0 ? size : (Math.min(this.listIndex, size));

        if (before == false)
        {
            ++index;
        }

        return Math.max(0, Math.min(size, index));
    }

    protected void removeEntry()
    {
        StringListConfig config = this.parent.getParentGui().getConfig();
        List<String> list = new ArrayList<>(config.getStrings());
        final int size = list.size();

        if (this.listIndex >= 0 && this.listIndex < size)
        {
            list.remove(this.listIndex);
            config.setStrings(list);

            this.parent.refreshEntries();
            this.parent.markConfigsModified();
        }
    }

    protected void moveEntry(boolean down)
    {
        StringListConfig config = this.parent.getParentGui().getConfig();
        List<String> list = new ArrayList<>(config.getStrings());
        final int size = list.size();

        if (this.listIndex >= 0 && this.listIndex < size)
        {
            String tmp;
            int index1 = this.listIndex;
            int index2 = -1;

            if (down && this.listIndex < (size - 1))
            {
                index2 = index1 + 1;
            }
            else if (down == false && this.listIndex > 0)
            {
                index2 = index1 - 1;
            }

            if (index2 >= 0)
            {
                this.parent.applyPendingModifications();

                tmp = list.get(index1);
                list.set(index1, list.get(index2));
                list.set(index2, tmp);
                config.setStrings(list);

                this.parent.refreshEntries();
                this.parent.markConfigsModified();
            }
        }
    }

    protected boolean canBeMoved(boolean down)
    {
        final int size = this.parent.getParentGui().getConfig().getStrings().size();
        return (this.listIndex >= 0 && this.listIndex < size) &&
                ((down && this.listIndex < (size - 1)) || (down == false && this.listIndex > 0));
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        if (this.isOdd)
        {
            RenderUtils.drawRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0x20FFFFFF, this.getZLevel());
        }
        // Draw a slightly lighter background for even entries
        else
        {
            RenderUtils.drawRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0x30FFFFFF, this.getZLevel());
        }

        this.drawSubWidgets(mouseX, mouseY, isActiveGui, hoveredWidgetId);
        this.drawTextFields(mouseX, mouseY, isActiveGui, hoveredWidgetId);

        super.render(mouseX, mouseY, isActiveGui, hoveredWidgetId);
    }

    protected static class ListenerResetConfig implements IButtonActionListener
    {
        protected final WidgetStringListEditEntry parent;
        protected final ButtonGeneric buttonReset;

        public ListenerResetConfig(ButtonGeneric buttonReset, WidgetStringListEditEntry parent)
        {
            this.buttonReset = buttonReset;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            this.parent.textField.setText(this.parent.defaultValue);
            this.buttonReset.setEnabled(this.parent.textField.getText().equals(this.parent.defaultValue) == false);
        }
    }

    protected static class ListenerListActions implements IButtonActionListener
    {
        protected final ButtonType type;
        protected final WidgetStringListEditEntry parent;

        public ListenerListActions(ButtonType type, WidgetStringListEditEntry parent)
        {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            if (this.type == ButtonType.ADD)
            {
                this.parent.insertEntry(false);
            }
            else if (this.type == ButtonType.REMOVE)
            {
                this.parent.removeEntry();
            }
            else
            {
                this.parent.moveEntry(this.type == ButtonType.MOVE_DOWN);
            }
        }
    }

    protected enum ButtonType
    {
        ADD         (BaseGuiIcon.PLUS, "malilib.gui.button.hover.list.add_after"),
        REMOVE      (BaseGuiIcon.MINUS, "malilib.gui.button.hover.list.remove"),
        MOVE_UP     (BaseGuiIcon.ARROW_UP, "malilib.gui.button.hover.list.move_up"),
        MOVE_DOWN   (BaseGuiIcon.ARROW_DOWN, "malilib.gui.button.hover.list.move_down");

        protected final BaseGuiIcon icon;
        protected final String hoverKey;

        ButtonType(BaseGuiIcon icon, String translationKey)
        {
            this.icon = icon;
            this.hoverKey = translationKey;
        }

        public IGuiIcon getIcon()
        {
            return this.icon;
        }

        public String getHoverKey()
        {
            return this.hoverKey;
        }
    }
}

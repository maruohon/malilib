package fi.dy.masa.malilib.gui.widget.list.entry;

import java.util.List;
import java.util.function.Consumer;
import fi.dy.masa.malilib.gui.button.GenericButton;
import fi.dy.masa.malilib.gui.button.ButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.util.BaseGuiIcon;
import fi.dy.masa.malilib.gui.widget.WidgetLabel;
import fi.dy.masa.malilib.gui.widget.WidgetTextFieldBase;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.render.RenderUtils;

public class StringListEditEntryWidget extends BaseDataListEntryWidget<String>
{
    protected final DataListWidget<String> parent;
    protected final List<String> stringList;
    protected final String defaultValue;
    protected final String initialValue;
    protected final WidgetLabel labelWidget;
    protected final WidgetTextFieldBase textField;
    protected final GenericButton addButton;
    protected final GenericButton removeButton;
    protected final GenericButton upButton;
    protected final GenericButton downButton;
    protected final GenericButton resetButton;

    public StringListEditEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                     String initialValue, String defaultValue, DataListWidget<String> parent)
    {
        super(x, y, width, height, listIndex, originalListIndex, initialValue);

        this.defaultValue = defaultValue;
        this.initialValue = initialValue;
        this.parent = parent;

        // This is a reference to the current entries list, which can be modified
        this.stringList = parent.getCurrentEntries();

        int textFieldWidth = width - 142;

        this.labelWidget = new WidgetLabel(x + 2, y + 7, 0xC0C0C0C0, String.format("%5d:", originalListIndex + 1));
        this.textField = new WidgetTextFieldBase(x + 28, y + 2, textFieldWidth, 16, initialValue);

        this.addButton    = this.createListActionButton(x, y, ButtonType.ADD);
        this.removeButton = this.createListActionButton(x, y, ButtonType.REMOVE);
        this.upButton     = this.createListActionButton(x, y, ButtonType.MOVE_UP);
        this.downButton   = this.createListActionButton(x, y, ButtonType.MOVE_DOWN);

        this.resetButton = new GenericButton(x, y, -1, 16, "malilib.gui.button.reset.caps");
        this.resetButton.setRenderDefaultBackground(false);
        this.resetButton.setRenderOutline(true);
        this.resetButton.setOutlineColorNormal(0xFF404040);
        this.resetButton.setTextColorDisabled(0xFF505050);

        this.resetButton.setEnabled(initialValue.equals(this.defaultValue) == false);
        this.resetButton.setActionListener((btn, mbtn) -> {
            this.textField.setText(this.defaultValue);

            if (this.originalListIndex < this.stringList.size())
            {
                this.stringList.set(this.originalListIndex, this.defaultValue);
            }

            this.resetButton.setEnabled(this.textField.getText().equals(this.defaultValue) == false);
        });

        this.textField.setUpdateListenerAlways(true);
        this.textField.setListener((newText) -> {
            if (this.originalListIndex < this.stringList.size())
            {
                this.stringList.set(this.originalListIndex, newText);
            }

            this.resetButton.setEnabled(newText.equals(this.defaultValue) == false);
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.labelWidget);
        this.addWidget(this.textField);
        this.addWidget(this.addButton);
        this.addWidget(this.removeButton);

        if (this.canBeMoved(true))
        {
            this.addWidget(this.downButton);
        }

        if (this.canBeMoved(false))
        {
            this.addWidget(this.upButton);
        }

        this.addWidget(this.resetButton);

        this.updateSubWidgetPositions(this.getX(), this.getY());
    }

    @Override
    public void updateSubWidgetPositions(int oldX, int oldY)
    {
        super.updateSubWidgetPositions(oldX, oldY);

        int x = this.getX();
        int y = this.getY();

        this.labelWidget.setPosition(x + 1, y + 6);
        this.textField.setPosition(x + 30, y + 2);

        x = this.textField.getRight() + 2;
        this.addButton.setPosition(x, y + 2);

        x = this.addButton.getRight() + 2;
        this.removeButton.setPosition(x, y + 2);

        x = this.removeButton.getRight() + 2;
        this.upButton.setPosition(x, y + 2);

        x = this.upButton.getRight() + 2;
        this.downButton.setPosition(x, y + 2);

        x = this.downButton.getRight() + 2;
        this.resetButton.setPosition(x, y + 2);
    }

    protected GenericButton createListActionButton(int x, int y, ButtonType type)
    {
        GenericButton button = new GenericButton(x, y, type.getIcon(), type.getHoverKey());
        button.setRenderOutline(true);
        button.setActionListener(type.createListener(this));
        return button;
    }

    protected int getInsertionIndex(List<String> list, boolean before)
    {
        final int size = list.size();
        int index = this.originalListIndex < 0 ? size : (Math.min(this.originalListIndex, size));

        if (before == false)
        {
            ++index;
        }

        return Math.max(0, Math.min(size, index));
    }

    protected void insertEntry(boolean before)
    {
        int index = this.getInsertionIndex(this.stringList, before);
        this.stringList.add(index, "");
        this.parent.refreshEntries();
    }

    protected void removeEntry()
    {
        final int size = this.stringList.size();

        if (this.originalListIndex >= 0 && this.originalListIndex < size)
        {
            this.stringList.remove(this.originalListIndex);
            this.parent.refreshEntries();
        }
    }

    protected void moveEntry(boolean down)
    {
        List<String> list = this.stringList;
        final int size = list.size();

        if (this.originalListIndex >= 0 && this.originalListIndex < size)
        {
            String tmp;
            int index1 = this.originalListIndex;
            int index2 = -1;

            if (down && this.originalListIndex < (size - 1))
            {
                index2 = index1 + 1;
            }
            else if (down == false && this.originalListIndex > 0)
            {
                index2 = index1 - 1;
            }

            if (index2 >= 0)
            {
                tmp = list.get(index1);
                list.set(index1, list.get(index2));
                list.set(index2, tmp);

                this.parent.refreshEntries();
            }
        }
    }

    protected boolean canBeMoved(boolean down)
    {
        final int size = this.stringList.size();
        return (this.originalListIndex >= 0 && this.originalListIndex < size) &&
                ((down && this.originalListIndex < (size - 1)) || (down == false && this.originalListIndex > 0));
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

        super.render(mouseX, mouseY, isActiveGui, hoveredWidgetId);
    }

    protected enum ButtonType
    {
        ADD         (BaseGuiIcon.PLUS, "malilib.gui.button.hover.list.add_after", (w) -> w.insertEntry(false)),
        REMOVE      (BaseGuiIcon.MINUS, "malilib.gui.button.hover.list.remove", StringListEditEntryWidget::removeEntry),
        MOVE_UP     (BaseGuiIcon.ARROW_UP, "malilib.gui.button.hover.list.move_up", (w) -> w.moveEntry(false)),
        MOVE_DOWN   (BaseGuiIcon.ARROW_DOWN, "malilib.gui.button.hover.list.move_down", (w) -> w.moveEntry(true));

        protected final BaseGuiIcon icon;
        protected final String translationKey;
        protected final Consumer<StringListEditEntryWidget> action;

        ButtonType(BaseGuiIcon icon, String translationKey, Consumer<StringListEditEntryWidget> action)
        {
            this.icon = icon;
            this.translationKey = translationKey;
            this.action = action;
        }

        public IGuiIcon getIcon()
        {
            return this.icon;
        }

        public String getHoverKey()
        {
            return this.translationKey;
        }

        public ButtonActionListener createListener(final StringListEditEntryWidget widget)
        {
            return (btn, mbtn) -> this.action.accept(widget);
        }
    }
}

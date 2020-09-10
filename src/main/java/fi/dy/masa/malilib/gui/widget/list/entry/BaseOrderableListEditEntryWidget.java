package fi.dy.masa.malilib.gui.widget.list.entry;

import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.icon.BaseIcon;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.ButtonActionListener;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.render.RenderUtils;

public abstract class BaseOrderableListEditEntryWidget<DATATYPE> extends BaseDataListEntryWidget<DATATYPE>
{
    protected final DataListWidget<DATATYPE> parent;
    protected final List<DATATYPE> dataList;
    protected final GenericButton addButton;
    protected final GenericButton removeButton;
    protected final GenericButton upButton;
    protected final GenericButton downButton;
    protected int nextWidgetX;

    @Nullable protected LabelWidget labelWidget;

    public BaseOrderableListEditEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                            DATATYPE initialValue, DataListWidget<DATATYPE> parent)
    {
        super(x, y, width, height, listIndex, originalListIndex, initialValue);

        this.parent = parent;

        // This is a reference to the current entries list, which can be modified
        this.dataList = parent.getCurrentEntries();

        this.addButton    = this.createListActionButton(x, y, ButtonType.ADD);
        this.removeButton = this.createListActionButton(x, y, ButtonType.REMOVE);
        this.upButton     = this.createListActionButton(x, y, ButtonType.MOVE_UP);
        this.downButton   = this.createListActionButton(x, y, ButtonType.MOVE_DOWN);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        if (this.labelWidget != null)
        {
            this.addWidget(this.labelWidget);
        }

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
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int x = this.getX();
        int y = this.getY();

        if (this.labelWidget != null)
        {
            this.labelWidget.setPosition(x + 3, y + 6);
            this.nextWidgetX = this.labelWidget.getRight() + 4;
        }

        this.updateSubWidgetsToGeometryChangesPre(this.nextWidgetX, y);

        x = this.nextWidgetX;
        this.addButton.setPosition(x, y + 2);

        x = this.addButton.getRight() + 2;
        this.removeButton.setPosition(x, y + 2);

        x = this.removeButton.getRight() + 2;
        this.upButton.setPosition(x, y + 2);

        x = this.upButton.getRight() + 2;
        this.downButton.setPosition(x, y + 2);

        this.nextWidgetX = this.downButton.getRight();

        this.updateSubWidgetsToGeometryChangesPost(this.nextWidgetX, y);
    }

    protected void updateSubWidgetsToGeometryChangesPre(int x, int y)
    {
    }

    protected void updateSubWidgetsToGeometryChangesPost(int x, int y)
    {
    }

    protected GenericButton createListActionButton(int x, int y, ButtonType type)
    {
        GenericButton button = new GenericButton(x, y, type.getIcon(), type.getHoverKey());
        button.setRenderOutline(true);
        button.setActionListener(type.createListener(this));
        return button;
    }

    protected abstract DATATYPE getNewDataEntry();

    protected int getInsertionIndex(List<DATATYPE> list, boolean before)
    {
        final int size = list.size();
        int index = this.originalListIndex < 0 ? size : (Math.min(this.originalListIndex, size));

        if (before == false)
        {
            ++index;
        }

        return Math.max(0, Math.min(size, index));
    }

    protected void insertEntryAfter()
    {
        this.insertEntry(false);
    }

    protected void insertEntry(boolean before)
    {
        int index = this.getInsertionIndex(this.dataList, before);
        this.dataList.add(index, this.getNewDataEntry());
        this.parent.refreshEntries();
        this.parent.focusWidget(index);
    }

    protected void removeEntry()
    {
        final int size = this.dataList.size();

        if (this.originalListIndex >= 0 && this.originalListIndex < size)
        {
            this.dataList.remove(this.originalListIndex);
            this.parent.refreshEntries();
        }
    }

    protected void moveEntryDown()
    {
        this.moveEntry(true);
    }

    protected void moveEntryUp()
    {
        this.moveEntry(false);
    }

    protected void moveEntry(boolean down)
    {
        List<DATATYPE> list = this.dataList;
        final int size = list.size();

        if (this.originalListIndex >= 0 && this.originalListIndex < size)
        {
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
                DATATYPE tmp = list.get(index1);
                list.set(index1, list.get(index2));
                list.set(index2, tmp);

                this.parent.refreshEntries();
            }
        }
    }

    protected boolean canBeMoved(boolean down)
    {
        final int size = this.dataList.size();
        return (this.originalListIndex >= 0 && this.originalListIndex < size) &&
                ((down && this.originalListIndex < (size - 1)) || (down == false && this.originalListIndex > 0));
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        if (this.isOdd)
        {
            RenderUtils.drawRect(x, y, this.getWidth(), this.getHeight(), 0x20FFFFFF, z);
        }
        // Draw a slightly lighter background for even entries
        else
        {
            RenderUtils.drawRect(x, y, this.getWidth(), this.getHeight(), 0x30FFFFFF, z);
        }

        super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hoveredWidgetId);
    }

    protected enum ButtonType
    {
        ADD         (BaseIcon.PLUS,         "malilib.gui.button.hover.list.add_after",  BaseOrderableListEditEntryWidget::insertEntryAfter),
        REMOVE      (BaseIcon.MINUS,        "malilib.gui.button.hover.list.remove",     BaseOrderableListEditEntryWidget::removeEntry),
        MOVE_UP     (BaseIcon.ARROW_UP,     "malilib.gui.button.hover.list.move_up",    BaseOrderableListEditEntryWidget::moveEntryUp),
        MOVE_DOWN   (BaseIcon.ARROW_DOWN,   "malilib.gui.button.hover.list.move_down",  BaseOrderableListEditEntryWidget::moveEntryDown);

        protected final BaseIcon icon;
        protected final String translationKey;
        protected final Consumer<BaseOrderableListEditEntryWidget<?>> action;

        ButtonType(BaseIcon icon, String translationKey, Consumer<BaseOrderableListEditEntryWidget<?>> action)
        {
            this.icon = icon;
            this.translationKey = translationKey;
            this.action = action;
        }

        public Icon getIcon()
        {
            return this.icon;
        }

        public String getHoverKey()
        {
            return this.translationKey;
        }

        public ButtonActionListener createListener(final BaseOrderableListEditEntryWidget<?> widget)
        {
            return (btn, mbtn) -> this.action.accept(widget);
        }
    }
}

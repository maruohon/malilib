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
    protected boolean dragged;
    protected int nextWidgetX;
    protected int dragStartX;
    protected int dragStartY;
    protected int draggableRegionEndX = -1;

    @Nullable protected LabelWidget labelWidget;

    public BaseOrderableListEditEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                            DATATYPE initialValue, DataListWidget<DATATYPE> parent)
    {
        super(x, y, width, height, listIndex, originalListIndex, initialValue);

        this.setBackgroundColorHovered(0x30FFFFFF);
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

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.canDragAt(mouseX, mouseY))
        {
            this.dragged = true;
            this.dragStartX = mouseX;
            this.dragStartY = mouseY;
            return true;
        }

        return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onMouseReleasedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.dragged)
        {
            int x = this.getX();

            if (mouseX >= x && mouseX <= x + this.getWidth())
            {
                int newIndex = this.getNewIndexFromDrag(mouseY);
                this.scheduleTask(() -> this.moveEntry(newIndex));
            }

            this.dragged = false;
        }

        super.onMouseReleasedImpl(mouseX, mouseY, mouseButton);
    }

    protected boolean canDragAt(int mouseX, int mouseY)
    {
        return mouseX <= this.draggableRegionEndX;
    }

    protected int getNewIndexFromDrag(int mouseY)
    {
        List<BaseListEntryWidget> list = this.parent.getListEntryWidgets();
        int newIndex = -1;

        if (mouseY > this.dragStartY)
        {
            for (BaseListEntryWidget widget : list)
            {
                if (mouseY < widget.getY() + (widget.getHeight() / 2))
                {
                    break;
                }

                newIndex = widget.getListIndex();
            }
        }
        else if (mouseY < this.dragStartY)
        {
            for (int i = list.size() - 1; i >= 0; --i)
            {
                BaseListEntryWidget widget = list.get(i);

                if (mouseY > widget.getY() + (widget.getHeight() / 2))
                {
                    break;
                }

                newIndex = widget.getListIndex();
            }
        }

        return newIndex;
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
        this.moveEntry(this.originalListIndex + 1);
    }

    protected void moveEntryUp()
    {
        this.moveEntry(this.originalListIndex - 1);
    }

    protected void moveEntry(int newIndex)
    {
        List<DATATYPE> list = this.dataList;
        final int size = list.size();
        final int oldIndex = this.originalListIndex;

        if (oldIndex >= 0 && oldIndex < size &&
            newIndex >= 0 && newIndex < size &&
            newIndex != oldIndex)
        {
            DATATYPE entry = list.remove(oldIndex);
            list.add(newIndex, entry);

            this.parent.refreshEntries();
        }
    }

    protected boolean canBeMoved(boolean down)
    {
        final int size = this.dataList.size();
        return (this.originalListIndex >= 0 && this.originalListIndex < size) &&
                ((down && this.originalListIndex < (size - 1)) || (down == false && this.originalListIndex > 0));
    }

    @Override
    public boolean isHoveredForRender(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        return this.dragged == false && super.isHoveredForRender(mouseX, mouseY, isActiveGui, hoveredWidgetId);
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);
        int width = this.getWidth();
        int height = this.getHeight();

        if (this.dragged)
        {
            int newIndex = this.getNewIndexFromDrag(mouseY);

            if (newIndex != -1)
            {
                int off = (newIndex - this.listIndex) * height - 1;

                if (newIndex > this.listIndex)
                {
                    off += height;
                }

                RenderUtils.renderRectangle(x - 2, y + off, width + 4, 2, 0xFF00FFFF, z + 70);
            }

            x += (mouseX - this.dragStartX);
            y += (mouseY - this.dragStartY);
            z += 60;
        }

        if (this.isOdd)
        {
            RenderUtils.renderRectangle(x, y, width, height, 0x20FFFFFF, z);
        }
        // Draw a slightly lighter background for even entries
        else
        {
            RenderUtils.renderRectangle(x, y, width, height, 0x30FFFFFF, z);
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

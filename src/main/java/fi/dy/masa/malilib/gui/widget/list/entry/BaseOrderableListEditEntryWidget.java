package fi.dy.masa.malilib.gui.widget.list.entry;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;

public abstract class BaseOrderableListEditEntryWidget<DATATYPE> extends BaseDataListEntryWidget<DATATYPE>
{
    protected final List<DATATYPE> dataList;
    protected final GenericButton addButton;
    protected final GenericButton removeButton;
    protected final GenericButton upButton;
    protected final GenericButton downButton;
    protected Supplier<DATATYPE> newEntryFactory = () -> null;
    protected boolean canReOrder = true;
    protected boolean dragged;
    protected boolean useAddButton = true;
    protected boolean useMoveButtons = true;
    protected boolean useRemoveButton = true;
    protected int nextWidgetX;
    protected int dragStartX;
    protected int dragStartY;
    protected int draggableRegionEndX = -1;
    @Nullable protected LabelWidget labelWidget;

    @SuppressWarnings("unchecked")
    public BaseOrderableListEditEntryWidget(DATATYPE data,
                                            DataListEntryWidgetData constructData)
    {
        super(data, constructData);

        // This is a reference to the current entries list, which can be modified
        this.dataList = ((DataListWidget<DATATYPE>) constructData.listWidget).getNonFilteredDataList();

        this.addButton    = this.createListActionButton(ButtonType.ADD);
        this.removeButton = this.createListActionButton(ButtonType.REMOVE);
        this.upButton     = this.createListActionButton(ButtonType.MOVE_UP);
        this.downButton   = this.createListActionButton(ButtonType.MOVE_DOWN);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        if (this.labelWidget != null)
        {
            this.addWidget(this.labelWidget);
        }

        if (this.useAddButton)
        {
            this.addWidget(this.addButton);
        }

        if (this.useRemoveButton)
        {
            this.addWidget(this.removeButton);
        }

        if (this.useMoveButtons)
        {
            if (this.canBeMoved(true))
            {
                this.addWidget(this.downButton);
            }

            if (this.canBeMoved(false))
            {
                this.addWidget(this.upButton);
            }
        }
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getX();
        int y = this.getY();

        this.draggableRegionEndX = this.getRight();

        if (this.labelWidget != null)
        {
            this.labelWidget.setX(x + 3);
            this.labelWidget.centerVerticallyInside(this);
            this.nextWidgetX = this.labelWidget.getRight() + 4;
        }

        this.updateSubWidgetsToGeometryChangesPre(this.nextWidgetX, y);

        if (this.useAddButton)
        {
            this.addButton.setX(this.nextWidgetX);
            this.addButton.centerVerticallyInside(this);
            this.nextWidgetX = this.addButton.getRight() + 2;
        }

        if (this.useRemoveButton)
        {
            this.removeButton.setX(this.nextWidgetX);
            this.removeButton.centerVerticallyInside(this);
            this.nextWidgetX = this.removeButton.getRight() + 2;
        }

        if (this.useMoveButtons)
        {
            this.upButton.setX(this.nextWidgetX);
            this.upButton.centerVerticallyInside(this);
            this.nextWidgetX = this.upButton.getRight() + 2;

            this.downButton.setX(this.nextWidgetX);
            this.downButton.centerVerticallyInside(this);
            this.nextWidgetX = this.downButton.getRight() + 2;
        }

        this.updateSubWidgetsToGeometryChangesPost(this.nextWidgetX, y);
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.canReOrder() && this.canDragAt(mouseX, mouseY))
        {
            this.dragged = true;
            this.dragStartX = mouseX;
            this.dragStartY = this.getY() + this.getHeight() / 2;
            return true;
        }

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
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

        super.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    protected void updateSubWidgetsToGeometryChangesPre(int x, int y)
    {
    }

    protected void updateSubWidgetsToGeometryChangesPost(int x, int y)
    {
    }

    protected MultiIcon getIconForButton(ButtonType type)
    {
        switch (type)
        {
            case ADD:       return DefaultIcons.LIST_ADD_PLUS_13;
            case REMOVE:    return DefaultIcons.LIST_REMOVE_MINUS_13;
            case MOVE_UP:   return DefaultIcons.ARROW_UP;
            case MOVE_DOWN: return DefaultIcons.ARROW_DOWN;
        }

        return DefaultIcons.EMPTY;
    }

    protected GenericButton createListActionButton(ButtonType type)
    {
        GenericButton button = GenericButton.create(this.getIconForButton(type), type.createListener(this));
        button.translateAndAddHoverString(type.getHoverKey());
        return button;
    }

    public void setCanReOrder(boolean canReOrder)
    {
        this.canReOrder = canReOrder;
    }

    protected boolean canReOrder()
    {
        return this.canReOrder && BaseScreen.isShiftDown() == false && BaseScreen.isCtrlDown() == false;
    }

    protected boolean canDragAt(int mouseX, int mouseY)
    {
        return mouseX <= this.draggableRegionEndX;
    }

    protected int getNewIndexFromDrag(int mouseY)
    {
        List<? extends BaseListEntryWidget> list = this.listWidget.getEntryWidgetList();
        int newIndex = this.getDataListIndex();

        if (mouseY > this.dragStartY)
        {
            for (BaseListEntryWidget widget : list)
            {
                if (mouseY < widget.getY() + (widget.getHeight() / 2))
                {
                    break;
                }

                newIndex = widget.getDataListIndex();
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

                newIndex = widget.getDataListIndex();
            }
        }

        return newIndex;
    }

    @Nullable
    protected DATATYPE getNewDataEntry()
    {
        return this.newEntryFactory.get();
    }

    protected int getInsertionIndex(List<DATATYPE> list)
    {
        final int size = list.size();
        int index = this.originalListIndex < 0 ? size : (Math.min(this.originalListIndex, size)) + 1;
        return Math.max(0, Math.min(size, index));
    }

    protected boolean insertEntryAfter()
    {
        DATATYPE entry = this.getNewDataEntry();

        if (entry != null)
        {
            int index = this.getInsertionIndex(this.dataList);
            this.dataList.add(index, entry);
            this.listWidget.refreshEntries();
            this.listWidget.focusWidget(index);
            return  true;
        }

        return false;
    }

    protected boolean removeEntry()
    {
        final int size = this.dataList.size();

        if (this.originalListIndex >= 0 && this.originalListIndex < size)
        {
            this.dataList.remove(this.originalListIndex);
            this.listWidget.refreshEntries();
            return true;
        }

        return false;
    }

    protected boolean moveEntryDown()
    {
        return this.moveEntry(this.originalListIndex + 1);
    }

    protected boolean moveEntryUp()
    {
        return this.moveEntry(this.originalListIndex - 1);
    }

    protected boolean moveEntry(int newIndex)
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

            this.listWidget.refreshEntries();
            return true;
        }

        return false;
    }

    protected boolean canBeMoved(boolean down)
    {
        final int size = this.dataList.size();
        return (this.originalListIndex >= 0 && this.originalListIndex < size) &&
                ((down && this.originalListIndex < (size - 1)) || (down == false && this.originalListIndex > 0));
    }

    @Override
    public boolean isHoveredForRender(ScreenContext ctx)
    {
        return this.dragged == false && super.isHoveredForRender(ctx);
    }

    @Override
    public boolean shouldRenderHoverInfo(ScreenContext ctx)
    {
        return super.shouldRenderHoverInfo(ctx) &&
               BaseScreen.isCtrlDown() == false &&
               BaseScreen.isShiftDown() == false;
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        int width = this.getWidth();
        int height = this.getHeight();
        int listIndex = this.getDataListIndex();
        
        if (this.dragged && listIndex >= 0)
        {
            int newIndex = this.getNewIndexFromDrag(ctx.mouseY);
            int off = (newIndex - listIndex) * height - 1;

            if (newIndex > listIndex)
            {
                off += height;
            }

            ShapeRenderUtils.renderRectangle(x - 2, y + off, z + 50, width + 4, 2, 0xFF00FFFF);

            x += (ctx.mouseX - this.dragStartX);
            y += (ctx.mouseY - this.dragStartY);
            z += 60;

            ShapeRenderUtils.renderOutline(x - 1, y - 1, z, width + 2, height + 2, 1, 0xFFFFFFFF);

            int bgColor = 0xFF303030;
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, bgColor);
        }

        super.renderAt(x, y, z, ctx);
    }

    protected enum ButtonType
    {
        ADD         ("malilib.hover.button.list.add_after", BaseOrderableListEditEntryWidget::insertEntryAfter),
        REMOVE      ("malilib.hover.button.list.remove",    BaseOrderableListEditEntryWidget::removeEntry),
        MOVE_UP     ("malilib.hover.button.list.move_up",   BaseOrderableListEditEntryWidget::moveEntryUp),
        MOVE_DOWN   ("malilib.hover.button.list.move_down", BaseOrderableListEditEntryWidget::moveEntryDown);

        private final String translationKey;
        private final Function<BaseOrderableListEditEntryWidget<?>, Boolean> action;

        ButtonType(String translationKey, Function<BaseOrderableListEditEntryWidget<?>, Boolean> action)
        {
            this.translationKey = translationKey;
            this.action = action;
        }

        public String getHoverKey()
        {
            return this.translationKey;
        }

        public EventListener createListener(final BaseOrderableListEditEntryWidget<?> widget)
        {
            return () -> this.action.apply(widget);
        }
    }
}

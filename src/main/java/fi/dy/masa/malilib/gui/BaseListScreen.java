package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.InteractableWidget;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.list.BaseListWidget;

public abstract class BaseListScreen<LISTWIDGET extends BaseListWidget> extends BaseTabbedScreen
{
    private int listX;
    private int listY;
    protected int totalListMarginX;
    protected int totalListMarginY;
    private LISTWIDGET widget;

    protected BaseListScreen(int listX, int listY, int totalListMarginX, int totalListMarginY)
    {
        this(listX, listY, totalListMarginX, totalListMarginY, "N/A", Collections.emptyList(), null);
    }

    protected BaseListScreen(int listX, int listY, int totalListMarginX, int totalListMarginY,
                             String screenId, List<? extends ScreenTab> screenTabs, @Nullable ScreenTab defaultTab)
    {
        super(screenId, screenTabs, defaultTab);

        this.totalListMarginX = totalListMarginX;
        this.totalListMarginY = totalListMarginY;
        this.shouldCreateTabButtons = screenTabs.isEmpty() == false;

        this.setListPosition(listX, listY);
    }

    @Nullable
    protected abstract LISTWIDGET createListWidget(int listX, int listY, int listWidth, int listHeight);

    protected void setListPosition(int listX, int listY)
    {
        this.listX = listX;
        this.listY = listY;
    }

    protected int getListX()
    {
        return this.x + this.listX;
    }

    protected int getListY()
    {
        return this.y + this.listY;
    }

    protected int getListWidth()
    {
        return this.screenWidth - this.totalListMarginX;
    }

    protected int getListHeight()
    {
        return this.screenHeight - this.totalListMarginY;
    }

    @Nullable
    public LISTWIDGET getListWidget()
    {
        if (this.widget == null)
        {
            this.reCreateListWidget();
        }

        return this.widget;
    }

    protected void reCreateListWidget()
    {
        this.widget = this.createListWidget(this.getListX(), this.getListY(), this.getListWidth(), this.getListHeight());

        if (this.widget != null)
        {
            this.widget.setZLevel((int) this.zLevel + 2);
            this.widget.initWidget();
        }
    }

    public boolean isSearchOpen()
    {
        BaseListWidget listWidget = this.getListWidget();
        return listWidget != null && listWidget.isSearchOpen();
    }

    protected void updateListPosition(int listX, int listY)
    {
        this.setListPosition(listX, listY);

        // Only update the widget if it has already been created.
        // Using the getter method would force the widget to be created now,
        // and that would lead to duplicated call to the setPositionAndSize() method.
        if (this.widget != null)
        {
            this.widget.setPositionAndSize(listX, listY, this.getListWidth(), this.getListHeight());
        }
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        if (this.widget != null)
        {
            this.widget.setPosition(this.getListX(), this.getListY());
        }
    }

    @Override
    protected int getCurrentScrollbarPosition()
    {
        LISTWIDGET listWidget = this.getListWidget();
        return listWidget != null ? listWidget.getScrollbar().getValue() : -1;
    }

    @Override
    protected void setCurrentScrollbarPosition(int position)
    {
        LISTWIDGET listWidget = this.getListWidget();

        if (listWidget != null)
        {
            listWidget.setRequestedScrollBarPosition(position);
        }
    }

    @Override
    protected InteractableWidget getTopHoveredWidget(int mouseX, int mouseY, @Nullable InteractableWidget highestFoundWidget)
    {
        highestFoundWidget = super.getTopHoveredWidget(mouseX, mouseY, highestFoundWidget);
        BaseListWidget listWidget = this.getListWidget();

        if (listWidget != null)
        {
            highestFoundWidget = listWidget.getTopHoveredWidget(mouseX, mouseY, highestFoundWidget);
        }

        return highestFoundWidget;
    }

    @Override
    protected List<BaseTextFieldWidget> getAllTextFields()
    {
        List<BaseTextFieldWidget> textFields = new ArrayList<>(super.getAllTextFields());
        BaseListWidget listWidget = this.getListWidget();

        if (listWidget != null)
        {
            textFields.addAll(listWidget.getAllTextFields());
        }

        return textFields;
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        BaseListWidget listWidget = this.getListWidget();

        if (listWidget != null)
        {
            listWidget.setPositionAndSize(this.getListX(), this.getListY(), this.getListWidth(), this.getListHeight());
        }
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        BaseListWidget listWidget = this.getListWidget();

        if (listWidget != null)
        {
            listWidget.onGuiClosed();
        }
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (super.onMouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        BaseListWidget listWidget = this.getListWidget();

        return listWidget != null && listWidget.tryMouseClick(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (super.onMouseReleased(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        BaseListWidget listWidget = this.getListWidget();

        if (listWidget != null)
        {
            listWidget.onMouseReleased(mouseX, mouseY, mouseButton);
        }

        return false;
    }

    @Override
    public boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (super.onMouseScrolled(mouseX, mouseY, mouseWheelDelta))
        {
            return true;
        }

        BaseListWidget listWidget = this.getListWidget();

        return listWidget != null && listWidget.tryMouseScroll(mouseX, mouseY, mouseWheelDelta);
    }

    @Override
    public boolean onMouseMoved(int mouseX, int mouseY)
    {
        if (super.onMouseMoved(mouseX, mouseY))
        {
            return true;
        }

        BaseListWidget listWidget = this.getListWidget();

        return listWidget != null && listWidget.onMouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        // Try to handle everything except ESC in the parent first
        if (keyCode != Keyboard.KEY_ESCAPE && super.onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        BaseListWidget listWidget = this.getListWidget();

        if (listWidget != null && listWidget.onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        // If the list widget or its sub widgets didn't consume the ESC, then send that to the parent (to close the GUI)
        return keyCode == Keyboard.KEY_ESCAPE && super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onCharTyped(char charIn, int modifiers)
    {
        BaseListWidget listWidget = this.getListWidget();

        if (listWidget != null && listWidget.onCharTyped(charIn, modifiers))
        {
            return true;
        }

        return super.onCharTyped(charIn, modifiers);
    }

    @Override
    protected void renderCustomContents(int mouseX, int mouseY, ScreenContext ctx)
    {
        BaseListWidget listWidget = this.getListWidget();

        if (listWidget != null)
        {
            listWidget.renderAt(listWidget.getX(), listWidget.getY(), listWidget.getZLevel(), ctx);
        }
    }

    @Override
    public void renderDebug(ScreenContext ctx)
    {
        super.renderDebug(ctx);

        BaseListWidget widget = this.getListWidget();

        if (widget != null && ctx.isActiveScreen)
        {
            widget.renderDebug(widget.isMouseOver(ctx.mouseX, ctx.mouseY), ctx);
        }
    }
}

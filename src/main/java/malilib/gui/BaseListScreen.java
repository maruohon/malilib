package malilib.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

import malilib.gui.tab.ScreenTab;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.BaseTextFieldWidget;
import malilib.gui.widget.InteractableWidget;
import malilib.gui.widget.list.BaseListWidget;
import malilib.input.Keys;

public abstract class BaseListScreen<LISTWIDGET extends BaseListWidget> extends BaseTabbedScreen
{
    private LISTWIDGET listWidget;
    protected int listX;
    protected int listY;
    protected int totalListMarginX;
    protected int totalListMarginY;

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

    @Override
    protected void initScreen()
    {
        super.initScreen();
        this.getListWidget().refreshEntries();
    }

    @Override
    protected void onScreenClosed()
    {
        this.getListWidget().onScreenClosed();
        super.onScreenClosed();
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();
        this.getListWidget().setPositionAndSize(this.getListX(), this.getListY(), this.getListWidth(), this.getListHeight());
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (super.onMouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        return this.getListWidget().tryMouseClick(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (super.onMouseReleased(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        this.getListWidget().onMouseReleased(mouseX, mouseY, mouseButton);

        return false;
    }

    @Override
    public boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (super.onMouseScrolled(mouseX, mouseY, mouseWheelDelta))
        {
            return true;
        }

        return this.getListWidget().tryMouseScroll(mouseX, mouseY, mouseWheelDelta);
    }

    @Override
    public boolean onMouseMoved(int mouseX, int mouseY)
    {
        if (super.onMouseMoved(mouseX, mouseY))
        {
            return true;
        }

        return this.getListWidget().onMouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        // Try to handle everything except ESC in the parent first
        if (keyCode != Keys.KEY_ESCAPE && super.onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        if (this.getListWidget().onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        // If the list widget or its sub widgets didn't consume the ESC, then send that to the parent (to close the GUI)
        return keyCode == Keys.KEY_ESCAPE && super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onCharTyped(char charIn, int modifiers)
    {
        if (super.onCharTyped(charIn, modifiers))
        {
            return true;
        }

        return this.getListWidget().onCharTyped(charIn, modifiers);
    }

    public LISTWIDGET getListWidget()
    {
        if (this.listWidget == null)
        {
            this.listWidget = this.createListWidget();
            this.listWidget.setTaskQueue(this::addTask);
            this.listWidget.setZ(this.z + 10);
            this.listWidget.initListWidget();
        }

        return this.listWidget;
    }

    protected abstract LISTWIDGET createListWidget();

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

    protected void setListPosition(int listX, int listY)
    {
        this.listX = listX;
        this.listY = listY;
    }

    protected void updateListPosition(int listX, int listY)
    {
        this.setListPosition(listX, listY);
        this.getListWidget().setPositionAndSize(listX, listY, this.getListWidth(), this.getListHeight());
    }

    public boolean isSearchOpen()
    {
        return this.getListWidget().isSearchOpen();
    }

    @Override
    protected int getCurrentScrollbarPosition()
    {
        return this.getListWidget().getScrollbar().getValue();
    }

    @Override
    protected void setCurrentScrollbarPosition(int position)
    {
        this.getListWidget().setRequestedScrollBarPosition(position);
    }

    @Override
    protected InteractableWidget getTopHoveredWidget(int mouseX, int mouseY, @Nullable InteractableWidget highestFoundWidget)
    {
        highestFoundWidget = super.getTopHoveredWidget(mouseX, mouseY, highestFoundWidget);
        return this.getListWidget().getTopHoveredWidget(mouseX, mouseY, highestFoundWidget);
    }

    @Override
    protected List<BaseTextFieldWidget> getAllTextFields()
    {
        List<BaseTextFieldWidget> textFields = new ArrayList<>(super.getAllTextFields());

        textFields.addAll(this.getListWidget().getAllTextFields());

        return textFields;
    }

    @Override
    protected void renderCustomContents(ScreenContext ctx)
    {
        this.getListWidget().render(ctx);
    }

    @Override
    public void renderDebug(ScreenContext ctx)
    {
        super.renderDebug(ctx);

        if (ctx.isActiveScreen)
        {
            this.getListWidget().renderDebug(this.getListWidget().isMouseOver(ctx.mouseX, ctx.mouseY), ctx);
        }
    }
}

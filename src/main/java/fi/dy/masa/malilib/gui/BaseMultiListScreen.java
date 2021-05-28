package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.InteractableWidget;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.list.BaseListWidget;

public class BaseMultiListScreen extends BaseTabbedScreen
{
    protected final ArrayList<BaseListWidget> listWidgets = new ArrayList<>();

    public BaseMultiListScreen(String screenId, List<? extends ScreenTab> screenTabs, @Nullable ScreenTab defaultTab)
    {
        super(screenId, screenTabs, defaultTab);

        this.shouldCreateTabButtons = screenTabs.isEmpty() == false;
    }

    @Override
    protected int getCurrentScrollbarPosition()
    {
        return 0;
    }

    @Override
    protected void setCurrentScrollbarPosition(int position)
    {
    }

    protected void addListWidget(BaseListWidget widget)
    {
        widget.setTaskQueue(this::addTask);
        widget.setZLevel((int) this.zLevel + 2);
        widget.initWidget();

        this.listWidgets.add(widget);
    }

    @Override
    protected void clearElements()
    {
        super.clearElements();

        this.listWidgets.clear();
    }

    @Override
    protected InteractableWidget getTopHoveredWidget(int mouseX, int mouseY, @Nullable InteractableWidget highestFoundWidget)
    {
        highestFoundWidget = super.getTopHoveredWidget(mouseX, mouseY, highestFoundWidget);

        for (BaseListWidget listWidget : this.listWidgets)
        {
            highestFoundWidget = listWidget.getTopHoveredWidget(mouseX, mouseY, highestFoundWidget);
        }

        return highestFoundWidget;
    }

    @Override
    protected List<BaseTextFieldWidget> getAllTextFields()
    {
        List<BaseTextFieldWidget> textFields = new ArrayList<>(super.getAllTextFields());

        for (BaseListWidget listWidget : this.listWidgets)
        {
            textFields.addAll(listWidget.getAllTextFields());
        }

        return textFields;
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        for (BaseListWidget listWidget : this.listWidgets)
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

        for (BaseListWidget listWidget : this.listWidgets)
        {
            if (listWidget.tryMouseClick(mouseX, mouseY, mouseButton))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (super.onMouseReleased(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        for (BaseListWidget listWidget : this.listWidgets)
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

        for (BaseListWidget listWidget : this.listWidgets)
        {
            if (listWidget.tryMouseScroll(mouseX, mouseY, mouseWheelDelta))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onMouseMoved(int mouseX, int mouseY)
    {
        if (super.onMouseMoved(mouseX, mouseY))
        {
            return true;
        }

        for (BaseListWidget listWidget : this.listWidgets)
        {
            if (listWidget.onMouseMoved(mouseX, mouseY))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        // Try to handle everything except ESC in the parent first
        if (keyCode != Keyboard.KEY_ESCAPE && super.onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        for (BaseListWidget listWidget : this.listWidgets)
        {
            if (listWidget.onKeyTyped(keyCode, scanCode, modifiers))
            {
                return true;
            }
        }

        // If the list widget or its sub widgets didn't consume the ESC, then send that to the parent (to close the GUI)
        return keyCode == Keyboard.KEY_ESCAPE && super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onCharTyped(char charIn, int modifiers)
    {
        if (super.onCharTyped(charIn, modifiers))
        {
            return true;
        }

        for (BaseListWidget listWidget : this.listWidgets)
        {
            if (listWidget.onCharTyped(charIn, modifiers))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void renderCustomContents(ScreenContext ctx)
    {
        for (BaseListWidget listWidget : this.listWidgets)
        {
            listWidget.renderAt(listWidget.getX(), listWidget.getY(), listWidget.getZLevel(), ctx);
        }
    }

    @Override
    public void renderDebug(ScreenContext ctx)
    {
        super.renderDebug(ctx);

        if (ctx.isActiveScreen)
        {
            for (BaseListWidget listWidget : this.listWidgets)
            {
                listWidget.renderDebug(listWidget.isMouseOver(ctx.mouseX, ctx.mouseY), ctx);
            }
        }
    }
}

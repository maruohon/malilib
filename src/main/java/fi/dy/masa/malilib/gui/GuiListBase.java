package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.WidgetBase;
import fi.dy.masa.malilib.gui.widget.WidgetListBase;
import fi.dy.masa.malilib.gui.widget.WidgetTextFieldBase;

public abstract class GuiListBase<LISTWIDGET extends WidgetListBase> extends GuiBase
{
    private int listX;
    private int listY;
    private LISTWIDGET widget;

    protected GuiListBase(int listX, int listY)
    {
        this.setListPosition(listX, listY);
    }

    protected void setListPosition(int listX, int listY)
    {
        this.listX = listX;
        this.listY = listY;
    }

    protected int getListX()
    {
        return this.listX;
    }

    protected int getListY()
    {
        return this.listY;
    }

    protected abstract LISTWIDGET createListWidget(int listX, int listY);

    protected abstract int getBrowserWidth();

    protected abstract int getBrowserHeight();

    @Nullable
    protected LISTWIDGET getListWidget()
    {
        if (this.widget == null)
        {
            this.reCreateListWidget();
        }

        return this.widget;
    }

    protected void reCreateListWidget()
    {
        this.widget = this.createListWidget(this.getListX(), this.getListY());
    }

    public boolean isSearchOpen()
    {
        return this.getListWidget() != null && this.getListWidget().isSearchOpen();
    }

    protected void updateListPosition(int listX, int listY)
    {
        WidgetListBase listWidget = this.getListWidget();

        if (listWidget != null)
        {
            int scrollbarPosition = listWidget.getScrollbar().getValue();
            this.setListPosition(listX, listY);
            this.reCreateListWidget();

            // Fetch the new reference...
            listWidget = this.getListWidget();
            listWidget.getScrollbar().setValue(scrollbarPosition);
            listWidget.refreshEntries();
        }
    }

    @Override
    protected WidgetBase getTopHoveredWidget(int mouseX, int mouseY, @Nullable WidgetBase highestFoundWidget)
    {
        highestFoundWidget = super.getTopHoveredWidget(mouseX, mouseY, highestFoundWidget);
        WidgetListBase listWidget = this.getListWidget();

        if (listWidget != null)
        {
            highestFoundWidget = listWidget.getTopHoveredWidget(mouseX, mouseY, highestFoundWidget);
        }

        return highestFoundWidget;
    }

    @Override
    protected List<WidgetTextFieldBase> getAllTextFields()
    {
        List<WidgetTextFieldBase> textFields = new ArrayList<>(super.getAllTextFields());
        WidgetListBase listWidget = this.getListWidget();

        if (listWidget != null)
        {
            textFields.addAll(listWidget.getAllTextFields());
        }

        return textFields;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        WidgetListBase listWidget = this.getListWidget();

        if (listWidget != null)
        {
            listWidget.setSize(this.getBrowserWidth(), this.getBrowserHeight());
            listWidget.initWidget();
        }
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        if (this.getListWidget() != null)
        {
            this.getListWidget().onGuiClosed();
        }
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (super.onMouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        return this.getListWidget() != null && this.getListWidget().onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (super.onMouseReleased(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        if (this.getListWidget() != null)
        {
            this.getListWidget().onMouseReleased(mouseX, mouseY, mouseButton);
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

        return this.getListWidget() != null && this.getListWidget().onMouseScrolled(mouseX, mouseY, mouseWheelDelta);
    }

    @Override
    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        // Try to handle everything except ESC in the parent first
        if (keyCode != Keyboard.KEY_ESCAPE && super.onKeyTyped(typedChar, keyCode))
        {
            return true;
        }

        if (this.getListWidget() != null && this.getListWidget().onKeyTyped(typedChar, keyCode))
        {
            return true;
        }

        // If the list widget or its sub widgets didn't consume the ESC, then send that to the parent (to close the GUI)
        return keyCode == Keyboard.KEY_ESCAPE && super.onKeyTyped(typedChar, keyCode);
    }

    @Override
    public void drawContents(int mouseX, int mouseY, float partialTicks)
    {
        if (this.getListWidget() != null)
        {
            boolean isActiveGui = GuiUtils.getCurrentScreen() == this;
            int hoveredId = isActiveGui && this.hoveredWidget != null ? this.hoveredWidget.getId() : -1;
            this.getListWidget().render(mouseX, mouseY, isActiveGui, hoveredId);
        }
    }

    @Override
    public void renderDebug(int mouseX, int mouseY)
    {
        super.renderDebug(mouseX, mouseY);

        WidgetListBase widget = this.getListWidget();

        if (widget != null)
        {
            boolean renderAll = MaLiLibConfigs.Debug.GUI_DEBUG_ALL.getBooleanValue();
            boolean infoAlways = MaLiLibConfigs.Debug.GUI_DEBUG_INFO_ALWAYS.getBooleanValue();

            widget.renderDebug(mouseX, mouseY, widget.isMouseOver(mouseX, mouseY), renderAll, infoAlways);
        }
    }
}

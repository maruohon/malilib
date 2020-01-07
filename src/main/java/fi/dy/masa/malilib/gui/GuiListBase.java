package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;

public abstract class GuiListBase<TYPE, WIDGET extends WidgetListEntryBase<TYPE>, WIDGETLIST extends WidgetListBase<TYPE, WIDGET>> extends GuiBase
{
    private int listX;
    private int listY;
    private WIDGETLIST widget;

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

    protected abstract WIDGETLIST createListWidget(int listX, int listY);

    protected abstract int getBrowserWidth();

    protected abstract int getBrowserHeight();

    @Nullable
    protected ISelectionListener<TYPE> getSelectionListener()
    {
        return null;
    }

    @Nullable
    protected WIDGETLIST getListWidget()
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
        return this.getListWidget().isSearchOpen();
    }

    @Override
    public GuiBase setParent(GuiScreen parent)
    {
        return super.setParent(parent);
    }

    protected void updateListPosition(int listX, int listY)
    {
        int scrollbarPosition = this.getListWidget().getScrollbar().getValue();
        this.setListPosition(listX, listY);
        this.reCreateListWidget();
        this.getListWidget().getScrollbar().setValue(scrollbarPosition);
        this.getListWidget().refreshEntries();
    }

    @Override
    public void initGui()
    {
        super.initGui();

        if (this.getListWidget() != null)
        {
            this.getListWidget().setSize(this.getBrowserWidth(), this.getBrowserHeight());
            this.getListWidget().initGui();
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

        return this.getListWidget() != null && this.getListWidget().onMouseReleased(mouseX, mouseY, mouseButton);
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
        if (keyCode == Keyboard.KEY_ESCAPE && super.onKeyTyped(typedChar, keyCode))
        {
            return true;
        }

        return false;
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);

        if (this.getListWidget() != null)
        {
            this.getListWidget().setWorldAndResolution(mc, width, height);
        }
    }

    @Override
    public void drawContents(int mouseX, int mouseY, float partialTicks)
    {
        if (this.getListWidget() != null)
        {
            this.getListWidget().drawContents(mouseX, mouseY, partialTicks);
        }
    }
}

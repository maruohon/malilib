package fi.dy.masa.malilib.registry;

import fi.dy.masa.malilib.action.ActionRegistry;
import fi.dy.masa.malilib.command.ClientCommandHandler;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.ConfigManagerImpl;
import fi.dy.masa.malilib.config.serialization.JsonConfigSerializerRegistry;
import fi.dy.masa.malilib.event.dispatch.ClientWorldChangeEventDispatcher;
import fi.dy.masa.malilib.event.dispatch.ClientWorldChangeEventDispatcherImpl;
import fi.dy.masa.malilib.event.dispatch.InitializationDispatcher;
import fi.dy.masa.malilib.event.dispatch.InitializationDispatcherImpl;
import fi.dy.masa.malilib.event.dispatch.RenderEventDispatcher;
import fi.dy.masa.malilib.event.dispatch.RenderEventDispatcherImpl;
import fi.dy.masa.malilib.event.dispatch.TickEventDispatcher;
import fi.dy.masa.malilib.event.dispatch.TickEventDispatcherImpl;
import fi.dy.masa.malilib.gui.config.indicator.ConfigStatusWidgetRegistry;
import fi.dy.masa.malilib.gui.config.registry.ConfigScreenRegistry;
import fi.dy.masa.malilib.gui.config.registry.ConfigTabExtensionRegistry;
import fi.dy.masa.malilib.gui.config.registry.ConfigTabRegistry;
import fi.dy.masa.malilib.gui.config.registry.ConfigTabRegistryImpl;
import fi.dy.masa.malilib.gui.config.registry.ConfigWidgetRegistry;
import fi.dy.masa.malilib.gui.icon.IconRegistry;
import fi.dy.masa.malilib.input.HotkeyManager;
import fi.dy.masa.malilib.input.HotkeyManagerImpl;
import fi.dy.masa.malilib.input.InputDispatcher;
import fi.dy.masa.malilib.input.InputDispatcherImpl;
import fi.dy.masa.malilib.interoperation.BlockPlacementPositionHandler;
import fi.dy.masa.malilib.network.ClientPacketChannelHandler;
import fi.dy.masa.malilib.network.ClientPacketChannelHandlerImpl;
import fi.dy.masa.malilib.overlay.InfoOverlay;
import fi.dy.masa.malilib.overlay.InfoWidgetManager;
import fi.dy.masa.malilib.overlay.InfoWidgetRegistry;
import fi.dy.masa.malilib.overlay.message.MessageRedirectManager;

public class Registry
{
    // Registries
    public static final ActionRegistry ACTION_REGISTRY = new ActionRegistry();
    public static final ConfigStatusWidgetRegistry CONFIG_STATUS_WIDGET = new ConfigStatusWidgetRegistry();
    public static final ConfigScreenRegistry CONFIG_SCREEN = new ConfigScreenRegistry();
    public static final ConfigTabRegistry CONFIG_TAB = new ConfigTabRegistryImpl();
    public static final ConfigTabExtensionRegistry CONFIG_TAB_EXTENSION = new ConfigTabExtensionRegistry();
    public static final ConfigWidgetRegistry CONFIG_WIDGET = new ConfigWidgetRegistry();
    public static final IconRegistry ICON = new IconRegistry();
    public static final InfoWidgetRegistry INFO_WIDGET = new InfoWidgetRegistry();
    public static final JsonConfigSerializerRegistry JSON_CONFIG_SERIALIZER = new JsonConfigSerializerRegistry();

    // Various "managers" or "handlers"
    public static final ClientCommandHandler CLIENT_COMMAND_HANDLER = new ClientCommandHandler();
    public static final ConfigManager CONFIG_MANAGER = new ConfigManagerImpl();
    public static final HotkeyManager HOTKEY_MANAGER = new HotkeyManagerImpl();
    public static final InfoOverlay INFO_OVERLAY = new InfoOverlay();
    public static final InfoWidgetManager INFO_WIDGET_MANAGER = new InfoWidgetManager(INFO_OVERLAY);
    public static final MessageRedirectManager MESSAGE_REDIRECT_MANAGER = new MessageRedirectManager();

    // Event dispatchers and handlers
    public static final BlockPlacementPositionHandler BLOCK_PLACEMENT_POSITION_HANDLER = new BlockPlacementPositionHandler();
    public static final ClientPacketChannelHandler CLIENT_PACKET_CHANNEL_HANDLER = new ClientPacketChannelHandlerImpl();
    public static final ClientWorldChangeEventDispatcher CLIENT_WORLD_CHANGE_EVENT_DISPATCHER = new ClientWorldChangeEventDispatcherImpl();
    public static final InitializationDispatcher INITIALIZATION_DISPATCHER = new InitializationDispatcherImpl();
    public static final InputDispatcher INPUT_DISPATCHER = new InputDispatcherImpl();
    public static final RenderEventDispatcher RENDER_EVENT_DISPATCHER = new RenderEventDispatcherImpl();
    public static final TickEventDispatcher TICK_EVENT_DISPATCHER = new TickEventDispatcherImpl();
}

package malilib.registry;

import malilib.action.ActionRegistry;
import malilib.config.ConfigManager;
import malilib.config.ConfigManagerImpl;
import malilib.config.serialization.JsonConfigSerializerRegistry;
import malilib.event.dispatch.ClientWorldChangeEventDispatcher;
import malilib.event.dispatch.ClientWorldChangeEventDispatcherImpl;
import malilib.event.dispatch.InitializationDispatcher;
import malilib.event.dispatch.InitializationDispatcherImpl;
import malilib.event.dispatch.RenderEventDispatcher;
import malilib.event.dispatch.RenderEventDispatcherImpl;
import malilib.event.dispatch.TickEventDispatcher;
import malilib.event.dispatch.TickEventDispatcherImpl;
import malilib.gui.config.indicator.ConfigStatusWidgetRegistry;
import malilib.gui.config.registry.ConfigScreenRegistry;
import malilib.gui.config.registry.ConfigTabExtensionRegistry;
import malilib.gui.config.registry.ConfigTabRegistry;
import malilib.gui.config.registry.ConfigTabRegistryImpl;
import malilib.gui.config.registry.ConfigWidgetRegistry;
import malilib.gui.icon.IconRegistry;
import malilib.input.HotkeyManager;
import malilib.input.HotkeyManagerImpl;
import malilib.input.InputDispatcher;
import malilib.input.InputDispatcherImpl;
import malilib.interoperation.BlockPlacementPositionHandler;
import malilib.network.ClientPacketChannelHandler;
import malilib.network.ClientPacketChannelHandlerImpl;
import malilib.overlay.InfoOverlay;
import malilib.overlay.InfoWidgetManager;
import malilib.overlay.InfoWidgetRegistry;
import malilib.overlay.message.MessageRedirectManager;
import malilib.util.text.TranslationOverrideManager;

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
    /*
    public static final ClientCommandHandler CLIENT_COMMAND_HANDLER = new ClientCommandHandler();
    */
    public static final ConfigManager CONFIG_MANAGER = new ConfigManagerImpl();
    public static final HotkeyManager HOTKEY_MANAGER = new HotkeyManagerImpl();
    public static final InfoOverlay INFO_OVERLAY = new InfoOverlay();
    public static final InfoWidgetManager INFO_WIDGET_MANAGER = new InfoWidgetManager(INFO_OVERLAY);
    public static final MessageRedirectManager MESSAGE_REDIRECT_MANAGER = new MessageRedirectManager();
    public static final TranslationOverrideManager TRANSLATION_OVERRIDE_MANAGER = new TranslationOverrideManager();

    // Event dispatchers and handlers
    public static final BlockPlacementPositionHandler BLOCK_PLACEMENT_POSITION_HANDLER = new BlockPlacementPositionHandler();
    public static final ClientPacketChannelHandler CLIENT_PACKET_CHANNEL_HANDLER = new ClientPacketChannelHandlerImpl();
    public static final ClientWorldChangeEventDispatcher CLIENT_WORLD_CHANGE_EVENT_DISPATCHER = new ClientWorldChangeEventDispatcherImpl();
    public static final InitializationDispatcher INITIALIZATION_DISPATCHER = new InitializationDispatcherImpl();
    public static final InputDispatcher INPUT_DISPATCHER = new InputDispatcherImpl();
    public static final RenderEventDispatcher RENDER_EVENT_DISPATCHER = new RenderEventDispatcherImpl();
    public static final TickEventDispatcher TICK_EVENT_DISPATCHER = new TickEventDispatcherImpl();
}

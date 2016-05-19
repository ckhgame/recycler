package ovh.corail.recycler.handler;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import ovh.corail.recycler.core.Main;
import ovh.corail.recycler.packet.RecycleMessage;
import ovh.corail.recycler.packet.ClientProgressMessage;
import ovh.corail.recycler.packet.ServerProgressMessage;
import ovh.corail.recycler.packet.SoundMessage;
import ovh.corail.recycler.packet.TakeAllMessage;
import ovh.corail.recycler.packet.VisualMessage;

public class PacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(Main.MOD_ID);

	public static void init() {
		int id = 0;
		INSTANCE.registerMessage(RecycleMessage.Handler.class, RecycleMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(VisualMessage.Handler.class, VisualMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(ClientProgressMessage.Handler.class, ClientProgressMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(ServerProgressMessage.Handler.class, ServerProgressMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(TakeAllMessage.Handler.class, TakeAllMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(SoundMessage.Handler.class, SoundMessage.class, id++, Side.CLIENT);
	}
}

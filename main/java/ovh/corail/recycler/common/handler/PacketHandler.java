package ovh.corail.recycler.common.handler;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import ovh.corail.recycler.common.Main;
import ovh.corail.recycler.common.packets.ButtonMessage;
import ovh.corail.recycler.common.packets.ProgressMessage;
import ovh.corail.recycler.common.packets.VisualMessage;

public class PacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(Main.MODID);

	public static void init() {
		int id = 0;
		INSTANCE.registerMessage(ButtonMessage.class, ButtonMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(VisualMessage.class, VisualMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(ProgressMessage.class, ProgressMessage.class, id++, Side.CLIENT);
	}
}

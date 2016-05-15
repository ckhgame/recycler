package ovh.corail.recycler.handler;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import ovh.corail.recycler.core.Main;
import ovh.corail.recycler.packet.ButtonMessage;
import ovh.corail.recycler.packet.ProgressMessage;
import ovh.corail.recycler.packet.ResetProgressMessage;
import ovh.corail.recycler.packet.SwitchWorkingMessage;
import ovh.corail.recycler.packet.TakeAllMessage;
import ovh.corail.recycler.packet.VisualMessage;

public class PacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(Main.MOD_ID);

	public static void init() {
		int id = 0;
		INSTANCE.registerMessage(ButtonMessage.Handler.class, ButtonMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(VisualMessage.Handler.class, VisualMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(ProgressMessage.Handler.class, ProgressMessage.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(TakeAllMessage.Handler.class, TakeAllMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(SwitchWorkingMessage.Handler.class, SwitchWorkingMessage.class, id++, Side.SERVER);
		INSTANCE.registerMessage(ResetProgressMessage.Handler.class, ResetProgressMessage.class, id++, Side.SERVER);
	}
}

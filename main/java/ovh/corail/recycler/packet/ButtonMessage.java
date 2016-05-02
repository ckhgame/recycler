package ovh.corail.recycler.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.recycler.tileentity.TileEntityRecycler;

public class ButtonMessage implements IMessage, IMessageHandler<ButtonMessage, IMessage> {
	int id, x, y, z;

	public ButtonMessage() {
	}

	public ButtonMessage(int id, int x, int y, int z) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public IMessage onMessage(final ButtonMessage message, final MessageContext ctx) {
		IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				TileEntityRecycler tile = (TileEntityRecycler) ctx.getServerHandler().playerEntity.worldObj
						.getTileEntity(new BlockPos(message.x, message.y, message.z));
				switch (message.id) {
				case 0: // Recycle
					tile.recycle(null);
					break;
				case 1: // Auto-recycle
					tile.switchWorking(); 
					break;
				}
			}
		});
		return null;
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(this.id);
	}
}

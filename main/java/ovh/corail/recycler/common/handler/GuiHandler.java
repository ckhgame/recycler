package ovh.corail.recycler.common.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import ovh.corail.recycler.common.ContainerRecycler;
import ovh.corail.recycler.common.tileentity.TileEntityRecycler;
import ovh.corail.recycler.gui.GuiRecycler;

public class GuiHandler implements IGuiHandler {
	public static final int RECYCLER = 0;

	public static int getGuiID() {
		return RECYCLER;
	}

	@Override
	public Object getServerGuiElement(int guiId, EntityPlayer playerIn, World worldIn, int x, int y, int z) {
		if (guiId != RECYCLER) {
			System.err.println("Invalid ID: expected " + RECYCLER + ", received " + guiId);
		}
		TileEntity tileEntity = worldIn.getTileEntity(new BlockPos(x, y, z));
		if (tileEntity instanceof TileEntityRecycler) {
			return new ContainerRecycler(playerIn, worldIn, x, y, z, (TileEntityRecycler) tileEntity);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int guiId, EntityPlayer playerIn, World worldIn, int x, int y, int z) {
		if (guiId != RECYCLER) {
			System.err.println("Invalid ID: expected " + RECYCLER + ", received " + guiId);
		}
		TileEntity tileEntity = worldIn.getTileEntity(new BlockPos(x, y, z));
		if (tileEntity instanceof TileEntityRecycler) {
			return new GuiRecycler(playerIn, worldIn, x, y, z, (TileEntityRecycler) tileEntity);
		}
		return null;
	}

}
package ovh.corail.recycler.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import ovh.corail.recycler.client.gui.GuiRecycler;
import ovh.corail.recycler.container.ContainerRecycler;
import ovh.corail.recycler.tileentity.TileEntityRecycler;

public class GuiHandler implements IGuiHandler {
	private static final int MOD_TILE_ENTITY_GUI =0;
	public static int getGuiID() {
		return MOD_TILE_ENTITY_GUI;
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID != getGuiID()) {
			System.err.println("Invalid ID: expected " + getGuiID() + ", received " + ID);
		}
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
		if (tileEntity instanceof TileEntityRecycler) { 
			return new ContainerRecycler(player, world, x, y, z, (TileEntityRecycler) tileEntity);
		} 
		return null;
	} 

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID != getGuiID()) {
			System.err.println("Invalid ID: expected " + getGuiID() + ", received " + ID);
		}
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
		if (tileEntity instanceof TileEntityRecycler) {
			return new GuiRecycler(player, world, x, y, z, (TileEntityRecycler) tileEntity);
		}
		return null;
	}

}
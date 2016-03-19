package ovh.corail.recycler.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BasicBlock extends Block {

	public BasicBlock(String unlocalizedName, Material material, float hardness, float resistance) {
		super(material);
		setUnlocalizedName(unlocalizedName);
		setCreativeTab(CreativeTabs.tabBlock);
		setHardness(hardness);
		setResistance(resistance);
		// setStepSound, setLightOpacity, setLightLevel,
		// setHarvestLevel, setBlockUnbreakable, setTickRandomly
		GameRegistry.registerBlock(this, unlocalizedName);
	}

	public BasicBlock(String unlocalizedName, float hardness, float resistance) {
		this(unlocalizedName, Material.rock, hardness, resistance);
	}

	public BasicBlock(String unlocalizedName) {
		this(unlocalizedName, 2.0f, 10.0f);
	}
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state) {
		world.scheduleUpdate(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), this, this.tickRate(world));	
	}
	@Override
	public int tickRate(World world) {
	    return 10;
	}
}

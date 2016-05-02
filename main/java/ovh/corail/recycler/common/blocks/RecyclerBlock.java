package ovh.corail.recycler.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ovh.corail.recycler.common.tileentity.TileEntityRecycler;
import ovh.corail.recycler.core.Main;
import ovh.corail.recycler.core.MainUtil;

public class RecyclerBlock extends BlockContainer {
	private static final String name = "recycler";
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	private static final boolean isGrind = false;
	protected static final AxisAlignedBB north = new AxisAlignedBB(0.25D, 0.0D, 0.125D, 0.75D, 0.5D, 1.0D);
	protected static final AxisAlignedBB east = new AxisAlignedBB(0.0D, 0.0D, 0.25D, 0.875D, 0.5D, 0.75D);
	protected static final AxisAlignedBB south = new AxisAlignedBB(0.25D, 0.0D, 0.0D, 0.75D, 0.5D, 0.875D);
	protected static final AxisAlignedBB west = new AxisAlignedBB(0.125D, 0.0D, 0.25D, 1.0D, 0.5D, 0.75D);

	public RecyclerBlock() {
		super(Material.rock);
		setCreativeTab(CreativeTabs.tabMisc);
		setRegistryName(name);
		setUnlocalizedName(name);
		setHardness(2.0f);
		setResistance(10.0f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	protected boolean canSilkHarvest() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
		if (!world.isRemote) {
			if (player instanceof EntityPlayer) {
				player.openGui(Main.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return true;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return true;
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing) state.getValue(FACING)).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityRecycler();
	}
	
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing enumfacing = state.getValue(FACING);
		if (enumfacing == EnumFacing.NORTH)
			return north;
		if (enumfacing == EnumFacing.EAST)
			return east;
		if (enumfacing == EnumFacing.SOUTH)
			return south;
		if (enumfacing == EnumFacing.WEST)
			return west;
		return super.getBoundingBox(state, source, pos);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		/** TODO to be checked */
		TileEntity tileentity = world.getTileEntity(pos);
		if (tileentity instanceof TileEntityRecycler) {
			InventoryHelper.dropInventoryItems(world, pos, (IInventory) tileentity);
			world.updateComparatorOutputLevel(pos, this);
		}
		super.breakBlock(world, pos, state);
	}

}

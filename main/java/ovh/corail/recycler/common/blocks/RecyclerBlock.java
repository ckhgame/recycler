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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ovh.corail.recycler.common.Main;
import ovh.corail.recycler.common.MainUtil;
import ovh.corail.recycler.common.tileentity.RecyclerTile;

public class RecyclerBlock extends BlockContainer {
	private static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	private boolean isGrind=false;
	private String name;

	public RecyclerBlock() {
		super(Material.rock);
		name = "recycler";
		setCreativeTab(CreativeTabs.tabMisc);
		setUnlocalizedName(name);
		setHardness(2.0f);
		setResistance(10.0f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		GameRegistry.registerBlock(this, name);
	}

	private boolean isGrind() {
		return isGrind;
	}
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
		if (!world.isRemote) {
			if (player instanceof EntityPlayer) {
				player.openGui(Main.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}

		return true;
	}
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL; //TODO verifier que c bien le meme que 3
    }
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
	}

	@Override
	public int tickRate(World world) {
		return 1;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new RecyclerTile();
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		// TODO a verifier
		TileEntity tileentity = world.getTileEntity(pos);
		if (tileentity instanceof RecyclerTile) {
			InventoryHelper.dropInventoryItems(world, pos, (IInventory) tileentity);
			world.updateComparatorOutputLevel(pos, this);
		}
		super.breakBlock(world, pos, state);
	}
	/**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing)state.getValue(FACING)).getIndex();
    }
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] { FACING });
    }
}

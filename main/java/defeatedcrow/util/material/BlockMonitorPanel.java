package defeatedcrow.util.material;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockMonitorPanel extends DCBlockContainerBase {

	protected static final AxisAlignedBB AABB_FULL = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	protected static final AxisAlignedBB AABB_AXIS_X1 = new AxisAlignedBB(0.0D, 0.125D, 0.125D, 0.25D, 0.875D, 0.875D);
	protected static final AxisAlignedBB AABB_AXIS_Y1 = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);
	protected static final AxisAlignedBB AABB_AXIS_Z1 = new AxisAlignedBB(0.125D, 0.125D, 0.0D, 0.875D, 0.875D, 0.25D);
	protected static final AxisAlignedBB AABB_AXIS_X2 = new AxisAlignedBB(0.75D, 0.125D, 0.125D, 1.0D, 0.875D, 0.875D);
	protected static final AxisAlignedBB AABB_AXIS_Y2 = new AxisAlignedBB(0.125D, 0.75D, 0.125D, 0.875D, 1.0D, 0.875D);
	protected static final AxisAlignedBB AABB_AXIS_Z2 = new AxisAlignedBB(0.125D, 0.125D, 0.75D, 0.875D, 0.875D, 1.0D);

	public BlockMonitorPanel(String s) {
		super(Material.CLAY, s);
		this.setSoundType(SoundType.METAL);
		this.setHardness(1.5F);
		this.setResistance(15.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(DCStateBase.SIDE, EnumSide.DOWN).withProperty(
				DCStateBase.POWERED, false));
		this.fullBlock = false;
		this.lightOpacity = 0;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		if (DCStateBase.hasProperty(state, DCStateBase.SIDE)) {
			switch (DCStateBase.getSide(state, DCStateBase.SIDE)) {
			case DOWN:
				return AABB_AXIS_Y1;
			case UP:
				return AABB_AXIS_Y2;
			case WEST:
				return AABB_AXIS_X1;
			case EAST:
				return AABB_AXIS_X2;
			case NORTH:
				return AABB_AXIS_Z1;
			case SOUTH:
				return AABB_AXIS_Z2;
			default:
				return AABB_FULL;
			}
		}
		return AABB_FULL;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof ITagGetter) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag != null) {
				((ITagGetter) tile).setNBT(tag);
			}
		}
	}

	@Override
	public IBlockState getPlaceState(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer, EnumHand hand) {
		IBlockState state = super.getPlaceState(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
		state = state.withProperty(DCStateBase.SIDE, EnumSide.fromFacing(facing.getOpposite()));
		return state;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity tile = world.getTileEntity(pos);
		int i = this.damageDropped(state);
		ItemStack drop = new ItemStack(this, 1, i);

		if (tile != null && tile instanceof ITagGetter) {
			NBTTagCompound tag = new NBTTagCompound();
			tag = ((ITagGetter) tile).getNBT(tag);
			if (tag != null) {
				drop.setTagCompound(tag);
			}
		}

		if (!world.isRemote) {
			EntityItem entityitem = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
					drop);
			float f3 = 0.05F;
			entityitem.motionX = (float) world.rand.nextGaussian() * f3;
			entityitem.motionY = (float) world.rand.nextGaussian() * f3 + 0.25F;
			entityitem.motionZ = (float) world.rand.nextGaussian() * f3;
			world.spawnEntity(entityitem);
		}
		world.updateComparatorOutputLevel(pos, state.getBlock());
		super.breakBlock(world, pos, state);

	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(DCStateBase.POWERED) ? 15 : 0;
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
		return calcRedstone(world.getTileEntity(pos));
	}

	protected int calcRedstone(TileEntity tile) {
		if (tile != null && tile instanceof TileMonitorBase) {
			float amo = ((TileMonitorBase) tile).getGauge();
			if (amo > 0) {
				return MathHelper.ceil(amo * 15F);
			}
		}
		return 0;
	}

	// state関連
	@Override
	public IBlockState getStateFromMeta(int meta) {
		int m = meta & 7;
		IBlockState state = this.getDefaultState().withProperty(DCStateBase.SIDE, EnumSide.fromIndex(m)).withProperty(
				DCStateBase.POWERED, Boolean.valueOf((meta & 8) > 0));
		return state;
	}

	// state
	@Override
	public int getMetaFromState(IBlockState state) {
		int f = 0;
		int i = 0;

		f = state.getValue(DCStateBase.SIDE).index;
		i = state.getValue(DCStateBase.POWERED) ? 8 : 0;
		return i + f;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {
				DCStateBase.SIDE,
				DCStateBase.POWERED
		});

	}

}

package defeatedcrow.util.material;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFaucet_SUS extends DCBlockContainerBase {

	protected static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.3125D, 0.0D, 0.5D, 0.6875D, 0.75D, 1.0D);
	protected static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.3125D, 0.0D, 0.0D, 0.6875D, 0.75D, 0.5D);
	protected static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0.5D, 0.0D, 0.3125D, 1.0D, 0.75D, 0.6875D);
	protected static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(0.0D, 0.0D, 0.3125D, 0.5D, 0.75D, 0.6875D);
	protected static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 1.0D, 0.6875D);

	public BlockFaucet_SUS(String s) {
		super(Material.ROCK, s);
		this.setHardness(2.0F);
		this.setResistance(15.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(DCStateBase.SIDE, EnumSide.DOWN).withProperty(
				DCStateBase.POWERED, false));
		this.fullBlock = false;
		this.lightOpacity = 0;
	}

	/* GUIは持たない。 */
	@Override
	public boolean onRightClick(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && hand == EnumHand.MAIN_HAND) {
			boolean power = DCStateBase.getBool(state, DCStateBase.POWERED);
			IBlockState state2 = state.withProperty(DCStateBase.POWERED, !power);
			world.setBlockState(pos, state2, 2);
			world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.8F, 2.0F);
		}
		return true;
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
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public List<ItemStack> getSubItemList() {
		List<ItemStack> list = Lists.newArrayList();
		list.add(new ItemStack(this, 1, 0));
		return list;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing face = DCStateBase.getSide(state, DCStateBase.SIDE).face;
		switch (face) {
		case EAST:
			return AABB_EAST;
		case NORTH:
			return AABB_NORTH;
		case SOUTH:
			return AABB_SOUTH;
		case WEST:
			return AABB_WEST;
		default:
			return AABB_DOWN;

		}
	}

	// 設置・破壊処理
	@Override
	public IBlockState getPlaceState(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer, EnumHand hand) {
		IBlockState state = super.getPlaceState(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
		if (facing == EnumFacing.UP) {
			facing = EnumFacing.DOWN;
		}
		state = state.withProperty(DCStateBase.SIDE, EnumSide.fromFacing(facing.getOpposite()));
		return state;
	}

	@Override
	public int damageDropped(IBlockState state) {
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

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileFaucet_SUS();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
		tooltip.add(TextFormatting.YELLOW.toString() + TextFormatting.BOLD.toString() + "=== Output ===");
		tooltip.add("WATER: 1000 mB/5t");
		tooltip.add(TextFormatting.YELLOW.toString() + TextFormatting.BOLD.toString() + "=== Tips ===");
		tooltip.add("Right-Click: Turn off this device.");
	}
}
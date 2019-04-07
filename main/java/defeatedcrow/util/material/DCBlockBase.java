package defeatedcrow.util.material;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import defeatedcrow.util.core.DCUtilCore;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DCBlockBase extends Block {

	public DCBlockBase(Material m, String s) {
		super(m);
		this.setUnlocalizedName(s);
		this.setCreativeTab(DCUtilCore.util);
	}

	/**
	 * -> onRightClick
	 */
	@Deprecated
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		return onRightClick(world, pos, state, player, hand, side, hitX, hitY, hitZ);
	}

	public boolean onRightClick(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
	}

	/**
	 * -> getPlaceState
	 */
	@Deprecated
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		IBlockState state = this.getPlaceState(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
		return state;
	}

	public IBlockState getPlaceState(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer, EnumHand hand) {
		IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
		return state;
	}

	/**
	 * -> onNeighborChange
	 */
	@Deprecated
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos from) {
		this.onNeighborChange(state, world, pos, block, from);
	}

	public void onNeighborChange(IBlockState state, World world, BlockPos pos, Block block, @Nullable BlockPos from) {}

	/**
	 * -> getSubItemList
	 */
	@Deprecated
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (machCreativeTab(tab, getCreativeTabToDisplayOn())) {
			List<ItemStack> itms = getSubItemList();
			list.addAll(itms);
		}
	}

	public static boolean machCreativeTab(CreativeTabs target, CreativeTabs tab) {
		return tab != null && (target == CreativeTabs.SEARCH || target == tab);
	}

	public List<ItemStack> getSubItemList() {
		List<ItemStack> list = Lists.newArrayList();
		return list;
	}

	/**
	 * -> getCollisionBoxList
	 */
	@Deprecated
	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean b) {
		this.getCollisionBoxList(state, world, pos, entityBox, collidingBoxes, entity, b);
	}

	public void getCollisionBoxList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean b) {
		super.addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getCollisionBoundingBox(world, pos));
	}

	/**
	 * -> getCollisionBox
	 */
	@Deprecated
	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return this.getCollisionBox(blockState, worldIn, pos);
	}

	public AxisAlignedBB getCollisionBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return blockState.getBoundingBox(worldIn, pos);
	}

	/** solid side */
	public boolean isSolidFace(IBlockState state, BlockPos pos, EnumFacing face) {
		return state.getMaterial().isSolid();
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
		return isSolidFace(state, pos, face) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(world, pos, state, rand);
	}

}

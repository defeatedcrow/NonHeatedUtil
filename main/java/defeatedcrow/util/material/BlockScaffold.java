package defeatedcrow.util.material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockScaffold extends DCBlockBase {

	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.01D, 0.0D, 0.01D, 0.99D, 1.0D, 0.99D);

	public BlockScaffold(Material m, String s) {
		super(m, s);
		this.setTickRandomly(false);
		this.setHardness(0.1F);
		this.setResistance(15.0F);
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
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return face == EnumFacing.UP ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

	@Override
	public List<ItemStack> getSubItemList() {
		List<ItemStack> list = Lists.newArrayList();
		list.add(new ItemStack(this));
		return list;
	}

	// 設置・破壊処理
	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Override
	public int quantityDropped(Random random) {
		return 1;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(state.getBlock());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
		tooltip.add(TextFormatting.YELLOW.toString() + TextFormatting.BOLD.toString() + "=== Tips ===");
		tooltip.add(I18n.format("dcutil.tip.scaffold"));
	}

	/* 機能部分 */

	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		return true;
	}

	// 伸張
	@Override
	public boolean onRightClick(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player != null && !world.isRemote) {
			ItemStack held = player.getHeldItem(hand);
			if (!held.isEmpty() && held.getItem() instanceof ItemBlock) {

				ItemBlock tI = (ItemBlock) held.getItem();
				Block tB = tI.getBlock();

				if (tB == this || tB instanceof BlockContainer)
					return false;

				// 設置State
				IBlockState tS = tB.getStateForPlacement(world, pos, side, hitX, hitY, hitZ, held.getItemDamage(),
						player, hand);

				if (tS == null)
					return true;

				Set<BlockPos> set = new LinkedHashSet<>();
				set = getLumberTargetList(world, pos, this, 100);

				int count = 0;
				for (BlockPos p2 : set) {
					world.setBlockState(p2, tS);
					count++;
					if (count >= held.getCount())
						break;
				}

				if (count > 0) {
					held.shrink(count);
					ItemStack drop = new ItemStack(this, count);
					EntityItem dropE = new EntityItem(world, player.posX, player.posY + 0.5D, player.posZ, drop);
					world.spawnEntity(dropE);
				}

				return true;
			}
		}
		return true;
	}

	private double dist(Vec3i v1, Vec3i v2) {
		double d0 = (double) v1.getX() - (double) v2.getX();
		double d1 = (double) v1.getY() - (double) v2.getY();
		double d2 = (double) v1.getZ() - (double) v2.getZ();
		return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
	}

	/**
	 * ruby氏に感謝!
	 *
	 * @date 2020.02.04
	 * @author ruby
	 */
	public static Set<BlockPos> getLumberTargetList(World world, BlockPos pos, Block block, int limit) {
		List<BlockPos> nextTargets = new ArrayList<>();
		nextTargets.add(pos);
		Set<BlockPos> founds = new LinkedHashSet<>();
		do {
			nextTargets = nextTargets.stream().flatMap(target -> Arrays.stream(EnumFacing.values()).map(
					target::offset)).filter(fixedPos -> world.getBlockState(fixedPos).getBlock().equals(block)).limit(
							limit - founds.size()).filter(founds::add).collect(Collectors.toList());

		} while (founds.size() <= limit && !nextTargets.isEmpty());

		return founds;
	}
}

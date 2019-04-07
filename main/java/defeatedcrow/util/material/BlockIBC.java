package defeatedcrow.util.material;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import defeatedcrow.util.core.DCUtilInit;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockIBC extends DCTileBlock {

	public BlockIBC(String s) {
		super(Material.CLAY, s, 0);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileIBC();
	}

	@Override
	public boolean onRightClick(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player != null) {
			ItemStack heldItem = player.getHeldItem(hand);
			if (hand == EnumHand.MAIN_HAND) {

				TileEntity tile = world.getTileEntity(pos);
				if (!world.isRemote && tile instanceof TileIBC) {
					if (!heldItem.isEmpty()) {
						if (onActivateDCTank(tile, heldItem, world, state, side, player)) {
							world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.8F,
									2.0F);
						}
					} else {
						FluidStack f = ((TileIBC) tile).inputT.getFluid();
						if (f != null) {
							String name = f.getLocalizedName();
							int i = f.amount;
							String mes1 = "Stored Fluid: " + name + " " + i + "mB";
							player.sendMessage(new TextComponentString(mes1));
						}
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public List<ItemStack> getSubItemList() {
		List<ItemStack> list = Lists.newArrayList();
		list.add(new ItemStack(this, 1, 0));
		return list;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	public static void changeLitState(World world, BlockPos pos, boolean f) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() == DCUtilInit.IBC) {
			if (f) {
				world.setBlockState(pos, state.withProperty(DCStateBase.TYPE4, 1), 3);
			} else {
				world.setBlockState(pos, state.withProperty(DCStateBase.TYPE4, 0), 3);
			}
		}
	}

	public static boolean isLit(IBlockAccess world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() != DCUtilInit.IBC)
			return false;
		int meta = state.getBlock().getMetaFromState(state) & 3;
		return meta > 0;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		int meta = this.getMetaFromState(state) & 3;
		return meta == 1 ? 15 : 0;
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
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		return calcRedstone(worldIn.getTileEntity(pos));
	}

	private int calcRedstone(TileEntity te) {
		if (te != null && te instanceof TileIBC) {
			TileIBC ibc = (TileIBC) te;
			DCTank tank = ibc.inputT;
			float amo = tank.getFluidAmount() * 15.0F / tank.getCapacity();
			int lit = MathHelper.floor(amo);
			return lit;
		}
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		IFluidHandlerItem cont = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
		if (cont != null && cont.getTankProperties() != null && cont.getTankProperties().length > 0) {
			FluidStack f = cont.getTankProperties()[0].getContents();
			if (f != null && f.getFluid() != null) {
				tooltip.add(TextFormatting.BOLD.toString() + "CONTAINED FLUID");
				tooltip.add("Fluid: " + f.getLocalizedName());
				tooltip.add("Amount: " + f.amount + " mB");
			} else {
				tooltip.add(TextFormatting.BOLD.toString() + "CONTAINED FLUID");
				tooltip.add("Fluid: Empty");
				tooltip.add("Amount: 0 mB");
			}
		} else {
			tooltip.add(TextFormatting.BOLD.toString() + "CONTAINED FLUID");
			tooltip.add("Fluid: Empty");
			tooltip.add("Amount: 0 mB");
		}
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	public static boolean onActivateDCTank(TileEntity tile, ItemStack item, World world, IBlockState state,
			EnumFacing side, EntityPlayer player) {
		if (!item.isEmpty() && tile != null && item.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,
				side) && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)) {
			ItemStack copy = item.copy();
			if (item.getCount() > 1)
				copy.setCount(1);
			IFluidHandlerItem dummy = copy.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
			IFluidHandler intank = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);
			IFluidHandler outtank = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
					EnumFacing.DOWN);

			// dummyを使った検証
			if (dummy != null && dummy.getTankProperties() != null && intank instanceof DCTank && outtank instanceof DCTank) {
				int max = dummy.getTankProperties()[0].getCapacity();
				FluidStack f1 = dummy.drain(max, false);
				DCTank dc_in = (DCTank) intank;
				DCTank dc_out = (DCTank) outtank;

				ItemStack ret = ItemStack.EMPTY;
				boolean success = false;
				// input
				if (f1 != null && dc_in.fill(f1, false) > 0) {
					int f2 = dc_in.fill(f1, false);
					FluidStack fill = dummy.drain(f2, true);
					ret = dummy.getContainer();
					if (fill != null && fill.amount > 0) {
						dc_in.fill(fill, true);
						success = true;
					}
				}
				// output
				else if (f1 == null && dc_out.drain(max, false) != null) {
					int drain = dummy.fill(dc_out.drain(max, false), true);
					ret = dummy.getContainer();
					if (drain > 0) {
						dc_out.drain(drain, true);
						success = true;
					}
				}

				if (success) {
					if (!player.capabilities.isCreativeMode) {
						item.shrink(1);
					}
					tile.markDirty();
					player.inventory.markDirty();
					if (!ret.isEmpty()) {
						EntityItem drop = new EntityItem(world, player.posX, player.posY + 0.25D, player.posZ, ret);
						world.spawnEntity(drop);
					}
					return true;
				}
			}
		}
		return false;
	}

}

package defeatedcrow.util.material;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMonitor extends DCItemBlockBase {

	public ItemMonitor(Block block) {
		super(block);
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {
		if (player != null && player.isSneaking()) {
			ItemStack held = player.getHeldItem(hand);
			IBlockState state = world.getBlockState(pos);
			if (!world.isRemote && !held.isEmpty() && held.getItem() instanceof ItemMonitor) {
				String mes1 = "Stored coordinate: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ", " + facing;
				player.sendMessage(new TextComponentString(mes1));

				NBTTagCompound tag = held.getTagCompound();
				if (tag == null) {
					tag = new NBTTagCompound();
				}

				tag.setInteger("card.dim", world.provider.getDimension());
				tag.setInteger("card.X", pos.getX());
				tag.setInteger("card.Y", pos.getY());
				tag.setInteger("card.Z", pos.getZ());
				tag.setInteger("card.facing", facing.getIndex());

				held.setTagCompound(tag);

			}
			return EnumActionResult.SUCCESS;
		}
		return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		tooltip.add(TextFormatting.BOLD.toString() + "Tier 3");
		if (!stack.isEmpty() && stack.hasTagCompound()) {
			int dim = getDim(stack);
			BlockPos pos = getPos(stack);
			EnumFacing face = getFacing(stack);
			if (pos != null && face != null) {
				tooltip.add(TextFormatting.BOLD.toString() + "=== Stored Data ===");
				tooltip.add("Dim: " + dim);
				tooltip.add("Pos: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
				tooltip.add("Side: " + face);
			}
		}
		tooltip.add(TextFormatting.YELLOW.toString() + TextFormatting.BOLD.toString() + "=== Output ===");
		tooltip.add("Redstone Signal");
	}

	public BlockPos getPos(ItemStack item) {
		if (!item.isEmpty() && item.hasTagCompound()) {
			NBTTagCompound tag = item.getTagCompound();
			if (tag.hasKey("card.dim")) {
				int x = tag.getInteger("card.X");
				int y = tag.getInteger("card.Y");
				int z = tag.getInteger("card.Z");
				return new BlockPos(x, y, z);
			}
		}
		return null;
	}

	public int getDim(ItemStack item) {
		if (!item.isEmpty() && item.hasTagCompound()) {
			NBTTagCompound tag = item.getTagCompound();
			if (tag.hasKey("card.dim"))
				return tag.getInteger("card.dim");
		}
		return 0;
	}

	public EnumFacing getFacing(ItemStack item) {
		if (!item.isEmpty() && item.hasTagCompound()) {
			NBTTagCompound tag = item.getTagCompound();
			if (tag.hasKey("card.dim")) {
				int id = tag.getInteger("card.facing");
				if (id >= 0 && id < 6)
					return EnumFacing.getFront(id);
			}
		}
		return null;
	}

}

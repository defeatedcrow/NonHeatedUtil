package defeatedcrow.util.material;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFlowerPot extends DCItemBase {

	public ItemFlowerPot() {
		super();
	}

	@Override
	public int getMaxMeta() {
		return 0;
	}

	@Override
	public String[] getNameSuffix() {
		String[] s = {
				"brown"
		};
		return s;
	}

	public Entity getPlacementEntity(World world, EntityPlayer player, double x, double y, double z, ItemStack item) {
		int i = item.getMetadata();
		EntityFlowerPot ret = new EntityFlowerPot(world, x, y, z, player);
		return ret;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation2(ItemStack stack, @Nullable World world, List<String> tooltip) {
		tooltip.add("Placeable as entity");
	}

	/* 設置動作 */
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if (player != null && player.isSneaking()) {
			if (!world.isRemote && pos.getY() > 0 && pos.getY() < 255 && player.canPlayerEdit(pos, facing, stack)) {
				IBlockState state = world.getBlockState(pos);
				Block block = state.getBlock();
				if (block != Blocks.TALLGRASS && block != Blocks.VINE && block != Blocks.DEADBUSH) {
					if (canSpawnHere(world, pos)) {
						double fX = facing.getFrontOffsetX() * 0.25D;
						double fY = facing.getFrontOffsetY() * 0.25D;
						double fZ = facing.getFrontOffsetZ() * 0.25D;
						Entity entity = this.getPlacementEntity(world, player, pos.getX() + hitX + fX,
								pos.getY() + hitY + fY, pos.getZ() + hitZ + fZ, stack);
						if (entity != null) {
							if (this.spawnPlacementEntity(world, entity)) {
								if (player instanceof EntityPlayerMP) {
									CriteriaTriggers.SUMMONED_ENTITY.trigger((EntityPlayerMP) player, entity);
								}
								stack.shrink(1);
								return EnumActionResult.SUCCESS;
							}
						}
					}
				}
			}
			return EnumActionResult.SUCCESS;
		} else {
			return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
		}
	}

	public boolean canSpawnHere(World world, BlockPos pos) {
		return true;
	}

	public boolean spawnPlacementEntity(World world, Entity entity) {
		if (entity != null) {
			return world.spawnEntity(entity);
		}
		return false;
	}

}

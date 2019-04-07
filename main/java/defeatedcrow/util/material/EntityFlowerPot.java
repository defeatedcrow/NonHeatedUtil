package defeatedcrow.util.material;

import javax.annotation.Nullable;

import defeatedcrow.util.core.DCUtilInit;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class EntityFlowerPot extends DCEntityBase {

	private static final DataParameter<ItemStack> FLOWER = EntityDataManager.<ItemStack>createKey(EntityFlowerPot.class,
			DataSerializers.ITEM_STACK);

	public EntityFlowerPot(World worldIn) {
		super(worldIn);
	}

	public EntityFlowerPot(World worldIn, double posX, double posY, double posZ) {
		super(worldIn, posX, posY, posZ);
	}

	public EntityFlowerPot(World worldIn, double posX, double posY, double posZ, @Nullable EntityPlayer player) {
		super(worldIn, posX, posY, posZ, player);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(FLOWER, ItemStack.EMPTY);
	}

	@Override
	protected ItemStack drops() {
		return new ItemStack(DCUtilInit.flowerPot, 1, 0);
	}

	public void setFlower(ItemStack item) {
		if (!item.isEmpty()) {
			this.dataManager.set(FLOWER, item);
		}
	}

	public ItemStack getFlower() {
		ItemStack item = (this.dataManager.get(FLOWER));
		return item;
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if (player != null && player.isSneaking()) {
			ItemStack hold = player.getHeldItem(hand);
			ItemStack flower = this.dataManager.get(FLOWER);
			if (isFlower(hold)) {
				setFlower(hold);
				this.playSound(SoundEvents.BLOCK_GRASS_PLACE, 1.0F, 1.0F);
				return true;
			}
		}
		return super.processInitialInteract(player, hand);
	}

	public boolean isFlower(ItemStack item) {
		if (!item.isEmpty()) {
			if (item.getItem() instanceof ItemBlock) {
				Block b = ((ItemBlock) item.getItem()).getBlock();
				int i = item.getItemDamage();
				if (b instanceof IPlantable || b.getStateFromMeta(i).getMaterial() == Material.PLANTS)
					return true;
			}
		}
		return false;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		ItemStack itemstack = this.dataManager.get(FLOWER);

		if (!itemstack.isEmpty()) {
			compound.setTag("FlowerItem", itemstack.writeToNBT(new NBTTagCompound()));
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		NBTTagCompound nbttagcompound = compound.getCompoundTag("FlowerItem");

		if (nbttagcompound != null) {
			ItemStack itemstack = new ItemStack(nbttagcompound);

			if (!itemstack.isEmpty()) {
				this.dataManager.set(FLOWER, itemstack);
			}
		}
	}

}

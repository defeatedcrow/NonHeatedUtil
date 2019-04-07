package defeatedcrow.util.material;

import net.minecraft.nbt.NBTTagCompound;

public interface ITagGetter {

	public NBTTagCompound getNBT(NBTTagCompound tag);

	public void setNBT(NBTTagCompound tag);

}

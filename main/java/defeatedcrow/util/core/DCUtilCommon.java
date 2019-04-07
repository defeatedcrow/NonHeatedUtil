package defeatedcrow.util.core;

import defeatedcrow.util.inventory.ContainerHopperFilter;
import defeatedcrow.util.inventory.ContainerHopperFluid;
import defeatedcrow.util.inventory.GuiHopperFilter;
import defeatedcrow.util.inventory.GuiHopperFluid;
import defeatedcrow.util.material.BlockFaucet;
import defeatedcrow.util.material.BlockFaucet_SUS;
import defeatedcrow.util.material.BlockHopperFilter;
import defeatedcrow.util.material.BlockHopperFluid;
import defeatedcrow.util.material.BlockIBC;
import defeatedcrow.util.material.BlockMClock;
import defeatedcrow.util.material.BlockMonitorComparator;
import defeatedcrow.util.material.BlockMonitorRedStone;
import defeatedcrow.util.material.BlockRClock;
import defeatedcrow.util.material.BlockSteelLadder;
import defeatedcrow.util.material.DCItemBlockBase;
import defeatedcrow.util.material.EntityBigCushion;
import defeatedcrow.util.material.EntityFlowerPot;
import defeatedcrow.util.material.ItemCushionGray;
import defeatedcrow.util.material.ItemFlowerPot;
import defeatedcrow.util.material.ItemGearDC;
import defeatedcrow.util.material.ItemIBC;
import defeatedcrow.util.material.ItemMonitor;
import defeatedcrow.util.material.TileFaucet;
import defeatedcrow.util.material.TileFaucet_SUS;
import defeatedcrow.util.material.TileHopperFilter;
import defeatedcrow.util.material.TileHopperFluid;
import defeatedcrow.util.material.TileIBC;
import defeatedcrow.util.material.TileMCClock_L;
import defeatedcrow.util.material.TileMonitorComparator;
import defeatedcrow.util.material.TileMonitorRedStone;
import defeatedcrow.util.material.TileRealtimeClock_L;
import defeatedcrow.util.packet.DCUtilPacket;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DCUtilCommon implements IGuiHandler {

	public void loadMaterial() {
		DCUtilInit.steelGear = new ItemGearDC().setUnlocalizedName("dcutil_steel");
		registerItem(DCUtilInit.steelGear, "dcutil_steel");

		DCUtilInit.flowerPot = new ItemFlowerPot().setUnlocalizedName("dcutil_flowerpot");
		registerItem(DCUtilInit.flowerPot, "dcutil_flowerpot");

		DCUtilInit.cushionGray = new ItemCushionGray().setUnlocalizedName("dcutil_cushion");
		registerItem(DCUtilInit.cushionGray, "dcutil_cushion");

		DCUtilInit.fenceLadderSteel = new BlockSteelLadder("dcutil_ladder_steel").setUnlocalizedName(
				"dcutil_ladder_steel");
		registerBlock(DCUtilInit.fenceLadderSteel, "dcutil_ladder_steel");

		DCUtilInit.realtimeClock_L = new BlockRClock(Material.GROUND, "dcutil_realtimeclock_l");
		registerBlock(DCUtilInit.realtimeClock_L, "dcutil_realtimeclock_l");

		DCUtilInit.mcClock_L = new BlockMClock(Material.GROUND, "dcutil_mcclock_l");
		registerBlock(DCUtilInit.mcClock_L, "dcutil_mcclock_l");

		DCUtilInit.faucet = new BlockFaucet("dcutil_faucet");
		registerBlock(DCUtilInit.faucet, "dcutil_faucet");

		DCUtilInit.faucet_adv = new BlockFaucet_SUS("dcutil_faucet_adv");
		registerBlock(DCUtilInit.faucet_adv, "dcutil_faucet_adv");

		DCUtilInit.IBC = new BlockIBC("dcutil_ibc");
		DCUtilInit.IBC.setRegistryName(DCUtilCore.MOD_ID, "dcutil_ibc");
		ForgeRegistries.BLOCKS.register(DCUtilInit.IBC);
		ForgeRegistries.ITEMS.register(new ItemIBC(DCUtilInit.IBC));

		DCUtilInit.hopperFilter = new BlockHopperFilter("dcutil_hopper_filter");
		registerBlock(DCUtilInit.hopperFilter, "dcutil_hopper_filter");

		DCUtilInit.hopperFluid = new BlockHopperFluid("dcutil_hopper_fluid");
		registerBlock(DCUtilInit.hopperFluid, "dcutil_hopper_fluid");

		DCUtilInit.monitorRS = new BlockMonitorRedStone("dcutil_monitor_rs");
		DCUtilInit.monitorRS.setRegistryName(DCUtilCore.MOD_ID, "dcutil_monitor_rs");
		ForgeRegistries.BLOCKS.register(DCUtilInit.monitorRS);
		ForgeRegistries.ITEMS.register(new ItemMonitor(DCUtilInit.monitorRS));

		DCUtilInit.monitorCM = new BlockMonitorComparator("dcutil_monitor_cm");
		DCUtilInit.monitorCM.setRegistryName(DCUtilCore.MOD_ID, "dcutil_monitor_cm");
		ForgeRegistries.BLOCKS.register(DCUtilInit.monitorCM);
		ForgeRegistries.ITEMS.register(new ItemMonitor(DCUtilInit.monitorCM));

	}

	public void loadEntity() {
		addEntity(EntityBigCushion.class, "cushion", 0);
		addEntity(EntityFlowerPot.class, "flower_pot", 1);
	}

	public void loadInit() {
		DCUtilPacket.init();
		DCUtilRecipe.addOreDic();
		if (ConfigDCUtil.enableSteel) {
			DCUtilRecipe.addAnother();
		}
	}

	public void loadTE() {
		GameRegistry.registerTileEntity(TileRealtimeClock_L.class, new ResourceLocation("dcs_util",
				"dcutil_te_realtime_clock"));
		GameRegistry.registerTileEntity(TileMCClock_L.class, new ResourceLocation("dcs_util", "dcutil_te_mc_clock"));
		GameRegistry.registerTileEntity(TileFaucet.class, new ResourceLocation("dcs_util", "dcutil_te_faucet"));
		GameRegistry.registerTileEntity(TileFaucet_SUS.class, new ResourceLocation("dcs_util", "dcutil_te_faucet_sus"));
		GameRegistry.registerTileEntity(TileIBC.class, new ResourceLocation("dcs_util", "dcutil_te_ibc"));
		GameRegistry.registerTileEntity(TileHopperFilter.class, new ResourceLocation("dcs_util",
				"dcutil_te_hopper_filter"));
		GameRegistry.registerTileEntity(TileHopperFluid.class, new ResourceLocation("dcs_util",
				"dcutil_te_hopper_fluid"));
		GameRegistry.registerTileEntity(TileMonitorRedStone.class, new ResourceLocation("dcs_util",
				"dcutil_te_monitor_rs"));
		GameRegistry.registerTileEntity(TileMonitorComparator.class, new ResourceLocation("dcs_util",
				"dcutil_te_monitor_cm"));
	}

	public static void registerBlock(Block block, String name) {
		if (block == null)
			return;
		Block reg = block.setRegistryName(DCUtilCore.MOD_ID, name);
		ForgeRegistries.BLOCKS.register(reg);
		ForgeRegistries.ITEMS.register(new DCItemBlockBase(reg));
	}

	public static void registerItem(Item item, String name) {
		if (item == null)
			return;
		ForgeRegistries.ITEMS.register(item.setRegistryName(DCUtilCore.MOD_ID, name));
	}

	public static void addEntity(Class<? extends Entity> regClass, String name, int f) {
		String regName = "dcutil." + name;
		EntityRegistry.registerModEntity(new ResourceLocation(DCUtilCore.MOD_ID, regName), regClass, regName, f,
				DCUtilCore.instance, 128, 5, true);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		if (!world.isBlockLoaded(pos))
			return null;
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileHopperFilter)
			return new ContainerHopperFilter((TileHopperFilter) tile, player);
		if (tile instanceof TileHopperFluid)
			return new ContainerHopperFluid((TileHopperFluid) tile, player);
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		if (!world.isBlockLoaded(pos))
			return null;
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileHopperFilter)
			return new GuiHopperFilter((TileHopperFilter) tile, player);
		if (tile instanceof TileHopperFluid)
			return new GuiHopperFluid((TileHopperFluid) tile, player);
		return null;
	}

}

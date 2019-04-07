package defeatedcrow.util.core;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import defeatedcrow.util.packet.FluidIDRegisterDC;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = DCUtilCore.MOD_ID, name = DCUtilCore.MOD_NAME,
		version = DCUtilCore.MOD_MEJOR + "." + DCUtilCore.MOD_MINOR + "." + DCUtilCore.MOD_BUILD,
		dependencies = DCUtilCore.MOD_DEPENDENCIES, acceptedMinecraftVersions = DCUtilCore.MOD_ACCEPTED_MC_VERSIONS,
		useMetadata = true)
public class DCUtilCore {
	public static final String MOD_ID = "dcs_util";
	public static final String MOD_NAME = "NonHeatedUtil";
	public static final int MOD_MEJOR = 1;
	public static final int MOD_MINOR = 0;
	public static final int MOD_BUILD = 0;
	public static final String MOD_DEPENDENCIES = "";
	public static final String MOD_ACCEPTED_MC_VERSIONS = "[1.12,1.12.2]";
	public static final String PACKAGE_BASE = "dcutil";
	public static final String PACKAGE_ID = "dcs_util";

	@SidedProxy(clientSide = "defeatedcrow.util.client.DCUtilClient",
			serverSide = "defeatedcrow.util.core.DCUtilCommon")
	public static DCUtilCommon proxy;

	@Instance("dcs_util")
	public static DCUtilCore instance;

	public static final Logger LOGGER = LogManager.getLogger(PACKAGE_ID);

	public static final CreativeTabs util = new CreativeTabUtil(MOD_ID);

	public static boolean isDebug = false;
	public static boolean serverStarted = false;
	public static boolean loadedMain = false;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		File dir = new File(event.getModConfigurationDirectory(), "defeatedcrow/nonheated_util/core.cfg");
		ConfigDCUtil.INSTANCE.load(new Configuration(dir));
		File configDir = new File(event.getModConfigurationDirectory(), "defeatedcrow/nonheated_util/");
		FluidIDRegisterDC.setDir(configDir);
		FluidIDRegisterDC.pre();

		proxy.loadMaterial();
		proxy.loadEntity();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.loadInit();
		proxy.loadTE();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		FluidIDRegisterDC.post();
	}
}

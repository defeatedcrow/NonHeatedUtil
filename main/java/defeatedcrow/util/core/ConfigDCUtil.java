package defeatedcrow.util.core;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigDCUtil {

	private ConfigDCUtil() {}

	public static final ConfigDCUtil INSTANCE = new ConfigDCUtil();

	private final String BR = System.getProperty("line.separator");

	public static boolean enableSteel = true;

	public void load(Configuration cfg) {

		try {
			cfg.load();

			cfg.addCustomCategoryComment("setting", "This setting is for game play.");

			Property steel = cfg
					.get("setting", "Enable Steel Smelting Recipe", enableSteel, "If you don't need new steel, turn this false.");

			enableSteel = steel.getBoolean();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cfg.save();
		}

	}
}

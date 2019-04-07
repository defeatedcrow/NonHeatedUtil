package defeatedcrow.util.material;

public class ItemGearDC extends DCItemBase {

	private final int maxMeta;

	private static String[] names = {
			"gear",
			"ingot"
	};

	public ItemGearDC() {
		super();
		maxMeta = 1;
	}

	@Override
	public int getMaxMeta() {
		return maxMeta;
	}

	@Override
	public String[] getNameSuffix() {
		return names;
	}

}

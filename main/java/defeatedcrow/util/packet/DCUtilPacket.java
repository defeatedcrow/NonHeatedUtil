package defeatedcrow.util.packet;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class DCUtilPacket {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("dcs_util");

	public static void init() {
		INSTANCE.registerMessage(MHandlerFTank.class, MessageFTank.class, 0, Side.CLIENT);
		INSTANCE.registerMessage(MHandlerMonitor.class, MessageMonitor.class, 1, Side.CLIENT);
	}
}

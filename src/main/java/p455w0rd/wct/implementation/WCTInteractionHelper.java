package p455w0rd.wct.implementation;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import p455w0rd.wct.api.IWCTInteractionHelper;
import p455w0rd.wct.api.IWirelessCraftingTermHandler;
import p455w0rd.wct.handlers.GuiHandler;
import p455w0rd.wct.init.ModItems;
import p455w0rd.wct.items.ItemWCT;
import p455w0rd.wct.sync.network.NetworkHandler;
import p455w0rd.wct.sync.packets.PacketOpenGui;
import p455w0rd.wct.util.WCTUtils;

/**
 * @author p455w0rd
 *
 */
public class WCTInteractionHelper implements IWCTInteractionHelper {

	@Override
	public void openWirelessCraftingTerminalGui(final EntityPlayer player) {
		if ((player == null) || (player instanceof FakePlayer) || (player instanceof EntityPlayerMP) || FMLCommonHandler.instance().getSide() == Side.SERVER) {
			return;
		}

		ItemStack is = WCTUtils.getWirelessTerm(player.inventory);
		if (is == null) {
			return;
		}
		if (is.hasTagCompound()) {
			//String sourceKey = is.getTagCompound().getString(ItemWCT.LINK_KEY_STRING);
			//if ((sourceKey != null) && (!sourceKey.isEmpty())) {
			if (isTerminalLinked(ModItems.WCT, is)) {
				NetworkHandler.instance().sendToServer(new PacketOpenGui(GuiHandler.GUI_WCT));
			}
		}

	}

	public static boolean isTerminalLinked(final IWirelessCraftingTermHandler wirelessTerminal, final ItemStack wirelessTerminalItemstack) {
		return (!wirelessTerminal.getEncryptionKey(wirelessTerminalItemstack).isEmpty());
	}

}
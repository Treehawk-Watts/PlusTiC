package landmaster.plustic.gui.container;

import landmaster.plustic.traits.*;
import net.minecraft.client.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.items.*;
import slimeknights.tconstruct.library.utils.*;

public class ContainerMOTS extends Container {
	@CapabilityInject(MusicOfTheSpheres.IMOTSItemHandler.class)
	private static Capability<MusicOfTheSpheres.IMOTSItemHandler> MOTS_ITEM_CAP = null;
	
	public ContainerMOTS(EntityPlayer player) {
		if (player.getHeldItemMainhand().hasCapability(MOTS_ITEM_CAP, null)) {
			MusicOfTheSpheres.IMOTSItemHandler cap = player.getHeldItemMainhand().getCapability(MOTS_ITEM_CAP, null);
			addSlotToContainer(new SlotItemHandler(cap, 0, 80, 35) {
				@Override
				public void onSlotChanged() {
					super.onSlotChanged();
					if (!player.world.isRemote) {
						ItemStack stack = cap.getStackInSlot(0);
						if (stack.isEmpty()
								|| !(stack.getItem() instanceof ItemRecord)) {
							cap.stop(player);
						} else {
							cap.play(player, ((ItemRecord)stack.getItem()).getSound());
						}
					}
				}
			});
		}
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
	
		for (int k = 0; k < 9; k++) {
			addSlotToContainer(new Slot(player.inventory, k, 8 + k * 18, 142));
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return TinkerUtil.hasTrait(TagUtil.getTagSafe(Minecraft.getMinecraft().player.getHeldItemMainhand()),
				MusicOfTheSpheres.musicofthespheres.identifier);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);
		
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			
			int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size();
			
			if (index < containerSlots) {
				if (!this.mergeItemStack(itemstack1, containerSlots, inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, containerSlots, false)) {
				return ItemStack.EMPTY;
			}
			
			if (itemstack1.getCount() == 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
			
			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			
			slot.onTake(player, itemstack1);
		}
		
		return itemstack;
	}
}

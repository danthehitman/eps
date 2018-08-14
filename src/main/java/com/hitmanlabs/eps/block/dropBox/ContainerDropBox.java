package com.hitmanlabs.eps.block.dropBox;

import com.hitmanlabs.eps.block.ShippablePackage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDropBox extends Container {

    // Stores the tile entity instance for later use
    private TileDropBox tile;

    // These store cache values, used by the server to only update the client side tile entity when values have changed
    private int [] cachedFields;

    // must assign a slot index to each of the slots used by the GUI.
    // For this container, we can see the furnace fuel, input, and output slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container using addSlotToContainer(), it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 = package slot (tileEntity 0)
    //  37 = payment slot (tileEntity 1)
    //  38 = receipt slot (tileEntity 2)

    private final int PACKAGE_STACK_LIMIT = 1;
    private final int PAYMENT_STACK_LIMIT = 64;
    private final int RECEIPT_STACK_LIMIT = 1;

    private final int HOTBAR_SLOT_COUNT = 9;
    private final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;

    public final int PACKAGE_SLOT_COUNT = 1;
    public final int PAYMENT_SLOT_COUNT = 1;
    public final int RECIEPT_SLOT_COUNT = 1;

    public final int DROP_BOX_SLOT_COUNT = PACKAGE_SLOT_COUNT + PAYMENT_SLOT_COUNT + RECIEPT_SLOT_COUNT;

    // slot index is the unique index for all slots in this container i.e. 0 - 35 for invPlayer then 36 - 8 for tileDropBox
    private final int VANILLA_FIRST_SLOT_INDEX = 0;
    private final int FIRST_PACKAGE_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private final int FIRST_PAYMENT_SLOT_INDEX = FIRST_PACKAGE_SLOT_INDEX + PACKAGE_SLOT_COUNT;
    private final int FIRST_RECEIPT_SLOT_INDEX = FIRST_PAYMENT_SLOT_INDEX + PAYMENT_SLOT_COUNT;

    // slot number is the slot number within each component; i.e. invPlayer slots 0 - 35, and tileDropBox slots 0 - 2
    private final int FIRST_PACKAGE_SLOT_NUMBER = 0;
    private final int FIRST_PAYMENT_SLOT_NUMBER = FIRST_PACKAGE_SLOT_NUMBER + PACKAGE_SLOT_COUNT;
    private final int FIRST_RECEIPT_SLOT_NUMBER = FIRST_PAYMENT_SLOT_NUMBER + PAYMENT_SLOT_COUNT;

    public ContainerDropBox(InventoryPlayer invPlayer, TileDropBox tileDropBox) {
        this.tile = tileDropBox;

        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 183;
        // Add the players hotbar to the gui - the [xpos, ypos] location of each item
        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) {
            int slotNumber = x;
            addSlotToContainer(new Slot(invPlayer, slotNumber, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));
        }

        final int PLAYER_INVENTORY_XPOS = 8;
        final int PLAYER_INVENTORY_YPOS = 125;
        // Add the rest of the players inventory to the gui
        for (int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++) {
            for (int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++) {
                int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
                int xpos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
                addSlotToContainer(new Slot(invPlayer, slotNumber,  xpos, ypos));
            }
        }

        final int PACKAGE_SLOTS_X_POS = 53;
        final int PACKAGE_SLOTS_YPOS = 96;
        // Add the tile fuel slots
        for (int x = 0; x < PACKAGE_SLOT_COUNT; x++) {
            int slotNumber = x + FIRST_PACKAGE_SLOT_NUMBER;
            addSlotToContainer(new SlotPackage(tileDropBox, slotNumber, PACKAGE_SLOTS_X_POS + SLOT_X_SPACING * x, PACKAGE_SLOTS_YPOS));
        }

        final int INPUT_SLOTS_XPOS = 26;
        final int INPUT_SLOTS_YPOS = 24;
        // Add the tile input slots
        for (int y = 0; y < PAYMENT_SLOT_COUNT; y++) {
            int slotNumber = y + FIRST_PAYMENT_SLOT_NUMBER;
            addSlotToContainer(new SlotPaymentInput(tileDropBox, slotNumber, INPUT_SLOTS_XPOS, INPUT_SLOTS_YPOS+ SLOT_Y_SPACING * y));
        }

        final int OUTPUT_SLOTS_XPOS = 134;
        final int OUTPUT_SLOTS_YPOS = 24;
        // Add the tile output slots
        for (int y = 0; y < RECIEPT_SLOT_COUNT; y++) {
            int slotNumber = y + RECIEPT_SLOT_COUNT;
            addSlotToContainer(new SlotReceipt(tileDropBox, slotNumber, OUTPUT_SLOTS_XPOS, OUTPUT_SLOTS_YPOS + SLOT_Y_SPACING * y));
        }
    }


    // Checks each tick to make sure the player is still able to access the inventory and if not closes the gui
    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return tile.isUsableByPlayer(player);
    }

    // This is where you specify what happens when a player shift clicks a slot in the gui
    //  (when you shift click a slot in the TileEntity Inventory, it moves it to the first available position in the hotbar and/or
    //    player inventory.  When you you shift-click a hotbar or player inventory item, it moves it to the first available
    //    position in the TileEntity inventory - either input or fuel as appropriate for the item you clicked)
    // At the very least you must override this and return EMPTY_ITEM or the game will crash when the player shift clicks a slot
    // returns EMPTY_ITEM if the source slot is empty, or if none of the source slot items could be moved.
    //   otherwise, returns a copy of the source stack
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int sourceSlotIndex) {
        Slot sourceSlot = inventorySlots.get(sourceSlotIndex);
        if (sourceSlot == null || !sourceSlot.getHasStack()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getStack();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX && sourceSlotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so put one item into an open slot of the appropriate type.

            //If the stack is a ShippablePackage try and put it in a package slot.
            if (sourceStack.getItem() instanceof ShippablePackage) {
                insertItemsIntoSlots(sourceStack, FIRST_PACKAGE_SLOT_INDEX, FIRST_PAYMENT_SLOT_INDEX, 1);
            } else if (TileDropBox.isShippingPayment(sourceStack.getItem())) {
                insertItemsIntoSlots(sourceStack, FIRST_PACKAGE_SLOT_INDEX, FIRST_PAYMENT_SLOT_INDEX, 1);
            } else
                return sourceStack;
        } else if (sourceSlotIndex >= FIRST_PAYMENT_SLOT_INDEX && sourceSlotIndex < FIRST_PAYMENT_SLOT_INDEX + DROP_BOX_SLOT_COUNT) {
            // This is a drop box slot so merge the stack into the players inventory: try the hotbar first and then the main inventory
            //   because the main inventory slots are immediately after the hotbar slots, we can just merge with a single call
            if (!mergeItemStack(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  //EMPTY_ITEM;
            }
        } else {
            System.err.print("Invalid slotIndex:" + sourceSlotIndex);
            return ItemStack.EMPTY;  //EMPTY_ITEM;
        }

        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {  //getStackSize()
            sourceSlot.putStack(ItemStack.EMPTY);  // Empty Item
        } else {
            sourceSlot.onSlotChanged();
        }

        sourceSlot.onTake(player, sourceStack);  // onPickupFromSlot()
        return copyOfSourceStack;
    }

    /* Client Synchronization */

    // This is where you check if any values have changed and if so send an update to any clients accessing this container
    // The container itemstacks are tested in Container.detectAndSendChanges, so we don't need to do that
    // We iterate through all of the TileEntity Fields to find any which have changed, and send them.
    // You don't have to use fields if you don't wish to; just manually match the ID in sendWindowProperty with the value in
    //   updateProgressBar()
    // The progress bar values are restricted to shorts.  If you have a larger value (eg int), it's not a good idea to try and split it
    //   up into two shorts because the progress bar values are sent independently, and unless you add synchronisation logic at the
    //   receiving side, your int value will be wrong until the second short arrives.  Use a custom packet instead.
    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        boolean allFieldsHaveChanged = false;
        boolean fieldHasChanged [] = new boolean[tile.getFieldCount()];
        if (cachedFields == null) {
            cachedFields = new int[tile.getFieldCount()];
            allFieldsHaveChanged = true;
        }
        for (int i = 0; i < cachedFields.length; ++i) {
            if (allFieldsHaveChanged || cachedFields[i] != tile.getField(i)) {
                cachedFields[i] = tile.getField(i);
                fieldHasChanged[i] = true;
            }
        }

        // go through the list of listeners (players using this container) and update them if necessary
        for (IContainerListener listener : this.listeners) {
            for (int fieldID = 0; fieldID < tile.getFieldCount(); ++fieldID) {
                if (fieldHasChanged[fieldID]) {
                    // Note that although sendWindowProperty takes 2 ints on a server these are truncated to shorts
                    listener.sendWindowProperty(this, fieldID, cachedFields[fieldID]);
                }
            }
        }
    }

    private ItemStack insertItemsIntoSlots(ItemStack sourceStack, int start, int stop, int countToInsert) {
        for (int i = start; i < stop; i++) {
            Slot slot = this.inventorySlots.get(i);
            ItemStack itemstack = slot.getStack();
            if (itemstack.isEmpty()) {
                slot.putStack(sourceStack.splitStack(countToInsert));
                slot.onSlotChanged();
                break;
            } else
                continue;
        }
        return sourceStack;
    }

    public class SlotPackage extends Slot {
        public SlotPackage(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        // if this function returns false, the player won't be able to insert the given item into this slot
        @Override
        public boolean isItemValid(ItemStack stack) {
            return TileDropBox.isItemValidForPackageSlot(stack);
        }
    }

    public class SlotPaymentInput extends Slot {
        public SlotPaymentInput(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        // if this function returns false, the player won't be able to insert the given item into this slot
        @Override
        public boolean isItemValid(ItemStack stack) {
            return TileDropBox.isItemValidForPaymentSlot(stack);
        }
    }

    public class SlotReceipt extends Slot {
        public SlotReceipt(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        // if this function returns false, the player won't be able to insert the given item into this slot
        @Override
        public boolean isItemValid(ItemStack stack) {
            return TileDropBox.isItemValidForReceiptSlot(stack);
        }
    }
}

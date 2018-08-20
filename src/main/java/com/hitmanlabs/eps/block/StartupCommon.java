package com.hitmanlabs.eps.block;

import com.hitmanlabs.eps.Eps;
import com.hitmanlabs.eps.GuiHandlerRegistry;
import com.hitmanlabs.eps.block.dropBox.BlockDropBox;
import com.hitmanlabs.eps.block.dropBox.GuiHandlerDropBox;
import com.hitmanlabs.eps.block.dropBox.TileDropBox;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * User: The Grey Ghost
 * Date: 24/12/2014
 *
 * The Startup classes for this example are called during startup, in the following order:
 *  preInitCommon
 *  preInitClientOnly
 *  initCommon
 *  initClientOnly
 *  postInitCommon
 *  postInitClientOnly
 *  See MinecraftByExample class for more information
 */
public class StartupCommon
{
    public static BlockDropBox blockDropBox;  // this holds the unique instance of your block
    public static ItemBlock itemBlockDropBox;  // this holds the unique instance of the ItemBlock corresponding to your block

    public static void preInitCommon()
    {
        // each instance of your block should have two names:
        // 1) a registry name that is used to uniquely identify this block.  Should be unique within your mod.  use lower case.
        // 2) an 'unlocalised name' that is used to retrieve the text name of your block in the player's language.  For example-
        //    the unlocalised name might be "water", which is printed on the user's screen as "Wasser" in German or
        //    "aqua" in Italian.
        //
        //    Multiple block can have the same unlocalised name - for example
        //  +----RegistryName----+---UnlocalisedName----+
        //  |  flowing_water     +       water          |
        //  |  stationary_water  +       water          |
        //  +--------------------+----------------------+
        //
        blockDropBox = (BlockDropBox)(new BlockDropBox().setUnlocalizedName("eps_block_dropbox_unlocalised_name"));
        blockDropBox.setRegistryName("eps_block_dropbox_registry_name");
        ForgeRegistries.BLOCKS.register(blockDropBox);

        // We also need to create and register an ItemBlock for this block otherwise it won't appear in the inventory
        itemBlockDropBox = new ItemBlock(blockDropBox);
        itemBlockDropBox.setRegistryName(blockDropBox.getRegistryName());
        ForgeRegistries.ITEMS.register(itemBlockDropBox);

        // Each of your tile entities needs to be registered with a name that is unique to your mod.
        GameRegistry.registerTileEntity(TileDropBox.class, "eps_block_dropbox_tile_entity");

        // You need to register a GUIHandler for the container.  However there can be only one handler per mod, so for the purposes
        //   of this project, we create a single GuiHandlerRegistry for all examples.
        // We register this GuiHandlerRegistry with the NetworkRegistry, and then tell the GuiHandlerRegistry about
        //   each example's GuiHandler, in this case GuiHandlerMBE31, so that when it gets a request from NetworkRegistry,
        //   it passes the request on to the correct example's GuiHandler.
        NetworkRegistry.INSTANCE.registerGuiHandler(Eps.instance, GuiHandlerRegistry.getInstance());
        GuiHandlerRegistry.getInstance().registerGuiHandler(new GuiHandlerDropBox(), GuiHandlerDropBox.getGuiID());
    }

    public static void initCommon()
    {
    }

    public static void postInitCommon()
    {
    }

}

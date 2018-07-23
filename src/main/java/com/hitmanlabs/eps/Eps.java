package com.hitmanlabs.eps;

        import com.hitmanlabs.eps.handler.RegistrationHandler;
        import com.hitmanlabs.eps.proxy.CommonProxy;
        import net.minecraft.util.ResourceLocation;
        import net.minecraftforge.common.MinecraftForge;
        import net.minecraftforge.fml.common.Mod;
        import net.minecraftforge.fml.common.SidedProxy;
        import net.minecraftforge.fml.common.event.FMLInitializationEvent;
        import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
        import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
        import net.minecraftforge.fml.common.network.NetworkRegistry;
        import net.minecraftforge.fml.common.registry.EntityRegistry;
        import net.minecraftforge.fml.relauncher.Side;
        import org.apache.logging.log4j.Logger;

@Mod(modid = ModContants.MODID, name = ModContants.MOD_NAME, version = ModContants.VERSION)
public class Eps {

    @SidedProxy(clientSide = ModContants.CLIENT_PROXY_CLASS, serverSide = ModContants.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        /* Common Events */
        MinecraftForge.EVENT_BUS.register(new ConfigurationHandler());

        RegistrationHandler.init();

        /* Packet Handler Init */
        PacketHandler.init();

        /* Configuration Handler Init */
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());

        /* Custom triggers Init */
        Triggers.init();

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        if(event.getSide() == Side.CLIENT)
        {
            MinecraftForge.EVENT_BUS.register(new MirrorRenderer());
        }
        MinecraftForge.EVENT_BUS.register(new PlayerEvents());

        /* GUI Handler Registering */
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        /* TileEntity Registering */
        FurnitureTileEntities.register();

        /* Entity Registering */
        EntityRegistry.registerModEntity(new ResourceLocation("cfm:mountable_block"), EntitySittableBlock.class, "MountableBlock", 0, this, 80, 1, false);
        if(event.getSide() == Side.CLIENT)
        {
            EntityRegistry.registerModEntity(new ResourceLocation("cfm:mirror"), EntityMirror.class, "Mirror", 1, this, 80, 1, false);
        }

        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        /* Initialize API */
        RecipeRegistry.registerDefaultRecipes();
        RecipeRegistry.registerConfigRecipes();
        Recipes.addCommRecipesToLocal();
        Recipes.updateDataList();

        proxy.postInit(event);
    }


}

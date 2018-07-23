package com.hitmanlabs.eps.proxy;

import com.hitmanlabs.eps.ModContants;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = ModContants.MODID, value = Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance().getClient().world;
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public void preInit(@Nonnull FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(@Nonnull FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void postInit(@Nonnull FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @Override
    public long getTickCount() {
        return clientTickCount;
    }

    @Override
    protected void onClientTick() {
        if (!Minecraft.getMinecraft().isGamePaused()) {
            ++clientTickCount;
        }
    }

    @Override
    public boolean isDedicatedServer() {
        return false;
    }
}

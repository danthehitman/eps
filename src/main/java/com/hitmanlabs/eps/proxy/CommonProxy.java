package com.hitmanlabs.eps.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;

public class CommonProxy {

    protected long serverTickCount = 0;
    protected long clientTickCount = 0;
    protected final TickTimer tickTimer = new TickTimer();

    public CommonProxy() {
    }

    public World getClientWorld() {
        return null;
    }

    public EntityPlayer getClientPlayer() {
        return null;
    }

    public void preInit(@Nonnull FMLPreInitializationEvent event) {
    }

    public void init(@Nonnull FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(tickTimer);
    }

    public void postInit(@Nonnull FMLPostInitializationEvent event) {
    }

    public long getTickCount() {
        return serverTickCount;
    }

    protected void onServerTick() {
        ++serverTickCount;
    }

    protected void onClientTick() {
    }

    public boolean isDedicatedServer() {
        return true;
    }

    public final class TickTimer {

        @SubscribeEvent
        public void onTick(@Nonnull TickEvent.ServerTickEvent evt) {
            if (evt.phase == TickEvent.Phase.END) {
                onServerTick();
            }
        }

        @SubscribeEvent
        public void onTick(@Nonnull TickEvent.ClientTickEvent evt) {
            if (evt.phase == TickEvent.Phase.END) {
                onClientTick();
            }
        }
    }

}

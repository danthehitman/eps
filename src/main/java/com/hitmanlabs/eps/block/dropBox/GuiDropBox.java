package com.hitmanlabs.eps.block.dropBox;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiDropBox extends GuiContainer {

    // This is the resource location for the background image
    private static final ResourceLocation texture = new ResourceLocation("eps", "textures/gui/eps_dropbox_bg.png");
    private TileDropBox tileEntity;

    public GuiDropBox(InventoryPlayer invPlayer, TileDropBox tileDropBox) {
        super(new ContainerDropBox(invPlayer, tileDropBox));

        // Set the width and height of the gui
        xSize = 176;
        ySize = 207;

        this.tileEntity = tileDropBox;
    }

    // some [x,y] coordinates of graphical elements
    final int READY_ICON_XPOS = 54;
    final int READY_ICON_YPOS = 80;
    final int READY_ICON_U = 176;   // texture position of flame icon
    final int READY_ICON_V = 0;
    final int READY_WIDTH = 14;
    final int READY_HEIGHT = 14;

    final int SEND_BUTTON_XPOS = 49;
    final int SEND_BUTTON_YPOS = 60;
    final int SEND_BUTTON_U = 0;   // texture position of flame icon
    final int SEND_BUTTON_V = 207;
    final int SEND_WIDTH = 25;
    final int SEND_HEIGHT = 17;

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int x, int y) {
        // Bind the image texture
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        // Draw the image
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        if (tileEntity.isReadyToSend()) {
            drawTexturedModalRect(guiLeft + SEND_BUTTON_XPOS, guiTop + SEND_BUTTON_YPOS, SEND_BUTTON_U, SEND_BUTTON_V,
                    SEND_WIDTH, SEND_HEIGHT);

            drawTexturedModalRect(guiLeft + READY_ICON_XPOS, guiTop + READY_ICON_YPOS,
                    READY_ICON_U, READY_ICON_V, READY_WIDTH, READY_HEIGHT);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        final int LABEL_XPOS = 5;
        final int LABEL_YPOS = 5;
        fontRenderer.drawString(tileEntity.getDisplayName().getUnformattedText(), LABEL_XPOS, LABEL_YPOS, Color.darkGray.getRGB());

        List<String> hoveringText = new ArrayList<>();

        // If the mouse is over the progress bar add the progress bar hovering text
        if (isInRect(guiLeft + SEND_BUTTON_XPOS, guiTop + SEND_BUTTON_YPOS, SEND_WIDTH, SEND_HEIGHT, mouseX, mouseY)){
            hoveringText.add("This is the send button.  It should only be active when ready to send.");
        }
        // If hoveringText is not empty draw the hovering text
        if (!hoveringText.isEmpty()){
            drawHoveringText(hoveringText, mouseX - guiLeft, mouseY - guiTop, fontRenderer);
        }
//		// You must re bind the texture and reset the colour if you still need to use it after drawing a string
//		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
//		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    }

    // Returns true if the given x,y coordinates are within the given rectangle
    public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY){
        return ((mouseX >= x && mouseX <= x+xSize) && (mouseY >= y && mouseY <= y+ySize));
    }
}

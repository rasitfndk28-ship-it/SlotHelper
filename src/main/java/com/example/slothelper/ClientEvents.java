package com.example.slothelper;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SlotHelper.MOD_ID, value = Dist.CLIENT)
public final class ClientEvents {
    private static final int SLOT_1 = 0; // Hotbar'daki 1. Slot
    private static final int SLOT_2 = 1; // Hotbar'daki 2. Slot

    private ClientEvents() {
    }

    @SubscribeEvent
    public static void onMouseButton(InputEvent.MouseButton.Pre event) {
        // Sadece farenin tuşuna basıldığı an çalışır
        if (event.getAction() != 1) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        ItemStack mainHandItem = mc.player.getMainHandItem();
        int button = event.getButton();
        HitResult hit = mc.hitResult;

        // El durumlarını kontrol et
        boolean elBosMu = mainHandItem.isEmpty();
        boolean eldeBlokVarMi = !elBosMu && mainHandItem.getItem() instanceof BlockItem;
        boolean eldeAletVeyaKilicVarMi = isWeaponOrTool(mainHandItem);

        // 1. SOL TIK: (El Boş VEYA Elde Blok Varsa) ve Canlıya Vurursan -> 1. Slota Geç
        if (button == 0) {
            boolean canliyaVurulduMu = hit != null && hit.getType() == HitResult.Type.ENTITY;
            boolean solTikSartlariUygunMu = elBosMu || eldeBlokVarMi;

            if (solTikSartlariUygunMu && canliyaVurulduMu) {
                mc.player.getInventory().selected = SLOT_1;
            }
        } 
        
        // 2. SAĞ TIK: (Elde Kılıç/Alet Varsa VEYA El Boşsa) ve Bloğa Tıklarsan -> 2. Slota Geç
        else if (button == 1) {
            boolean blogaTiklandiMu = hit != null && hit.getType() == HitResult.Type.BLOCK;
            boolean sagTikSartlariUygunMu = elBosMu || eldeAletVeyaKilicVarMi;

            if (sagTikSartlariUygunMu && blogaTiklandiMu) {
                mc.player.getInventory().selected = SLOT_2;
            }
        }
    }

    // Eldeki eşyanın Kılıç, Kazma, Balta, Kürek veya Çapa olup olmadığını kontrol eder
    private static boolean isWeaponOrTool(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        return item instanceof SwordItem || item instanceof DiggerItem;
    }
}

package fr.kilian.basiceconomy.listeners.handlers;

import fr.kilian.basiceconomy.listeners.GuiListener;
import fr.kilian.basiceconomy.managers.PriceEditSession;
import fr.kilian.basiceconomy.model.ItemMarket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArticlesHandler {

    public static void handleAllArticles(InventoryClickEvent event) {
        event.setCancelled(true);

        Material type = GuiListener.getClickedType(event);
        if (type == null) return;

        if (type == Material.ARROW || type == Material.BOOK) {
            return;
        }

        ItemMarket itemMarket = ItemMarket.itemsMarket.get(type);
        if (itemMarket == null) return;

        Player player     = (Player) event.getWhoClicked();
        boolean  isLeft     = event.getClick().isLeftClick();
        ItemStack current = event.getCurrentItem();

        if (event.getClick() == ClickType.DROP) {
            if(GuiListener.getMarket().isDisable(itemMarket)) {
                GuiListener.getMarket().enableItem(itemMarket);
                player.closeInventory();
                GuiListener.getGui().open(player, GuiListener.getGui().allArticles(0));
            } else{
                GuiListener.getMarket().disableItem(itemMarket);
                current.editMeta(meta -> {
                    List<Component> lore = meta.lore();

                    if (lore == null) {
                        lore = new ArrayList<>();
                    }

                    lore.add(Component.empty());
                    lore.add(
                            Component.text("✘ Cet article est désactivé.")
                                    .color(NamedTextColor.RED)
                                    .decoration(TextDecoration.ITALIC, false)
                    );

                    meta.lore(lore); // IMPORTANT
                });
            }
        }else{
            PriceEditSession.EditType editType   = isLeft ? PriceEditSession.EditType.BUY : PriceEditSession.EditType.SELL;
            String   priceLabel = isLeft ? "achat" : "vente";

            PriceEditSession.put(player.getUniqueId(), itemMarket, editType);
            player.closeInventory();

            player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").color(NamedTextColor.GOLD));
            player.sendMessage(Component.text(" ✦ Modification de prix")
                    .color(NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true));
            player.sendMessage(Component.text(" Article : ").color(NamedTextColor.GRAY)
                    .append(Component.text(type.name()).color(NamedTextColor.WHITE)));
            player.sendMessage(Component.text(" Prix cible : ").color(NamedTextColor.GRAY)
                    .append(Component.text(priceLabel).color(NamedTextColor.YELLOW)));
            player.sendMessage(Component.text(" ▶ Tape le nouveau prix dans le chat.").color(NamedTextColor.AQUA));
            player.sendMessage(Component.text(" ▶ Tape ").color(NamedTextColor.AQUA)
                    .append(Component.text("annuler").color(NamedTextColor.RED))
                    .append(Component.text(" pour abandonner.").color(NamedTextColor.AQUA)));
            player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").color(NamedTextColor.GOLD));
        }
    }

}

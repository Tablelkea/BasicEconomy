package fr.kilian.basiceconomy.listeners;

import fr.kilian.basiceconomy.Main;
import fr.kilian.basiceconomy.managers.*;
import fr.kilian.basiceconomy.managers.PriceEditSession.EditType;
import fr.kilian.basiceconomy.managers.BuySession.*;
import fr.kilian.basiceconomy.model.EcoPlayer;
import fr.kilian.basiceconomy.model.ItemMarket;
import fr.kilian.basiceconomy.model.enums.Category;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GuiListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() == Material.AIR) return;
        if (event.getCurrentItem().getType() == Material.GRAY_STAINED_GLASS_PANE) {
            event.setCancelled(true);
            return;
        }

        String title = stripColor(event.getView().getTitle());

        if      (title.equals(GuiManager.ADMIN_PANEL_TITLE))        handleAdminPanel(event);
        else if (title.equals(GuiManager.SHOP_TITLE))               handleShop(event);
        else if (title.startsWith(GuiManager.ALL_ARTICLES_TITLE)) handleAllArticles(event);
        else if (title.startsWith(GuiManager.SHOP_CATEGORY_PREFIX)) handleShopCategory(event);
        else if (title.equals(GuiManager.ALL_PLAYERS_TITLE)) handleAllPlayers(event);
        else if (title.equals(GuiManager.ALL_CATEGORY_TITLE)) {
            event.setCancelled(true);
        }

        ItemStack current = event.getCurrentItem();
        if (title.startsWith(GuiManager.ALL_ARTICLES_TITLE)) {

            int currentPage = getGui().extractPage(title);

            if (current.getType() == Material.ARROW) {

                if(event.getSlot() == 53){
                    getGui().open(player, getGui().allArticles(currentPage + 1));

                }else if(event.getSlot() == 45){
                    getGui().open(player, getGui().allArticles(currentPage - 1));
                }
            }
        }
    }

    private String stripColor(String input) {
        return input.replaceAll("§[0-9a-fk-orA-FK-OR]", "");
    }

    private void handleAdminPanel(InventoryClickEvent event) {
        event.setCancelled(true);

        Material type = getClickedType(event);
        if (type == null) return;

        Player player = (Player) event.getWhoClicked();

        switch (type) {
            case PLAYER_HEAD -> getGui().open(player, getGui().allPlayersBalance());
            case CHEST       -> getGui().open(player, getGui().allCategory());
            case GOLD_INGOT  -> getGui().open(player, getGui().allArticles(0));
            default          -> {}
        }
    }

    private void handleShop(InventoryClickEvent event) {
        event.setCancelled(true);

        Material type = getClickedType(event);
        if (type == null) return;

        Category matched = null;
        for (Category cat : Category.values()) {
            if (cat.getMaterial() == type) {
                matched = cat;
                break;
            }
        }

        if (matched == null) return;

        getGui().open((Player) event.getWhoClicked(), getGui().createCategoryGui(matched));
    }

    private void handleAllArticles(InventoryClickEvent event) {
        event.setCancelled(true);

        Material type = getClickedType(event);
        if (type == null) return;

        if (type == Material.ARROW || type == Material.BOOK) {
            return;
        }

        ItemMarket itemMarket = ItemMarket.itemsMarket.get(type);
        if (itemMarket == null) return;

        Player   player     = (Player) event.getWhoClicked();
        boolean  isLeft     = event.getClick().isLeftClick();
        ItemStack current = event.getCurrentItem();

        if (event.getClick() == ClickType.DROP) {
            if(getMarket().isDisable(itemMarket)) {
                getMarket().enableItem(itemMarket);
                player.closeInventory();
                getGui().open(player, getGui().allArticles(0));
            } else{
                getMarket().disableItem(itemMarket);
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
            EditType editType   = isLeft ? EditType.BUY : EditType.SELL;
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

    private void handleShopCategory(InventoryClickEvent event) {
        event.setCancelled(true);

        Material type = getClickedType(event);
        if (type == null) return;

        ItemMarket itemMarket = ItemMarket.itemsMarket.get(type);
        if (itemMarket == null) return;

        Player player = (Player) event.getWhoClicked();
        EcoPlayer ecoPlayer = EcoPlayer.ecoPlayers.get(player.getUniqueId());

        boolean  isLeft     = event.getClick().isLeftClick();
        BuySession.TradeType tradeType = isLeft ? BuySession.TradeType.BUY : BuySession.TradeType.SELL;
        String    label     = isLeft ? "achat" : "vente";
        double    prix      = isLeft ? itemMarket.getBuyPrice() : itemMarket.getSellPrice();

        BuySession.put(player.getUniqueId(), itemMarket, tradeType);
        player.closeInventory();

        player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                .color(NamedTextColor.GOLD));
        player.sendMessage(Component.text(" ✦ " + (isLeft ? "Achat" : "Vente"))
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.BOLD, true));
        player.sendMessage(Component.text(" Article : ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(type.name())
                        .color(NamedTextColor.WHITE)));
        player.sendMessage(Component.text(" Prix unitaire (" + label + ") : ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(prix + "$")
                        .color(NamedTextColor.YELLOW)));
        if(isLeft)
            player.sendMessage(Component.text(" Quantité maximum achetable: ")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text((int)ecoPlayer.getBalance() / itemMarket.getBuyPrice())
                            .color(NamedTextColor.GREEN)));
        player.sendMessage(Component.text(" ▶ Tape la quantité souhaitée dans le chat.")
                .color(NamedTextColor.AQUA));
        player.sendMessage(Component.text(" ▶ Tape ")
                .color(NamedTextColor.AQUA)
                .append(Component.text("annuler")
                        .color(NamedTextColor.RED))
                .append(Component.text(" pour abandonner.")
                        .color(NamedTextColor.AQUA)));
        player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                .color(NamedTextColor.GOLD));
    }

    private void handleAllPlayers(InventoryClickEvent event) {
        event.setCancelled(true);

        Material type = getClickedType(event);
        if (type != Material.PLAYER_HEAD) return;

        ItemStack item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;

        String targetName = PlainTextComponentSerializer
                .plainText()
                .serialize(item.getItemMeta().displayName());

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) return;

        Player  admin    = (Player) event.getWhoClicked();
        boolean isLeft   = event.getClick().isLeftClick();
        boolean isMiddle = event.getClick() == ClickType.MIDDLE;

        BalanceEditSession.EditType editType;
        String label;

        if (isMiddle) {
            editType = BalanceEditSession.EditType.SET;
            label    = "Définir";
        } else if (isLeft) {
            editType = BalanceEditSession.EditType.ADD;
            label    = "Ajouter";
        } else {
            editType = BalanceEditSession.EditType.REMOVE;
            label    = "Retirer";
        }

        EcoPlayer eco = EcoPlayer.ecoPlayers.get(target.getUniqueId());
        if (eco == null) return;

        BalanceEditSession.put(admin.getUniqueId(), target.getUniqueId(), targetName, editType);
        admin.closeInventory();

        admin.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").color(NamedTextColor.GOLD));
        admin.sendMessage(Component.text(" ✦ Modification de balance")
                .color(NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true));
        admin.sendMessage(Component.text(" Joueur : ").color(NamedTextColor.GRAY)
                .append(Component.text(targetName).color(NamedTextColor.WHITE)));
        admin.sendMessage(Component.text(" Balance actuelle : ").color(NamedTextColor.GRAY)
                .append(Component.text(eco.getStringBalance() + "$").color(NamedTextColor.YELLOW)));
        admin.sendMessage(Component.text(" Action : ").color(NamedTextColor.GRAY)
                .append(Component.text(label).color(NamedTextColor.AQUA)));
        admin.sendMessage(Component.text(" ▶ Tape le montant dans le chat.").color(NamedTextColor.AQUA));
        admin.sendMessage(Component.text(" ▶ Tape ").color(NamedTextColor.AQUA)
                .append(Component.text("annuler").color(NamedTextColor.RED))
                .append(Component.text(" pour abandonner.").color(NamedTextColor.AQUA)));
        admin.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━").color(NamedTextColor.GOLD));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (PriceEditSession.hasPending(player.getUniqueId())) {
            handlePriceChat(event, player);
            return;
        }

        if (BalanceEditSession.hasPending(player.getUniqueId())) {
            handleBalanceChat(event, player);
            return;
        }

        // Session joueur (quantité)
        if (BuySession.hasPending(player.getUniqueId())) {
            handleTradeChat(event, player);
        }
    }

    private void handlePriceChat(AsyncPlayerChatEvent event, Player player) {
        event.setCancelled(true);
        String input = event.getMessage().trim();

        if (input.equalsIgnoreCase("annuler")) {
            PriceEditSession.consume(player.getUniqueId());
            player.sendMessage(Component.text("✘ Modification annulée.").color(NamedTextColor.RED));
            return;
        }

        double newPrice;
        try {
            newPrice = Double.parseDouble(input);
            if (newPrice < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("✘ Prix invalide. Saisis un nombre positif ou ")
                    .color(NamedTextColor.RED)
                    .append(Component.text("annuler").color(NamedTextColor.GOLD))
                    .append(Component.text(".").color(NamedTextColor.RED)));
            return;
        }

        PriceEditSession session    = PriceEditSession.consume(player.getUniqueId());
        ItemMarket       itemMarket = session.getItemMarket();
        String           label;

        if (session.getEditType() == EditType.BUY) {
            getMarket().editItemPrices(itemMarket, itemMarket.getSellPrice(), newPrice);
            label = "achat";
        } else {
            getMarket().editItemPrices(itemMarket, newPrice, itemMarket.getBuyPrice());
            label = "vente";
        }

        player.sendMessage(Component.text("✔ Prix de " + label + " mis à jour !")
                .color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true));
        player.sendMessage(Component.text("  Article : ").color(NamedTextColor.GRAY)
                .append(Component.text(itemMarket.getMaterial().name()).color(NamedTextColor.WHITE)));
        player.sendMessage(Component.text("  Nouveau prix : ").color(NamedTextColor.GRAY)
                .append(Component.text(newPrice + "$").color(NamedTextColor.GREEN)));

        Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () ->
                getGui().open(player, getGui().allArticles(0))
        );
    }

    private void handleTradeChat(AsyncPlayerChatEvent event, Player player) {
        event.setCancelled(true);
        String input = event.getMessage().trim();

        if (input.equalsIgnoreCase("annuler")) {
            BuySession.consume(player.getUniqueId());
            player.sendMessage(Component.text("✘ Transaction annulée.").color(NamedTextColor.RED));
            return;
        }

        int quantite;
        try {
            quantite = Integer.parseInt(input);
            if (quantite <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("✘ Quantité invalide. Saisis un entier positif ou ")
                    .color(NamedTextColor.RED)
                    .append(Component.text("annuler").color(NamedTextColor.GOLD))
                    .append(Component.text(".").color(NamedTextColor.RED)));
            return;
        }

        BuySession session    = BuySession.consume(player.getUniqueId());
        ItemMarket    itemMarket = session.getItemMarket();
        EcoPlayer     eco        = EcoPlayer.ecoPlayers.get(player.getUniqueId());
        MarketManager market     = getMarket();

        if (eco == null) {
            player.sendMessage(Component.text("✘ Profil économique introuvable. Reconnecte-toi.")
                    .color(NamedTextColor.RED));
            return;
        }

        if (session.getTradeType() == BuySession.TradeType.BUY) {
            if (market.canBuy(eco, itemMarket, quantite)) {
                market.buyItems(eco, itemMarket, quantite);
                double total = itemMarket.getBuyPrice() * quantite;
                player.sendMessage(Component.text("✔ Achat effectué !")
                        .color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true));
                player.sendMessage(Component.text("  Article : ").color(NamedTextColor.GRAY)
                        .append(Component.text(itemMarket.getMaterial().name()).color(NamedTextColor.WHITE)));
                player.sendMessage(Component.text("  Quantité : ").color(NamedTextColor.GRAY)
                        .append(Component.text(quantite + "x").color(NamedTextColor.WHITE)));
                player.sendMessage(Component.text("  Total payé : ").color(NamedTextColor.GRAY)
                        .append(Component.text(total + "$").color(NamedTextColor.RED)));
                market.updateBuyPrice(itemMarket, quantite);
            } else {
                player.sendMessage(Component.text("✘ Solde insuffisant pour " + quantite + "x " + itemMarket.getMaterial().name() + ".")
                        .color(NamedTextColor.RED));
            }
        } else {
            if (market.canSell(eco, itemMarket, quantite)) {
                market.sellItems(eco, itemMarket, quantite);
                double total = itemMarket.getSellPrice() * quantite;
                player.sendMessage(Component.text("✔ Vente effectuée !")
                        .color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true));
                player.sendMessage(Component.text("  Article : ").color(NamedTextColor.GRAY)
                        .append(Component.text(itemMarket.getMaterial().name()).color(NamedTextColor.WHITE)));
                player.sendMessage(Component.text("  Quantité : ").color(NamedTextColor.GRAY)
                        .append(Component.text(quantite + "x").color(NamedTextColor.WHITE)));
                player.sendMessage(Component.text("  Total reçu : ").color(NamedTextColor.GRAY)
                        .append(Component.text(total + "$").color(NamedTextColor.GREEN)));
                market.updateSellPrice(itemMarket, quantite);
            } else {
                player.sendMessage(Component.text("✘ Vous n'avez pas " + quantite + "x " + itemMarket.getMaterial().name() + " en inventaire.")
                        .color(NamedTextColor.RED));
            }
        }
    }

    private void handleBalanceChat(AsyncPlayerChatEvent event, Player admin) {
        event.setCancelled(true);
        String input = event.getMessage().trim();

        if (input.equalsIgnoreCase("annuler")) {
            BalanceEditSession.consume(admin.getUniqueId());
            admin.sendMessage(Component.text("✘ Modification annulée.").color(NamedTextColor.RED));
            return;
        }

        double montant;
        try {
            montant = Double.parseDouble(input);
            if (montant < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            admin.sendMessage(Component.text("✘ Montant invalide. Saisis un nombre positif ou ")
                    .color(NamedTextColor.RED)
                    .append(Component.text("annuler").color(NamedTextColor.GOLD))
                    .append(Component.text(".").color(NamedTextColor.RED)));
            return;
        }

        BalanceEditSession session = BalanceEditSession.consume(admin.getUniqueId());
        EcoPlayer         eco     = EcoPlayer.ecoPlayers.get(session.getTargetUuid());

        if (eco == null) {
            admin.sendMessage(Component.text("✘ Joueur introuvable (déconnecté ?).")
                    .color(NamedTextColor.RED));
            return;
        }

        double avant = eco.getBalance();
        String label;

        switch (session.getEditType()) {
            case ADD    -> { eco.credit(montant);                    label = "ajouté";   }
            case REMOVE -> { eco.debit(montant);                     label = "retiré";   }
            case SET    -> { eco.setBalance(montant);                label = "défini à"; }
            default     -> { return; }
        }

        admin.sendMessage(Component.text("✔ Balance mise à jour !")
                .color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true));
        admin.sendMessage(Component.text("  Joueur : ").color(NamedTextColor.GRAY)
                .append(Component.text(session.getTargetName()).color(NamedTextColor.WHITE)));
        admin.sendMessage(Component.text("  Avant : ").color(NamedTextColor.GRAY)
                .append(Component.text(avant + "$").color(NamedTextColor.YELLOW)));
        admin.sendMessage(Component.text("  " + label + " : ").color(NamedTextColor.GRAY)
                .append(Component.text(montant + "$").color(NamedTextColor.AQUA)));
        admin.sendMessage(Component.text("  Après : ").color(NamedTextColor.GRAY)
                .append(Component.text(eco.getStringBalance() + "$").color(NamedTextColor.GREEN)));

        // Notifie le joueur ciblé
        org.bukkit.entity.Player target = org.bukkit.Bukkit.getPlayer(session.getTargetUuid());
        if (target != null) {
            target.sendMessage(Component.text("✦ Votre balance a été modifiée par un administrateur.")
                    .color(NamedTextColor.GOLD));
            target.sendMessage(Component.text("  Nouvelle balance : ").color(NamedTextColor.GRAY)
                    .append(Component.text(eco.getStringBalance() + "$").color(NamedTextColor.GREEN)));
        }

        Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () ->
                getGui().open(admin, getGui().allPlayersBalance())
        );
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private GuiManager    getGui()    { return Main.getInstance().getGuiManager();    }
    private MarketManager getMarket() { return Main.getInstance().getMarketManager(); }

    private Material getClickedType(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        return (item == null || item.getType() == Material.AIR) ? null : item.getType();
    }
}
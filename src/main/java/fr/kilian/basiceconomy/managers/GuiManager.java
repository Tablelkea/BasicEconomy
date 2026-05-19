package fr.kilian.basiceconomy.managers;

import fr.kilian.basiceconomy.model.EcoPlayer;
import fr.kilian.basiceconomy.model.ItemMarket;
import fr.kilian.basiceconomy.model.enums.Category;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiManager {

    public final Map<Material, ItemStack> clickableItem = new HashMap<>();

    // Titles
    public static final String ADMIN_PANEL_TITLE    = "✦ Panel Administrateur";
    public static final String SHOP_TITLE           = "✦ Boutique";
    public static final String SHOP_CATEGORY_PREFIX = "✦ Boutique • ";
    public static final String ALL_PLAYERS_TITLE    = "✦ Liste des joueurs";
    public static final String ALL_CATEGORY_TITLE   = "✦ Catégories";
    public static final String ALL_ARTICLES_TITLE   = "✦ Tous les articles";

    public ItemStack errorItemstack() {
        return buildItem(Material.STRUCTURE_VOID, 1, Component.text("VIDE"), null, false);
    }

    public ItemStack voidGlass() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setHideTooltip(true);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildItem(Material material, int count, Component title, List<Component> lore, boolean glint) {
        ItemStack item = new ItemStack(material, count);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(title);
        if (lore != null) meta.lore(lore);
        meta.setEnchantmentGlintOverride(glint);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createItemstack(@NonNull Category category) {
        ItemStack item = buildItem(
                category.getMaterial(),
                1,
                title(prettyCategory(category)),
                List.of(
                        subtitle("Ouvrir la catégorie"),
                        action("Clic pour accéder")
                ),
                false
        );

        clickableItem.put(item.getType(), item);
        return item;
    }

    public ItemStack createMarketItemstack(@NonNull ItemMarket itemMarket) {
        ItemStack item = buildItem(
                itemMarket.getMaterial(),
                1,
                title(pretty(itemMarket.getMaterial())),
                List.of(
                        success("Prix achat : " + itemMarket.getBuyPrice() + "$"),
                        danger("Prix vente : " + itemMarket.getSellPrice() + "$"),
                        Component.empty(),
                        action("Clic gauche → Acheter"),
                        action("Clic droit → Vendre")
                ),
                false
        );

        clickableItem.put(item.getType(), item);
        return item;
    }

    public ItemStack createAdminMarketItemstack(@NonNull ItemMarket itemMarket) {
        ItemStack item = buildItem(
                itemMarket.getMaterial(),
                1,
                title(pretty(itemMarket.getMaterial())),
                List.of(
                        lore("Catégorie : " + itemMarket.getCategory()),
                        success("Prix achat : " + itemMarket.getBuyPrice()),
                        danger("Prix vente : " + itemMarket.getSellPrice()),
                        lore("Demande : " + itemMarket.getDemand()),
                        lore("Offre : " + itemMarket.getSupply()),
                        Component.empty(),
                        action("Clic gauche → Modifier achat"),
                        action("Clic droit → Modifier vente")
                ),
                false
        );

        clickableItem.put(item.getType(), item);
        return item;
    }

    public ItemStack createCustomItemstack(Material material, int count, Component title, List<Component> lore, boolean glint) {
        ItemStack item = buildItem(material, count, title, lore, glint);
        clickableItem.put(material, item);
        return item;
    }

    public void open(@NonNull Player player, Inventory inventory) {
        player.openInventory(inventory);
    }

    public void fill(@NonNull Inventory inventory) {
        ItemStack glass = voidGlass();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, glass);
        }
    }

    public Inventory createCategoryGui(@NonNull Category category) {
        String label = prettyCategory(category);

        Inventory inventory = Bukkit.createInventory(
                null,
                27,
                Component.text(SHOP_CATEGORY_PREFIX + label)
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.BOLD, true)
        );


        for (ItemMarket item : ItemMarket.itemsMarket.values()) {
            if (item.getCategory() == category) {
                inventory.addItem(createMarketItemstack(item));
            }
        }

        return inventory;
    }

    public Inventory createMarketGui() {
        Category[] categories = Category.values();
        int count = categories.length;

        int rows = Math.max(3, (int) Math.ceil((count + 2) / 9.0) + 2);
        int size = rows * 9;

        Inventory market = Bukkit.createInventory(
                null,
                size,
                Component.text(SHOP_TITLE)
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.BOLD, true)
        );

        fill(market);

        List<Integer> slots = centeredSlots(count, rows);

        for (int i = 0; i < count; i++) {
            market.setItem(slots.get(i), createItemstack(categories[i]));
        }

        return market;
    }

    private List<Integer> centeredSlots(int count, int rows) {
        List<Integer> slots = new ArrayList<>();

        int usableRows   = rows - 2;
        int itemsPerRow  = 7;

        int placed    = 0;
        int rowIndex  = 1;

        while (placed < count && rowIndex <= usableRows) {
            int remaining   = count - placed;
            int inThisRow   = Math.min(remaining, itemsPerRow);

            // Centrage : décalage pour que les items soient au milieu des 9 slots
            int startCol = (9 - inThisRow) / 2;

            for (int col = 0; col < inThisRow; col++) {
                slots.add(rowIndex * 9 + startCol + col);
            }

            placed   += inThisRow;
            rowIndex++;
        }

        return slots;
    }

    public Inventory createAdminPanel() {
        Inventory panel = Bukkit.createInventory(null, 27, ADMIN_PANEL_TITLE);
        fill(panel);

        panel.setItem(11, createCustomItemstack(
                Material.PLAYER_HEAD,
                Bukkit.getOnlinePlayers().size(),
                title("Joueurs"),
                List.of(
                        subtitle("Gestion des joueurs"),
                        action("Clique pour ouvrir")
                ),
                true
        ));

        panel.setItem(13, createCustomItemstack(
                Material.CHEST,
                Category.values().length,
                title("Catégories"),
                List.of(
                        subtitle("Voir les catégories"),
                        action("Clique pour ouvrir")
                ),
                true
        ));

        panel.setItem(15, createCustomItemstack(
                Material.GOLD_INGOT,
                1,
                title("Prix des articles"),
                List.of(
                        subtitle("Modifier les prix"),
                        action("Clique pour ouvrir")
                ),
                true
        ));

        return panel;
    }

    public Inventory allPlayersBalance() {
        Inventory inventory = Bukkit.createInventory(
                null,
                54,
                Component.text(ALL_PLAYERS_TITLE)
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.BOLD, true)
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            EcoPlayer ecoPlayer = EcoPlayer.ecoPlayers.get(player.getUniqueId());

            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();

            meta.setPlayerProfile(player.getPlayerProfile());
            meta.displayName(title(player.getName()));

            meta.lore(List.of(
                    success("Balance : " + ecoPlayer.getStringBalance() + "$"),
                    subtitle("Joueur connecté")
            ));

            item.setItemMeta(meta);
            inventory.addItem(item);
        }

        return inventory;
    }

    public Inventory allCategory() {
        Inventory inventory = Bukkit.createInventory(
                null,
                27,
                Component.text(ALL_CATEGORY_TITLE)
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.BOLD, true)
        );

        for (Category category : Category.values()) {
            List<ItemMarket> categoryItems = ItemMarket.items.stream()
                    .filter(item -> item.getCategory() == category)
                    .toList();

            List<Component> lores = categoryItems.stream()
                    .map(item -> lore(pretty(item.getMaterial())))
                    .toList();

            ItemStack stack = new ItemStack(category.getMaterial(), categoryItems.size());

            ItemMeta meta = stack.getItemMeta();
            meta.displayName(title(prettyCategory(category)));

            meta.lore(lores);
            meta.setEnchantmentGlintOverride(true);

            stack.setItemMeta(meta);

            inventory.addItem(stack);
        }

        return inventory;
    }

    public Inventory allArticles() {
        Inventory inventory = Bukkit.createInventory(
                null,
                54,
                Component.text(ALL_ARTICLES_TITLE)
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.BOLD, true)
        );

        fill(inventory);

        int slot = 0;

        for (ItemMarket item : ItemMarket.itemsMarket.values()) {
            if (slot >= inventory.getSize()) break;
            inventory.setItem(slot++, createAdminMarketItemstack(item));
        }

        return inventory;
    }

    private static @NonNull Component title(String text) {
        return Component.text(text)
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.BOLD, true)
                .decoration(TextDecoration.ITALIC, false);
    }

    private static @NonNull Component subtitle(String text) {
        return Component.text(text)
                .color(NamedTextColor.YELLOW)
                .decoration(TextDecoration.BOLD, false)
                .decoration(TextDecoration.ITALIC, false);
    }

    private static @NonNull Component lore(String text) {
        return Component.text("• " + text)
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false);
    }

    private static @NonNull Component success(String text) {
        return Component.text("✔ " + text)
                .color(NamedTextColor.GREEN)
                .decoration(TextDecoration.ITALIC, false);
    }

    private static @NonNull Component danger(String text) {
        return Component.text("✘ " + text)
                .color(NamedTextColor.RED)
                .decoration(TextDecoration.ITALIC, false);
    }

    private static @NonNull Component action(String text) {
        return Component.text("▶ " + text)
                .color(NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false);
    }

    private static @NonNull String pretty(@NonNull Material material) {
        String[] words = material.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();

        for (String word : words) {
            sb.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }

        return sb.toString().trim();
    }

    @Contract(pure = true)
    private static @NonNull String prettyCategory(@NonNull Category category) {
        return switch (category) {
            case ORES -> "Minerais";
            case WOOD -> "Bois";
            case FOOD -> "Nourriture";
            case NETHER -> "Nether";
            case END -> "End";
            case BLOCKS -> "Block";
            case FARMING -> "Ferme";
            case MOB_DROPS -> "Loots";
        };
    }
}
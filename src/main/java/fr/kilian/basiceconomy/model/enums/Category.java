package fr.kilian.basiceconomy.model.enums;

import fr.kilian.basiceconomy.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public enum Category {

    ORES(Material.DIAMOND_ORE, Component.text("MINERAIS")),
    WOOD(Material.OAK_LOG, Component.text("BOIS")),
    FOOD(Material.COOKED_BEEF, Component.text("NOURRITURE")),
    NETHER(Material.NETHERRACK, Component.text("NETHER")),
    END(Material.END_STONE, Component.text("END")),
    FARMING(Material.STONE_HOE, Component.text("FARMING")),
    MOB_DROPS(Material.ROTTEN_FLESH, Component.text("DROPS")),
    BLOCKS(Material.BRICKS, Component.text("BLOCKS"));

    final Material material;
    final Component title;

    Category(Material material, Component title){
        this.material = material;
        this.title = title;

        Main.getInstance().getMarketManager().materialCategory.put(material, this);
    }

    public Material getMaterial() {
        return material;
    }

    public Component getTitle() {
        return title;
    }

}

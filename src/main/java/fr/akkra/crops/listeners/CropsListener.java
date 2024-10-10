package fr.akkra.crops.listeners;

import fr.akkra.crops.CropsBreaker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CropsListener implements Listener {

    private final CropsBreaker cropsBreaker;
    private final List<Material> cropsToBreak;
    private final HashMap<Material, Material> cropsToReplace;

    public CropsListener(CropsBreaker instance) {
        this.cropsBreaker = instance;
        FileConfiguration config = cropsBreaker.getConfig();
        this.cropsToBreak = loadCropsToBreak(config);
        this.cropsToReplace = loadCropsToReplace(config);
    }

    private List<Material> loadCropsToBreak(FileConfiguration config) {
        List<Material> crops = new ArrayList<>();
        for (String key : config.getConfigurationSection("crops").getKeys(false)) {
            if (config.getBoolean("crops." + key + ".break")) {
                try {
                    crops.add(Material.valueOf(key));
                } catch (IllegalArgumentException e) {
                    Bukkit.getConsoleSender().sendMessage("[CropsBreaker] Matérial invalide : " + key);
                }
            }
        }
        return crops;
    }

    private HashMap<Material, Material> loadCropsToReplace(FileConfiguration config) {
        HashMap<Material, Material> replacements = new HashMap<>();
        for (Material cropMaterial : cropsToBreak) {
            String replaceMaterialString = config.getString("crops." + cropMaterial + ".replace-with");
            if (replaceMaterialString != null) {
                try {
                    replacements.put(cropMaterial, Material.valueOf(replaceMaterialString));
                } catch (IllegalArgumentException e) {
                    Bukkit.getConsoleSender().sendMessage("[CropsBreaker] Matérial de remplacement invalide : " + replaceMaterialString);
                }
            }
        }
        return replacements;
    }

    @EventHandler
    public void onGrow(BlockGrowEvent event) {
        Material grownBlockType = event.getNewState().getType();
        if (cropsToBreak.contains(grownBlockType)) {
            new BlockBreakTask(grownBlockType, cropsToReplace.get(grownBlockType), event.getBlock().getLocation()).runTaskLater(cropsBreaker, 10L);
        }
    }

    private class BlockBreakTask extends BukkitRunnable {
        private final Material material;
        private final Material replacement;
        private final Location location;

        public BlockBreakTask(Material material, Material replacement, Location location) {
            this.material = material;
            this.replacement = replacement;
            this.location = location;
        }

        @Override
        public void run() {
            if (location.getBlock().getType() == material) {
                location.getBlock().breakNaturally();
                if (replacement != null) {
                    location.getBlock().setType(replacement);
                }
            }
        }
    }
}

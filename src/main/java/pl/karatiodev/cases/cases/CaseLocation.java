package pl.karatiodev.cases.cases;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Getter
@Setter
@NoArgsConstructor
public class CaseLocation extends OkaeriConfig {

    private String world;

    private double x, y, z;

    public CaseLocation(Location location){
        this.world = location.getWorld() == null ? null : location.getWorld().getName();

        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public Location toLocation(){
        if(world == null) return null;

        var bukkitWorld = Bukkit.getWorld(world);
        if(bukkitWorld == null) return null;

        return new Location(bukkitWorld, x, y, z);
    }

    public boolean matches(Location location){
        if(location == null || location.getWorld() == null) return false;

        return world != null
                && world.equals(location.getWorld().getName())
                && x == location.getBlockX()
                && y == location.getBlockY()
                && z == location.getBlockZ();
    }
}

package me.jashscript.openspawners.models;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Objects;

public class BlockLocation {

    private final String world;
    private final int x;
    private final int y;
    private final int z;

    public BlockLocation(String world, int x, int y, int z) {
        this.world = Objects.requireNonNull(world, "world");
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockLocation(Location location) {
        this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static BlockLocation fromString(String string){
        String[] array = string.split(",");
        return new BlockLocation(array[0], Integer.valueOf(array[1]), Integer.valueOf(array[2]), Integer.valueOf(array[3]));
    }

    public BlockLocation(Block block) {
        this(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public boolean isInChunk(Chunk chunk) {
        return x >> 4 == chunk.getX() && z >> 4 == chunk.getZ() && world.equals(chunk.getWorld().getName());
    }

    public boolean blockEquals(Block block) {
        return x == block.getX() && y == block.getY() && z == block.getZ() && world.equals(block.getWorld().getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BlockLocation that = (BlockLocation) o;
        return x == that.x && y == that.y && z == that.z && world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(this.getWorld()), this.getX(), this.getY(), this.getZ());
    }

    @Override
    public String toString() {
        return world + "," + x + "," + y + "," + z;
    }
}

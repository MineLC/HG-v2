package lc.minelc.hg.utils;

import org.bukkit.Location;

public final class EntityLocation {
    private final double x, y, z;
    private final float yaw, pitch;

    public EntityLocation(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public final String toString() {
        return x + "," + y + "," + z + "," + yaw + "," + pitch;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = 19 * hash + (int)(Double.doubleToLongBits(this.x) ^ Double.doubleToLongBits(this.x) >>> 32);
        hash = 19 * hash + (int)(Double.doubleToLongBits(this.y) ^ Double.doubleToLongBits(this.y) >>> 32);
        hash = 19 * hash + (int)(Double.doubleToLongBits(this.z) ^ Double.doubleToLongBits(this.z) >>> 32);
        hash = 19 * hash + Float.floatToIntBits(this.pitch);
        hash = 19 * hash + Float.floatToIntBits(this.yaw);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof EntityLocation otherEntity)
            ? otherEntity.x == this.x && otherEntity.y == this.y && otherEntity.z == this.z
            : false;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public float yaw() {
        return yaw;
    }
    
    public float pitch() {
        return pitch;
    }

    public static EntityLocation create(String text) {
        final String[] split = text.split(",");

        return new EntityLocation(
            Double.parseDouble(split[0]),
            Double.parseDouble(split[1]),
            Double.parseDouble(split[2]),
            Float.parseFloat(split[3]),
            Float.parseFloat(split[4])
        );
    }

    public static EntityLocation toEntityLocation(final Location location, float yaw, float pitch) {
        return new EntityLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ(), yaw, pitch);
    }
}

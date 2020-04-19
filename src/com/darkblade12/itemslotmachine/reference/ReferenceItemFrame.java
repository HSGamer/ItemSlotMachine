package com.darkblade12.itemslotmachine.reference;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import com.darkblade12.itemslotmachine.safe.SafeLocation;

public final class ReferenceItemFrame extends ReferenceLocation {
    private BlockFace initialFacing;
    private Direction initialDirection;

    public ReferenceItemFrame(int l, int f, int u, BlockFace initialFacing, Direction initialDirection) {
        super(l, f, u);
        this.initialFacing = initialFacing;
        this.initialDirection = initialDirection;
    }

    public ReferenceItemFrame(ReferenceLocation location, BlockFace initialFacing, Direction initialDirection) {
        this(location.l, location.f, location.u, initialFacing, initialDirection);
    }

    public static ItemFrame findItemFrame(Location location) {
        for (Entity entity : location.getChunk().getEntities()) {
            Location entityLocation = entity.getLocation().getBlock().getLocation();

            if (entity instanceof ItemFrame && SafeLocation.noDistance(location, entityLocation)) {
                return (ItemFrame) entity;
            }
        }

        return null;
    }

    public static ReferenceItemFrame fromBukkitItemFrame(Location viewPoint, Direction viewDirection, ItemFrame frame) {
        ReferenceLocation location = fromBukkitLocation(viewPoint, viewDirection, frame.getLocation());
        return new ReferenceItemFrame(location, frame.getFacing(), viewDirection);
    }

    public static ReferenceItemFrame fromBukkitItemFrame(Player viewer, ItemFrame frame) {
        return fromBukkitItemFrame(viewer.getLocation(), Direction.getViewDirection(viewer), frame);
    }

    private BlockFace rotate(Direction viewDirection) {
        return Direction.rotate(initialFacing, initialDirection, viewDirection);
    }

    public void place(Location viewPoint, Direction viewDirection) {
        Location l = getBukkitLocation(viewPoint, viewDirection);
        World w = l.getWorld();
        ItemFrame frame = (ItemFrame) w.spawnEntity(l, EntityType.ITEM_FRAME);
        frame.setFacingDirection(rotate(viewDirection), true);
    }

    public void place(Player viewer) {
        place(viewer.getLocation(), Direction.getViewDirection(viewer));
    }

    public BlockFace getInitialFacing() {
        return this.initialFacing;
    }

    public Direction getInitialDirection() {
        return this.initialDirection;
    }

    public Block getAttachedBlock(Location viewPoint, Direction viewDirection) {
        BlockFace face = rotate(viewDirection).getOppositeFace();
        return getBukkitBlock(viewPoint, viewDirection).getRelative(face);
    }

    public ItemFrame getBukkitItemFrame(Location viewPoint, Direction viewDirection) {
        return findItemFrame(getBukkitLocation(viewPoint, viewDirection));
    }

    public ItemFrame getBukkitItemFrame(Player viewer) {
        return getBukkitItemFrame(viewer.getLocation(), Direction.getViewDirection(viewer));
    }

    @Override
    public ReferenceItemFrame clone() {
        return new ReferenceItemFrame(l, f, u, initialFacing, initialDirection);
    }
}
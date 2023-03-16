//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.aceliip.korra.pressure;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AirAbility;
import com.projectkorra.projectkorra.ability.util.CollisionManager;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Pressure extends AirAbility implements AddonAbility {
    private long cooldown;
    private double range;
    private double damage;
    private boolean isCharged;
    private boolean launched;
    private Location origin;
    private Location location;
    private Vector direction;
    private double radius;
    private double t;
    private Permission perm;

    public Pressure(Player player) {
        super(player);
        if (this.bPlayer.canBend(this)) {
            this.setFields();
            this.start();
        }
    }

    public void setFields() {
        this.cooldown = ConfigManager.getConfig().getLong("ExtraAbilities.Aceliip.Pressure.Cooldown");
        this.range = ConfigManager.getConfig().getDouble("ExtraAbilities.Aceliip.Pressure.Range");
        this.radius = ConfigManager.getConfig().getDouble("ExtraAbilities.Aceliip.Pressure.CollisionRadius");
        this.damage = ConfigManager.getConfig().getDouble("ExtraAbilities.Aceliip.Pressure.Damage");
    }

    public void progress() {
        if (!this.player.isDead() && this.player.isOnline()) {
            if (this.player.isSneaking() && !this.launched) {
                this.chargeAnimation();
            } else {
                if (!this.isCharged) {
                    this.remove();
                    return;
                }

                if (!this.launched) {
                    this.bPlayer.addCooldown(this);
                    this.launched = true;
                }

                this.blast();
            }

        }
    }

    private void blast() {
        this.direction = GeneralMethods.getTargetedLocation(this.player, 1).getDirection();
        this.location.add(this.direction);
        ParticleEffect.CLOUD.display(this.location, 5, 0.7F, 0.7F, 0.7F, -0.3F);
        ParticleEffect.CLOUD.display(this.location, 10, 0.15F, 0.15F, 0.15F, -0.15F);
        this.location.getWorld().playSound(this.location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0F, 1.5F);
        Iterator<Entity> entityIterator = GeneralMethods.getEntitiesAroundPoint(this.location, 2.5).iterator();

        Entity e;
        do {
            if (!entityIterator.hasNext()) {
                if (this.origin.distance(this.location) > this.range || !isTransparent(location.getBlock()) || GeneralMethods.checkDiagonalWall(this.location, this.direction)) {
                    ParticleEffect.FLASH.display(location, 1, 0, 0, 0);
                    this.location.getWorld().playSound(this.location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0F, 0.5F);
                    this.location.getWorld().playSound(this.location, Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 2.0F, Float.MIN_VALUE);
                    this.remove();
                    this.bPlayer.addCooldown(this);
                    return;
                }

                return;
            }

            e = entityIterator.next();
        } while (!(e instanceof LivingEntity) || e.getEntityId() == this.player.getEntityId());

        DamageHandler.damageEntity(e, this.damage, this);
        LivingEntity le = (LivingEntity) e;
        le.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 180, 1), true);
        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 2), true);
        this.location.getWorld().playSound(this.location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0F, 0.5F);
        this.location.getWorld().playSound(this.location, Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 2.0F, Float.MIN_VALUE);
        ParticleEffect.FLASH.display(location, 1, 0, 0, 0);
        this.remove();
    }

    private void chargeAnimation() {
        this.t += 0.09817477042468103;
        Location location = this.player.getEyeLocation().add(player.getLocation().getDirection());
        if (this.t >= 12.566370614359172) {
            Location loc = this.player.getEyeLocation();
            ParticleEffect.CLOUD.display(loc, 10, .7F, .7F, .7F, -0.1F);
            if (this.player.isSneaking() && !this.launched) {
                this.location = GeneralMethods.getTargetedLocation(this.player, 1);
                this.origin = GeneralMethods.getTargetedLocation(this.player, 1);
                this.direction = GeneralMethods.getTargetedLocation(this.player, 1).getDirection();
                this.isCharged = true;
                this.launched = false;
            }

            if (this.isCharged) {
                location.getWorld().playSound(location, Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 0.2F, 3F);
            }
        } else {
            for (double phi = 0.0; phi <= 6.283185307179586; phi += 2.0943951023931953) {
                double x = 0.5 * (13.566370614359172 - this.t) * Math.cos(this.t + phi);
                double y = 0.5 * (13.566370614359172 - this.t);
                double z = 0.5 * (13.566370614359172 - this.t) * Math.sin(this.t + phi);
                location.add(x, y, z);
                ParticleEffect.CLOUD.display(location, 5, 0.0F, 0.0F, 0.0F, 0.0F);
                if (ThreadLocalRandom.current().nextBoolean())
                    playAirbendingSound(location);
                //location.getWorld().playSound(location, Sound.ENTITY_CREEPER_DEATH, 0.2F, 1.0F);
                location.subtract(x, y, z);
            }
        }
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return "Pressure";
    }

    public String getDescription() {
        return "Pressureize a gust of wind that damages and disorients players on impact.";
    }

    public String getInstruction() {
        return "Hold SHIFT until charge animation finishes, then release.";
    }

    public boolean isHarmlessAbility() {
        return false;
    }

    public boolean isSneakAbility() {
        return true;
    }

    public String getAuthor() {
        return "Aceliip";
    }

    public String getVersion() {
        return "v1.5";
    }

    public void load() {
        ConfigManager.getConfig().addDefault("ExtraAbilities.Aceliip.Pressure.Cooldown", 2000);
        ConfigManager.getConfig().addDefault("ExtraAbilities.Aceliip.Pressure.Range", 30);
        ConfigManager.getConfig().addDefault("ExtraAbilities.Aceliip.Pressure.CollisionRadius", 2.5);
        ConfigManager.getConfig().addDefault("ExtraAbilities.Aceliip.Pressure.Damage", 10);
        ConfigManager.defaultConfig.save();
        ProjectKorra.plugin.getServer().getLogger().info(this.getName() + " " + this.getVersion() + " Developed by " + this.getAuthor() + " has been enabled!");
        this.perm = new Permission("bending.ability.Pressure");
        ProjectKorra.plugin.getServer().getPluginManager().addPermission(this.perm);
        this.perm.setDefault(PermissionDefault.TRUE);
        ProjectKorra.plugin.getServer().getPluginManager().registerEvents(new PressureListener(), ProjectKorra.plugin);
    }

    public void stop() {
        ProjectKorra.plugin.getServer().getLogger().info(this.getName() + " " + this.getVersion() + " Developed by " + this.getAuthor() + " has been disabled!");
        ProjectKorra.plugin.getServer().getPluginManager().removePermission(this.perm);
        super.remove();
    }
}

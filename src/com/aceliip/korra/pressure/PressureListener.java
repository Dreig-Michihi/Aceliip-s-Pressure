//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.aceliip.korra.pressure;

import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PressureListener implements Listener {
    public PressureListener() {
    }

    @EventHandler
    public void onSwing(PlayerToggleSneakEvent event) {
        if (!event.isCancelled()) {
            if (!CoreAbility.hasAbility(event.getPlayer(), Pressure.class)) {
                new Pressure(event.getPlayer());
            }
        }
    }
}

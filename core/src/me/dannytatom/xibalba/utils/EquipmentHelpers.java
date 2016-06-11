package me.dannytatom.xibalba.utils;

import com.badlogic.ashley.core.Entity;
import me.dannytatom.xibalba.Main;
import me.dannytatom.xibalba.components.EquipmentComponent;
import me.dannytatom.xibalba.components.ItemComponent;

public class EquipmentHelpers {
    private final Main main;

    public EquipmentHelpers(Main main) {
        this.main = main;
    }

    public Entity getWeapon() {
        return main.player.getComponent(EquipmentComponent.class).rightHand;
    }

    /**
     * Get the amount of damage their primary weapon does.
     *
     * @return Amount of damage
     */
    public int getWeaponDamage() {
        Entity weapon = getWeapon();

        if (weapon != null) {
            return weapon.getComponent(ItemComponent.class).attributes.get("damage");
        } else {
            return 0;
        }
    }
}

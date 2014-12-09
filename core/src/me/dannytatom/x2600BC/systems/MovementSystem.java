package me.dannytatom.x2600BC.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import me.dannytatom.x2600BC.Constants;
import me.dannytatom.x2600BC.Mappers;
import me.dannytatom.x2600BC.MoveAction;
import me.dannytatom.x2600BC.components.AttributesComponent;
import me.dannytatom.x2600BC.components.PositionComponent;

public class MovementSystem extends IteratingSystem {
    int[][] geometry;

    public MovementSystem(int[][] geometry) {
        super(Family.all(PositionComponent.class).get());

        this.geometry = geometry;
    }

    public void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = Mappers.position.get(entity);
        AttributesComponent attributes = Mappers.attributes.get(entity);

        if ((attributes.actions.indexOf("move") > -1) && (attributes.speed >= MoveAction.COST) && (position.moveDir != null)) {
            switch (position.moveDir) {
                case "N":
                    if (geometry[position.x][position.y + 1] != Constants.WALL) {
                        position.y += 1;
                    }

                    break;
                case "E":
                    if (geometry[position.x + 1][position.y] != Constants.WALL) {
                        position.x += 1;
                    }

                    break;
                case "S":
                    if (geometry[position.x][position.y - 1] != Constants.WALL) {
                        position.y -= 1;
                    }

                    break;
                case "W":
                    if (geometry[position.x - 1][position.y] != Constants.WALL) {
                        position.x -= 1;
                    }

                    break;
            }

            attributes.actions.remove(attributes.actions.indexOf("move"));
            attributes.speed -= MoveAction.COST;
            position.moveDir = null;
        }
    }
}

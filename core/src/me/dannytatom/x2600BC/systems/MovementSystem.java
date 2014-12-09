package me.dannytatom.x2600BC.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import me.dannytatom.x2600BC.Constants;
import me.dannytatom.x2600BC.Mappers;
import me.dannytatom.x2600BC.MoveAction;
import me.dannytatom.x2600BC.components.AttributesComponent;
import me.dannytatom.x2600BC.components.PositionComponent;

import java.util.Objects;

public class MovementSystem extends IteratingSystem {
    private int[][] map;

    public MovementSystem(int[][] map) {
        super(Family.all(PositionComponent.class).get());

        this.map = map;
    }

    public void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = Mappers.position.get(entity);
        AttributesComponent attributes = Mappers.attributes.get(entity);

        if (attributes.actions.indexOf("move") > -1 && attributes.speed >= MoveAction.COST) {
            if (position.moveN) {
                if (map[position.x][position.y + 1] != Constants.WALL_TOP && map[position.x][position.y + 1] != Constants.WALL_FRONT) {
                    position.y += 1;
                }
            }

            if (position.moveS) {
                if (map[position.x][position.y - 1] != Constants.WALL_TOP && map[position.x][position.y - 1] != Constants.WALL_FRONT) {
                    position.y -= 1;
                }
            }

            if (position.moveW) {
                if (map[position.x - 1][position.y] != Constants.WALL_TOP && map[position.x - 1][position.y] != Constants.WALL_FRONT) {
                    position.x -= 1;
                }
            }

            if (position.moveE) {
                if (map[position.x + 1][position.y] != Constants.WALL_TOP && map[position.x + 1][position.y] != Constants.WALL_FRONT) {
                    position.x += 1;
                }
            }

            attributes.actions.remove(attributes.actions.indexOf("move"));
            attributes.speed -= MoveAction.COST;
        }
    }
}

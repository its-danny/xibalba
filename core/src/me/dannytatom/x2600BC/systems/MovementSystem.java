package me.dannytatom.x2600BC.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import me.dannytatom.x2600BC.Constants;
import me.dannytatom.x2600BC.Mappers;
import me.dannytatom.x2600BC.components.PositionComponent;

public class MovementSystem extends IteratingSystem {
    private int[][] map;

    public MovementSystem(int[][] map) {
        super(Family.all(PositionComponent.class).get());

        this.map = map;
    }

    public void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = Mappers.position.get(entity);

        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            if (map[position.x][position.y + 1] != Constants.WALL) {
                position.y += 1;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            if (map[position.x][position.y - 1] != Constants.WALL) {
                position.y -= 1;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            if (map[position.x - 1][position.y] != Constants.WALL) {
                position.x -= 1;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            if (map[position.x + 1][position.y] != Constants.WALL) {
                position.x += 1;
            }
        }
    }
}

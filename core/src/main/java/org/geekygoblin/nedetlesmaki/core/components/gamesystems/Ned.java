/*
 * Copyright © 2013, Pierre Marijon <pierre@marijon.fr>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * The Software is provided "as is", without warranty of any kind, express or 
 * implied, including but not limited to the warranties of merchantability, 
 * fitness for a particular purpose and noninfringement. In no event shall the 
 * authors or copyright holders X be liable for any claim, damages or other 
 * liability, whether in an action of contract, tort or otherwise, arising from,
 * out of or in connection with the software or the use or other dealings in the
 * Software.
 */
package org.geekygoblin.nedetlesmaki.core.components.gamesystems;

import com.artemis.Entity;
import im.bci.jnuit.animation.IAnimationCollection;
import im.bci.jnuit.animation.PlayMode;
import im.bci.jnuit.artemis.sprite.Sprite;
import im.bci.jnuit.artemis.sprite.SpritePuppetControls;
import org.geekygoblin.nedetlesmaki.core.IAssets;
import org.geekygoblin.nedetlesmaki.core.backend.LevelIndex;
import org.geekygoblin.nedetlesmaki.core.backend.Position;
import org.geekygoblin.nedetlesmaki.core.backend.PositionIndexed;
import org.geekygoblin.nedetlesmaki.core.constants.AnimationTime;
import pythagoras.f.Vector3;

/**
 *
 * @author pierre
 */
public class Ned extends GameObject {

    enum MoveType {

        WALK,
        PUSH,
        FLY,
    }

    private IAnimationCollection animation;

    public Ned(PositionIndexed pos, Entity entity, LevelIndex index, IAssets assets) {
        super(pos, entity, index);
        this.animation = assets.getAnimations("ned.json");
    }

    @Override
    public Position moveTo(Position diff) {
        Position n_pos = Position.sum(this.pos, diff);
        GameObject n_obj = this.index.getGameObject(n_pos);

        if (n_obj == null) {
            this.pos.setPosition(n_pos);
            this.run_animation(diff, MoveType.WALK);
            return this.pos;
        } else {
            Position n_move_to = n_obj.moveTo(diff);
            if (!n_move_to.equals(n_pos)) {
                if (n_obj instanceof GreenMaki) {
                    this.pos.setPosition(n_pos);
                    this.run_animation(diff, MoveType.PUSH);
                    return this.pos;
                }
                if (n_obj instanceof OrangeMaki) {
                    this.pos.setPosition(Position.deduction(n_move_to, diff));
                    this.run_animation(diff, MoveType.PUSH);
                    return this.pos;
                }
            }
        }

        return this.pos;
    }

    @Override
    public void save(Memento m
    ) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Memento undo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    private void run_animation(Position diff, MoveType type) {
        Sprite sprite = this.entity.getComponent(Sprite.class
        );
        SpritePuppetControls updatable = this.entity.getComponent(SpritePuppetControls.class);

        if (updatable
                == null) {
            updatable = new SpritePuppetControls(sprite);
        }

        if (type == MoveType.WALK) {
            if (diff.equals(Position.getUp())) {
                updatable.startAnimation(this.animation.getAnimationByName("walk_up"), PlayMode.ONCE)
                        .moveToRelative(new Vector3(diff.getY(), diff.getX(), 0), AnimationTime.base);
            } else if (diff.equals(Position.getDown())) {
                updatable.startAnimation(this.animation.getAnimationByName("walk_down"), PlayMode.ONCE)
                        .moveToRelative(new Vector3(diff.getY(), diff.getX(), 0), AnimationTime.base);
            } else if (diff.equals(Position.getRight())) {
                updatable.startAnimation(this.animation.getAnimationByName("walk_right"), PlayMode.ONCE)
                        .moveToRelative(new Vector3(diff.getY(), diff.getX(), 0), AnimationTime.base);
            } else if (diff.equals(Position.getLeft())) {
                updatable.startAnimation(this.animation.getAnimationByName("walk_left"), PlayMode.ONCE)
                        .moveToRelative(new Vector3(diff.getY(), diff.getX(), 0), AnimationTime.base);
            }
        }

        this.entity.addComponent(updatable);

        this.entity.changedInWorld();
    }
}
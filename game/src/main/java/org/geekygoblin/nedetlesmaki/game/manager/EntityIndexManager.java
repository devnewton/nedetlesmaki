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
package org.geekygoblin.nedetlesmaki.game.manager;

import java.util.Stack;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.artemis.Entity;
import com.artemis.EntityManager;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Mapper;

import org.geekygoblin.nedetlesmaki.game.NamedEntities;
import org.geekygoblin.nedetlesmaki.game.Game;
import org.geekygoblin.nedetlesmaki.game.components.gamesystems.Position;
import org.geekygoblin.nedetlesmaki.game.components.gamesystems.Square;
/**
 *
 * @author natir
 */
@Singleton
public class EntityIndexManager extends EntityManager {
    
    private Square[][] index;
    private Stack<Square[][]> oldIndex;
    
    @Mapper
    ComponentMapper<Position> positionMapper;

    @Inject
    public EntityIndexManager() {
	super();
	this.index = new Square[15][15];
	this.oldIndex = new Stack();
    }

    @Override
    public void added(Entity e) {
	Position p = e.getComponent(Position.class);
	
	if(p != null) {
	    this.index[p.getX()][p.getY()] = new Square(e);
	    super.added(e);
	}
    }

    @Override
    public void deleted(Entity e) {
	Position p = e.getComponent(Position.class);
	
	if(p != null) {
	    this.index[p.getX()][p.getY()].setEntity(null);
	    super.deleted(e);
    	}
    }

     public Entity getEntity(int x, int y) {

	 Square test = this.getSquare(x, y);

	 if(test != null) { return test.getEntity(); }
	 else { return null; }
     }

     public Square getSquare(int x, int y) {
	 int trueX = x, trueY = y;
	 
	 if(x >= 14) { trueX = 14; }
	 if(x <= 0) { trueX = 0; }
	 if(y >= 14) { trueY = 14; }
	 if(y <= 0) { trueY = 0; }
	 
	 Square test = index[trueX][trueY];

	 if(test != null) { return test; }
	 else { return null; }
     }

    public boolean moveEntity(int x, int y, int x2, int y2) {
	Entity tmpE = index[x][y].getEntity();

	Square newC = this.index[x2][y2];
	
	if(newC != null) {
	    this.index[x2][y2].setEntity(tmpE);
	}
	else {
	    this.index[x2][y2] = new Square(tmpE);
	}

	this.index[x][y].setEntity(null);

	return true;
    }

    public boolean saveWorld() {
	Square[][] clone = new Square[15][15];
	for(int i = 0; i != 15; i++) {
	    for(int j = 0; j != 15; j++) {
		Square e = new Square(this.index[i][j]);
		
		clone[i][j] = e;
	    }
	}

	this.oldIndex.push(clone);
        return true;
    }
    
    public void cleanStack() {
	this.oldIndex.clear();
    }

    public int sizeOfStack() {
	return this.oldIndex.size();
    }

    public Square[][] getLastWorld() {
	if(!this.oldIndex.empty()) {
	    Square[][] o = this.oldIndex.peek();
	    if(o != null) {
		return this.oldIndex.peek();
	    }
	}

	return null;
    }

    public Square[][] getThisWorld() {
	return this.index;
    }
}

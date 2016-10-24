package com.uja.game.objectUtilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.uja.game.gameObjects.GameObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A QuadTree is a spatial data structure which allows for more efficient querying of neighbours.
 * Created by jonathanalp on 2016/09/05.
 */

public class QuadTree {

    public static final int MAX_ITEMS = 10;
    public static final int MAX_HEIGHT = 5;
    public static final int UPDATES_BEFORE_REFRESH = 200;
    public static final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private int height, updates;
    private List<GameObject> objects;
    private Rectangle bounds;
    private QuadTree[] nodes;

    public QuadTree(float x, float y, float width, float height) {
        this(0, new Rectangle(x, y, width, height));
    }

    public QuadTree(int height, Rectangle bounds) {
        this.height = height;
        this.bounds = bounds;
        objects = new ArrayList<GameObject>();
        nodes = new QuadTree[4];
        updates = 0;
    }

    public void clear() {
        objects.clear();

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    private boolean hasObject() {
        return objects.size() > 0;
    }

    private void clearEmptySplits() {
        if (nodes[0] != null) {
            // No children have objects
            if (!nodes[0].hasObject() && !nodes[1].hasObject() && !nodes[2].hasObject() && !nodes[3].hasObject())
                for (int i = 0; i < nodes.length; i++)
                    nodes[i] = null;

            else
                for (int i = 0; i < nodes.length; i++)
                    nodes[i].clearEmptySplits();
        }
    }

    private void split() {
        int subWidth = (int) (bounds.getWidth() / 2);
        int subHeight = (int) (bounds.getHeight() / 2);
        int x = (int) bounds.getX();
        int y = (int) bounds.getY();

        nodes[0] = new QuadTree(height + 1, new Rectangle(x + subWidth, y, subWidth, subHeight));
        nodes[1] = new QuadTree(height + 1, new Rectangle(x, y, subWidth, subHeight));
        nodes[2] = new QuadTree(height + 1, new Rectangle(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new QuadTree(height + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    private int getIndex(double x, double y, double width, double height) {
        int index = -1;
        double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
        double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

        // Object can completely fit within the top quadrants
        boolean topQuadrant = (y < horizontalMidpoint && y + height < horizontalMidpoint);
        // Object can completely fit within the bottom quadrants
        boolean bottomQuadrant = (y > horizontalMidpoint);

        // Object can completely fit within the left quadrants
        if (x < verticalMidpoint && x + width < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        }
        // Object can completely fit within the right quadrants
        else if (x > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    // Return index that an object should be placed into (-1 means parent)
    private int getIndex(GameObject object) {
        return getIndex(object.getX(), object.getY(), object.getWidth(), object.getHeight());
    }

    public void add(GameObject object) {
        // Find where to place it
        if (nodes[0] != null) {
            int index = getIndex(object);

            if (index != -1) {
                nodes[index].add(object);
                return;
            }
        }

        objects.add(object);

        // We should split if the following is true
        if (objects.size() > MAX_ITEMS && height < MAX_HEIGHT) {
            if (nodes[0] == null) {
                split();
            }

            // Re-place entities
            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1)
                    nodes[index].add(objects.remove(i));
                else i++;
            }
        }
    }

    public void remove(GameObject object) {
        if (objects.contains(object)) {
            objects.remove(object);
            return;
        }

        if (nodes[0] != null)
            for (QuadTree node : nodes) node.remove(object);
    }

    public void update(GameObject object) {
        remove(object);
        add(object);
        if (++updates >= UPDATES_BEFORE_REFRESH) {
            updates = 0;
            clearEmptySplits();
        }
    }

    // Retrieve colliding entities
    public List retrieve(List returnObjects, GameObject object) {
        int index = getIndex(object);
        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(returnObjects, object);
        }

        returnObjects.addAll(objects);

        return returnObjects;
    }

    // Same as before, just with x, y, width, height
    public List retrieve(List returnObjects, double x, double y, double width, double height) {
        int index = getIndex(x, y, width, height);
        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(returnObjects, x, y, width, height);
        }

        returnObjects.addAll(objects);

        return returnObjects;
    }

    public void renderDebug(SpriteBatch sb) {
        sb.end();
        Gdx.gl.glEnable(GL30.GL_BLEND);
        shapeRenderer.setProjectionMatrix(sb.getProjectionMatrix());

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL30.GL_BLEND);
        sb.begin();

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].renderDebug(sb);
            }
        }
    }

}

package com.uja.game.gameObjects.puzzleObjects.characters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.uja.game.gameObjects.GameObject;
import com.uja.game.gameObjects.puzzleObjects.triggering.Triggerable;
import com.uja.game.objectUtilities.QuadTree;
import com.uja.game.objectUtilities.TextureManager;
import com.uja.game.objectUtilities.TileMap;
import com.uja.game.objectUtilities.pathing.Path;
import com.uja.game.objectUtilities.pathing.VirtualNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * A Guard is a Triggerable Entity that can exhibit intelligent behaviour in following the player.
 * Created by jonathanalp on 2016/09/05.
 */

public class Guard extends Entity implements Triggerable {

    private static final String[] textures = new String[]
            {"levels/guard/forward1.png", "levels/guard/forward2.png", "levels/guard/forward3.png",
                    "levels/guard/left1.png", "levels/guard/left2.png", "levels/guard/left3.png",
                    "levels/guard/right1.png", "levels/guard/right2.png", "levels/guard/right3.png",
                    "levels/guard/backward1.png", "levels/guard/backward2.png", "levels/guard/backward3.png",
                    "levels/guard/dead.png"};
    private static float magicFactor = 3;

    private float speed;
    private Algorithm algorithm;

    private List<Integer> ids;
    private boolean triggered;

    private Path path;
    private TileMap tileMap;
    private VirtualNode currentTile;

    private List<Rectangle> debugRectangles;
    private List<Rectangle> startAndEndRectangles;

    public Guard(float x, float y, float speed, Algorithm algorithm, List<Integer> ids, TileMap tileMap) {
        this(x, y, 26, 26, new TextureManager(textures, true), speed, algorithm, ids, tileMap);
    }

    public Guard(float x, float y, int width, int height, TextureManager textureManager, float speed, Algorithm algorithm, List<Integer> ids, TileMap tileMap) {
        super(x, y, width, height, textureManager);
        this.speed = speed;
        this.algorithm = algorithm;
        this.ids = ids;
        triggered = false;
        path = new Path();
        textureManager.switchTexture(1);
        this.tileMap = tileMap;
        currentTile = null;

        debugRectangles = new ArrayList<Rectangle>();
        startAndEndRectangles = new ArrayList<Rectangle>();
    }

    public static Algorithm convertStringToAlgorithm(String algorithm) {
        return Algorithm.valueOf(algorithm);
    }

    public void update(float dt, Player player, QuadTree collisionTree) {
        // Perform appropriate algorithm
        if (!triggered) return;

        switch (algorithm) {
            case LineOfSight:
                lineOfSight(dt, player);
                break;
            case Predictive:
                predictive(dt, player);
                break;
            case AStar:
                aStar(dt, player, collisionTree);
                break;
        }

        right = dx > 0;
        left = dx < 0;
        up = dy > 0;
        down = dy < 0;

        super.update(dt);
    }

    // Try to move directly to where the player is.
    public void lineOfSight(float dt, Player player) {
        pointAndGoTowards(dt, player.getX(), player.getY());
    }

    public void predictive(float dt, Player player) {
        float closingSpeed = Math.abs(player.speed - speed);
        float closingTime = (float) (Math.hypot(x - player.getX(), y - player.getY()) / closingSpeed);

        float predictedX = player.getX() + player.dx * closingTime / dt;
        float predictedY = player.getY() + player.dy * closingTime / dt;

        pointAndGoTowards(dt, predictedX, predictedY);
    }

    public void aStar(float dt, Player player, QuadTree collisionTree) {
        Integer[] ourBlock = tileMap.positionToTileCoordinates(x + width / 2, y + height / 2);
        Integer[] playerBlock = tileMap.positionToTileCoordinates(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2);
        startAndEndRectangles.add(new Rectangle(ourBlock[0] * tileMap.getTileWidth(), ourBlock[1] * tileMap.getTileHeight(), tileMap.getTileWidth(), tileMap.getTileHeight()));
        startAndEndRectangles.add(new Rectangle(playerBlock[0] * tileMap.getTileWidth(), playerBlock[1] * tileMap.getTileHeight(), tileMap.getTileWidth(), tileMap.getTileHeight()));

        VirtualNode startNode = new VirtualNode(ourBlock[0], ourBlock[1], 0, heuristicCost(player));
        VirtualNode endNode = new VirtualNode(playerBlock[0], playerBlock[1], -1, -1);

        Set<VirtualNode> visitedNodes = new HashSet<VirtualNode>();
        PriorityQueue<Path> paths = new PriorityQueue<Path>();
        paths.add(new Path(startNode));

        while (!paths.isEmpty()) {
            Path currentPath = paths.poll();
            VirtualNode currentNode = currentPath.currentNode();

            if (visitedNodes.contains(currentNode)) continue;
            visitedNodes.add(currentNode);

            if (currentNode.equals(endNode)) {
                if (currentPath.getPath().size() > 1) {

                    VirtualNode headingTo;

                    if (tileMap.fullyEnclosedInTile(this) || (currentTile != null && currentTile.equals(currentPath.getPath().getFirst()))) {
                        currentTile = currentPath.getPath().getFirst();
                        headingTo = currentPath.secondNode();
                    } else headingTo = currentPath.getPath().getFirst();

                    this.path = currentPath;

                    pointAndGoTowards(dt, headingTo.getX() * tileMap.getTileWidth() + magicFactor, headingTo.getY() * tileMap.getTileHeight() + magicFactor);
                    return;
                } else break;
            }

            for (int xDiff = -1; xDiff <= 1; xDiff++) {
                for (int yDiff = -1; yDiff <= 1; yDiff++) {
                    if (xDiff == 0 && yDiff == 0) continue;
                    VirtualNode possibleNeighbour = new VirtualNode(currentNode.getX() + xDiff, currentNode.getY() + yDiff, currentNode.cost() + Math.hypot(xDiff, yDiff), heuristicCost(player));

                    // Check collisions
                    float newTileX = (float) (possibleNeighbour.getX() * tileMap.getTileWidth() + magicFactor);
                    float newTileY = (float) (possibleNeighbour.getY() * tileMap.getTileHeight() + magicFactor);

                    float midPointX = (currentNode.getX() * tileMap.getTileWidth() + magicFactor + newTileX) / 2;
                    float midPointY = (currentNode.getY() * tileMap.getTileHeight() + magicFactor + newTileY) / 2;

                    if (tileMap.validTile(possibleNeighbour.getY(), possibleNeighbour.getX())
                            && !checkNeighbours(collisionTree, player, newTileX, newTileY)
                            && !checkNeighbours(collisionTree, player, midPointX, midPointY)) {
                        Path newPath = new Path(currentPath);
                        newPath.addNode(possibleNeighbour);
                        paths.add(newPath);
                    }

                    // Debug Code for nice debug outout
                    if (!tileMap.validTile(possibleNeighbour.getY(), possibleNeighbour.getX()))
                        debugRectangles.add(new Rectangle(newTileX - magicFactor, newTileY - magicFactor, tileMap.getTileWidth(), tileMap.getTileHeight()));
                    if (checkNeighbours(collisionTree, player, newTileX, newTileY))
                        debugRectangles.add(new Rectangle(newTileX, newTileY, width, height));
                    if (checkNeighbours(collisionTree, player, midPointX, midPointY))
                        debugRectangles.add(new Rectangle(midPointX, midPointY, width, height));
                }
            }
        }

        lineOfSight(dt, player);
    }

    private boolean checkNeighbours(QuadTree collisionTree, Player player, float newX, float newY) {
        // Spatial Structure
        List<GameObject> neighbours = new ArrayList<GameObject>();
        collisionTree.retrieve(neighbours, newX, newY, width, height);
        for (GameObject neighbour : neighbours) {
            if (neighbour == this || neighbour == player) continue;
            // Bounding Shape
            if (newX < neighbour.getX() + neighbour.getWidth() && neighbour.getX() < newX + width &&
                    newY < neighbour.getY() + neighbour.getHeight() && neighbour.getY() < newY + height) {
                // Pixel Level Test
                if (neighbour.pixelCollision(this, newX, newY)) return true;
            }
        }
        return false;
    }

    // Simple heuristic of direct distance to player
    private double heuristicCost(Player player) {
        return Math.hypot(player.getX() - x, player.getY() - y);
    }

    private void pointAndGoTowards(float dt, float goToX, float goToY) {
        float diffX = goToX - x;
        float diffY = goToY - y;

        float theta = (float) Math.atan2(diffY, diffX);
        float distance = dt * speed;

        dx = (float) (distance * Math.cos(theta));
        dy = (float) (distance * Math.sin(theta));
    }

    @Override
    public void renderDebug(SpriteBatch sb) {
        super.renderDebug(sb);

        sb.end();
        int sign = 1;
        float startX = x + width / 2;
        float startY = y + height / 2;
        for (VirtualNode node : path.getPath()) {
            float newX = tileMap.convertToMidpointX(node.getX());
            float newY = tileMap.convertToMidpointY(node.getY());

            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.line(startX - 1 * sign, startY - 1 * sign, newX + 1 * sign, newY + 1 * sign);
            shapeRenderer.end();

            sign *= -1;
            startX = newX;
            startY = newY;
        }
        for (Rectangle rectangle : debugRectangles) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            shapeRenderer.end();
        }
        debugRectangles.clear();
        for (Rectangle rectangle : startAndEndRectangles) {
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            shapeRenderer.end();
        }
        startAndEndRectangles.clear();
        sb.begin();
    }

    @Override
    public void trigger(int id) {
        if (ids.contains(id)) triggered = true;
    }

    public enum Algorithm {LineOfSight, Predictive, AStar}

}

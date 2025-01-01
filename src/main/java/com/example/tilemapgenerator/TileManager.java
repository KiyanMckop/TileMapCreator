package com.example.tilemapgenerator;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;


public class TileManager {

    public Image tileset;
    public int tileSize;

    public TileManager(int tileSize) {
        this.tileSize = tileSize;
    }

    public WritableImage[][] splitTileMap() {
        int rows = (int) tileset.getHeight() / tileSize;
        int cols = (int) tileset.getWidth() / tileSize;

        WritableImage[][] tiles = new WritableImage[rows][cols];

        // Slice the tileset into individual tiles
        PixelReader reader = tileset.getPixelReader();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                tiles[row][col] = new WritableImage(
                        reader,
                        col * tileSize,
                        row * tileSize,
                        tileSize,
                        tileSize
                );
            }
        }
        return tiles;
    }

    public void setTileset(String path){
        this.tileset = new Image(path);
    }

}

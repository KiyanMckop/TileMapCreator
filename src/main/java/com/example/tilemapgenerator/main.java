package com.example.tilemapgenerator;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class main implements Initializable {


    //components
    public ImageView ivPreview;
    public ListView<Label> lvAssetContainer;
    public Slider areaSlider;
    public CheckBox cbShowGridLines;
    public Canvas canTileMapImport;
    public Canvas canMain;
    public Button btnTileMapImport;

    public File imageDir;

    public ListView lvLayers;
    public StackPane stackLayers;
    List<int[][]> IndexMaps = new ArrayList<>();
    int areaSize = 0;
    List<WritableImage> drawImages;
    WritableImage[][] allImages;
    WritableImage[] allImagesFlattened;

    TileManager tm = new TileManager(16);
    FileDialog fileDialog = new FileDialog();
    LayersManger layersManger = new LayersManger();
    AutoTiling autoTiler = new AutoTiling();


    public void addFiles(ActionEvent actionEvent) {

        populateAssetContainer(lvAssetContainer);

    }

    public void populateAssetContainer(ListView<Label> assetContainer){

        fileDialog.setDirectory(fileDialog.chooseDirectory());
        File[] allFiles = fileDialog.getFilesFromFolder();

        if (allFiles != null) {
            for (File file : allFiles) {
                Label fileLabel = new Label(file.getName());
                fileLabel.setPrefWidth(assetContainer.getPrefWidth());
                fileLabel.setOnMouseClicked(e ->{
                    imageDir = new File(fileDialog.directory + "\\" + fileLabel.getText());
                    try {
                        Image image = new Image(imageDir.toURI().toURL().toString());
                        ivPreview.setImage(image);
                    } catch (MalformedURLException ex) {
                        throw new RuntimeException(ex);
                    }

                });
                assetContainer.getItems().add(fileLabel);
            }
        }

    }

    public void showGridLines(ActionEvent actionEvent) {
        if (cbShowGridLines.isSelected()){
            drawCanvasGrid(canMain, tm.tileSize);
        }else {
            int width = (int)canMain.getWidth();
            int height = (int)canMain.getHeight();
            canMain.getGraphicsContext2D().clearRect(0, 0, width, height);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //checkbox setup
        cbShowGridLines.setSelected(true);

        //area slider setup
        areaSlider.setMin(0);
        areaSlider.setMax(3);
        areaSlider.setValue(0);
        areaSlider.setBlockIncrement(1);
        areaSlider.setShowTickMarks(true);
        areaSlider.setShowTickLabels(true);
        //area slider event listener
        areaSlider.valueProperty().addListener((observableValue, number, t1) -> {
            this.areaSize = t1.intValue();
        });


        lvLayers.setOnMouseClicked(e ->{
            canMain = layersManger.getSelectedCanvas(stackLayers, lvLayers);

            canMain.setOnMouseClicked(event ->{
                Tile tile = new Tile();
                tile.x = (int)event.getX()/ tm.tileSize;
                tile.y = (int)event.getY()/ tm.tileSize;
                clearGirdTile(canMain, tile);

                int imageIndex = getEightDirectionBinary(tile);
                populateIndex(imageIndex + 1, tile);

                paintImage(this.allImagesFlattened[imageIndex], tile, canMain);
                updateEightBitNeighbours(tile);

            });

            canMain.setOnDragDetected(event -> canMain.startFullDrag());

            canMain.setOnMouseDragOver(event ->{
                Tile tile = new Tile();
                tile.x = (int)event.getX()/ tm.tileSize;
                tile.y = (int)event.getY()/ tm.tileSize;
                clearGirdTile(canMain, tile);

                int imageIndex = getEightDirectionBinary(tile);
                populateIndex(imageIndex + 1, tile);
                paintImage(this.allImagesFlattened[imageIndex], tile, canMain);

                updateEightBitNeighbours(tile);
            });


            canTileMapImport.setOnMouseClicked(event -> {
                this.drawImages = selectTileMapTile(event, this.areaSize);
            });
        });
    }

    public int getNeighbours(Tile tile){
        Canvas canvas = layersManger.getSelectedCanvas(stackLayers, lvLayers);
        int indexMapNum = stackLayers.getChildren().indexOf(canvas);

        byte north = 0, south = 0, east = 0, west = 0;

        if (IndexMaps.get(indexMapNum)[tile.x][tile.y - 1]  -1 > -1) {
            north = 1;
        }
        if (IndexMaps.get(indexMapNum)[tile.x][tile.y + 1]  -1 > -1) {
            south = 1;
        }
        if (IndexMaps.get(indexMapNum)[tile.x + 1][tile.y] -1 > -1) {
            east = 1;
        }
        if (IndexMaps.get(indexMapNum)[tile.x - 1][tile.y] -1 > -1) {
            west = 1;
        }

        return autoTiler.getBinaryValue(north, south, east, west);
    }

    public int getEightDirectionBinary(Tile tile){
        Canvas canvas = layersManger.getSelectedCanvas(stackLayers, lvLayers);
        int indexMapNum = stackLayers.getChildren().indexOf(canvas);

        byte north = 0, south = 0, east = 0, west = 0;
        byte northEast = 0, northWest = 0, southEast = 0, southWest = 0;


        if (IndexMaps.get(indexMapNum)[tile.x][tile.y - 1]  -1 > -1) {
            north = 1;
        }
        if (IndexMaps.get(indexMapNum)[tile.x][tile.y + 1]  -1 > -1) {
            south = 1;
        }
        if (IndexMaps.get(indexMapNum)[tile.x + 1][tile.y] -1 > -1) {
            east = 1;
        }
        if (IndexMaps.get(indexMapNum)[tile.x - 1][tile.y] -1 > -1) {
            west = 1;
        }

        if (IndexMaps.get(indexMapNum)[tile.x + 1][tile.y - 1]  -1 > -1){
            northEast = (byte) ((north == 1 && east == 1) ? 1 : 0);

        }
        if (IndexMaps.get(indexMapNum)[tile.x - 1][tile.y - 1]  -1 > -1){
            northWest = (byte) ((north == 1 && west == 1) ? 1 : 0);

        }
        if (IndexMaps.get(indexMapNum)[tile.x + 1][tile.y + 1]  -1 > -1){
            southEast = (byte) ((south == 1 && east == 1) ? 1 : 0);
        }
        if (IndexMaps.get(indexMapNum)[tile.x - 1][tile.y + 1]  -1 > -1){
            southWest = (byte) ((south == 1 && west == 1) ? 1 : 0);
        }

        return autoTiler.getEightBitDirectional(north, northEast, northWest, south,
                southEast, southWest, east, west);

    }


    public void updateFourBitNeighbours(Tile tile){
        List<Tile> surroundingTiles = getGridAreaRange(tile.x,
                tile.y,
                (int)canTileMapImport.getWidth(),
                (int)canTileMapImport.getHeight(),
                1);

        Canvas canvas = layersManger.getSelectedCanvas(stackLayers, lvLayers);
        int indexMapNum = stackLayers.getChildren().indexOf(canvas);

        for (Tile cell: surroundingTiles){
            if (IndexMaps.get(indexMapNum)[cell.x][cell.y] - 1> -1) {
                int imageIndex = getNeighbours(cell);
                populateIndex(imageIndex + 1, cell);
                paintImage(this.allImagesFlattened[imageIndex], cell, canMain);
            }
        }
    }

    public void updateEightBitNeighbours(Tile tile){
        List<Tile> surroundingTiles = getGridAreaRange(tile.x,
                tile.y,
                (int)canTileMapImport.getWidth(),
                (int)canTileMapImport.getHeight(),
                1);

        Canvas canvas = layersManger.getSelectedCanvas(stackLayers, lvLayers);
        int indexMapNum = stackLayers.getChildren().indexOf(canvas);

        for (Tile cell: surroundingTiles){
            if (IndexMaps.get(indexMapNum)[cell.x][cell.y] - 1> -1) {
                int imageIndex = getEightDirectionBinary(cell);
                populateIndex(imageIndex + 1, cell);
                paintImage(this.allImagesFlattened[imageIndex], cell, canMain);
            }
        }
    }

    public void clearImportList(ActionEvent actionEvent) {
        lvAssetContainer.getItems().clear();
    }

    private List<Tile> getGridAreaRange(int centerCol, int centerRow, int gridWidth, int gridHeight, int areaSize) {
        List<Tile> neighbors = new ArrayList<>();

        // Loop through all possible neighbors within the given size
        for (int dr = -areaSize; dr <= areaSize; dr++) {
            for (int dc = -areaSize; dc <= areaSize; dc++) {

                int newRow = centerRow + dr;
                int newCol = centerCol + dc;

                // Check boundaries using grid size
                if (newRow >= 0 && newRow < gridHeight && newCol >= 0 && newCol < gridWidth) {
                    // Create a new array for each neighbor
                    Tile tile = new Tile();
                    tile.x = newCol;
                    tile.y = newRow;
                    neighbors.add(tile);
                }
            }
        }

        return neighbors;
    }

    public List<WritableImage> selectTileMapTile(MouseEvent e, int areaSize){
        int row = (int) e.getX() / tm.tileSize;
        int col = (int) e.getY() / tm.tileSize;
        GraphicsContext g2 = canTileMapImport.getGraphicsContext2D();


        List<WritableImage> allTiles = new ArrayList<>();

        List<Tile> gridCells = getGridAreaRange(row,
                col,
                (int)canTileMapImport.getWidth(),
                (int)canTileMapImport.getHeight(),
                areaSize);

        for (Tile tile : gridCells){

            WritableImage tileImage = getSelectedTile(tile.y, tile.x);
            if (tileImage != null){
                allTiles.add(tileImage);
            }

        }

        g2.setLineWidth(1);
        g2.setStroke(Color.BLUEVIOLET);
        int x = gridCells.getFirst().x * tm.tileSize;
        int y = gridCells.getFirst().y * tm.tileSize;

        int width = (gridCells.getLast().x - gridCells.getFirst().x + 1) * tm.tileSize;
        int height = (gridCells.getLast().y - gridCells.getFirst().y + 1) * tm.tileSize;

        g2.strokeRect(x, y, width, height);

        return allTiles;
    }

    public List<Tile> getTilesOnDrag(MouseEvent e){

        List<Tile> allTiles = new ArrayList<>();
        Tile tile = new Tile();

        tile.x = (int) e.getX()/ tm.tileSize;
        tile.y = (int) e.getY()/tm.tileSize;



        allTiles.add(tile);

        if ((int)e.getX()/ tm.tileSize != tile.x && (int)e.getY()/ tm.tileSize != tile.y){
            tile.x = (int) e.getX()/ tm.tileSize;
            tile.y = (int) e.getY()/tm.tileSize;
            allTiles.add(tile);
        }

        return allTiles;

    }

    public void exportMap(ActionEvent actionEvent ) throws IOException {

        ExportMap exporter = new ExportMap(IndexMaps);
        exporter.exportSingleTextFile();

        //message for user
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setContentText("done");
        alert.showAndWait();

    }

    public void importTileMap(ActionEvent actionEvent) {

        int gridWidth = (int) canTileMapImport.getWidth();
        int gridHeight = (int) canTileMapImport.getHeight();

        tm.tileSize = 16;
        tm.setTileset(new File(fileDialog.chooseFile()).toURI().toString());
        this.allImages = tm.splitTileMap();
        this.allImagesFlattened = new WritableImage[allImages.length * allImages[0].length];

        GraphicsContext gc = canTileMapImport.getGraphicsContext2D();

        int x = 0;

        for (int i = 0; i < allImages.length; i++){
            for (int j = 0; j < allImages[i].length; j++){
                gc.drawImage(allImages[i][j], j*tm.tileSize, i*tm.tileSize);
                this.allImagesFlattened[x] = allImages[i][j];
                x++;
            }
        }
    }

    public WritableImage getSelectedTile(int col, int row){
        if (tm.tileset != null){
            this.allImages = tm.splitTileMap();
            try {
                return allImages[col][row];
            }catch (ArrayIndexOutOfBoundsException e){
                return null;
            }
        }
        return null;
    }

    public void setCanvas(Canvas canvas, int tileSize){
        int numCols = (int) canvas.getWidth()/tileSize;
        int numRows = (int) canvas.getHeight()/tileSize;

        drawCanvasGrid(canvas, tileSize);

    }

    private void drawCanvasGrid(Canvas canvas, int tileSize){
        int gridWidth = (int) canvas.getWidth()/tileSize;
        int gridHeight = (int) canvas.getHeight()/tileSize;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, gridWidth * tileSize, gridHeight * tileSize);
        gc.setLineWidth(1);
        gc.setStroke(Color.GRAY);


        for (int x = 0; x <= gridWidth; x++){
            gc.strokeLine(x * tileSize, 0, x * tileSize, gridHeight * tileSize);
        }

        for (int y = 0; y <= gridHeight; y++){
            gc.strokeLine(0, y * tileSize, gridWidth * tileSize, y * tileSize);
        }

    }

    public void clearGirdTile(Canvas canvas, Tile tile){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(tile.x * tm.tileSize, tile.y * tm.tileSize, tm.tileSize, tm.tileSize);
    }

    public void paintImage(WritableImage image, Tile tile, Canvas canvas){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(image, tile.x * tm.tileSize -1, tile.y * tm.tileSize -1, tm.tileSize +1, tm.tileSize +1);
    }

    public void paintImage(List<WritableImage> images, Tile tile, Canvas canvas){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (WritableImage image : images) {
            gc.drawImage(image, tile.x * tm.tileSize -1, tile.y * tm.tileSize -1, tm.tileSize +1, tm.tileSize +1);
        }
    }

    public void populateIndexMap(int col, int row){
        Canvas canvas = layersManger.getSelectedCanvas(stackLayers, lvLayers);
        int indexMapNum = stackLayers.getChildren().indexOf(canvas);

        int index = 0;

        for (int i = 0;  i < allImages.length; i++) {
            for (int j = 0; j < allImages[i].length; j++) {
                index++;
                for (WritableImage image : drawImages){
                    if (image == allImages[i][j]) {
                        IndexMaps.get(indexMapNum)[col][row] = index;
                        break;
                    }
                }
            }
        }
    }

    public void populateIndex(int imageIndex, Tile tile){

        Canvas canvas = layersManger.getSelectedCanvas(stackLayers, lvLayers);
        int indexMapNum = stackLayers.getChildren().indexOf(canvas);
        IndexMaps.get(indexMapNum)[tile.x][tile.y] = imageIndex;

    }

    public void btnAddLayer(ActionEvent actionEvent) {

        this.canMain = layersManger.addLayer((int)stackLayers.getPrefWidth(), (int)stackLayers.getPrefHeight());

        stackLayers.getChildren().add(canMain);

//        canMain.setOnMouseClicked(event ->{
//            Tile tile = new Tile();
//            tile.x = (int)event.getX()/ tm.tileSize;
//            tile.y = (int)event.getY()/ tm.tileSize;
//
//            clearGirdTile(canMain, tile);
//            int imageIndex = getNeighbours(tile);
//            populateIndex(imageIndex, tile);
//            paintImage(this.allImagesFlattened[imageIndex], tile, canMain);
//            updateNeighbours(tile);
//
//        });

        layersManger.addLayerListItem(lvLayers, stackLayers);

        this.IndexMaps.add(new int[50][50]);

    }

    public void btnDeleteLayers(ActionEvent actionEvent) {
        int layerIndex = lvLayers.getSelectionModel().getSelectedIndex();
        layersManger.deleteLayer(stackLayers, lvLayers, layerIndex);
        this.IndexMaps.clear();
    }

}

package com.example.tilemapgenerator;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import java.util.List;

public class LayersManger {

    public void deleteLayer(StackPane parent, ListView layersList, int layerIndex){

        parent.getChildren().remove(layerIndex);
        layersList.getItems().remove(layerIndex);

    }

    public Canvas addLayer(int width, int height){
        return new Canvas(width, height);
    }

    public List<Node> getAllLayer(StackPane parent){
        return parent.getChildren();
    }

    public void hideLayer(StackPane parent, ListView layerList, int layerIndex){
        parent.getChildren().get(layerIndex).setVisible(false);
        HBox node = (HBox) layerList.getItems().get(layerIndex);
        node.setStyle("-fx-background-color: grey;");
    }

    public void showLayer(StackPane parent, ListView layerList, int layerIndex){
        parent.getChildren().get(layerIndex).setVisible(true);
        HBox node = (HBox) layerList.getItems().get(layerIndex);
        node.setStyle("-fx-background-color: white;");
    }

    public Canvas getSelectedCanvas(StackPane parent, ListView layerList){

        int index = layerList.getSelectionModel().getSelectedIndex();
        return (Canvas) parent.getChildren().get(index);

    }

    public void addLayerListItem(ListView layerList, StackPane parent){
        int numItems = layerList.getItems().size();

        HBox hBox = new HBox();
        hBox.setSpacing(20);
        hBox.setPrefWidth(layerList.getPrefWidth());

        TextField layerLabel = new TextField();
        layerLabel.setText("Layer: " + numItems);
        layerLabel.setStyle(null);

        CheckBox chLayerVisibility = new CheckBox();
        chLayerVisibility.setSelected(true);
        chLayerVisibility.setText("Hide");

        chLayerVisibility.setOnMouseClicked(e ->{
            chLayerVisibility.setText("Hide");
            int canvasIndex = layerList.getSelectionModel().getSelectedIndex();
            parent.getChildren().get(canvasIndex).setVisible(chLayerVisibility.isSelected());
        });

        hBox.getChildren().add(layerLabel);
        hBox.getChildren().add(chLayerVisibility);

        layerList.getItems().add(hBox);
        layerList.getFocusModel().focus(numItems);

    }

}

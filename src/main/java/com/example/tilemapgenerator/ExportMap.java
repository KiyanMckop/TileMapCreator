package com.example.tilemapgenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportMap {

    List<int[][]> allMaps;
    private final FileDialog fileDialog = new FileDialog();

    public ExportMap(List<int[][]>maps){
        this.allMaps = maps;
    }

    public void exportSingleJson(){

    }

    public void exportSingleTextFile(){


        try {
            String filePath = fileDialog.chooseDirectory() + "\\tileMap.txt";
            File file = new File(filePath);

            if (file.createNewFile()) {
                FileWriter fileWriter = new FileWriter(file);
                for (int[][] map : this.allMaps) {
                    for (int i = 0; i < map.length; i++) {
                        for (int j = 0; j < map[i].length; j++) {
                            fileWriter.write(map[i][j] - 1 + " ");
                        }
                        fileWriter.write("\n");
                    }
                }
                fileWriter.close();
            }else {
                System.out.println("File Already Exists");
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void exportSeparateJson(){

    }

    public void exportSeparateTextFile() throws IOException {

        int index = 0;

        for (int[][] map : this.allMaps) {
            System.out.println(index);
            String filePath = fileDialog.chooseDirectory() + "\\layer(" + index + ").txt";
            File file = new File(filePath);
            index += 1;

            try {
                if (!file.createNewFile()) {
                    System.out.println("file already exists");
                }else {
                    FileWriter myWriter = new FileWriter(filePath);
                    for (int i = 0; i < map.length; i++) {
                        for (int j = 0; j < map[i].length; j++) {
                            System.out.println(map[i][j]);
                            myWriter.write((map[j][i] - 1) + " ");
                        }
                        myWriter.write("\n");
                    }
                    myWriter.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

package com.example.tilemapgenerator;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileDialog {

    String directory;
    List<String> extensions = List.of("png", "jpg", "jpeg", "gif", "bmp");

    public void setDirectory(String directory){
        this.directory = directory;
    }

    public void setExtensions(List<String> extensions){
        this.extensions = extensions;
    }


    public File[] getFilesFromFolder(){
        File folder = new File(this.directory);
        return folder.listFiles();
    }


    public String chooseDirectory(){

        DirectoryChooser dir_chooser = new DirectoryChooser();
        Stage dialogStage = new Stage();

        File file = dir_chooser.showDialog(dialogStage);

        if (file != null) {
            return file.getAbsolutePath();
        }else {
            return null;
        }
    }

    public String chooseFile(){

        FileChooser fileChooser = new FileChooser();
        Stage dialogStage = new Stage();

        File file = fileChooser.showOpenDialog(dialogStage);

        if (file != null) {
            return file.getAbsolutePath();
        }else {
            return null;
        }
    }

    public List<Path> getFilesByExtension() throws IOException {
        Path directoryPath = Paths.get(directory);
        try (Stream<Path> paths = Files.walk(directoryPath)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> hasValidExtension(path, extensions))
                    .toList();
        }
    }

    private static boolean hasValidExtension(Path path, List<String> extensions) {
        String fileName = path.getFileName().toString().toLowerCase();
        return extensions.stream().anyMatch(fileName::endsWith);
    }

}

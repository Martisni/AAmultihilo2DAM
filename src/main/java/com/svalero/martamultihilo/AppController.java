package com.svalero.martamultihilo;

import com.svalero.martamultihilo.util.R;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppController {
    public TextField tfUrl;
    public TabPane tpDownloads;
    public ListView lvDownloads;
    public Button btDownload;
    public String directorio="descargas";

    private Map<String, DownloadController> allDownloads;

    public AppController() {
        allDownloads = new HashMap<>();
    }

    @FXML
    public void launchDownload(ActionEvent event) {
        String urlText = tfUrl.getText();
        tfUrl.clear();
        tfUrl.requestFocus();

        launchDownload(urlText);
    }

    private void launchDownload(String url) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(R.getUI("download.fxml"));

            DownloadController downloadController = new DownloadController(url);
            loader.setController(downloadController);
            VBox downloadBox = loader.load();

            String filename = url.substring(url.lastIndexOf("/") + 1); //nombre que aparece en la pestaña
            tpDownloads.getTabs().add(new Tab(filename, downloadBox)); //se genera en la sección del panel tpDownloads

            allDownloads.put(url, downloadController);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @FXML
    public void stopAllDownloads() {
        for (DownloadController downloadController : allDownloads.values())
            downloadController.stop();
    }

    //Metodo para importar descargas desde un fichero elegido por el usuario y ponerlas en cola:
    @FXML
    public void imports() {

        //Se pide el fichero al usuario:
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(directorio));
        File file = fileChooser.showOpenDialog(null);

        try {
            List<String> urls = Files.readAllLines(file.toPath()); // Lee el fichero y carga linea a linea en un List
            urls.forEach(url -> launchDownload(url)); //Se recoge linea a linea del archivo y los lanza como descarga
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @FXML
    public String directoryChooser(){

        DirectoryChooser directoryChooser =new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(directorio));

        File selectedDirectory = directoryChooser.showDialog(null);
        System.out.println(selectedDirectory.getAbsolutePath());

        directorio = String.valueOf(selectedDirectory);

        return directorio;
    }

    //metodo para leer el historial de descargas y listarlas
    @FXML
    public void historial(ActionEvent event) {

        String bufferLine;
        File log = new File("multidescargas.log");//Asignamos la ruta del archivo.log del registro

        try {
            BufferedReader br = new BufferedReader(new FileReader(log));//leemos lo que hay en el archivo

            while ((bufferLine = br.readLine())!=null){//mientras no lea una linea vacia
                lvDownloads.getItems().add(bufferLine.substring(bufferLine.lastIndexOf("Des") -3));//Añadimos las lineas y recortamos el texto para que se vea mas claro el registro
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    @Override
    public String toString() {
        return "AppController{" +
                "tfUrl=" + tfUrl +
                ", btDownload=" + btDownload +
                ", tpDownloads=" + tpDownloads +
                ", directorio='" + directorio + '\'' +
                ", allDownloads=" + allDownloads +
                '}';
    }
}

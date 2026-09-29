package com.monterroso.analizadorLexico.frontend;

import com.monterroso.analizadorLexico.backend.archivos.GeneradorAFD;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class VentanaAFDFX {

    private static final String RUTA_IMAGEN = "afd_promptzal.png";

    private final Stage stageDialogo;
    private final ImageView visorImagen;
    private final Label lblAviso;

    public VentanaAFDFX(Stage ventanaPadre) {
        stageDialogo = new Stage();
        stageDialogo.initOwner(ventanaPadre);

        stageDialogo.initModality(Modality.APPLICATION_MODAL);
        stageDialogo.setTitle("Visor de AFD");

        // Centro: ScrollPane con el visor de imagen y etiqueta de aviso
        visorImagen = new ImageView();
        visorImagen.setPreserveRatio(true);

        lblAviso = new Label();

        // StackPane centra la imagen o el texto de aviso automáticamente
        StackPane contenedorImagen = new StackPane(visorImagen, lblAviso);
        contenedorImagen.setPadding(new Insets(10));

        ScrollPane scrollImagenAFD = new ScrollPane(contenedorImagen);
        scrollImagenAFD.setFitToWidth(true);
        scrollImagenAFD.setFitToHeight(true);

        // Piso: HBox (Fila horizontal)
        Button btnRegenerar = new Button("Regenerar");
        Button btnGuardarImagen = new Button("Guardar Imagen");
        Button btnCerrar = new Button("Cerrar");

        btnRegenerar.setOnAction(e -> regenerarAFD());
        btnGuardarImagen.setOnAction(e -> guardarImagen());
        btnCerrar.setOnAction(e -> stageDialogo.close()); // Equivalente a dispose()

        // El resorte invisible que separa los botones a la izquierda y derecha
        Region resorte = new Region();
        HBox.setHgrow(resorte, Priority.ALWAYS);

        HBox barraInferior = new HBox(10, btnRegenerar, resorte, btnGuardarImagen, btnCerrar);
        barraInferior.setAlignment(Pos.CENTER_LEFT);
        barraInferior.setPadding(new Insets(10, 0, 0, 0));


        // Ensamble en el Borderpane
        BorderPane raiz = new BorderPane();
        raiz.setPadding(new Insets(10));
        raiz.setCenter(scrollImagenAFD);
        raiz.setBottom(barraInferior);
        //auto generación
        if (!new File(RUTA_IMAGEN).exists()) {
            try {
                new GeneradorAFD().generarImagen(RUTA_IMAGEN);
            } catch (Exception ignored) {
            }
        }
        cargarImagen();

        Scene escena = new Scene(raiz, 900, 680);
        stageDialogo.setScene(escena);
    }

    public void mostrar() {
        stageDialogo.showAndWait();
    }

    private void cargarImagen() {
        File archivo = new File(RUTA_IMAGEN);
        if (archivo.exists()) {
            try (FileInputStream stream = new FileInputStream(archivo)) {
                visorImagen.setImage(new Image(stream));
                lblAviso.setText(null);
            } catch (IOException ex) {
                visorImagen.setImage(null);
                lblAviso.setText("Error al leer la imagen: " + ex.getMessage());
            }
        } else {
            visorImagen.setImage(null);
            lblAviso.setText("No se encontró " + RUTA_IMAGEN + ". Genera el AFD primero.");
        }
    }

    private void regenerarAFD() {
        try {
            new GeneradorAFD().generarImagen(RUTA_IMAGEN);
            cargarImagen();
            mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "AFD regenerado correctamente.");
        } catch (Exception ex) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error al generar el AFD",
                    "No se pudo generar la imagen. ¿Tienes Graphviz instalado y accesible desde la línea de comandos?\n\n" + ex.getMessage());
        }
    }

    private void guardarImagen() {
        File origen = new File(RUTA_IMAGEN);
        if (!origen.exists()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Nada que guardar", "No hay imagen generada todavía.");
            return;
        }

        FileChooser selector = new FileChooser();
        selector.setInitialFileName("afd_promptzal.png");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagen PNG (*.png)", "*.png"));
        File destino = selector.showSaveDialog(stageDialogo);

        if (destino != null) {
            try {
                Files.copy(origen.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                mostrarMensaje(Alert.AlertType.INFORMATION, "Éxito", "Imagen guardada.");
            } catch (IOException ex) {
                mostrarMensaje(Alert.AlertType.ERROR, "Error", "No se pudo guardar: " + ex.getMessage());
            }
        }
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alerta = new Alert(tipo, contenido, ButtonType.OK);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.initOwner(stageDialogo);
        alerta.showAndWait();
    }
}
package com.monterroso.analizadorLexico.frontend;

import com.monterroso.analizadorLexico.backend.archivos.GeneradorReportes;
import com.monterroso.analizadorLexico.backend.modelos.ErrorLexico;
import com.monterroso.analizadorLexico.backend.modelos.Token;
import com.monterroso.analizadorLexico.backend.motor.AnalizadorLexico;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class VentanaPrincipalFX extends Application {

    // Controles visuales
    private TextArea editor;
    private TableView<Token> tablaTokens;
    private TableView<ErrorLexico> tablaErrores;
    private Label barraEstado;

    // Estado del último análisis
    private List<Token> ultimosTokens;
    private List<ErrorLexico> ultimosErrores;
    private int ultimoTotalLineas;

    @Override
    public void start(Stage stagePrincipal) {


        // Top: Barra de herramientas y sus 6 botones
        Button botonAbrir = new Button("Abrir");
        Button botonGuardar = new Button("Guardar");
        Button botonAnalizar = new Button("Analizar");
        Button botonReportes = new Button("Generar Reportes");
        Button btnVerAFD = new Button("Ver AFD");
        Button btnEstadisticas = new Button("Estadísticas");

        // Conectamos cada botón con su método
        botonAbrir.setOnAction(e -> abrirArchivo(stagePrincipal));
        botonGuardar.setOnAction(e -> guardarArchivo(stagePrincipal));
        botonAnalizar.setOnAction(e -> analizarCodigo());
        botonReportes.setOnAction(e -> generarReportes());
        btnVerAFD.setOnAction(e -> new VentanaAFDFX(stagePrincipal).mostrar());
        btnEstadisticas.setOnAction(e -> abrirEstadisticas(stagePrincipal));

        ToolBar barraHerramientas = new ToolBar(
                botonAbrir, botonGuardar, new Separator(),
                botonAnalizar, botonReportes, new Separator(),
                btnVerAFD, btnEstadisticas
        );


        // Centro: SplitPane con Editor arriba y Tablas abajo


        // Mitad superior: El editor de texto con un título
        editor = new TextArea();
        editor.setPromptText("Escribe o abre un archivo .pz aquí...");

        TitledPane contenedorEditor = new TitledPane("Editor .pz", editor);
        contenedorEditor.setCollapsible(false); // Evita que se minimice por accidente
        contenedorEditor.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(editor, Priority.ALWAYS);

        // Mitad inferior: Pestañas con las dos tablas
        tablaTokens = construirTablaTokens();
        tablaErrores = construirTablaErrores();

        Tab tabTokens = new Tab("Tokens", tablaTokens);
        tabTokens.setClosable(false); // Evita que el usuario cierre la pestaña con una 'X'

        Tab tabErrores = new Tab("Errores", tablaErrores);
        tabErrores.setClosable(false);

        TabPane panelPestanas = new TabPane(tabTokens, tabErrores);

        // se une la mitad superior e inferior en el SplitPane vertical
        SplitPane divisorCentral = new SplitPane(contenedorEditor, panelPestanas);
        divisorCentral.setOrientation(Orientation.VERTICAL);
        divisorCentral.setDividerPositions(0.40); // 40% del espacio para el editor, 60% para las tablas


        // Piso: La barra de estado
        barraEstado = new Label("Listo");
        barraEstado.setPadding(new Insets(5, 10, 5, 10));


        // Ensamble final
        BorderPane contenedorRaiz = new BorderPane();
        contenedorRaiz.setTop(barraHerramientas);
        contenedorRaiz.setCenter(divisorCentral);
        contenedorRaiz.setBottom(barraEstado);

        // el lienzo (Scene) de 1024x680 y mostramos la ventana
        Scene escena = new Scene(contenedorRaiz, 1024, 680);
        stagePrincipal.setTitle("Analizador Léxico PromptZal (JavaFX)");
        stagePrincipal.setScene(escena);
        stagePrincipal.show();
    }

    // Construcción de las tablas
    private TableView<Token> construirTablaTokens() {
        TableView<Token> tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Token, Integer> colNum = new TableColumn<>("#");
        colNum.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getNumero()));

        TableColumn<Token, String> colLexema = new TableColumn<>("Lexema");
        colLexema.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLexema()));

        TableColumn<Token, Object> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getTipo()));

        TableColumn<Token, Integer> colFila = new TableColumn<>("Fila");
        colFila.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getFila()));

        TableColumn<Token, Integer> colColumna = new TableColumn<>("Columna");
        colColumna.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getColumna()));

        tabla.getColumns().addAll(colNum, colLexema, colTipo, colFila, colColumna);
        return tabla;
    }

    private TableView<ErrorLexico> construirTablaErrores() {
        TableView<ErrorLexico> tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<ErrorLexico, String> colLexema = new TableColumn<>("Lexema");
        colLexema.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLexema()));

        TableColumn<ErrorLexico, String> colDesc = new TableColumn<>("Descripción");
        colDesc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));

        TableColumn<ErrorLexico, Integer> colFila = new TableColumn<>("Fila");
        colFila.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getFila()));

        TableColumn<ErrorLexico, Integer> colColumna = new TableColumn<>("Columna");
        colColumna.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getColumna()));

        tabla.getColumns().addAll(colLexema, colDesc, colFila, colColumna);
        return tabla;
    }


    // Lógica de los botones
    private void abrirArchivo(Stage stage) {
        FileChooser selector = new FileChooser();
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PromptZal (*.pz)", "*.pz"));
        File archivo = selector.showOpenDialog(stage);

        if (archivo != null) {
            try {
                editor.setText(Files.readString(archivo.toPath()));
                barraEstado.setText("Archivo abierto: " + archivo.getName());
            } catch (IOException ex) {
                mostrarError("No se pudo abrir el archivo: " + ex.getMessage());
            }
        }
    }

    private void guardarArchivo(Stage stage) {
        FileChooser selector = new FileChooser();
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PromptZal (*.pz)", "*.pz"));
        File archivo = selector.showSaveDialog(stage);

        if (archivo != null) {
            Path ruta = archivo.toPath();
            if (!ruta.toString().endsWith(".pz")) {
                ruta = Path.of(ruta.toString() + ".pz");
            }
            try {
                Files.writeString(ruta, editor.getText());
                barraEstado.setText("Guardado en: " + ruta.getFileName());
            } catch (IOException ex) {
                mostrarError("No se pudo guardar el archivo: " + ex.getMessage());
            }
        }
    }

    private void analizarCodigo() {
        AnalizadorLexico analizador = new AnalizadorLexico(editor.getText());
        analizador.analizar();

        ultimosTokens = analizador.getTokens();
        ultimosErrores = analizador.getErrores();
        ultimoTotalLineas = analizador.getTotalLineas();

        tablaTokens.getItems().setAll(ultimosTokens);
        tablaErrores.getItems().setAll(ultimosErrores);

        barraEstado.setText(ultimosTokens.size() + " tokens, " + ultimosErrores.size() + " errores.");
    }

    private void generarReportes() {
        if (ultimosTokens == null) {
            mostrarAdvertencia("Primero presiona \"Analizar\".");
            return;
        }
        GeneradorReportes reportes = new GeneradorReportes();
        reportes.generarReporteTokens(ultimosTokens, "reporte_tokens.html");
        reportes.generarReporteErrores(ultimosErrores, "reporte_errores.html");
        reportes.generarReporteEstadisticas(ultimosTokens, ultimosErrores, ultimoTotalLineas, "reporte_estadisticas.html");
        barraEstado.setText("Reportes generados en la carpeta del proyecto.");
    }

    // antiguos JOptionPane.showMessageDialog
    private void mostrarAdvertencia(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK);
        alerta.setHeaderText("Error");
        alerta.showAndWait();
    }

    private void mostrarAvisoTemporal(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    private void abrirEstadisticas(Stage stagePrincipal) {
        if (ultimosTokens == null) {
            mostrarAdvertencia("Primero presiona \"Analizar\".");
            return;
        }
        new VentanaEstadisticasFX(stagePrincipal, ultimosTokens, ultimosErrores, ultimoTotalLineas).mostrar();
    }
}
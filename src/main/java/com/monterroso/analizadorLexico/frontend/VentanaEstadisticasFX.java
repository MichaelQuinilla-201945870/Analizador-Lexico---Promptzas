package com.monterroso.analizadorLexico.frontend;

import com.monterroso.analizadorLexico.backend.modelos.ErrorLexico;
import com.monterroso.analizadorLexico.backend.modelos.TipoToken;
import com.monterroso.analizadorLexico.backend.modelos.Token;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class VentanaEstadisticasFX {

    public record FilaFrecuencia(TipoToken tipo, int cantidad) {}

    private final Stage stageDialogo;

    public VentanaEstadisticasFX(Stage padre, List<Token> tokens, List<ErrorLexico> errores, int totalLineas) {
        stageDialogo = new Stage();
        stageDialogo.initOwner(padre);
        stageDialogo.initModality(Modality.APPLICATION_MODAL);
        stageDialogo.setTitle("Estadísticas del Análisis");

        // Piso 1: Las 3 tarjetas superiores en un HBox
        VBox tarjetaTokens = crearTarjeta(String.valueOf(tokens.size()), "Tokens", "#0099FF", "#00CCFF");
        VBox tarjetaLineas = crearTarjeta(String.valueOf(totalLineas), "Líneas con Tokens", "#00CC00", "#00CC00");
        VBox tarjetaErrores = crearTarjeta(String.valueOf(errores.size()), "Errores", "#CC0000", "#FF0000");

        HBox filaTarjetas = new HBox(35, tarjetaTokens, tarjetaLineas, tarjetaErrores);
        filaTarjetas.setAlignment(Pos.CENTER);


        // Piso 2: Subtítulo
        Label lblTituloTabla = new Label("Frecuencia por tipo de token");
        lblTituloTabla.setFont(Font.font("System", FontWeight.BLACK, 18));

        // Piso 3: Tabla de Frecuencias con texto coloreado por TipoToken
        TableView<FilaFrecuencia> tablaFrecuencia = new TableView<>();
        tablaFrecuencia.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(tablaFrecuencia, Priority.ALWAYS);

        TableColumn<FilaFrecuencia, TipoToken> colTipo = new TableColumn<>("Tipo de token");
        colTipo.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().tipo()));

        colTipo.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(TipoToken tipo, boolean vacio) {
                super.updateItem(tipo, vacio);
                if (vacio || tipo == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(tipo.name());
                    setStyle("-fx-text-fill: " + colorHexParaTipo(tipo) + "; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<FilaFrecuencia, Integer> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().cantidad()));

        tablaFrecuencia.getColumns().addAll(colTipo, colCantidad);

        // Calculamos las frecuencias igual que en tu método calcularYMostrar original
        for (TipoToken tipo : TipoToken.values()) {
            int contador = 0;
            for (Token t : tokens) {
                if (t.getTipo() == tipo) {
                    contador++;
                }
            }
            tablaFrecuencia.getItems().add(new FilaFrecuencia(tipo, contador));
        }

        // Piso 4: Botón Cerrar

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setOnAction(e -> stageDialogo.close());

        HBox barraInferior = new HBox(btnCerrar);
        barraInferior.setAlignment(Pos.CENTER_RIGHT);

        // Ensamblaje final
        VBox contenedorPrincipal = new VBox(20, filaTarjetas, lblTituloTabla, tablaFrecuencia, barraInferior);
        contenedorPrincipal.setPadding(new Insets(30, 40, 20, 40));

        Scene escena = new Scene(contenedorPrincipal, 900, 620);
        stageDialogo.setScene(escena);
    }

    // Molde reutilizable para crear cada tarjeta con su borde redondeado
    private VBox crearTarjeta(String numero, String textoInferior, String colorBorde, String colorNumero) {
        Label lblNumero = new Label(numero);
        lblNumero.setFont(Font.font("System", FontWeight.BOLD, 56));
        lblNumero.setStyle("-fx-text-fill: " + colorNumero + ";");

        Label lblTexto = new Label(textoInferior);
        lblTexto.setFont(Font.font("System", FontWeight.BLACK, 13));

        VBox tarjeta = new VBox(5, lblNumero, lblTexto);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPrefSize(250, 140);
        tarjeta.setStyle(
                "-fx-border-color: " + colorBorde + ";"
                        + "-fx-border-width: 4;"
                        + "-fx-border-radius: 8;"
                        + "-fx-background-radius: 8;"
        );
        return tarjeta;
    }

    //paleta de colores
    private String colorHexParaTipo(TipoToken tipo) {
        return switch (tipo) {
            case DIRECTIVA -> "#4DB6AC";
            case PALABRA_RESERVADA -> "#64A0DC";
            case COMANDO_IA -> "#C77DBB";
            case CONECTOR -> "#E0A458";
            case LITERAL_CADENA -> "#E06C75";
            case LITERAL_ENTERO, LITERAL_DECIMAL -> "#98C379";
            default -> "#778899";
        };
    }

    public void mostrar() {
        stageDialogo.showAndWait();
    }
}

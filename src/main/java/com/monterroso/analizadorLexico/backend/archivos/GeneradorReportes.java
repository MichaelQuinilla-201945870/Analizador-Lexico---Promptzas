package com.monterroso.analizadorLexico.backend.archivos;


import com.monterroso.analizadorLexico.backend.modelos.ErrorLexico;
import com.monterroso.analizadorLexico.backend.modelos.TipoToken;
import com.monterroso.analizadorLexico.backend.modelos.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author seo
 */
public class GeneradorReportes {


    public void generarReporteTokens(List<Token> tokens, String rutaSalida) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta charset=\"UTF-8\"><title>Reporte de Tokens</title>")
                .append(estilos())
                .append("</head><body>")
                .append("<h1>Reporte de Tokens</h1>") // encabezados de la tabla

                .append("<p>Total de tokens reconocidos: ").append(tokens.size()).append("</p>")
                .append("<table><tr><th>#</th><th>Lexema</th><th>Tipo</th><th>Fila</th><th>Columna</th></tr>");

        for (Token t : tokens) {

            String colorTexto = obtenerColor(t.getTipo());

            html.append("<tr>")
                    .append("<td>").append(t.getNumero()).append("</td>")

                    .append("<td style=\"color: ").append(colorTexto).append("; font-weight: bold;\">")
                    .append(correctorSimbolos(t.getLexema())).append("</td>")

                    .append("<td style=\"color: ").append(colorTexto).append("; font-weight: bold;\">")
                    .append(t.getTipo()).append("</td>")

                    .append("<td>").append(t.getFila()).append("</td>")
                    .append("<td>").append(t.getColumna()).append("</td></tr>");
        }

        html.append("</table></body></html>");
        escribirArchivo(rutaSalida, html.toString());
    }

    private String estilos() {
        return "<style>"
                + "body{font-family:Arial,sans-serif;margin:24px;}"
                + "table{border-collapse:collapse;width:100%;}"
                + "th,td{border:1px solid #ccc;padding:6px 10px;text-align:left;}"
                + "th{background:#2c3e50;color:white;}"
                + "tr:nth-child(even){background:#f5f5f5;}"
                + "</style>";
    }

    private String correctorSimbolos(String texto) {  //cambia los signos para que el html no se arruine
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private void escribirArchivo(String ruta, String contenido) {
        try {
            Files.writeString(Path.of(ruta), contenido);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo escribir el reporte: " + e.getMessage());
        }
    }

    public void generarReporteErrores(List<ErrorLexico> errores, String rutaSalida) {

        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta charset=\"UTF-8\"><title>Reporte de Errores</title>")
                .append(estilos())
                .append("</head><body>")
                .append("<h1>Reporte de Errores Lexicos</h1>");

        if (errores.isEmpty()) {
            html.append("<p>No se encontraron errores lexicos.</p>");
        } else {
            html.append("<p>Total de errores encontrados: ").append(errores.size()).append("</p>")
                    .append("<table><tr><th>Lexema / Caracter</th><th>Descripcion</th><th>Fila</th><th>Columna</th></tr>");
            for (ErrorLexico e : errores) {
                html.append("<tr><td>").append(correctorSimbolos(e.getLexema())).append("</td>")
                        .append("<td>").append(correctorSimbolos(e.getDescripcion())).append("</td>")
                        .append("<td>").append(e.getFila()).append("</td>")
                        .append("<td>").append(e.getColumna()).append("</td></tr>");
            }
            html.append("</table>");
        }

        html.append("</body></html>");
        escribirArchivo(rutaSalida, html.toString());
    }

    private String obtenerColor(TipoToken tipo) {
        return switch (tipo) {
            case DIRECTIVA -> "#9400D3"; // morado 
            case PALABRA_RESERVADA -> "#0000FF"; // azul
            case COMANDO_IA -> "#008000"; // verde 
            case FUNCION -> "#FFD700"; // amarillo
            case CONECTOR -> "#FF8C00"; // naranja 
            case IDENTIFICADOR -> "#778899"; // gris clarito
            case LITERAL_CADENA -> "#F8BBD0"; // rosa
            case LITERAL_ENTERO, LITERAL_DECIMAL -> "#00FFFF"; // aqua
            case OPERADOR -> "#B0C4DE"; // gris azulado
            case DELIMITADOR -> "#7FFF00"; // verde lima
            default -> "#000000"; // negro
        };
    }

    public void generarReporteEstadisticas(List<Token> tokens, List<ErrorLexico> errores,
                                           int totalLineas, String rutaSalida) {

        StringBuilder html = new StringBuilder();

        html.append("<html><head><meta charset=\"UTF-8\"><title>Reporte de Estadísticas</title>")
                .append(estilos())
                .append("</head><body>")
                .append("<h1>Reporte de Estadísticas</h1>")
                .append("<p>Total de tokens: ").append(tokens.size()).append("<br>")
                .append("Total de líneas: ").append(totalLineas).append("<br>")
                .append("Total de errores: ").append(errores.size()).append("</p>")
                .append("<table><tr><th>Tipo de token</th><th>Cantidad</th></tr>");

        for (TipoToken tipo : TipoToken.values()) {
            int contador = 0;
            for (Token t : tokens) {
                if (t.getTipo() == tipo) {
                    contador++;
                }
            }
            html.append("<tr><td>").append(tipo).append("</td>")
                    .append("<td>").append(contador).append("</td></tr>");
        }

        html.append("</table></body></html>");
        escribirArchivo(rutaSalida, html.toString());
    }

}

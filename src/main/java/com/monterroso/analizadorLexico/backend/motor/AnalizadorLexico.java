package com.monterroso.analizadorLexico.backend.motor;


import com.monterroso.analizadorLexico.backend.modelos.ErrorLexico;
import com.monterroso.analizadorLexico.backend.modelos.TipoToken;
import com.monterroso.analizadorLexico.backend.modelos.Token;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author seo
 */
public class AnalizadorLexico {

    private final String codigoFuente; // el texto completo a analizar
    private final List<Token> tokens = new ArrayList<>();  // libreta de aciertos
    private final List<ErrorLexico> errores = new ArrayList<>(); // libreta de errores
    private int indice = 0; //sera el indice del caracter que se esta evaluando en el momento dentro de codigofuente
    private int fila = 1;
    private int columna = 1;

    public AnalizadorLexico(String codigoFuente) {
        this.codigoFuente = codigoFuente;  // todo el texto del .pz, leido completo, como una sola cadena larga
    }

    public void analizar() {

        while (!finDeArchivo()) {

            char actual = charActual();

            if (actual == '\n') {
                avanzar();
                fila++;
                columna = 1;
                continue;
            }

            if (Character.isWhitespace(actual)) {
                avanzar();
                columna++;
                continue;
            }
            if (actual == '@') {
                reconocerDirectiva();
                continue;
            }
            if (Character.isLetter(actual) || actual == '_') {
                reconocerIdentificadorOPalabraClave();
                continue;
            }
            if (actual == '"') {
                reconocerCadena();
                continue;
            }
            if (Character.isDigit(actual)) {
                reconocerNumero();
                continue;
            }
            if (actual == '/' && (siguiente() == '/' || siguiente() == '*')) {
                reconocerComentario();
                continue;
            }
            reconocerSimbolo();
        }
    }

    private char siguiente() {

        if (indice + 1 >= codigoFuente.length()) {
            return '\0';
        }
        return codigoFuente.charAt(indice + 1);
    }

    private char charActual() {
        return codigoFuente.charAt(indice);
    }

    private boolean finDeArchivo() {
        return indice >= codigoFuente.length();  //para saber si el arvhico ya termino
    }

    private void avanzar() {
        indice++;
    }

    private void reconocerDirectiva() {
        int filaInicio = fila;
        int columnaInicio = columna;
        avanzar();
        columna++; // consume el '@'

        String nombre = Encadenar();

        columna += nombre.length();

        if (PalabrasClave.esDirectivaValida(nombre)) {
            tokens.add(new Token(tokens.size() + 1, "@" + nombre, TipoToken.DIRECTIVA, filaInicio, columnaInicio));
        } else {
            errores.add(new ErrorLexico("@" + nombre, "Directiva no reconocida", filaInicio, columnaInicio));
        }
    }


    // leector de caracteres consecutivamente mientras formen parte de un identificador  o palabra clave (letras, dígitos o guiones bajos).

    private String Encadenar() {

        int inicio = indice;
        while (!finDeArchivo() && (Character.isLetterOrDigit(charActual()) || charActual() == '_')) {
            avanzar();
        }
        return codigoFuente.substring(inicio, indice);
    }


    private void reconocerIdentificadorOPalabraClave() {

        int filaInicio = fila;
        int columnaInicio = columna;
        String lexema = Encadenar();
        columna += lexema.length();

        TipoToken tipo = PalabrasClave.tipoDePalabra(lexema);
        if (tipo == null) {
            tipo = TipoToken.IDENTIFICADOR;
        }
        tokens.add(new Token(tokens.size() + 1, lexema, tipo, filaInicio, columnaInicio));
    }

    private void reconocerCadena() {

        int filaInicio = fila;
        int columnaInicio = columna;

        //guardar la comilla de apertura como DELIMITADOR
        tokens.add(new Token(tokens.size() + 1, "\"", TipoToken.DELIMITADOR, filaInicio, columnaInicio));

        avanzar();
        columna++; // consumir la comilla inicial

        int inicioTexto = indice;
        int colTexto = columna;

        // avanzar por el texto hasta encontrar la comilla de cierre o un salto de linea
        while (!finDeArchivo() && charActual() != '"' && charActual() != '\n') {
            avanzar();
            columna++;
        }

        // se extrae el texto interior (SIN comillas
        String lexemaCadena = codigoFuente.substring(inicioTexto, indice);
        if (!lexemaCadena.isEmpty()) { // Solo agregamos el token si hay texto adentro
            tokens.add(new Token(tokens.size() + 1, lexemaCadena, TipoToken.LITERAL_CADENA, filaInicio, colTexto));
        }

        // verificamos y guardamos la comilla de cierre
        if (!finDeArchivo() && charActual() == '"') {
            tokens.add(new Token(tokens.size() + 1, "\"", TipoToken.DELIMITADOR, fila, columna));
            avanzar();
            columna++; //comilla final
        } else {
            // por si el archivo termino o hubo salto de linea antes de cerrar
            errores.add(new ErrorLexico(lexemaCadena, "Cadena sin cerrar (falta comilla final)", filaInicio, colTexto));
        }
    }

    private void reconocerNumero() {
        int filaInicio = fila;
        int columnaInicio = columna;
        int inicio = indice;

        while (!finDeArchivo() && Character.isDigit(charActual())) {
            avanzar();
        }

        TipoToken tipo = TipoToken.LITERAL_ENTERO;

        if (!finDeArchivo() && charActual() == '.' && Character.isDigit(siguiente())) {
            avanzar(); // consume el '.' para decimales
            while (!finDeArchivo() && Character.isDigit(charActual())) {
                avanzar();
            }
            tipo = TipoToken.LITERAL_DECIMAL;
        }

        String lexema = codigoFuente.substring(inicio, indice);
        columna += lexema.length();
        tokens.add(new Token(tokens.size() + 1, lexema, tipo, filaInicio, columnaInicio));
    }

    private void reconocerComentario() {

        int filaInicio = fila;
        int columnaInicio = columna;

        if (siguiente() == '/') {
            while (!finDeArchivo() && charActual() != '\n') {
                avanzar();
                columna++;
            }
            return; // comentario de linea: no genera token
        }

        avanzar();
        columna++; // consume '/'
        avanzar();
        columna++; // consume '*'

        while (true) {
            if (finDeArchivo()) {
                errores.add(new ErrorLexico("/*", "Comentario de bloque sin cerrar", filaInicio, columnaInicio));
                return;
            }
            if (charActual() == '*' && siguiente() == '/') {
                avanzar();
                columna++;
                avanzar();
                columna++;
                return; // comentario cerrado correctamente: tampoco genera token
            }
            if (charActual() == '\n') {
                avanzar();
                fila++;
                columna = 1;
            } else {
                avanzar();
                columna++;
            }
        }
    }

    private void reconocerSimbolo() {
        int filaInicio = fila;
        int columnaInicio = columna;
        char c = charActual();
        avanzar();
        columna++;

        switch (c) {
            case '=' -> tokens.add(new Token(tokens.size() + 1, "=", TipoToken.OPERADOR, filaInicio, columnaInicio));
            case '+' -> tokens.add(new Token(tokens.size() + 1, "+", TipoToken.OPERADOR, filaInicio, columnaInicio));
            case '{', '}', '(', ')', ',', ';' ->
                    tokens.add(new Token(tokens.size() + 1, String.valueOf(c), TipoToken.DELIMITADOR, filaInicio, columnaInicio));
            case '-' -> {
                if (!finDeArchivo() && charActual() == '>') {
                    avanzar();
                    columna++;
                    tokens.add(new Token(tokens.size() + 1, "->", TipoToken.CONECTOR, filaInicio, columnaInicio));
                } else {
                    errores.add(new ErrorLexico("-", "Carácter no reconocido", filaInicio, columnaInicio));
                }
            }
            default ->
                    errores.add(new ErrorLexico(String.valueOf(c), "Carácter no reconocido", filaInicio, columnaInicio));
        }
    }


    public List<Token> getTokens() {
        return tokens;
    }

    public List<ErrorLexico> getErrores() {
        return errores;
    }

    public int getTotalLineas() {

        if (codigoFuente.isEmpty()) {
            return 0;
        }
        // si el archivo termina con salto de linea, 'fila' ya apunta a una linea vacia
        return codigoFuente.endsWith("\n") ? fila - 1 : fila;
    }

}

package com.monterroso.analizadorLexico.backend.modelos;

/**
 *
 * @author seo
 */


// el token; la unidad minima de informacion (lexema) que tiene significado para el compilador. 

public class Token {

    private final int numero;       // identificador
    private final String lexema;    // cadena de texto exacta extraida del texto
    private final TipoToken tipo;   // clasificacion segun las reglas
    private final int fila;         // ubi en el eje Y
    private final int columna;      // ubi en el eje X

    // se modela como "final" para proteger la integridad de los datos una vez que el analizador lo ha reconocido y clasificado.

    public Token(int numero, String lexema, TipoToken tipo, int fila, int columna) {
        this.numero = numero;
        this.lexema = lexema;
        this.tipo = tipo;
        this.fila = fila;
        this.columna = columna;
    }

    @Override
    public String toString() {
        return numero + "\t" + lexema + "\t" + tipo + "\t" + fila + "\t" + columna;
    }


    public int getNumero() {
        return numero;
    }

    public String getLexema() {
        return lexema;
    }

    public TipoToken getTipo() {
        return tipo;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }


}

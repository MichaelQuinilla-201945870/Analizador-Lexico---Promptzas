package com.monterroso.analizadorLexico.backend.modelos;

    /**
     *
     * @author seo
     */
    public class ErrorLexico {

        private final String lexema;    // caracter o fragmento de texto invalido
        private final String descripcion;           // motivo del rechazo
        private final int fila;
        private final int columna;

        public ErrorLexico(String lexema, String descripcion, int fila, int columna) {
            this.lexema = lexema;
            this.descripcion = descripcion;
            this.fila = fila;
            this.columna = columna;
        }

        @Override
        public String toString() {
            return lexema + "\t" + descripcion + "\t" + fila + "\t" + columna;
        }

        public String getLexema() {
            return lexema;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public int getFila() {
            return fila;
        }

        public int getColumna() {
            return columna;
        }


    }

package com.monterroso.analizadorLexico.backend.motor;

import com.monterroso.analizadorLexico.backend.modelos.TipoToken;


/**
 *
 * @author seo
 */


//Diccionario, clasifica palabras, determina si pertenece al vocabulario y le da el token al que pertenecen.

public class PalabrasClave {


    // evalua una cadena de texto y determina su clasificacion.

    public static TipoToken tipoDePalabra(String palabra) {
        return switch (palabra) {
            case "AGENTE", "contexto", "variable", "EJECUTAR", "EXPORTAR" -> TipoToken.PALABRA_RESERVADA;
            case "PREGUNTAR", "GENERAR", "RESUMIR", "ANALIZAR", "TRADUCIR", "CLASIFICAR", "EXTRAER", "CODIFICAR" ->
                    TipoToken.COMANDO_IA;
            case "CARGAR" -> TipoToken.FUNCION;
            case "SOBRE", "DESDE", "EN", "COMO" -> TipoToken.CONECTOR;
            default -> null;  // no es palabra clave -> es identificador
        };
    }


    // verifica si el texto que le sigue a un símbolo "@" corresponde a una directiva valida
    // return true si es una directiva valida, false en caso contrario.

    public static boolean esDirectivaValida(String nombre) {
        return switch (nombre) {
            case "modelo", "rol", "formato" -> true;
            default -> false;
        };
    }

}

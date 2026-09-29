package com.monterroso.analizadorLexico.backend.modelos;

/**
 *
 * @author seo
 */

//catalogo estricto de los tipos de componentes lexicos validos en el leguaje

public enum TipoToken {

    DIRECTIVA,
    PALABRA_RESERVADA,
    COMANDO_IA,
    FUNCION,
    CONECTOR,
    IDENTIFICADOR,
    LITERAL_CADENA,
    LITERAL_ENTERO,
    LITERAL_DECIMAL,
    OPERADOR,
    DELIMITADOR

}

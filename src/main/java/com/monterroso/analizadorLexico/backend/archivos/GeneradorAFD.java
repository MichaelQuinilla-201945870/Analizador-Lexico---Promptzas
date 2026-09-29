/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monterroso.analizadorLexico.backend.archivos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author seo
 */
public class GeneradorAFD {

    public String codigoDot() {
        return """
                digraph AFD_PromptZal {
                    rankdir=LR;
                    node [fontname="Helvetica", fontsize=11];
                    edge [fontname="Helvetica", fontsize=9];
                
                    __start [shape=point];
                    __start -> q0;
                
                    node [shape=doublecircle];
                    qID; qDIR; qCOM_INI; qCAD_OK; qENT; qDEC; qFLECHA; qIGUAL; qMAS; qDELIM;
                
                    node [shape=circle, style=filled, fillcolor=lightgrey];
                    qERROR;
                
                    node [shape=circle, style=solid, fillcolor=white];
                    q0; qARROBA; qCAD; qPUNTO; qGUION; qSLASH; qCLINEA; qCBLOQUE; qCBLOQUE_AST;
                
                    q0 -> qID [label="letra, _"];
                    qID -> qID [label="letra, dígito, _"];
                
                    q0 -> qARROBA [label="@"];
                    qARROBA -> qDIR [label="letra"];
                    qARROBA -> qERROR [label="otro"];
                    qDIR -> qDIR [label="letra, dígito, _"];
                
                    q0 -> qCOM_INI [label="comilla (emite DELIMITADOR)"];
                    qCOM_INI -> qCAD [label=""];
                    qCAD -> qCAD [label="cualquiera, excepto comilla o salto de línea"];
                    qCAD -> qCAD_OK [label="comilla (emite LITERAL_CADENA y DELIMITADOR)"];
                    qCAD -> qERROR [label="salto de línea o EOF"];
                
                    q0 -> qENT [label="dígito"];
                    qENT -> qENT [label="dígito"];
                    qENT -> qPUNTO [label="."];
                    qPUNTO -> qDEC [label="dígito"];
                    qPUNTO -> qERROR [label="otro"];
                    qDEC -> qDEC [label="dígito"];
                
                    q0 -> qGUION [label="-"];
                    qGUION -> qFLECHA [label=">"];
                    qGUION -> qERROR [label="otro"];
                
                    q0 -> qIGUAL [label="="];
                    q0 -> qMAS [label="+"];
                    q0 -> qDELIM [label="{ } ( ) ,"];
                
                    q0 -> qSLASH [label="/"];
                    qSLASH -> qCLINEA [label="/"];
                    qSLASH -> qCBLOQUE [label="*"];
                    qSLASH -> qERROR [label="otro"];
                    qCLINEA -> qCLINEA [label="cualquiera, excepto salto de línea"];
                    qCLINEA -> q0 [label="salto de línea"];
                    qCBLOQUE -> qCBLOQUE [label="cualquiera, excepto *"];
                    qCBLOQUE -> qCBLOQUE_AST [label="*"];
                    qCBLOQUE -> qERROR [label="EOF"];
                    qCBLOQUE_AST -> qCBLOQUE_AST [label="*"];
                    qCBLOQUE_AST -> q0 [label="/"];
                    qCBLOQUE_AST -> qCBLOQUE [label="otro"];
                    qCBLOQUE_AST -> qERROR [label="EOF"];
                
                    q0 -> q0 [label="espacio, tab o salto de línea"];
                    q0 -> qERROR [label="cualquier otro carácter"];
                }
                """;
    }

    public void generarImagen(String rutaSalida) throws IOException, InterruptedException {

        Path archivoDot = Files.createTempFile("afd_promptzal", ".dot");
        Files.writeString(archivoDot, codigoDot());

        ProcessBuilder constructor = new ProcessBuilder("dot", "-Tpng", archivoDot.toString(), "-o", rutaSalida);

        constructor.redirectErrorStream(true);

        Process proceso = constructor.start();
        int codigoSalida = proceso.waitFor();

        Files.deleteIfExists(archivoDot);

        if (codigoSalida != 0) {
            throw new IOException("Graphviz terminó con código de error " + codigoSalida
                    + ". ¿Está instalado y accesible como \"dot\" desde la línea de comandos?");
        }
    }

}

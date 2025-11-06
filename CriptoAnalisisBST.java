/**
 *  Programa: CriptoAnalisisBST
 *  ---------------------------------------
 *  Este código hace un conteo de frecuencias usando un árbol binario (BST),
 *  mide cuánto tarda el análisis y trata de estimar la constante A 
 *  que usa la función hash del enemigo (por multiplicación).
 *  
 *  Qué sí hace:
 *    - Inserta símbolos en el árbol y cuenta cuántas veces aparecen.
 *    - Calcula porcentajes y tiempos.
 *    - Prueba varios valores de A y muestra el más cercano a 0.618.
 *    - Hace un intento simple de descifrar por sustitución.
 *    - Calcula los índices hash para cada carácter del mensaje interceptado.
 *  
 *  Qué no hace todavía:
 *    - No reconstruye el patrón completo de colisiones.
 *    - No descifra totalmente el mensaje.
 */

import java.util.*;

public class CriptoAnalisisBST {

    static String mensajeCifrado = "(/-.-4%(+28.%#+2/($(6(#(3(8%.-/2(+(/(6.(";

    public static void main(String[] args) {

        ArbolFrecuencias arbol = new ArbolFrecuencias();

        // Medi el tiempo que tarda en construir el árbol
        long inicio = System.nanoTime();
        for (char c : mensajeCifrado.toCharArray()) {
            arbol.insertar(c);
        }
        long fin = System.nanoTime();
        double tiempoMs = (fin - inicio) / 1_000_000.0;

        System.out.println("====================================");
        System.out.println("      ANÁLISIS DE FRECUENCIA ");
        System.out.println("====================================");

        arbol.mostrarFrecuencias();
        System.out.printf("\nTiempo de ejecución: %.3f ms\n", tiempoMs);

        // Prueba para estimar A (entre 0.60 y 0.63)
        System.out.println("====================================");
        System.out.println("\n    BÚSQUEDA DE CONSTANTE A ");
        System.out.println("====================================");
        double mejorA = buscarConstanteA(31);
        System.out.printf("Constante A aproximada encontrada: %.6f\n", mejorA);

        // Intento de sustitución más variado
        String intento = mensajeCifrado
            .replace('(', 'e')
            .replace('/', 'a')
            .replace('-', 'o')
            .replace('.', 's')
            .replace('%', 'l')
            .replace('+', 'r')
            .replace('2', 't')
            .replace('#', 'n')
            .replace('4', 'u')
            .replace('8', 'm');

        System.out.println("\nMensaje cifrado original:\n" + mensajeCifrado);
        System.out.println("\nIntento de lectura parcial:\n" + intento);
        System.out.println("\n(Aún falta deducir el mapeo completo del hash)");
    

        System.out.println("====================================");
        System.out.println("\n.   CÁLCULO DE ÍNDICES HASH ");
        System.out.println("====================================");
        int[] ascii = HashCripto.convertirASCII(mensajeCifrado);
        for (int k : ascii) {
            int mult = HashCripto.hashMultiplicacion(k);
            int div = HashCripto.hashDivision(k);
            System.out.printf("Char '%c' (ASCII %d) -> HashMult: %d | HashDiv: %d\n", 
                              (char)k, k, mult, div);
        }
        System.out.println("\nFin del análisis..");
    }

    // Búsqueda de A (revisando 200 valores entre 0.60 y 0.63)
    static double buscarConstanteA(int M) {
        double mejorA = 0;
        double menorError = Double.MAX_VALUE;
        double phi = 0.6180339887;

        for (int i = 0; i < 200; i++) {
            double A = 0.60 + (0.63 - 0.60) * i / 199.0;
            double error = Math.abs(A - phi);
            if (error < menorError) {
                menorError = error;
                mejorA = A;
            }
        }
        return mejorA;
    }
}

// -------------------------------------------------------------

class Nodo {
    char simbolo;
    int frecuencia;
    Nodo izq, der;
    static int totalSimbolos = 40; // total estimado

    Nodo(char s) {
        simbolo = s;
        frecuencia = 1;
        izq = der = null;
    }
}

class ArbolFrecuencias {

    Nodo raiz;

    public void insertar(char clave) {
        raiz = insertarNodo(raiz, clave);
    }

    private Nodo insertarNodo(Nodo nodo, char clave) {
        if (nodo == null) return new Nodo(clave);

        if (clave == nodo.simbolo) {
            nodo.frecuencia++;
        } else if (clave < nodo.simbolo) {
            nodo.izq = insertarNodo(nodo.izq, clave);
        } else {
            nodo.der = insertarNodo(nodo.der, clave);
        }
        return nodo;
    }

    public void mostrarFrecuencias() {
        recorrerInOrden(raiz);
    }

    private void recorrerInOrden(Nodo n) {
        if (n != null) {
            recorrerInOrden(n.izq);
            float porc = (float) n.frecuencia * 100 / Nodo.totalSimbolos;
            System.out.printf("'%c' → %d veces (%.2f%%)\n", n.simbolo, n.frecuencia, porc);
            recorrerInOrden(n.der);
        }
    }
}

class HashCripto {

    static int M = 31;
    static int OFFSET = 32;
    static double A = 0.618; // valor inicial aproximado

    static int hashMultiplicacion(int k) {
        double frac = (k * A) % 1; // solo la parte fraccionaria
        return (int) Math.floor(M * frac) + OFFSET;
    }

    static int hashDivision(int k) {
        return (k % M) + OFFSET;
    }

    static int[] convertirASCII(String str) {
        int[] arr = new int[str.length()];
        for (int i = 0; i < str.length(); i++) {
            arr[i] = (int) str.charAt(i);
        }
        return arr;
    }
}

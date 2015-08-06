package com.marzam.com.appventas.DevolucionesFull.CustomPrompt;

/**
 * Created by lcabral on 09/04/2015.
 */
import java.util.LinkedList;
import java.util.List;

public class NumeroEnorme implements Comparable<NumeroEnorme> {

    // El número se alamacena empezando desde la derecha
    // de modo que el comienzo de la lista son las unidades y el último la más
    // alta
    private List<Integer> numero;

    public NumeroEnorme() {
        numero = new LinkedList<Integer>();
        numero.add(0);
    }

    public NumeroEnorme(String n) {
        numero = new LinkedList<Integer>();
        char[] arrayCadena = n.toCharArray();
        for (int i = 0; i < arrayCadena.length; i++) {
            numero.add(0, Integer.parseInt(String.valueOf(arrayCadena[i])));
        }
    }

    public NumeroEnorme(int n) {
        this(n+"");
        if (n < 0) {
            try {
                throw new Exception("Sólo se admiten números naturales");
            } catch (Exception e) {
                System.out.println(e.getMessage());
                // e.printStackTrace();
            }
        }
    }

    // Métodos auxiliares internos
    private int longitud() {
        return numero.size();
    }

    public String toString() {
        String res = "";
        for (int i = 0; i < this.longitud(); i++) {
            res = numero.get(i) + res;
        }

        return res;
    }

    public int compareTo(NumeroEnorme n) {
        int res = 0;
        if (this.longitud() < n.longitud()) {
            res = -1;
        } else if (this.longitud() > n.longitud()) {
            res = 1;
        } else {
            boolean fin = false;
            int i = this.longitud()-1;
            while (!fin && i >= 0) {
                int n1 = this.numero.get(i);
                int n2 = n.numero.get(i);
                i++;
                if (n1 < n2) {
                    res = -1;
                    fin = true;
                } else if (n1 > n2) {
                    res = 1;
                    fin = true;
                }
            }
        }
        return res;
    }

    // Métodos de operaciones
    static public NumeroEnorme suma(String numero1, String numero2) {
        NumeroEnorme n1 = new NumeroEnorme(numero1);
        NumeroEnorme n2 = new NumeroEnorme(numero2);
        NumeroEnorme temp;
        if (n2.longitud() > n1.longitud()) {
            temp = n1;
            n1 = n2;
            n2 = temp;
        }
        int acarreo = 0;
        String res = "";
        for (int i = 0; i < n2.longitud(); i++) {
            int suma = n1.numero.get(i) + n2.numero.get(i) + acarreo;
            acarreo = suma / 10;
            suma = suma % 10;
            res = suma + res;
        }
        for (int i = n2.longitud()-1 + 1; i < n1.longitud(); i++) {
            int suma = n1.numero.get(i) + acarreo;
            acarreo = suma / 10;
            suma = suma % 10;
            res = suma + res;
        }
        if (acarreo != 0) {
            res = acarreo + res;
        }

        return new NumeroEnorme(res);
    }
}
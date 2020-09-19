package com.example.provasfollo.utility;

import java.lang.reflect.Method;

public class MyReflectionUtility {

    public static Method getMetodo(Class classe, String nomeMetodo) throws NoSuchMethodException {

        Method[] listaMetodi = classe.getMethods();
        for(Method m : listaMetodi){
            if(m.getName().equalsIgnoreCase(nomeMetodo)){
                return m;
            }

        }
        throw new NoSuchMethodException("Il metodo ricerato non esiste");
    }




}

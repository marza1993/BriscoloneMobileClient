package com.example.provasfollo.utility;

public class MathUtility
{

    // implementa l'operazione modulo (anche per numeri negativi)
    public static int mod(int a, int n)
    {
        int result = a % n;
        if ((result < 0 && n > 0) || (result > 0 && n < 0))
        {
            result += n;
        }
        return result;
    }

}
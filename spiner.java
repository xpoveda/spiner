
import java.io.*;
import java.util.*;

public class spiner
{

    public static final int COLUMNAS = 100;


    public static String[] variables;
    public static String   patron;

    public static ArrayList<ArrayList<String>> lista_sinonimos;

    public static String salida;

    public static boolean nopatron;

    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {

       if (args.length < 3 || args.length > 5)
       {
           System.out.println("USO: java -classpath . spiner patron.txt sinonimos.txt datos.txt [EOL] [NOPATRON]");
           System.out.println("USO: $variable*: Sustituye en patron.txt por lo que esta definido por la columna 'variable' en datos.txt");
           System.out.println("USO: [variable* y ]variable*: Elimina lo que hay en esa seccion del patron.txt si esta vacio en datos.txt. Se marca con un '-' en variables.txt");
           System.out.println("USO: {palabra}: Sustituye por uno de los sinonimos de sinonimos.txt");

           System.exit(0);
       }

       if (args.length == 5)
		nopatron = true;
       else	
		nopatron = false;

       patron          = leerpatron       (args[0]);
       lista_sinonimos = leersinonimos    (args[1]);

       variables       = leervariables    (args[2]);

       salida          = TratarVariables  (args[2]);      
       salida          = TratarSinonimos  (salida);


       //System.out.println("********* SALIDA **********");
     
       if (args.length >= 4)
       {
           if (args[3].equals("EOL"))
                salida = salida.replace("EOL","\n");
           else
                salida = salida.replace("EOL","");
       }
       else
           salida = salida.replace("EOL","");

       //System.out.println(salida);
         System.out.print(salida);
    }

    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////



    //******************************************************************
    // Leemos la primera fila de datos.txt y encapsulamos los nombres de
    // las variables en el array "variables".

    public static String[] leervariables(String s) throws IOException
    {
      //System.out.println("leervariables");

      FileReader f       = new FileReader(s);
      BufferedReader b   = new BufferedReader(f);

      String[] variables = new String[COLUMNAS];

      String s1          = b.readLine();
      StringTokenizer st = new StringTokenizer(s1, "|");

      int i = 0;
      while(st.hasMoreTokens())
      {
        variables[i] = rtrim(ltrim(st.nextToken()));

        if (nopatron)
            System.out.println(variables[i] + ": $" + variables[i] + "*");

        i++;
      }

      return(variables);
    }

    //******************************************************************
    // Leemos linea a linea del patron.txt encapsulando su informacion en
    // la variable "patron"de tipo string. Por cada salto de linea guardamos
    // los caracteres EOL que mas tarde nos serviran para devolver el string
    // con saltos de linea o todo seguido, segun nos convenga.

    public static String leerpatron(String s) throws IOException
    {
      //System.out.println("leerpatron");

      FileReader f     = new FileReader(s);
      BufferedReader b = new BufferedReader(f);

      String s1 = "";
      String s2 = "";

      while((s1 = b.readLine())!=null)
      {
        if (s1.indexOf("#") < 0)
       		s2 = s2 + s1 + "EOL";
      }

       return(s2);
    }



    //******************************************************************
    // Leemos las lineas de sinonimos.txt. Cada linea corresponde a un conjunto
    // de sinonimos separados por comas.

    public static ArrayList<ArrayList<String>> leersinonimos(String s) throws IOException
    {

     FileReader f     = new FileReader(s);
     BufferedReader b = new BufferedReader(f);

     ArrayList<ArrayList<String>> lista_sinonimos1 = new ArrayList<ArrayList<String>>();
     ArrayList<String>            sinonimos;

     String s1 = "";

     while((s1 = b.readLine())!=null)
     {
         sinonimos          = new ArrayList<String>();
         StringTokenizer st = new StringTokenizer(s1, ";");

         while(st.hasMoreTokens())
         {
          sinonimos.add(st.nextToken());
         }

         lista_sinonimos1.add(sinonimos);
     }

     return(lista_sinonimos1);
    }

    //******************************************************************
    // Nos saltamos la primera fila, donde estan el nombre de las variables
    // y vamos a por el resto donde estaran los datos variables por
    // columnas y pipes "|" como separador como resultado de un volcado
    // de la informacion de excel o spreadshets.
    //
    // Recorremos linea a linea del fichero de datos.txt para generar
    // una salida con los datos de las diferentes columnas que tenemos
    //
    // Cuando no hay dato en una columna se ha de poner "-".
    // Si esto ocurre se eliminara el contenido de ese dato del patron
    // automaticamente.


    public static String TratarVariables(String s) throws IOException
    {
       //System.out.println("TratarVariables");

       FileReader     f       = new FileReader(s);
       BufferedReader b       = new BufferedReader(f);

       String[]       valores = new String[COLUMNAS];

       String         cadena;

       int            columna, columnas, inicio, fin;

       String         s1 = "";
       String         s2 = "";


      cadena = b.readLine();

      while((cadena = b.readLine())!=null)
      {
          StringTokenizer st = new StringTokenizer(cadena, "|");

          columna = 0;
          while(st.hasMoreTokens())
          {
           valores[columna] = rtrim(ltrim(st.nextToken()));
           //System.out.println("VALORES:" + valores[columna]);
           columna++;
          }
          columnas = columna;

          columna = 0;
          s1 = patron;

          while (columna < columnas)
          {

           if (valores[columna].equals("-"))
           {
            inicio = s1.indexOf("[" + variables[columna] + "*");
            fin    = s1.indexOf("]" + variables[columna] + "*") + variables[columna].length() + 2;

            if (inicio >= 0)
              s1 = s1.substring(0, inicio) + s1.substring(fin,s1.length());

            s1 = s1.replace("$" + variables[columna] + "*", "");
           }
           else
           {          
            s1 = s1.replace("$" + variables[columna] + "*", aleatorio(valores[columna]));
            s1 = s1.replace("[" + variables[columna] + "*", "");
            s1 = s1.replace("]" + variables[columna] + "*", "");
           }

           columna++;
          }

          s2 = s2 + s1 + "\n";
      }

      return(s2);

    }


    //******************************************************************
    // A partir de la lista de sinonimos y de una palabra retornamos
    // un sinonimo (que puede ser la misma palabra) si aleatoriamente
    // asi ha salido o si no estuviera definido ningun sinonimo.

    public static String TratarSinonimos(String s)
    {
        //System.out.println("TratarSinonimos "+ s);

        int i = 0;
        String s1;

        int inicio, fin;
        String palabra;

        
        
        while (i < s.length())
        {
            s1 = s.substring(0,i);
            //System.out.println("TratarSinonimos "+ s1);

            inicio = s1.indexOf("{");
            fin    = s1.indexOf("}");
 

            if (inicio >= 0 && fin >= 0 && inicio<=fin)
            {
                //System.out.println("s1: " + s1);  
                //System.out.println("inicio: " + inicio + " fin: " + fin + " longitud: " + s1.length());
                palabra = s1.substring(inicio + 1, fin);
                s = s1.substring(0, inicio) + aleatorio(palabra) + s.substring(i, s.length());
                //System.out.println("sinonimo de : " + palabra + " en la posicion " + inicio);
            }
            i++;
            

        }

        return(s);
    }


    //******************************************************************
    // Se devuelve palabra aleatoria si existiera en sinonimos, sino la misma
    // palabra que hemos pasado por parametro

    public static String aleatorio(String s)
    {
       int i          = 0;
       int encontrado = 0;
       int posicion;

       String s1 = "";

       ArrayList<String> sinonimos;

       for (ArrayList<String> l : lista_sinonimos)
       {
         if (encontrado == 1)
            break;

         for (String k : l)
         {
            if (k.equals(s))
            {
                encontrado = 1;
                break;
            }
         }

         if (encontrado == 0)
            i++;
       }

       if (encontrado == 1)
       {
            sinonimos    = lista_sinonimos.get(i);

            //System.out.println("Por clave 1 elemento");
            if (s.substring(0,1).equals("k"))
            {
                posicion = 0;
                while (posicion == 0)
                {
                    Random rand  = new Random();
                    posicion = rand.nextInt(sinonimos.size());
                }
                return sinonimos.get(posicion);
            }

            //System.out.println("Por clave 2 elementos");
            if (s.substring(0,1).equals("2"))
            {
                posicion = 0;
                while (posicion == 0)
                {
                    Random rand  = new Random();
                    posicion = rand.nextInt(sinonimos.size());
                }
                s1 = sinonimos.get(posicion);

                if (sinonimos.size() > 1)
                {
                    posicion = 0;
                    while (posicion == 0 ||
                           sinonimos.get(posicion).equals(s1))
                    {
                        Random rand  = new Random();
                        posicion = rand.nextInt(sinonimos.size());
                    }

                    s1 = s1 + " o " + sinonimos.get(posicion);
                }
                return(s1);
            }

            //System.out.println("Caso simple");
            Random rand  = new Random();
            posicion = rand.nextInt(sinonimos.size());

            return sinonimos.get(posicion);
       }
       else
            return(s);
    }


    //******************************************************************
    // Eliminamos los espacios a la izquierda.

    public static String ltrim(String s)
    {
      int i = 0;
      while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
          i++;
      }
      return s.substring(i);
    }


    //******************************************************************
    // Eliminamos los espacios a la derecha.

    public static String rtrim(String s)
    {
      int i = s.length()-1;
      while (i >= 0 && Character.isWhitespace(s.charAt(i))) {
          i--;
      }
      return s.substring(0,i+1);
    }
}

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PrimosEspeciales {

    // Constantes
    public static final int MIN_NUMERO = 10;
    public static final int MAX_NUMERO = 100;
    public static final int UMBRAL_PRODUCTO = 15;
    public static final int NUMERO_HILOS = 4;

    // Clase:
    // - Comprueba si es primo -
    private class EsPrimo implements Callable<Boolean> {

        private int a_Numero = 0;

        // Constructor
        public EsPrimo (int p_Numero) {
            this.a_Numero = p_Numero;
        }

        // Comprobacion
        @Override
        public Boolean call() {
            boolean esPrimo = true;

            if  (a_Numero <= 1) {
                esPrimo = false;
            }

            for (int l_Divisor = 2; l_Divisor < a_Numero; l_Divisor++) {
                if (a_Numero % l_Divisor == 0) {
                    esPrimo = false; // Si da resto 0, no es primo
                }
            }
            return esPrimo;
        }
    }

    // Clase:
    // - Comprueba si la multiplicación de los dos dígitos es mayor que UMBRAL_PRODUCTO -
    private class ProductoDigitosMayor implements Callable<Boolean> {

        private int a_Numero = 0;

        // Constructor: recibe el número completo a evaluar
        public ProductoDigitosMayor(int p_Numero) {
            this.a_Numero = p_Numero;
        }

        // Comprobación
        @Override
        public Boolean call() {
            int l_Decenas = a_Numero / 10;
            int l_Unidades = a_Numero % 10;
            int l_Producto = l_Decenas * l_Unidades;

            return (l_Producto > UMBRAL_PRODUCTO);
        }
    }

    public static void main(String[] args) {

        PrimosEspeciales l_Aplicacion = new PrimosEspeciales();
        ThreadPoolExecutor l_Executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUMERO_HILOS);

        // Lista de tareas
        List<EsPrimo> l_ListaTareasEsPrimo = new ArrayList<EsPrimo>();
        List<ProductoDigitosMayor> l_ListaTareasSuma = new ArrayList<ProductoDigitosMayor>();

        // Lista de resultados
        List<Future<Boolean>> l_ResultadosPrimo = null;
        List<Future<Boolean>> l_ResultadosSuma = null;

        // Contador
        int l_Contador = 0;

        // Añadimos las tareas a las listas
        for (l_Contador = MIN_NUMERO; l_Contador < MAX_NUMERO; l_Contador++)
        {
            l_ListaTareasEsPrimo.add(l_Aplicacion.new EsPrimo(l_Contador));
            l_ListaTareasSuma.add(l_Aplicacion.new ProductoDigitosMayor(l_Contador));
        }

        // Pasamos tareas al Executor
        try {
            l_ResultadosPrimo = l_Executor.invokeAll(l_ListaTareasEsPrimo);
            l_ResultadosSuma = l_Executor.invokeAll(l_ListaTareasSuma);

        } catch (InterruptedException p_Excepcion) {
            System.out.println("ERROR: Ha fallado el invokeAll() con error: " + p_Excepcion);
        }

        // Apagamos executor
        l_Executor.shutdown();

        // Mostramos los números que cumplen
        System.out.println("Número que cumplen ambas condiciones: ");
        
        for (l_Contador  = 0; l_Contador < MAX_NUMERO; l_Contador++) {
            try {
                
                boolean l_EsPrimo = l_ResultadosPrimo.get(l_Contador).get();
                boolean l_EsSumaMultiplo = l_ResultadosSuma.get(l_Contador).get();
                
                if (l_EsPrimo && l_EsSumaMultiplo) {
                    System.out.println("El número "+l_Contador+" cumple ambas condiciones.");
                }
            } catch (InterruptedException | ExecutionException p_Excepcion) {
                System.out.println("ERROR: Ha fallado el get() de un Future con error: " + p_Excepcion);
            }
        }
        
    }
}

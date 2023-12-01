import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
//Libreria que contiene lectura y escritura es java.io........................;
class RepuestoElectronico {
    protected String nombre;
    protected double precio;

    public RepuestoElectronico(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void mostrarDetalles() {
        System.out.println("Nombre: " + nombre + " - Precio: $" + precio);
    }
}

class TecladoElectronico extends RepuestoElectronico {
    private String tipo;

    public TecladoElectronico(String nombre, double precio, String tipo) {
        super(nombre, precio);
        this.tipo = tipo;
    }

    @Override
    public void mostrarDetalles() {
        super.mostrarDetalles();
        System.out.println("Tipo: " + tipo);
    }
}

class TarjetaDeVideo extends RepuestoElectronico {
    private int memoria;

    public TarjetaDeVideo(String nombre, double precio, int memoria) {
        super(nombre, precio);
        this.memoria = memoria;
    }

    @Override
    public void mostrarDetalles() {
        super.mostrarDetalles();
        System.out.println("Memoria: " + memoria + " GB");
    }
}

class Compra {
    private Map<String, Integer> items;
    private String nombreCliente;
    private String direccionCliente;
    private String telefonoCliente;
    private double totalCompra;

    public Compra(Map<String, Integer> items, String nombreCliente, String direccionCliente, String telefonoCliente, double totalCompra) {
        this.items = items;
        this.nombreCliente = nombreCliente;
        this.direccionCliente = direccionCliente;
        this.telefonoCliente = telefonoCliente;
        this.totalCompra = totalCompra;
    }
    public void mostrarDetalles() {
        System.out.println("Cliente: " + nombreCliente);
        System.out.println("Dirección: " + direccionCliente);
        System.out.println("Número de teléfono: " + telefonoCliente);
        System.out.println("Total de la compra: $" + totalCompra);
        System.out.println("Productos agregados:");
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String nombreRepuesto = entry.getKey();
            int cantidad = entry.getValue();
            System.out.println("Nombre: " + nombreRepuesto + " - Cantidad: " + cantidad);
        }
        System.out.println("==============================");
        String csvFile = "factura.csv"; // Nombre del archivo CSV
        try (FileWriter writer = new FileWriter(csvFile)) {
            writeLine(writer, new String[]{" ============ Factura ============"});
            writeLine(writer, new String[]{"Cliente:", nombreCliente}); 
            writeLine(writer, new String[]{"Direccion:", direccionCliente});
            writeLine(writer, new String[]{"Número de telefono:", telefonoCliente});
            writeLine(writer, new String[]{"Total de la compra: $", String.valueOf(totalCompra)});
            writeLine(writer, new String[]{" ================================="});
            System.out.println("¡Factura creada correctamente en " + csvFile + "!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Método para escribir una línea en el archivo CSV
    private static void writeLine(FileWriter writer, String[] values) throws IOException {
        for (int i = 0; i < values.length; i++) {
            writer.append(escapeSpecialCharacters(values[i]));
            if (i < values.length - 1) {
                writer.append(",");
            }
        }
        writer.append("\n");
    }
    // Método para escapar caracteres especiales en CSV (por ejemplo, comas, comillas)
    private static String escapeSpecialCharacters(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = "\"" + value.replaceAll("\"", "\"\"") + "\"";
        }
        return value;
    }
}

class TiendaRepuestos {
    Map<String, RepuestoElectronico> inventario;
    Map<String, Integer> carrito;
    List<Compra> historialCompras;

    public TiendaRepuestos() {
        this.inventario = new HashMap<>();
        this.carrito = new HashMap<>();
        this.historialCompras = new ArrayList<>();
    }

    public void agregarRepuesto(RepuestoElectronico repuesto) {
        inventario.put(repuesto.getNombre(), repuesto);
    }

    public void agregarAlCarrito(String nombreRepuesto, int cantidad) {
        RepuestoElectronico repuesto = inventario.get(nombreRepuesto);
        if (repuesto != null) {
            carrito.put(nombreRepuesto, carrito.getOrDefault(nombreRepuesto, 0) + cantidad);
            System.out.println("Agregado al carrito: " + nombreRepuesto + " - Cantidad: " + cantidad);
        } else {
            System.out.println("Repuesto no encontrado en el inventario.");
        }
    }

    public void mostrarInventario(String categoria) {
        System.out.println("Inventario de la tienda - Categoría: " + categoria);
        int numeroProducto = 1;

        for (RepuestoElectronico repuesto : inventario.values()) {
            if ((repuesto instanceof TecladoElectronico && categoria.equals("Teclados")) ||
                    (repuesto instanceof TarjetaDeVideo && categoria.equals("Tarjetas de Video"))) {
                System.out.println(numeroProducto + ". " + repuesto.getNombre());
                numeroProducto++;
            }
        }
    }

    public void mostrarCarrito() {
        System.out.println("Carrito de compras:");
        for (Map.Entry<String, Integer> entry : carrito.entrySet()) {
            String nombreRepuesto = entry.getKey();
            int cantidad = entry.getValue();
            RepuestoElectronico repuesto = inventario.get(nombreRepuesto);
            if (repuesto != null) {
                System.out.println("Nombre: " + nombreRepuesto + " - Cantidad: " + cantidad + " - Subtotal: $" + repuesto.getPrecio() * cantidad);
            }
        }
        System.out.println("Total en el carrito: $" + calcularTotalCarrito());
    }

    public double calcularTotalCarrito() {
        double total = 0.0;
        for (Map.Entry<String, Integer> entry : carrito.entrySet()) {
            String nombreRepuesto = entry.getKey();
            int cantidad = entry.getValue();
            RepuestoElectronico repuesto = inventario.get(nombreRepuesto);
            if (repuesto != null) {
                total += repuesto.getPrecio() * cantidad;
            }
        }
        return total;
    }

    public void eliminarDelCarrito(String nombreRepuesto, int cantidad) {
        if (carrito.containsKey(nombreRepuesto)) {
            int cantidadActual = carrito.get(nombreRepuesto);
            if (cantidadActual <= cantidad) {
                carrito.remove(nombreRepuesto);
                System.out.println("Se eliminó completamente del carrito: " + nombreRepuesto);
            } else {
                carrito.put(nombreRepuesto, cantidadActual - cantidad);
                System.out.println("Se eliminaron " + cantidad + " unidades de " + nombreRepuesto + " del carrito.");
            }
        } else {
            System.out.println("El repuesto no está en el carrito.");
        }
    }

    public RepuestoElectronico buscarRepuesto(String nombre) {
        return inventario.get(nombre);
    }

    public void agregarCompra(Compra compra) {
        historialCompras.add(compra);
        System.out.println("Compra registrada con éxito.");
    }

    public void mostrarTodasLasCompras() {
        if (historialCompras.isEmpty()) {
            System.out.println("No hay compras registradas.");
        } else {
            System.out.println("Historial de compras:");
            for (Compra compra : historialCompras) {
                compra.mostrarDetalles();
            }
        }
    }
}

public class TiendaRC {
    public static void main(String[] args) {
        TiendaRepuestos tienda = new TiendaRepuestos();

        tienda.agregarRepuesto(new TecladoElectronico("Teclado Gamer", 80.0, "Mecánico"));
        tienda.agregarRepuesto(new TarjetaDeVideo("Nvidia GTX 1050", 150.0, 4));
        tienda.agregarRepuesto(new TecladoElectronico("Teclado Retroiluminado", 60.0, "Membrana"));
        tienda.agregarRepuesto(new TarjetaDeVideo("AMD RX 580", 200.0, 8));
        tienda.agregarRepuesto(new RepuestoElectronico("8GB de RAM", 50.0));
        tienda.agregarRepuesto(new RepuestoElectronico("16GB de RAM", 100.0));
        tienda.agregarRepuesto(new RepuestoElectronico("32GB de RAM", 150.0));
        tienda.agregarRepuesto(new RepuestoElectronico("Nvidia RTX 3060", 400.0));
        tienda.agregarRepuesto(new RepuestoElectronico("AMD Radeon RX 6700 XT", 450.0));
        tienda.agregarRepuesto(new RepuestoElectronico("Nvidia GTX 1660 Super", 350.0));
        tienda.agregarRepuesto(new RepuestoElectronico("Disco duro 128GB", 80.0));
        tienda.agregarRepuesto(new RepuestoElectronico("Disco duro 256GB", 120.0));
        tienda.agregarRepuesto(new RepuestoElectronico("Disco duro 512GB", 200.0));
        tienda.agregarRepuesto(new RepuestoElectronico("Fuente de poder", 60.0));
        tienda.agregarRepuesto(new RepuestoElectronico("Cable HDMI", 10.0));
        tienda.agregarRepuesto(new RepuestoElectronico("Cable VGA", 8.0));
        tienda.agregarRepuesto(new RepuestoElectronico("Cable USB", 5.0));
        tienda.agregarRepuesto(new RepuestoElectronico("Intel Core i5 10th Gen", 250.0));
        tienda.agregarRepuesto(new RepuestoElectronico("AMD Ryzen 7 5800X", 400.0));
        tienda.agregarRepuesto(new RepuestoElectronico("Intel Core i9 11th Gen", 600.0));
        tienda.agregarRepuesto(new RepuestoElectronico("Placa madre ASUS", 180.0));
        tienda.agregarRepuesto(new RepuestoElectronico("Placa madre MSI", 200.0));
        tienda.agregarRepuesto(new RepuestoElectronico("Placa madre Gigabyte", 220.0));
        Scanner scanner = new Scanner(System.in);
        while (true) {
            mostrarMenuPrincipal();


            int opcionMenuPrincipal = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea después del nextInt()

            switch (opcionMenuPrincipal) {
                case 1:
                    menuComprar(tienda, scanner);
                    break;
                case 2:
                    tienda.mostrarTodasLasCompras();
                    break;
                case 3:
                    visualizarContenidoCSV();
                    break;
                case 4:
                    System.out.println("Saliendo del programa. ¡Hasta luego!");
                    System.exit(0);
                default:
                    System.out.println("Opción no válida. Inténtelo de nuevo.");
                    break;
            }
        }
    }
    private static void mostrarMenuPrincipal() {
        try (BufferedReader br = new BufferedReader(new FileReader("Menuprincipal.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            System.out.print("Seleccione una opción: ");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo Menuprincipal.txt: " + e.getMessage());
        }
    }
    private static void menuComprar(TiendaRepuestos tienda, Scanner scanner) {
        while (true) {
            System.out.println("\n===========MENU DE COMPRAS==========");
            System.out.println("1. Para laptop");
            System.out.println("2. Para computadora");
            System.out.println("3. Ver total de la compra");
            System.out.println("4. Eliminar artículo agregado al carrito");
            System.out.println("5. Finalizar compra");
            System.out.println("6. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            int opcionPrincipal = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea después del nextInt()

            switch (opcionPrincipal) {
                case 1:
                    menuLaptop(tienda, scanner);
                    break;
                case 2:
                    menuComputadora(tienda, scanner);
                    break;
                case 3:
                    tienda.mostrarCarrito();
                    break;
                case 4:
                    menuEliminarDelCarrito(tienda, scanner);
                    break;
                case 5:
                    menuFinalizarCompra(tienda, scanner);
                    return;
                case 6:
                    return; // Volver al menú principal
                default:
                    System.out.println("Opción no válida. Inténtelo de nuevo.");
                    break;
            }
        }
    }

    private static void menuLaptop(TiendaRepuestos tienda, Scanner scanner) {
        while (true) {
            System.out.println("\n===========MENU LAPTOP==========");
            System.out.println("1. Memoria RAM");
            System.out.println("2. Tarjeta gráfica externa");
            System.out.println("3. Disco duro HDD/SSD");
            System.out.println("4. Volver al menú principal");

            System.out.print("Seleccione una opción: ");
            int opcionLaptop = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea después del nextInt()

            switch (opcionLaptop) {
                case 1:
                    menuMemoriaRAM(tienda, scanner);
                    break;
                case 2:
                    menuTarjetaGrafica(tienda, scanner);
                    break;
                case 3:
                    menuDiscoDuro(tienda, scanner);
                    break;
                case 4:
                    return; // Volver al menú principal
                default:
                    System.out.println("Opción no válida. Inténtelo de nuevo.");
                    break;
            }
        }
    }

    private static void menuMemoriaRAM(TiendaRepuestos tienda, Scanner scanner) {
        while (true) {
            System.out.println("\n===========MENU MEMORIA RAM==========");
            System.out.println("1. 8GB de RAM");
            System.out.println("2. 16GB de RAM");
            System.out.println("3. 32GB de RAM");
            System.out.println("4. Volver al menú anterior");

            System.out.print("Seleccione una opción: ");
            int opcionRAM = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea después del nextInt
            switch (opcionRAM) {
                case 1:
                    agregarAlCarrito(tienda, "8GB de RAM", scanner);
                    break;
                case 2:
                    agregarAlCarrito(tienda, "16GB de RAM", scanner);
                    break;
                case 3:
                    agregarAlCarrito(tienda, "32GB de RAM", scanner);
                    break;
                case 4:
                    return; // Volver al menú anterior
                default:
                    System.out.println("Opción no válida. Inténtelo de nuevo.");
                    break;
            }
        }
    }

    private static void menuTarjetaGrafica(TiendaRepuestos tienda, Scanner scanner) {
        while (true) {
            System.out.println("\n===========MENU TARJETA GRÁFICA EXTERNA==========");
            System.out.println("1. Nvidia RTX 3060");
            System.out.println("2. AMD Radeon RX 6700 XT");
            System.out.println("3. Nvidia GTX 1660 Super");
            System.out.println("4. Volver al menú anterior");

            System.out.print("Seleccione una opción: ");
            int opcionTarjetaGrafica = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea después del nextInt()

            switch (opcionTarjetaGrafica) {
                case 1:
                    agregarAlCarrito(tienda, "Nvidia RTX 3060", scanner);
                    break;
                case 2:
                    agregarAlCarrito(tienda, "AMD Radeon RX 6700 XT", scanner);
                    break;
                case 3:
                    agregarAlCarrito(tienda, "Nvidia GTX 1660 Super", scanner);
                    break;
                case 4:
                    return; // Volver al menú anterior
                default:
                    System.out.println("Opción no válida. Inténtelo de nuevo.");
                    break;
            }
        }
    }

    private static void menuDiscoDuro(TiendaRepuestos tienda, Scanner scanner) {
        while (true) {
            System.out.println("\n===========MENU DISCO DURO HDD/SSD==========");
            System.out.println("1. Disco duro 128GB");
            System.out.println("2. Disco duro 256GB");
            System.out.println("3. Disco duro 512GB");
            System.out.println("4. Volver al menú anterior");

            System.out.print("Seleccione una opción: ");
            int opcionDiscoDuro = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea después del nextInt()

            switch (opcionDiscoDuro) {
                case 1:
                    agregarAlCarrito(tienda, "Disco duro 128GB", scanner);
                    break;
                case 2:
                    agregarAlCarrito(tienda, "Disco duro 256GB", scanner);
                    break;
                case 3:
                    agregarAlCarrito(tienda, "Disco duro 512GB", scanner);
                    break;
                case 4:
                    return; // Volver al menú anterior
                default:
                    System.out.println("Opción no válida. Inténtelo de nuevo.");
                    break;
            }
        }
    }

    private static void menuComputadora(TiendaRepuestos tienda, Scanner scanner) {
        while (true) {
            System.out.println("\n===========MENU COMPUTADORA==========");
            System.out.println("1. Procesador");
            System.out.println("2. Placa madre");
            System.out.println("3. Fuente de poder");
            System.out.println("4. Volver al menú principal");

            System.out.print("Seleccione una opción: ");
            int opcionComputadora = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea después del nextInt()

            switch (opcionComputadora) {
                case 1:
                    menuProcesador(tienda, scanner);
                    break;
                case 2:
                    menuPlacaMadre(tienda, scanner);
                    break;
                case 3:
                    agregarAlCarrito(tienda, "Fuente de poder", scanner);
                    break;
                case 4:
                    return; // Volver al menú principal
                default:
                    System.out.println("Opción no válida. Inténtelo de nuevo.");
                    break;
            }
        }
    }

    private static void menuProcesador(TiendaRepuestos tienda, Scanner scanner) {
        while (true) {
            System.out.println("\n===========MENU PROCESADOR==========");
            System.out.println("1. Intel Core i5 10th Gen");
            System.out.println("2. AMD Ryzen 7 5800X");
            System.out.println("3. Intel Core i9 11th Gen");
            System.out.println("4. Volver al menú anterior");

            System.out.print("Seleccione una opción: ");
            int opcionProcesador = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea después del nextInt()

            switch (opcionProcesador) {
                case 1:
                    agregarAlCarrito(tienda, "Intel Core i5 10th Gen", scanner);
                    break;
                case 2:
                    agregarAlCarrito(tienda, "AMD Ryzen 7 5800X", scanner);
                    break;
                case 3:
                    agregarAlCarrito(tienda, "Intel Core i9 11th Gen", scanner);
                    break;
                case 4:
                    return; // Volver al menú anterior
                default:
                    System.out.println("Opción no válida. Inténtelo de nuevo.");
                    break;
            }
        }
    }

    private static void menuPlacaMadre(TiendaRepuestos tienda, Scanner scanner) {
        while (true) {
            System.out.println("\n===========MENU PLACA MADRE==========");
            System.out.println("1. Placa madre ASUS");
            System.out.println("2. Placa madre MSI");
            System.out.println("3. Placa madre Gigabyte");
            System.out.println("4. Volver al menú anterior");

            System.out.print("Seleccione una opción: ");
            int opcionPlacaMadre = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea después del nextInt()

            switch (opcionPlacaMadre) {
                case 1:
                    agregarAlCarrito(tienda, "Placa madre ASUS", scanner);
                    break;
                case 2:
                    agregarAlCarrito(tienda, "Placa madre MSI", scanner);
                    break;
                case 3:
                    agregarAlCarrito(tienda, "Placa madre Gigabyte", scanner);
                    break;
                case 4:
                    return; // Volver al menú anterior
                default:
                    System.out.println("Opción no válida. Inténtelo de nuevo.");
                    break;
            }
        }
    }
    private static void menuEliminarDelCarrito(TiendaRepuestos tienda, Scanner scanner) {
        System.out.print("Ingrese el nombre del repuesto que desea eliminar del carrito: ");
        String nombreRepuesto = scanner.nextLine();

        System.out.print("Ingrese la cantidad que desea eliminar: ");
        int cantidad = scanner.nextInt();
        scanner.nextLine(); // Consumir la nueva línea después
        tienda.eliminarDelCarrito(nombreRepuesto, cantidad);
    }

    private static void menuFinalizarCompra(TiendaRepuestos tienda, Scanner scanner) {
        System.out.println("\n===========FINALIZAR COMPRA==========");

        tienda.mostrarCarrito();

        System.out.println("Necesitamos datos para su factura");
        System.out.print("Nombre: ");
        String nombreCliente = scanner.nextLine();

        System.out.print("Dirección: ");
        String direccionCliente = scanner.nextLine();

       
        String telefonoCliente = obtenerTelefonoValidado(scanner);
       
        System.out.println("\nFactura de la compra:");
        System.out.println("Cliente: " + nombreCliente);
        System.out.println("Dirección: " + direccionCliente);
        System.out.println("Número de teléfono: " + telefonoCliente);
        System.out.println("Total de la compra: $" + tienda.calcularTotalCarrito());
        System.out.println("Productos agregados:");
        tienda.mostrarCarrito();

        // Puedes implementar la lógica para guardar la factura en un archivo aquí

        Compra compra = new Compra(tienda.carrito, nombreCliente, direccionCliente, telefonoCliente, tienda.calcularTotalCarrito());
        tienda.agregarCompra(compra);

        System.out.println("¡Gracias por su compra!");
        return;
    }

    private static void agregarAlCarrito(TiendaRepuestos tienda, String nombreRepuesto, Scanner scanner) {
        System.out.print("Ingrese la cantidad que desea comprar: ");
        int cantidad = scanner.nextInt();
        scanner.nextLine(); // Consumir la nueva línea después del nextInt()

        tienda.agregarAlCarrito(nombreRepuesto, cantidad);
    }
    private static String obtenerTelefonoValidado(Scanner scanner) {
        while (true) {
            System.out.print("Número de teléfono (solo 10 dígitos): ");
            String telefonoCliente = scanner.nextLine();
    
            // Verificar si la entrada contiene solo dígitos y tiene longitud 10
            if (telefonoCliente.matches("\\d{10}")) {
                return telefonoCliente;
            } else {
                System.out.println("Formato de número incorrecto. Ingresa 10 dígitos numéricos.");
            }
        }
    }
    private static void visualizarContenidoCSV() {
        String csvFile = "factura.csv"; // Nombre del archivo CSV
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            System.out.println("Contenido del archivo CSV:");
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                for (String value : data) {
                    System.out.print(value + " | ");
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package edu;

import java.util.Scanner;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import edu.eci.arsw.blueprints.config.AppConfig;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.services.BlueprintsServices;

public class BlueprintsMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(AppConfig.class);
        BlueprintsServices blueprintService = context.getBean(BlueprintsServices.class);

        System.out.println("Para iniciar presiona cualquier tecla\n");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Seleccione una opcion del programa de Arquitectura: \n\r"
                    + "1.Añadir un plano \n\r"
                    + "2.Buscar un plano \n\r"
                    + "3.Buscar un plano por autor \n\r"
                    + "4.Salir \n\r");
            int opcion = scanner.nextInt();
            scanner.nextLine();
            switch (opcion) {
                case 1:
                    System.out.println("Ingrese los detalles del nuevo plano:");
                    System.out.print("Autor: ");
                    String author = scanner.nextLine();
                    System.out.print("Punto 1 (x y): ");
                    int x1 = scanner.nextInt();
                    int y1 = scanner.nextInt();
                    System.out.print("Punto 2 (x y): ");
                    int x2 = scanner.nextInt();
                    int y2 = scanner.nextInt();
                    scanner.nextLine(); // Limpiar salto de línea
                    System.out.print("Nombre del plano: ");
                    String nameBp = scanner.nextLine();
                    Point[] points = new Point[] { new Point(x1, y1), new Point(x2, y2) };
                    Blueprint bp = new Blueprint(author, nameBp, points);
                    try {
                        blueprintService.addNewBlueprint(bp);
                    } catch (Exception e) {
                        System.out.println("Error al añadir el plano: " + e.getMessage());
                    }
                    break;
                case 2:
                    System.out.print("Autor: ");
                    String auth = scanner.nextLine();
                    System.out.print("Nombre del plano: ");
                    String nameBp2 = scanner.nextLine();
                    try {
                        System.out.println("Plano encontrado: " + blueprintService.getBlueprint(auth, nameBp2));
                    } catch (Exception e) {
                        System.out.println("Error al buscar el plano: " + e.getMessage());
                    }
                    break;

                case 3:
                    System.out.print("Autor: ");
                    String auth2 = scanner.nextLine();
                    try {
                        System.out.println("Planos del autor: " + blueprintService.getBlueprintsByAuthor(auth2));
                    } catch (Exception e) {
                        System.out.println("Error al buscar los planos: " + e.getMessage());
                    }
                    break;
                case 4:
                    System.out.println("Saliendo...");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Opción no válida");
            }
        }
    }
}

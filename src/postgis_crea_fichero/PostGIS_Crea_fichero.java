package postgis_crea_fichero;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex
 */
public class PostGIS_Crea_fichero {

    private static ArrayList<LineaFichero> lineasFichero = new ArrayList();
    private static Conexion c = new Conexion();

    public static void cargarFichero() {

        File text = new File("C:\\Users\\Alex\\Documents\\NetBeansProjects\\PostGIS_Crea_fichero\\incidents.txt");

        Scanner sc;
        try {
            sc = new Scanner(text);

            while (sc.hasNext()) {

                String fecha = sc.next();
                double longitud = sc.nextDouble();
                double latitud = sc.nextDouble();
                LineaFichero lf = new LineaFichero();
                lf.setFecha(fecha);
                lf.setLongitud(longitud / 1000);
                lf.setLatitud(latitud / 1000);
                lineasFichero.add(lf);

            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PostGIS_Crea_fichero.class.getName()).log(Level.SEVERE, null, ex);
        }

        Collections.sort(PostGIS_Crea_fichero.lineasFichero,
                (o1, o2) -> o1.getFecha().compareTo(o2.getFecha()));

    }

    public static void guardarBarrios() {

        try {
            Connection con = c.conectarBD();

            for (int i = 0; i < lineasFichero.size(); i++) {

                Statement stmt = null;
                stmt = con.createStatement();

                ResultSet rs = stmt.executeQuery("SELECT name FROM public.nyc_neighborhoods WHERE ST_Contains(ST_Transform(geom, 4326), ST_SetSRID(ST_Point("
                        + lineasFichero.get(i).getLongitud() + "," + lineasFichero.get(i).getLatitud() + "), 4326));");

                while (rs.next()) {

                    String name = rs.getString("name");
                    lineasFichero.get(i).setBarrio(name);

                }

                rs.close();
                stmt.close();
            }

            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(PostGIS_Crea_fichero.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void cargarFichero2() {

        BufferedReader objReader = null;
        try {
            String strCurrentLine;
            objReader = new BufferedReader(new FileReader("C:\\Users\\Alex\\Documents\\NetBeansProjects\\PostGIS_Crea_fichero\\incidents2.txt"));
            while ((strCurrentLine = objReader.readLine()) != null) {

                String[] splited = strCurrentLine.split(",");

                String fecha = splited[0];
                double longitud = Double.parseDouble(splited[1]);
                double latitud = Double.parseDouble(splited[2]);
                String barrio = splited[3];
                LineaFichero lf = new LineaFichero();
                lf.setFecha(fecha);
                lf.setLongitud(longitud);
                lf.setLatitud(latitud);
                lf.setBarrio(barrio);
                lineasFichero.add(lf);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objReader != null) {
                    objReader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    public static void mostrarLineasFichero() {

        for (int i = 0; i < lineasFichero.size(); i++) {
            System.out.println(lineasFichero.get(i).getFecha() + "  " + lineasFichero.get(i).getLongitud() + "  " + lineasFichero.get(i).getLatitud() + " " + lineasFichero.get(i).getBarrio());
        }
    }

    private static void crearFichero() {

        try {
            File myObj = new File("incidentes_v2.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();

        }

        FileWriter myWriter;
        try {
            myWriter = new FileWriter("incidentes_v2.txt");
            for (int i = 0; i < lineasFichero.size(); i++) {
                myWriter.write(lineasFichero.get(i).getFecha().substring(0, 7) + "," + lineasFichero.get(i).getLongitud() + "," + lineasFichero.get(i).getLatitud() + "," 
                        + lineasFichero.get(i).getBarrio()+"\n");
            }
            myWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(PostGIS_Crea_fichero.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) throws FileNotFoundException {
        cargarFichero();
        guardarBarrios();
        crearFichero();
        //mostrarLineasFichero();

    }

}

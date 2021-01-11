
package postgis_crea_fichero;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Alex
 */
public class Conexion {

    public Connection conectarBD() {
      Connection c = null;
      try {
         Class.forName("org.postgresql.Driver");
         c = DriverManager
            .getConnection("jdbc:postgresql://localhost:5432/nyc",
            "postgres", "postgres");
         
      } catch (Exception e) {
         e.printStackTrace();
         System.err.println(e.getClass().getName()+": "+e.getMessage());
         System.exit(0);
      }
      
      System.out.println("Opened database successfully");
      return c;
    }

    
}

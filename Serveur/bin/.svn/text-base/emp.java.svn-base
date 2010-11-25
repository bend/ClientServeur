package cours;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DatabaseMetaData;
import java.sql.ResultSetMetaData;

public class Employes {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Chargement du driver
		try {
			Class.forName("org.postgresql.Driver");
		} catch (Exception E) {
			System.err.println("Impossible de Charger le driver.");
			return;
		}
		
		try {
			
			/////////////// Exemple 1 ///////////
			// Connexion à la base
			Connection conn = DriverManager.getConnection("jdbc:postgresql://postgres-info/base5a00?user=user5a00&password=p00");

			// Statement
			Statement stmt = conn.createStatement();
			
			// Requête
			ResultSet rset = stmt.executeQuery("SELECT * FROM employes");
			while (rset.next()) {
				String nom = rset.getString("nom");
				String prenom = rset.getString("prenom");
				System.out.println(nom + " " + prenom);
			}
			
			// fermeture du résultat
			rset.close();
			
			// Fermeture statement
			stmt.close();
			
			
//			/////////////// Exemple 2 ///////////
//			// Connexion à la base
//			Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:8890/iut2?user=fbm");
//
//			// Statement
//			Statement stmt = conn.createStatement();
//			
//			// Requête
//			stmt.executeUpdate("INSERT INTO employes (nom, prenom) VALUES ('Goulian', 'Jérôme')");
//			
//			// Fermeture statement
//			stmt.close();
			
			
//			/////////////// Exemple 3 ///////////
//			// Connexion à la base
//			Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:8890/iut2?user=fbm");
//			
//			// Création d’un statement préparé à l’avance et appelé avec des paramètres
//			PreparedStatement stmt = conn.prepareStatement ("SELECT * FROM employes WHERE nom LIKE ? AND prenom LIKE ?");
//
//			// Préparation et exécution de la requête
//			stmt.setString(1, "Ma%");		// 1ème paramètre de la requête
//			stmt.setString(2, "%pe");		// 2ème paramètre de la requête
//
//			// Requête
//			ResultSet rset = stmt.executeQuery();
//			while (rset.next()) {
//				System.out.println (rset.getInt("id") + " " + rset.getString("nom") + " " + rset.getString("prenom"));
//			}
//
//			// fermeture du résultat
//			rset.close();
//			
//			// Fermeture statement
//			stmt.close();
			
			
//			/////////////// Exemple 4 ///////////
//			// Connexion à la base
//			Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:8890/base5a12?user=user5a12&password=p12");
//
//			// MetaData sur ResultSet
//			DatabaseMetaData data = conn.getMetaData();
//			String productName = data.getDatabaseProductName();
//			String productVersion = data.getDatabaseProductVersion();
//			System.out.println("productName : " + productName + " " + productVersion);
//			
//			// Statement
//			Statement stmt = conn.createStatement();
//			
//			// Requête
//			ResultSet rset = stmt.executeQuery("SELECT * FROM employes");
//			
//			// MetaData sur ResultSet
//			ResultSetMetaData restMetaData = rset.getMetaData();
//			int nbColumn = restMetaData.getColumnCount();
//			for (int i=1; i<nbColumn+1; i++) {
//				String name = rset.getMetaData().getColumnName(i);
//				int type = rset.getMetaData().getColumnType(i);
//				System.out.println(i + " " + name + " " + type);
//			}
//			
//			// Résultats
//			while (rset.next()) {
//				String nom = rset.getString("nom");
//				String prenom = rset.getString("prenom");
//				System.out.println(nom + " " + prenom);
//			}
//			
//			// fermeture du résultat
//			rset.close();
//			
//			// Fermeture statement
//			stmt.close();
			
			conn.close();
			
		} catch (SQLException E) {
			System.err.println("SQLException: " + E.getMessage());
			System.err.println("SQLState:     " + E.getSQLState());
		}
	}
}

package db;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBOperations {

	/**
	 * Create imdb database
	 */
	public void createNewDatabase() {

		String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/database/imdb.db";

		try {
			Connection conn = DriverManager.getConnection(url);
			if (conn != null) {
				DatabaseMetaData meta = conn.getMetaData();
				System.out.println("The driver name is " + meta.getDriverName());
				System.out.println("A new database has been created.");
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Create table to store movie details
	 */
	public void createTable() {

		String sql = "CREATE TABLE imdb250 (movie varchar(250), year varchar(10), rating varchar(10))";
		Connection conn = null;
		Statement stmt = null;

		try {

			conn = connect();
			stmt = conn.createStatement();
			stmt.execute(sql);

		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	/**
	 * Connect to the test.db database
	 *
	 * @return the Connection object
	 */
	public Connection connect() {
		// SQLite connection string
		String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/database/imdb.db";
		System.out.println(url);
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	/**
	 * Insert a new row into the imdb table
	 *
	 * @param movie
	 * @param year
	 * @param rating
	 */
	public void insert(String movie, String year, String rating) {
		String sql = "INSERT INTO imdb250(movie,year,rating) VALUES(?,?,?)";

		try {
			Connection conn = connect();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, movie);
			pstmt.setString(2, year);
			pstmt.setString(3, rating);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Display imdb250 table data
	 */
	public void displayTable() {
		String sql = "SELECT * FROM imdb250";
		Connection conn = null;
		Statement stmt = null;

		try {
			conn = connect();
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				System.out.println("Movie Name : " + rs.getString(1) + ", " + "Movie Release Year : " + rs.getString(2)
						+ ", " + "IMDB Rating : " + rs.getString(3));

			}

		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	/**
	 * Get movie details
	 */
	public String getDetails(String column, String movieName) {

		String sql = "SELECT " + column + " FROM imdb250 WHERE movie = " + movieName;
		Connection conn = null;
		Statement stmt = null;
		String result = null;

		try {
			conn = connect();
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				result = rs.getString(1);
			}

		} catch (SQLException e) {
			System.out.println(e);
		}
		return result;
	}

}

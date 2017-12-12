/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class AirBooking{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	static Scanner sc = new Scanner(System.in);
	
	public AirBooking(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + AirBooking.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		AirBooking esql = null;
		
		try{
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new AirBooking (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Passenger");
				System.out.println("2. Book Flight");
				System.out.println("3. Review Flight");
				System.out.println("4. Insert or Update Flight");
				System.out.println("5. List Flights From Origin to Destination");
				System.out.println("6. List Most Popular Destinations");
				System.out.println("7. List Highest Rated Destinations");
				System.out.println("8. List Flights to Destination in order of Duration");
				System.out.println("9. Find Number of Available Seats on a given Flight");
				System.out.println("10. < EXIT");
				
				switch (readChoice()){
					case 1: AddPassenger(esql); break;
					case 2: BookFlight(esql); break;
					case 3: TakeCustomerReview(esql); break;
					case 4: InsertOrUpdateRouteForAirline(esql); break;
					case 5: ListAvailableFlightsBetweenOriginAndDestination(esql); break;
					case 6: ListMostPopularDestinations(esql); break;
					case 7: ListHighestRatedRoutes(esql); break;
					case 8: ListFlightFromOriginToDestinationInOrderOfDuration(esql); break;
					case 9: FindNumberOfAvailableSeatsForFlight(esql); break;
					case 10: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice

	public static void AddPassenger(AirBooking esql){//1
		//Add a new passenger to the database
	    //needs trigger for passenger number, could also find a way to replace missing valuse that exist
	    System.out.println("Enter Name:");
	    String name = sc.nextLine();
	    System.out.println("Enter country:");
	    String country = sc.nextLine();
	    System.out.println("Enter Birth Date day:");
	    int day = sc.nextInt();
	    System.out.println("Enter Birth month:");
            int month = sc.nextInt();
	    System.out.println("Enter Birth Year:");
	    int year = sc.nextInt();
	    System.out.println("Enter passport Number:");
            String passportNum = sc.next();
	    sc.nextLine();
	    if(name.equals("") || country.equals("") || passportNum.length() != 10 || day <1 || year < 1900 ||
	    month < 1 || month > 12 || day > 31){
		System.out.println("invalid input Passenger not created");
		return;
	    }
	    String Date = Integer.toString(month) + "-" + Integer.toString(day) + "-" + Integer.toString(year);
	    System.out.println(Date);
	    try{
	        esql.executeQuery("INSERT INTO passenger (passnum, fullname, bdate, country) values('"+
				  passportNum + "','" + name + "','"+ Date + "','" + country + "')");
	    } catch(SQLException e){
		System.out.println(e);
	    }
	}
	
	public static void BookFlight(AirBooking esql){//2
		//Book Flight for an existing customer
	}
	
	public static void TakeCustomerReview(AirBooking esql){//3
		//Insert customer review into the ratings table
	    System.out.println("Enter pid");
	    int pid = sc.nextInt();
	    System.out.println("Enter Flight Number");
	    String flightnum = sc.next();
	    System.out.println("Enter Score 0-5");
	    int score = sc.nextInt();
	    System.out.println("Enter Comment or just press Enter to skip");
	    sc.nextLine();
	    String comment = sc.nextLine();;
	    try{
		int rc = esql.executeQuery("SELECT * FROM passenger WHERE pid = " + pid);
		if(rc == 0){
			System.out.println("Not a valid passenger");
			return;
		}
		rc = esql.executeQuery("SELECT * FROM flight WHERE flightnum = '" + flightnum + "'");
		if(rc == 0){
			System.out.println("Not A valid flight");
			return;
		}
		rc = esql.executeQuery("SELECT * FROM BOOKING WHERE pid = " + pid + " and flightnum = '" + flightnum + "' and departure <= current_date");
		if(rc == 0){
		    System.out.println("This passenger has never taken this flight");
		    return;
		}
		rc = esql.executeQuery("SELECT * FROM ratings WHERE pid = " + pid + " and flightnum = '" + flightnum + "'");
		if(rc > 0){
		    System.out.println("This passenger has already reviewed this flight");
		    return;
		}
		if(score > 5 || score < 0){
		    System.out.println("Invalid Score Provided");
		    return;
		}
		esql.executeQuery("Insert INTO ratings (pid, flightnum, score, comment) values(" + pid + ",'" + flightnum + "'," +
			score + ",'" + comment + "')");
	    } catch(SQLException e){
		System.out.println(e);
	    }
	}
	
	public static void InsertOrUpdateRouteForAirline(AirBooking esql){//4
		//Insert a new route for the airline
		System.out.println("Enter Flight Number: ");
		String flightnumber = sc.nextLine();
		while(flightnumber.length() < 5){
			System.out.println("Invalid flight number. Please enter a valid flight number.");
			flightnumber = sc.nextLine();
		}
		try{
			int count = esql.executeQuery("Select * from flight where flightnum = '" + flightnumber + "'");
			if(count == 0)
				insertFlight(flightnumber, esql);
			else
				updateFlight(flightnumber, esql);
		} catch (SQLException e){
			System.out.println(e);
		}
	}

	public static void updateFlight(String flightnumber, AirBooking esql){
		System.out.println("Enter Origin(enter to skip): ");
		String origin = sc.nextLine();
		System.out.println("Enter Destination(enter to skip): ");
		String destination = sc.nextLine();
		System.out.println("Enter plane type(enter to skip): ");
		String plane = sc.nextLine();
		System.out.println("Enter seating capacity(-1 to skip): ");
		int seats = sc.nextInt();
		while(seats < 1 && seats != -1){
			System.out.println("Flights can not have less than one seat. Please enter a valid seating capacity.");
			seats = sc.nextInt();
		}
		System.out.println("Enter duration(-1 to skip): ");
		int time = sc.nextInt();
		while(time < 1 && time != -1){
			System.out.println("Flight can not have a duration less than 0. Please enter a valid flight duration.");
			time = sc.nextInt();
		}
		System.out.println("Enter airidi(-1 to skip): ");
		int airid = sc.nextInt();
		sc.nextLine();
		try{
			List< List <String> > defaults = esql.executeQueryAndReturnResult("Select * from flight where flightnum = '" + flightnumber + "'");
			if(origin.length() == 0){
				origin = defaults.get(0).get(2).trim();
			}
			if(destination.length() == 0){
				destination = defaults.get(0).get(3).trim();
			}
			if(plane.length() == 0){
				plane = defaults.get(0).get(4).trim();
			}
			if(seats == -1){
				seats = Integer.parseInt(defaults.get(0).get(5));
			}
			if(time == -1){
				time = Integer.parseInt(defaults.get(0).get(6));
			}
			for(int i = 0; i < defaults.size(); ++i){
				for(int j = 0; j < defaults.get(i).size(); ++j){
					System.out.println(defaults.get(i).get(j));
				}
			}
			if(airid == -1){
				airid = Integer.parseInt(defaults.get(0).get(0));
			}
			else{
				int count = esql.executeQuery("SELECT * FROM airline WHERE airid = " + airid);
				if(count < 0){
					System.out.println("Invalid airid");
					return;
				}
			}
			esql.executeQuery("UPDATE flight set airid = " + airid + ", origin = '" + origin + "', destination = '" + destination + "', plane = '" + plane + "', seats = " + seats + 
				", duration = " + time +  " where flightnum = '" + flightnumber + "'");
		} catch(SQLException e){
			System.out.println(e);
		}
	}

	public static void insertFlight(String flightnumber, AirBooking esql){
		System.out.println("Enter Origin: ");
		String origin = sc.nextLine();
		while(origin.length() == 0){
			System.out.println("Empty origin entered. Please enter a valid origin.");
			origin = sc.nextLine();
		}
		System.out.println("Enter Destination: ");
		String destination = sc.nextLine();
		while(destination.length() == 0){
			System.out.println("Empty Destination Entered. Please Enter a valid destination.");
			destination = sc.nextLine();
		}
		System.out.println("Enter plane type: ");
		String plane = sc.nextLine();
		while(plane.length() == 0){
			System.out.println("Empty Plane type entered. Please enter a valid plane type.");
			plane = sc.nextLine();
		}
		System.out.println("Enter seating capacity: ");
		int seats = sc.nextInt();
		while(seats < 1){
			System.out.println("Flights can not have less than one seat. Please enter a valid seating capacity.");
			seats = sc.nextInt();
		}
		System.out.println("Enter duration: ");
		int time = sc.nextInt();
		while(time < 1){
			System.out.println("Flight can not have a duration less than 1. Please enter a valid flight duration.");
			time = sc.nextInt();
		}
		System.out.println("Enter airid: ");
		int airid = sc.nextInt();
		sc.nextLine();
		try{
			int count = esql.executeQuery("SELECT * FROM airline WHERE airid = " + airid);
			if(count < 0){
				System.out.println("Invalid airid");
				return;
			}
			esql.executeQuery("Insert into flight (airid, flightnum, origin, destination, plane, seats, duration)" + 
				"values (" + airid + ",'" + flightnumber + "','" + origin + "','" + destination + "','" + plane + "'," + seats + "," + time +")");
		} catch(SQLException e){
			System.out.println(e);
		}
	}
	
	public static void ListAvailableFlightsBetweenOriginAndDestination(AirBooking esql) throws Exception{//5
		//List all flights between origin and distination (i.e. flightNum,origin,destination,plane,duration)
		System.out.println("Enter Origin:");
		String origin = sc.nextLine();
		System.out.println("Enter Destination:");
		String destination = sc.nextLine();
		try{
			esql.executeQueryAndPrintResult("SELECT * FROM flight WHERE origin = '" + origin + "' and destination = '" + destination + "'");
		} catch (SQLException e){
			System.out.println(e);
		} 
	}
	
	public static void ListMostPopularDestinations(AirBooking esql){//6
		//Print the k most popular destinations based on the number of flights offered to them (i.e. destination, choices)
	}
	
	public static void ListHighestRatedRoutes(AirBooking esql){//7
		//List the k highest rated Routes (i.e. Airline Name, flightNum, Avg_Score)
		System.out.println("How many routes would you like to see?");
		int numRoutes = sc.nextInt();
		sc.nextLine();
		if(numRoutes < 1){
			System.out.println("cannont look for negative or 0 routes");
			return;
		}
		try{
			esql.executeQueryAndPrintResult("SELECT a.name, f.flightnum, avg_score From" +
				" (Select (SUM(score)/count(score)) avg_score, flightnum From ratings Group By(flightnum)) as average , flight f, airline a" +
				" WHERE f.flightnum = average.flightnum and a.airid = f.airid ORDER By avg_score DESC LIMIT " + numRoutes);
		} catch(SQLException e){
			System.out.println(e);
		}
	}
	
	public static void ListFlightFromOriginToDestinationInOrderOfDuration(AirBooking esql){//8
		//List flight to destination in order of duration (i.e. Airline name, flightNum, origin, destination, duration, plane)
	}
	
	public static void FindNumberOfAvailableSeatsForFlight(AirBooking esql){//9
		//
		
	}
	
}

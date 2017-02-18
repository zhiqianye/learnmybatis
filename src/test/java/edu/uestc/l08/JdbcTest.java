package edu.uestc.l08;

import java.sql.*;

//STEP 1. Import required packages
public class JdbcTest {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String DB_URL = "jdbc:postgresql://localhost:5432/coms1024";

    //  Database credentials
    static final String USER = "postgres";
    static final String PASS = "123456";

    public static void main(String[] args) {
        Connection conn = null;
        CallableStatement stmt = null;
        try{
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");

            String sql = "{call timestamp_minus (?, ?)}";
            stmt = conn.prepareCall(sql);
            stmt.setString(1, "2017-02-15");
            stmt.setString(2, "2017-02-13");
            ResultSet rs = stmt.executeQuery();

            //STEP 5: Extract data from result set
            while(rs.next()){
                //Retrieve by column name
                String indexCode  = rs.getString("c_index_code");
                int storageDockType = rs.getInt("c_storage_dock_type");
                String storageType = rs.getString("c_storage_type");
                String timeSegment = rs.getString("t_time_segment");

                //Display values
                System.out.print("indexCode: " + indexCode);
                System.out.print(", storageDockType: " + storageDockType);
                System.out.print(", storageType: " + storageType);
                System.out.println(", timeSegment: " + timeSegment);
            }
            //STEP 6: Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(ClassNotFoundException e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");
    }
}
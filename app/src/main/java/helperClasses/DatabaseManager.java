package helperClasses;

import android.content.Context;
import android.os.Bundle;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by savanpatel on 2017-02-08.
 */

public class DatabaseManager {

    private static DatabaseManager dbManager = null;
    private String MYSQLhost = "mysql01.dexagon.net:3306";
    private String MYSQLdatabase = "scaniq_RRinfo";
    private String MYSQLusername = "scaniq_uDBA";
    private String MYSQLpassword = "wind360";


    public DatabaseManager() {

    }

    public static DatabaseManager getInstance()
    {
        if( dbManager == null)
        {
            dbManager = new DatabaseManager();
        }
        return dbManager;
    }

    public Connection getConnection(Context context){
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://" + MYSQLhost +"/" + MYSQLdatabase + "?" + "user=" + MYSQLusername + "&password=" + MYSQLpassword);
        } catch (Exception e) {
            e.printStackTrace();
            AlertBoxBuilder.AlertBox(context,"Error","Something went wrong while storing data to database...\nPlease rescan the document!");
        }
            return con;
    }

    public ResultSet executeSelecteQuery(Connection con, String query,Context context){
        ResultSet rs = null;
        try {
            rs = con.createStatement().executeQuery(query);;
        } catch (Exception e) {
            AlertBoxBuilder.AlertBox(context,"Error","Something went wrong while storing data to database...\nPlease rescan the document!");
            e.printStackTrace();
        }
        return rs;
    }

    public ResultSet executeRegisterPreparedStatement(Connection con, String emailAccount, String androidID ,String carrierName,Context context){

        String preparedStatement = "{call REGISTER_NEW_UNIT(?,?,?)}";

        CallableStatement cs = null;
        ResultSet rs = null;
        try {
            cs = con.prepareCall(preparedStatement);
            cs.setString(1, emailAccount);
            cs.setString(2, androidID);
            cs.setString(3, carrierName);

            boolean hadResults = cs.execute();
            if (hadResults) {
                rs = cs.getResultSet();
            }
        } catch (SQLException e) {
            AlertBoxBuilder.AlertBox(context,"Error","Something went wrong while storing data to database...\nPlease rescan the document!");
            e.printStackTrace();
        }
        return rs;
    }

    public void closeConnection(Connection con,Context context)
    {
        try {
            if (!con.isClosed())
            {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertBoxBuilder.AlertBox(context,"Error","Something went wrong while connecting to database...\nPlease rescan the document!");
        }
    }

    public void executeStoreScannedDataPreparedStatement(Connection con, Bundle bundle, Context context){

        int newScanId = 0;

        String query = "INSERT INTO scaniq_RRinfo.Scan ("
                + " RR_ID,"
                + " SCAN_TimeStampRR, "
                + " SCAN_FileName, "
                + " SCAN_FileType, "
                + " SCAN_Pages, "
                + " SCAN_Bytes, "
                + " SCAN_SentTo, "
                + " SCAN_EmailTo, "
                + " SCAN_CopyTo, "
                + " SCAN_ScannerID) VALUES ("
                + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//EmailTo -> CC
//CopyTo - fax#

        try {
            java.sql.PreparedStatement st = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            st.setString(1,bundle.getString("RR_ID"));
            st.setString(2,bundle.getString("scanDate"));
            st.setString(3,bundle.getString("fileName"));
            st.setString(4,bundle.getString("fileType"));
            st.setInt(5,bundle.getInt("numberOfPages"));
            st.setLong(6,bundle.getLong("fileSize"));
            st.setString(7,bundle.getString("mailTo"));
            st.setString(8,bundle.getString("tempCCEmail"));
            st.setString(9,bundle.getString("validFaxNUmber"));
            st.setString(10,bundle.getString("Scanner_Serial_Num"));

            st.executeUpdate();

            ResultSet rss =  st.getGeneratedKeys();
            if (rss.next()) {
                newScanId = rss.getInt(1);
                System.out.println("SCANID = " + newScanId);
            }
            st.close();

            if(newScanId!=0)
            {
                executeStoreGPSData(con,bundle,newScanId,context);
            }

        } catch (SQLException e) {
            AlertBoxBuilder.AlertBox(context,"Error","Something went wrong while storing data to database...\nPlease rescan the document!");
            e.printStackTrace();
        }
    }

    private void executeStoreGPSData(Connection con,Bundle bundle,int newScanID,Context context){
        try {
            if (con == null || con.isClosed())
            {
                con = this.getConnection(context);
            }

            String query = "INSERT INTO scaniq_RRinfo.GPS_Events(RR_Id, GPS_TimestampServerUTC, GPS_Event, SCAN_ID, GPS_Lat, GPS_Long) "
                    + " VALUES ("
                    + "?, UTC_TIMESTAMP(), 'S', ?, ?, ?)";

            java.sql.PreparedStatement st = con.prepareStatement(query);
            st.setString(1,bundle.getString("RR_ID"));//1.
            st.setInt(2,newScanID);//2.
            st.setFloat(3, (float) bundle.getDouble("Scan_Lat"));//3.
            st.setFloat(4, (float) bundle.getDouble("Scan_Lon"));//4.
            st.executeUpdate();
            st.close();
            this.closeConnection(con,context);
        } catch (SQLException e) {
            e.printStackTrace();
            this.closeConnection(con,context);
            AlertBoxBuilder.AlertBox(context,"Error","Something went wrong while storing data to database...\nPlease rescan the document!");
        }

    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.guanzon.cas.inventory.models;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import javax.sql.rowset.CachedRowSet;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.appdriver.iface.GEntity;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class Model_Inv_Serial implements GEntity {

    final String XML = "Model_Inv_Serial.xml";

    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode

    /**
     * Entity constructor
     *
     * @param foValue - GhostRider Application Driver
     */
    public Model_Inv_Serial(GRider foValue) {
        if (foValue == null) {
            System.err.println("Application Driver is not set.");
            System.exit(1);
        }

        poGRider = foValue;

        initialize();
    }

    /**
     * Gets edit mode of the record
     *
     * @return edit mode
     */
    @Override
    public int getEditMode() {
        return pnEditMode;
    }

    /**
     * Gets the column index name.
     *
     * @param fnValue - column index number
     * @return column index name
     */
    @Override
    public String getColumn(int fnValue) {
        try {
            return poEntity.getMetaData().getColumnLabel(fnValue);
        } catch (SQLException e) {
        }
        return "";
    }

    /**
     * Gets the column index number.
     *
     * @param fsValue - column index name
     * @return column index number
     */
    @Override
    public int getColumn(String fsValue) {
        try {
            return MiscUtil.getColumnIndex(poEntity, fsValue);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Gets the total number of column.
     *
     * @return total number of column
     */
    @Override
    public int getColumnCount() {
        try {
            return poEntity.getMetaData().getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Gets the table name.
     *
     * @return table name
     */
    @Override
    public String getTable() {
        return "Inv_Serial";
    }

    /**
     * Gets the value of a column index number.
     *
     * @param fnColumn - column index number
     * @return object value
     */
    @Override
    public Object getValue(int fnColumn) {
        try {
            return poEntity.getObject(fnColumn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the value of a column index name.
     *
     * @param fsColumn - column index name
     * @return object value
     */
    @Override
    public Object getValue(String fsColumn) {
        try {
            return poEntity.getObject(MiscUtil.getColumnIndex(poEntity, fsColumn));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets column value.
     *
     * @param fnColumn - column index number
     * @param foValue - value
     * @return result as success/failed
     */
    @Override
    public JSONObject setValue(int fnColumn, Object foValue) {
        try {
            poJSON = MiscUtil.validateColumnValue(System.getProperty("sys.default.path.metadata") + XML, MiscUtil.getColumnLabel(poEntity, fnColumn), foValue);
            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

            poEntity.updateObject(fnColumn, foValue);
            poEntity.updateRow();

            poJSON = new JSONObject();
            poJSON.put("result", "success");
            poJSON.put("value", getValue(fnColumn));
        } catch (SQLException e) {
            e.printStackTrace();
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }

        return poJSON;
    }

    /**
     * Sets column value.
     *
     * @param fsColumn - column index name
     * @param foValue - value
     * @return result as success/failed
     */
    @Override
    public JSONObject setValue(String fsColumn, Object foValue) {
        poJSON = new JSONObject();

        try {
            return setValue(MiscUtil.getColumnIndex(poEntity, fsColumn), foValue);
        } catch (SQLException e) {
            e.printStackTrace();
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        return poJSON;
    }

    /**
     * Set the edit mode of the entity to new.
     *
     * @return result as success/failed
     */
    @Override
    public JSONObject newRecord() {
        pnEditMode = EditMode.ADDNEW;

        //replace with the primary key column info
        setSerialID(MiscUtil.getNextCode(getTable(), "sSerialID", true, poGRider.getConnection(), poGRider.getBranchCode()));
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }

    /**
     * Opens a record.
     *
     * @param fsCondition - filter values
     * @return result as success/failed
     */
    @Override
    public JSONObject openRecord(String fsCondition) {
        poJSON = new JSONObject();

        String lsSQL = getSQL();

        //replace the condition based on the primary key column of the record
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sSerialID = " + SQLUtil.toSQL(fsCondition));

        ResultSet loRS = poGRider.executeQuery(lsSQL);

        try {
            if (loRS.next()) {
                for (int lnCtr = 1; lnCtr <= loRS.getMetaData().getColumnCount(); lnCtr++) {
                    setValue(lnCtr, loRS.getObject(lnCtr));
                }

                pnEditMode = EditMode.UPDATE;

                poJSON.put("result", "success");
                poJSON.put("message", "Record loaded successfully.");
            } else {
                poJSON.put("result", "error");
                poJSON.put("message", "No record to load.");
            }
        } catch (SQLException e) {
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }

        return poJSON;
    }

    /**
     * Save the entity.
     *
     * @return result as success/failed
     */
    @Override
    public JSONObject saveRecord() {
        poJSON = new JSONObject();

        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            String lsSQL;
            if (pnEditMode == EditMode.ADDNEW) {
                //replace with the primary key column info
                setSerialID(MiscUtil.getNextCode(getTable(), "sSerialID", true, poGRider.getConnection(), poGRider.getBranchCode()));

                setModifiedDate(poGRider.getServerDate());
                lsSQL = makeSQL();
                if (!lsSQL.isEmpty()) {
                    if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), "") > 0) {
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record saved successfully.");
                    } else {
                        poJSON.put("result", "error");
                        poJSON.put("message", poGRider.getErrMsg());
                    }
                } else {
                    poJSON.put("result", "error");
                    poJSON.put("message", "No record to save.");
                }
            } else {
                Model_Inv_Serial loOldEntity = new Model_Inv_Serial(poGRider);

                setModifiedDate(poGRider.getServerDate());
                //replace with the primary key column info
                JSONObject loJSON = loOldEntity.openRecord(this.getSerialID());

                if ("success".equals((String) loJSON.get("result"))) {
                    //replace the condition based on the primary key column of the record
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, "sSerialID = " + SQLUtil.toSQL(this.getSerialID()),  "xBarCodex»xDescript»xBrandNme»xModelNme»xColorNme»xMeasurNm»xCategrNm»xBranchNm»xCompanyNm");

                    if (!lsSQL.isEmpty()) {
                        if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), "") > 0) {
                            poJSON.put("result", "success");
                            poJSON.put("message", "Record saved successfully.");
                        } else {
                            poJSON.put("result", "error");
                            poJSON.put("message", poGRider.getErrMsg());
                        }   
                    } else {
                        poJSON.put("result", "success");
                        poJSON.put("continue", true);
                        poJSON.put("message", "No updates has been made.");
                    }
                } else {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Record discrepancy. Unable to save record.");
                }
            }
        } else {
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid update mode. Unable to save record.");
            return poJSON;
        }

        return poJSON;
    }

    /**
     * Prints all the public methods used<br>
     * and prints the column names of this entity.
     */
    @Override
    public void list() {
        Method[] methods = this.getClass().getMethods();

        System.out.println("--------------------------------------------------------------------");
        System.out.println("LIST OF PUBLIC METHODS FOR " + this.getClass().getName() + ":");
        System.out.println("--------------------------------------------------------------------");
        for (Method method : methods) {
            System.out.println(method.getName());
        }

        try {
            int lnRow = poEntity.getMetaData().getColumnCount();

            System.out.println("--------------------------------------------------------------------");
            System.out.println("ENTITY COLUMN INFO");
            System.out.println("--------------------------------------------------------------------");
            System.out.println("Total number of columns: " + lnRow);
            System.out.println("--------------------------------------------------------------------");

            for (int lnCtr = 1; lnCtr <= lnRow; lnCtr++) {
                System.out.println("Column index: " + (lnCtr) + " --> Label: " + poEntity.getMetaData().getColumnLabel(lnCtr));
                if (poEntity.getMetaData().getColumnType(lnCtr) == Types.CHAR
                        || poEntity.getMetaData().getColumnType(lnCtr) == Types.VARCHAR) {

                    System.out.println("Column index: " + (lnCtr) + " --> Size: " + poEntity.getMetaData().getColumnDisplaySize(lnCtr));
                }
            }
        } catch (SQLException e) {
        }

    }

    /**
     * Sets the sSerialID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setSerialID(String fsValue) {
        return setValue("sSerialID", fsValue);
    }

    /**
     * @return The sSerialID of this record.
     */
    public String getSerialID() {
        return (String) getValue("sSerialID");
    }

    /**
     * @return The sBranchCd of this record.
     */
    public String getBranchCode() {
        return (String) getValue("sBranchCd");
    }
    
    /**
     * Sets the sBrandCde of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBranchCode(String fsValue) {
        return setValue("sBranchCd", fsValue);
    }
    
    /**
    /**
     * Sets the sSerial01 .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setSerial01(String fsValue){
        return setValue("sSerial01", fsValue);
    }
    
    /**
     * @return The sSerial01. 
     */
    public String getSerial01(){
        return (String) getValue("sSerial01");
    }
    /**
    /**
     * Sets the sSerial02 .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setSerial02(String fsValue){
        return setValue("sSerial02", fsValue);
    }
    
    /**
     * @return The sSerial02. 
     */
    public String getSerial02(){
        return (String) getValue("sSerial02");
    }
    
    /**
    /**
     * Sets the nUnitPrce .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setUnitPrice(Number fsValue){
        return setValue("nUnitPrce", fsValue);
    }
    
    /**
     * @return The nUnitPrce. 
     */
    public Object getUnitPrice(){
        return (Object) getValue("nUnitPrce");
    }
    
    /**
     * Sets the sStockIDx of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setStockID(String fsValue) {
        return setValue("sStockIDx", fsValue);
    }

    /**
     * @return The sStockIDx of this record.
     */
    public String getStockID() {
        return (String) getValue("sStockIDx");
    }

    /**
    /**
     * Sets the cLocation .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setLocation(String fsValue){
        return setValue("cLocation", fsValue);
    }
    
    /**
     * @return The cLocation. 
     */
    public String getLocation(){
        return (String) getValue("cLocation");
    }
    
    /**
    /**
     * Sets the cSoldStat .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setSoldStat(String fsValue){
        return setValue("cSoldStat", fsValue);
    }
    
    /**
     * @return The cSoldStat. 
     */
    public String getSoldStat(){
        return (String) getValue("cSoldStat");
    }
    
    
    /**
    /**
     * Sets the cUnitType .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setUnitType(String fsValue){
        return setValue("cUnitType", fsValue);
    }
    
    /**
     * @return The cUnitType. 
     */
    public String getUnitType(){
        return (String) getValue("cUnitType");
    }
    
    
    /**
    /**
     * Sets the sCompnyID .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setCompnyID(String fsValue){
        return setValue("sCompnyID", fsValue);
    }
    
    /**
     * @return The sCompnyID. 
     */
    public String getCompnyID(){
        return (String) getValue("sCompnyID");
    }
    
    /**
    /**
     * Sets the sWarranty .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setWarranty(String fsValue){
        return setValue("sWarranty", fsValue);
    }
    
    /**
     * @return The sWarranty. 
     */
    public String getWarranty(){
        return (String) getValue("sWarranty");
    }
    

    /**
     * Sets the xBarCodex of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBarcode(String fsValue) {
        return setValue("xBarCodex", fsValue);
    }

    /**
     * @return The xBarCodex of this record.
     */
    public String getBarcode() {
        return (String) getValue("xBarCodex");
    }
    
    /**
     * Sets the xDescript of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setDescript(String fsValue) {
        return setValue("xDescript", fsValue);
    }

    /**
     * @return The xDescript of this record.
     */
    public String getDescript() {
        return (String) getValue("xDescript");
    }
    /**
     * Sets the xBranchNm of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBranchName(String fsValue) {
        return setValue("xBranchNm", fsValue);
    }

    /**
     * @return The xBranchNm of this record.
     */
    public String getBranchName() {
        return (String) getValue("xBranchNm");  
    }

    /**
     * Sets the Inventory RecdStat of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setRecdStat(String fsValue) {
        return setValue("cRecdStat", fsValue);
    }

    /**
     * @return The Category RecdStat of this record.
     */
    public String getRecdStat() {
        return (String) getValue("cRecdStat");
    }

    /**
     * Sets record as active.
     *
     * @param fbValue
     * @return result as success/failed
     */
    public JSONObject setActive(boolean fbValue) {
        return setValue("cRecdStat", fbValue ? "1" : "0");
    }

    /**
     * @return If record is active.
     */
    public boolean isActive() {
        return ((String) getValue("cRecdStat")).equals("1");
    }


    /**
     * Sets the date and time the record was modified.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setModifiedDate(Date fdValue) {
        return setValue("dModified", fdValue);
    }

    /**
     * @return The date and time the record was modified.
     */
    public Date getModifiedDate() {
        return (Date) getValue("dModified");
    }
        
    /**
    /**
     * Sets the xCompanyNm .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setCompnyName(String fsValue){
        return setValue("xCompanyNm", fsValue);
    }
    
    /**
     * @return The xCompanyNm. 
     */
    public String getCompnyName(){
        return (String) getValue("xCompanyNm");
    }
    
    /**
    /**
     * Sets the xBrandNme .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setBrandName(String fsValue){
        return setValue("xBrandNme", fsValue);
    }
    
    /**
     * @return The xBrandNme. 
     */
    public String getBrandName(){
        return (String) getValue("xBrandNme");
    }
    
    /**
    /**
     * Sets the xModelNme .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setModelName(String fsValue){
        return setValue("xModelNme", fsValue);
    }
    
    /**
     * @return The xModelNme. 
     */
    public String getModelName(){
        return (String) getValue("xModelNme");
    }
    
    /**
    /**
     * Sets the xColorNme .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setColorName(String fsValue){
        return setValue("xColorNme", fsValue);
    }
    
    /**
     * @return The xColorNme    . 
     */
    public String getColorName(){
        return (String) getValue("xColorNme");
    }
    
    /**
    /**
     * Sets the xMeasurNm .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setMeasure(String fsValue){
        return setValue("xMeasurNm", fsValue);
    }
    
    /**
     * @return The xMeasurNm    . 
     */
    public String getMeasure(){
        return (String) getValue("xMeasurNm");
    }
    
    /**
    /**
     * Sets the xCategrNm .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setCategoryName(String fsValue){
        return setValue("xCategrNm", fsValue);
    }
    
    /**
     * @return The xCategrNm    . 
     */
    public String getCategoryName(){
        return (String) getValue("xCategrNm");
    }

    /**
     * Gets the SQL statement for this entity.
     *
     * @return SQL Statement
     */
    
    public String makeSQL() {
        return MiscUtil.makeSQL(this, "xBarCodex»xDescript»xBrandNme»xModelNme»xColorNme»xMeasurNm»xCategrNm»xBranchNm»xCompanyNm");
    }

    /**
     * Gets the SQL Select statement for this entity.
     *
     * @return SelectSQL Statement
     */
    public String makeSelectSQL() {
        return MiscUtil.makeSelect(this, "xBarCodex»xDescript»xBrandNme»xModelNme»xColorNme»xMeasurNm»xCategrNm»xBranchNm»xCompanyNm");
    }
public String getSQL(){
         String lsSQL = "SELECT " +
                        "     a.sSerialID " +
                        "   , a.sBranchCd " +
                        "   , a.sSerial01 " +
                        "   , a.sSerial02 " +
                        "   , a.nUnitPrce " +
                        "   , a.sStockIDx " +
                        "   , a.cLocation " +
                        "   , a.cSoldStat " +
                        "   , a.cUnitType " +
                        "   , a.sCompnyID " +
                        "   , a.sWarranty " +
                        "   , a.dModified " +
                        "   , b.sBarCodex xBarCodex " +
                        "   , b.sDescript xDescript " +
                        "   , c.sDescript xBrandNme " +
                        "   , d.sModelNme xModelNme " +
                        "   , e.sDescript xColorNme " +
                        "   , IFNULL(f.sMeasurNm,'') xMeasurNm " +
                        "   , IFNULL(g.sDescript,'') xCategrNm " +
                        "   , h.sBranchNm xBranchNm  " +
                        "   , i.sCompnyNm xCompanyNm  " +
                        "  FROM Inv_Serial a " +
                        "      LEFT JOIN Inventory b ON a.sStockIDx = b.sStockIDx " +
                        "      LEFT JOIN Brand c ON b.sBrandCde = c.sBrandCde " +
                        "      LEFT JOIN Model d ON b.sModelCde = d.sModelCde " +
                        "      LEFT JOIN Color e ON b.sColorCde = e.sColorCde " +
                        "      LEFT JOIN Measure f ON b.sMeasurID = f.sMeasurID " +
                        "      LEFT JOIN Category g ON b.sCategCd1 = g.sCategrCd"+
                        "      LEFT JOIN Branch h ON a.sBranchCd = h.sBranchCd " +
                        "      LEFT JOIN Company i ON a.sCompnyID = i.sCompnyID " ;

//        /validate result based on the assigned inventory type.
        if (!System.getProperty("store.inventory.industry").isEmpty())
            lsSQL = MiscUtil.addCondition(lsSQL, " b.sCategCd1 IN " + CommonUtils.getParameter(System.getProperty("store.inventory.industry")));
        
        
        return lsSQL;
    }
    private void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);
//            poEntity.updateString("cRecdStat", RecordStatus.ACTIVE);

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.guanzon.cas.model.inventory;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import javax.sql.rowset.CachedRowSet;
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
public class Model_Inv_Hist_Ledger implements GEntity {

    final String XML = "Model_Inv_Hist_Ledger.xml";

    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode

    /**
     * Entity constructor
     *
     * @param foValue - GhostRider Application Driver
     */
    public Model_Inv_Hist_Ledger(GRider foValue) {
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
        return "Inv_Hist_Ledger";
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
        setStockID(MiscUtil.getNextCode(getTable(), "sStockIDx", false, poGRider.getConnection(), ""));
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

        String lsSQL = makeSelectSQL();

        //replace the condition based on the primary key column of the record
        lsSQL = MiscUtil.addCondition(lsSQL, " sStockIDx = " + SQLUtil.toSQL(fsCondition));

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
                setStockID(MiscUtil.getNextCode(getTable(), "sStockIDx", false, poGRider.getConnection(), ""));

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
                Model_Inv_Hist_Ledger loOldEntity = new Model_Inv_Hist_Ledger(poGRider);

                //replace with the primary key column info
                JSONObject loJSON = loOldEntity.openRecord(this.getStockID());

                if ("success".equals((String) loJSON.get("result"))) {
                    //replace the condition based on the primary key column of the record
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, "sStockIDx = " + SQLUtil.toSQL(this.getStockID()),  "xBarCodex»xDescript»xWHouseNm");
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
     * @return The sWHouseID of this record.
     */
    public String getWHouseID() {
        return (String) getValue("sWHouseID");
    }
    
    /**
     * Sets the sWHouseID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setWHouseID(String fsValue) {
        return setValue("sWHouseID", fsValue);
    }
    
    /**
     * @return The nLedgerNo of this record.
     */
    public String getLedgerNo() {
        return (String) getValue("nLedgerNo");
    }
    
    /**
     * Sets the nLedgerNo of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setLedgerNo(String fsValue) {
        return setValue("nLedgerNo", fsValue);
    }

    /**
     * Sets the date and time the record was transact.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setTransactDate(Date fdValue) {
        return setValue("dTransact", fdValue);
    }

    /**
     * @return The date and time the record was transact.
     */
    public Date getTransactDate() {
        return (Date) getValue("dTransact");
    }

    
    /**
     * @return The sSourceCd of this record.
     */
    public String getSourceCode() {
        return (String) getValue("sSourceCd");
    }
    
    /**
     * Sets the sSourceCd of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setSourceCode(String fsValue) {
        return setValue("sSourceCd", fsValue);
    }
    
    /**
     * @return The sSourceNo of this record.
     */
    public String getSourceNo() {
        return (String) getValue("sSourceNo");
    }
    
    /**
     * Sets the sSourceNo of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setSourceNo(String fsValue) {
        return setValue("sSourceNo", fsValue);
    }

    /**
    /**
     * Sets the nQtyInxxx .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setQuantityIn(String fsValue){
        return setValue("nQtyInxxx", fsValue);
    }
    
    /**
     * @return The nQtyInxxx. 
     */
    public int getQuantityIn(){
        return (int) getValue("nQtyInxxx");
    }
    
    /**
    /**
     * Sets the nQtyOutxx .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setQuantityOut(String fsValue){
        return setValue("nQtyOutxx", fsValue);
    }
    
    /**
     * @return The nQtyOutxx. 
     */
    public int getQuantityOut(){
        return (int) getValue("nQtyOutxx");
    }
    

    /**
    /**
     * Sets the nQtyOrder .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setQuantityOrder(String fsValue){
        return setValue("nQtyOrder", fsValue);
    }
    
    /**
     * @return The nQtyOrder. 
     */
    public int getQuantityOrder(){
        return (int) getValue("nQtyOrder");
    }

    /**
    /**
     * Sets the nQtyIssue .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setQuantityIssue(String fsValue){
        return setValue("nQtyIssue", fsValue);
    }
    
    /**
     * @return The nQtyIssue. 
     */
    public int getQuantityIssue(){
        return (int) getValue("nQtyIssue");
    }
    
    /**
    /**
     * Sets the nPurPrice .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setPurchasePrice(String fsValue){
        return setValue("nPurPrice", fsValue);
    }
    
    /**
     * @return The nUnitPrce. 
     */
    public Double getPurchasePrice(){
        return (Double) getValue("nPurPrice");
    }
    
    /**
    /**
     * Sets the nUnitPrce .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setUnitPrice(String fsValue){
        return setValue("nUnitPrce", fsValue);
    }
    
    /**
     * @return The nUnitPrce. 
     */
    public Double getUnitPrice(){
        return (Double) getValue("nUnitPrce");
    }
    
    
    /**
     * Sets the nSelPrice .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setSelPrice(String fsValue){
        return setValue("nSelPrice", fsValue);
    }
    
    /**
     * @return The nSelPrice. 
     */
    public Double getSelPrice(){
        return (Double) getValue("nSelPrice");
    }
    
    
    /**
    /**
     * Sets the nQtyOnHnd .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setQuantityOnHand(String fsValue){
        return setValue("nQtyOnHnd", fsValue);
    }
    
    /**
     * @return The nQtyOnHnd. 
     */
    public int getQuantityOnHand(){
        return (int) getValue("nQtyOnHnd");
    }
    
    
    /**
     * Sets the date and time the record expiration date.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setExpiryDate(Date fdValue) {
        return setValue("dExpiryxx", fdValue);
    }

    /**
     * @return The date and time the record expiration date.
     */
    public Date getExpiryDate() {
        return (Date) getValue("dExpiryxx");
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
     * Sets the xWHouseNm of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setWareHouseName(String fsValue) {
        return setValue("xWHouseNm", fsValue);
    }

    /**
     * @return The xWHouseNm of this record.
     */
    public String getWareHouseName() {
        return (String) getValue("xWHouseNm");
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
     * Sets the user encoded/updated the record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setModifiedBy(String fsValue) {
        return setValue("sModified", fsValue);
    }

    /**
     * @return The user encoded/updated the record
     */
    public String getModifiedBy() {
        return (String) getValue("sModified");
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
     * Gets the SQL statement for this entity.
     *
     * @return SQL Statement
     */
    public String makeSQL() {
        return MiscUtil.makeSQL(this, "xBarCodex»xDescript»xWHouseNm");
    }

    /**
     * Gets the SQL Select statement for this entity.
     *
     * @return SelectSQL Statement
     */
    public String makeSelectSQL() {
        return MiscUtil.makeSelect(this, "xBarCodex»xDescript»xWHouseNm");
    }
    public String getSQL(){
        return "SELECTF" +
                        "   a.sStockIDx" +
                        " , a.sBranchCd" +
                        " , a.sWHouseID" +
                        " , a.nLedgerNo" +
                        " , a.dTransact" +
                        " , a.sSourceCd" +
                        " , a.sSourceNo" +
                        " , a.nQtyInxxx" +
                        " , a.nQtyOutxx" +
                        " , a.nQtyOrder" +
                        " , a.nQtyIssue" +
                        " , a.nPurPrice" +
                        " , a.nUnitPrce" +
                        " , a.nQtyOnHnd" +
                        " , a.dExpiryxx" +
                        " , a.sModified" +
                        " , a.dModified" +
                        " , b.sBarCodex xBarCodex" +
                        " , b.sDescript xDescript" +
                        " , c.sWHouseNm xWHouseNm " +
                        "FROM Inv_Hist_Ledger a" +
                        "    LEFT JOIN Inventory b ON a.sStockIDx = b.sStockIDx" +
                        "    LEFT JOIN Warehouse c ON a.sWhouseID = c.sWhouseID";
    }
    private void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);
            poEntity.updateString("cRecdStat", RecordStatus.ACTIVE);

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

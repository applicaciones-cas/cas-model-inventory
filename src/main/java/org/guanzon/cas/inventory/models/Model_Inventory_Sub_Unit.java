/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.guanzon.cas.inventory.models;

import java.lang.reflect.Method;
import java.math.BigDecimal;
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
public class Model_Inventory_Sub_Unit implements GEntity {

    final String XML = "Model_Inventory_Sub_Unit.xml";

    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode

    /**
     * Entity constructor
     *
     * @param foValue - GhostRider Application Driver
     */
    public Model_Inventory_Sub_Unit(GRider foValue) {
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
    
    public void setEditMode(int val) {
        pnEditMode = val;
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
        return "Inventory_Sub_Unit";
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
            if ("error".equals((String) poJSON.get("result"))) return poJSON;
            
            poEntity.updateObject(fnColumn, foValue);
            poEntity.updateRow();
            
            poJSON = new JSONObject();
            poJSON.put("result", "success");
            poJSON.put("value", getValue(fnColumn));
            System.out.println("poJSON = " + poJSON);
            
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
    public JSONObject setValue(String string, Object foValue) {
        try {
            return setValue(MiscUtil.getColumnIndex(poEntity, string), foValue);
        } catch (SQLException ex) {
            
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", ex.getMessage());
            return poJSON;
            
        }
        
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
//        setStockID(MiscUtil.getNextCode(getTable(), "sStockIDx", true, poGRider.getConnection(), poGRider.getBranchCode()));
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
        lsSQL = MiscUtil.addCondition(lsSQL, "a.sItmSubID = " + SQLUtil.toSQL(fsCondition));
        System.out.print("this is lsSQL openrec == " + lsSQL + "\n");
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
     * Opens a record.
     *
     * @param fsCondition - filter values
     * @return result as success/failed
     */
    public JSONObject openRecordWithCondition(String fsCondition, int fnEntryNox) {
        poJSON = new JSONObject();

        String lsSQL = getSQL();

        //replace the condition based on the primary key column of the record
        lsSQL = MiscUtil.addCondition(lsSQL, "a.sStockIDx = " + SQLUtil.toSQL(fsCondition));
        lsSQL = MiscUtil.addCondition(lsSQL, "a.nEntryNox = " + SQLUtil.toSQL(fnEntryNox));
        System.out.print("this is lsSQL openrec WithCondition == " + lsSQL + "\n");
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
//                setStockID(MiscUtil.getNextCode(getTable(), "a.sStockIDx", true, poGRider.getConnection(), poGRider.getBranchCode()));

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
                Model_Inventory_Sub_Unit loOldEntity = new Model_Inventory_Sub_Unit(poGRider);
                
                //replace with the primary key column info
                JSONObject loJSON = loOldEntity.openRecordWithCondition(this.getStockID(), this.getEntryNox());

                setModifiedDate(poGRider.getServerDate());
                if ("success".equals((String) loJSON.get("result"))) {
                    //replace the condition based on the primary key column of the record
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, "sStockIDx = " + SQLUtil.toSQL(this.getStockID()) + " AND nEntryNox = " + SQLUtil.toSQL(this.getEntryNox()),  "xBarCodex»xDescript»xBarCodeU»xDescripU»xMeasurID»xMeasurNm");
                    System.out.println("Update lsSQL value == " + lsSQL);
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
     * Sets the sStockIDx of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setSubItemID(String fsValue) {
        return setValue("sItmSubID", fsValue);
    }

    /**
     * @return The sStockIDx of this record.
     */
    public String getSubItemID() {
        return (String) getValue("sItmSubID");
    }

    /**
     * Sets the sBarCodex of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBarcode(String fsValue) {
        return setValue("xBarCodex", fsValue);
    }

    /**
     * @return The sBarCodex of this record.
     */
    public String getBarcode() {
        return (String) getValue("xBarCodex");
    }

    /**
     * Sets the sDescript Code of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setDescription(String fsValue) {
        return setValue("xDescript", fsValue);
    }

    /**
     * @return The sDescript Code of this record.
     */
    public String getDescription() {
        return (String) getValue("xDescript");
    }
    

    /**
     * Sets the sBriefDsc Code of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setSubItemDescription(String fsValue) {
        return setValue("xDescripU", fsValue);
    }

    /**
     * @return The sBriefDsc Code of this record.
     */
    public String getSubItemDescription() {
        return (String) getValue("xDescripU");
    }
    
    /**
     * @return The nQuantity. 
     */
    public Number getQuantity(){
        return (Number) getValue("nQuantity");
    }
    
    /**
     * Sets the nQuantity .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setQuantity(Number fsValue){
        return setValue("nQuantity", fsValue);
    }
    /**
     * @return The nEntryNox. 
     */
    public Integer getEntryNox(){
        return (Integer) getValue("nEntryNox");
    }
    
    /**
     * Sets the nEntryNox .
     * 
     * @param fsValue 
     * @return  True if the record assignment is successful.
     */
    public JSONObject setEntryNox(Number fsValue){
        return setValue("nEntryNox", fsValue);
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
     * @return The sMeasurID of this record.
     */
    public String getMeasureID() {
        return (String) getValue("sMeasurID");
    }
    
    /**
     * Sets the sMeasurID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setMeasureID(String fsValue) {
        return setValue("sMeasurID", fsValue);
    }
    
    /**
     * @return The xMeasurNm of this record.
     */
    public String getMeasureName() {
        return (String) getValue("xMeasurNm");
    }
    
    /**
     * Sets the xMeasurNm of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setMeasureName(String fsValue) {
        return setValue("xMeasurNm", fsValue);
    }
    /**
     * Gets the SQL statement for this entity.
     *
     * @return SQL Statement
     */
    public String makeSQL() {
        return MiscUtil.makeSQL(this, "xBarCodex»xDescript»xBarCodeU»xDescripU»xMeasurID»xMeasurNm");
    }

    /**
     * Gets the SQL Select statement for this entity.
     *
     * @return SelectSQL Statement
     */
    public String makeSelectSQL() {
        return MiscUtil.makeSelect(this, "xBarCodex»xDescript»xBarCodeU»xDescripU»xMeasurID»xMeasurNm");
    }

    public String getSQL(){
        return "SELECT" +
                "  a.sStockIDx" +
                ", a.nEntryNox" +
                ", a.sItmSubID" +
                ", a.nQuantity" +
                ", a.dModified" +
                ", b.sBarCodex xBarCodex" +
                ", b.sDescript xDescript" +
                ", c.sBarCodex xBarCodeU" +
                ", c.sDescript xDescripU" +
                ", c.sMeasurID xMeasurID" +
                ", d.sMeasurNm xMeasurNm" +
            " FROM Inventory_Sub_Unit a" +
                " LEFT JOIN Inventory b ON a.sStockIDx = b.sStockIDx" +
                " LEFT JOIN Inventory c ON a.sItmSubID = c.sStockIDx" + 
                " LEFT JOIN Measure d ON c.sMeasurID = d.sMeasurID";
    }
    private void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);
            poEntity.updateObject("nEntryNox", 1);
            poEntity.updateObject("nQuantity", 0.0);

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

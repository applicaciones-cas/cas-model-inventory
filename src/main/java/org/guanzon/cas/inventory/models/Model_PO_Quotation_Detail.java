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
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GEntity;
import org.json.simple.JSONObject;


/**
 * @author Michael Cuison
 */
public class Model_PO_Quotation_Detail implements GEntity{
    final String XML = "Model_PO_Quotation_Detail.xml";
    
    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode
    
    /**
     * Entity constructor
     * 
     * @param foValue - GhostRider Application Driver
     */
    public Model_PO_Quotation_Detail(GRider foValue){
        if (foValue == null){
            System.err.println("Application Driver is not set.");
            System.exit(1);
        }
        
        poGRider = foValue;
        
        initialize();
    }
    
    /**
     * Gets edit mode of the record
     * @return edit mode
     */
    @Override
    public int getEditMode() {
        return pnEditMode;
    }
    
    /**
     * Gets the column index name.
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
     * @return table name
     */
    @Override
    public String getTable() {
        return "PO_Quotation_Detail";
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
        setQuantity(0);
        setUnitPrice(0.00);
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
        poJSON.put("result", "information");
        poJSON.put("message", "Not supported yet.");

        return poJSON;
    }
    
    public JSONObject openRecord(String lsFilter, String fsCondition) {
        poJSON = new JSONObject();

        String lsSQL =getSQL();

        //replace the condition based on the primary key column of the record
        lsSQL = MiscUtil.addCondition(lsSQL, "sTransNox = " + SQLUtil.toSQL(lsFilter)
                + "AND nEntryNox = " + SQLUtil.toSQL(fsCondition));

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
        setModifiedDate(poGRider.getServerDate());
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE){
            String lsSQL;
            if (pnEditMode == EditMode.ADDNEW){
                //replace with the primary key column info
//                setTransactionNumber(MiscUtil.getNextCode(getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()));
                lsSQL = makeSQL();
                
                if (!lsSQL.isEmpty()){
                    if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), "") > 0){
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
                Model_PO_Quotation_Detail loOldEntity = new Model_PO_Quotation_Detail(poGRider);
                
                //replace with the primary key column info
                JSONObject loJSON = loOldEntity.openRecord(this.getTransactionNumber(), this.getEntryNumber().toString());
                
                if ("success".equals((String) loJSON.get("result"))){
                    //replace the condition based on the primary key column of the record
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, "sTransNox = " + SQLUtil.toSQL(this.getTransactionNumber())
                            + "AND nEntryNox = " + SQLUtil.toSQL(this.getEntryNumber().toString()), "xCategrNm»xInvTypNm");
                    
                    if (!lsSQL.isEmpty()){
                        if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), "") > 0){
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

            for (int lnCtr = 1; lnCtr <= lnRow; lnCtr++){
                System.out.println("Column index: " + (lnCtr) + " --> Label: " + poEntity.getMetaData().getColumnLabel(lnCtr));
                if (poEntity.getMetaData().getColumnType(lnCtr) == Types.CHAR ||
                    poEntity.getMetaData().getColumnType(lnCtr) == Types.VARCHAR){

                    System.out.println("Column index: " + (lnCtr) + " --> Size: " + poEntity.getMetaData().getColumnDisplaySize(lnCtr));
                }
            }
        } catch (SQLException e) {
        }
        
    }
    
    /**
     * Sets the TransactionNumber of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setTransactionNumber(String fsValue){
        return setValue("sTransNox", fsValue);
    }
    
    /**
     * @return The TransactionNumber of this record.
     */
    public String getTransactionNumber(){
        return (String) getValue("sTransNox");
    }
    
    /**
     * Sets the EntryNumber of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setEntryNumber(Integer fnValue){
        return setValue("nEntryNox", fnValue);
    }
    
    /**
     * @return The EntryNumber of this record. 
     */
    public Integer getEntryNumber(){
        return (Integer) getValue("nEntryNox");
    }
    
     /**
     * Sets the StockID of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setStockID(String fsValue){
        return setValue("sStockIDx", fsValue);
    }
    
    /**
     * @return The StockID of this record. 
     */
    public String getStockID(){
        return (String) getValue("sStockIDx");
    }
    
     /**
     * Sets the Description of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setDescription(String fsValue){
        return setValue("sDescript", fsValue);
    }
    
    /**
     * @return The Description of this record. 
     */
    public String getDescription(){
        return (String) getValue("sDescript");
    }
    
     /**
     * Sets the Quantity of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setQuantity(int fnValue){
        return setValue("nQuantity", fnValue);
    }
    
    /**
     * @return The Quantity of this record. 
     */
    public int getQuantity(){
        return (Integer) getValue("nQuantity");
    }
    
    /**
     * Sets the UnitPrce of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setUnitPrice(Number fnValue){
        return setValue("nUnitPrce", fnValue);
    }
    
    /**
     * @return The UnitPrce of this record. 
     */
    public Object getUnitPrice(){
        return (Object) getValue("nUnitPrce");
    }
    
    /**
     * Sets the DiscRate of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setDiscRate(Number fnValue){
        return setValue("nDiscRate", fnValue);
    }
    
    /**
     * @return The DiscRate of this record. 
     */
    public Number getDiscRate(){
        return (Number) getValue("nDiscRate");
    }
    
    /**
     * Sets the Bank Branch Name of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setDiscAmtx(Number fnValue){
        return setValue("nDiscAmtx", fnValue);
    }
    
    /**
     * @return The Bank Branch Name of this record. 
     */
    public Number getDiscAmtx(){
        return (Number) getValue("nDiscAmtx");
    }
    
    /**
     * Sets the RecordStatus of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setRecordStatus(String fsValue){
        return setValue("cRecdStat", fsValue);
    }
    
    /**
     * @return The RecordStatus of this record. 
     */
    public String getRecordStatus(){
        return (String) getValue("cRecdStat");
    }
    
    /**
     * Sets record as active.
     * 
     * @param fbValue
     * @return result as success/failed
     */
    public JSONObject setActive(boolean fbValue){
        return setValue("cRecdStat", fbValue ? "1" : "0");
    }
    
    /**
     * @return If record is active. 
     */
    public boolean isActive(){
        return ((String) getValue("cRecdStat")).equals("1");
    }
    
    /**
     * Sets the user encoded/updated the record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setModifiedBy(String fsValue){
        return setValue("sModified", fsValue);
    }
    
    /**
     * @return The user encoded/updated the record 
     */
    public String getModifiedBy(){
        return (String) getValue("sModified");
    }
    
    /**
     * Sets the date and time the record was modified.
     * 
     * @param fdValue 
     * @return result as success/failed
     */
    public JSONObject setModifiedDate(Date fdValue){
        return setValue("dModified", fdValue);
    }
    
    /**
     * @return The date and time the record was modified.
     */
    public Date getModifiedDate(){
        return (Date) getValue("dModified");
    }
    
    /**
     * Gets the SQL statement for this entity.
     * 
     * @return SQL Statement
     */
    public String makeSQL(){
        return MiscUtil.makeSQL(this, "xCategrNm»xInvTypNm");
    }
    
    public String makeSelectSQL() {
        return MiscUtil.makeSelect(this, "xCategrNm»xInvTypNm");
    }
    
    public String getSQL() {
        String lsSQL;

        lsSQL = "SELECT" +
                            "  a.sTransNox" +
                            ", a.nEntryNox" +
                            ", a.sStockIDx" +
                            ", a.sDescript" +
                            ", a.nQuantity" +
                            ", a.nUnitPrce" +
                            ", a.nDiscRate" +
                            ", a.nDiscAmtx" +
                            ", a.dModified" +
                            ", c.sDescript xCategrNm" +
                            ", d.sDescript xInvTypNm" +
                        " FROM " + getTable() + " a"+ 
                            " LEFT JOIN Inventory b ON a.sStockIDx = b.sStockIDx" +
                            " LEFT JOIN Category c ON b.sCategCd1 = c.sCategrCd" +
                            " LEFT JOIN Inv_Type d ON b.sInvTypCd = d.sInvTypCd";
        return lsSQL;
    }
    
    private void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);
            newRecord();
            

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    } 
}


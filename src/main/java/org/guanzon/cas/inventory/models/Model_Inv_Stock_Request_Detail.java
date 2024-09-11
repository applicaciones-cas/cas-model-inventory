


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
public class Model_Inv_Stock_Request_Detail implements GEntity{
    final String XML = "Model_Inv_Stock_Request_Detail.xml";
    
    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode
    
    /**
     * Entity constructor
     * 
     * @param foValue - GhostRider Application Driver
     */
    public Model_Inv_Stock_Request_Detail(GRider foValue){
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
    
    public void setEditMode(int val) {
        pnEditMode = val;
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
        return "Inv_Stock_Request_Detail";
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
        
//        //replace with the primary key column info
//        setTransactionNumber(MiscUtil.getNextCode(getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()));
        
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
        lsSQL = MiscUtil.addCondition(lsSQL, "sStockIDx = " + SQLUtil.toSQL(fsCondition));
        System.out.println("lsSQL Detail = " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        try {
            if (loRS.next()){
                for (int lnCtr = 1; lnCtr <= loRS.getMetaData().getColumnCount(); lnCtr++){
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
    public JSONObject openRecord(String lsFilter, String fsCondition) {
        poJSON = new JSONObject();

        String lsSQL = getSQL();

        //replace the condition based on the primary key column of the record
        lsSQL = MiscUtil.addCondition(lsSQL, "a.sTransNox = " + SQLUtil.toSQL(lsFilter)
                + "AND a.sStockIDx = " + SQLUtil.toSQL(fsCondition));
        System.out.println("model select = " + lsSQL);
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
        
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE){
            String lsSQL;
            if (pnEditMode == EditMode.ADDNEW){
                //replace with the primary key column info
//                setTransactionNumber(MiscUtil.getNextCode(getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()));
                
                setModifiedDate(poGRider.getServerDate());
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
                Model_Inv_Stock_Request_Detail loOldEntity = new Model_Inv_Stock_Request_Detail(poGRider);
                
                setModifiedDate(poGRider.getServerDate());
                //replace with the primary key column info
                JSONObject loJSON = loOldEntity.openRecord(this.getTransactionNumber(), this.getStockID());
                
                if ("success".equals((String) loJSON.get("result"))){
                    //replace the condition based on the primary key column of the record
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, "sTransNox = " + SQLUtil.toSQL(this.getTransactionNumber()) +  " AND sStockIDx = " + SQLUtil.toSQL(this.getStockID()), "xBarCodex»xDescript»xCategr01»xCategr02»xInvTypNm»xBrandNme»xModelNme»xModelDsc»xColorNme»xMeasurNm");
                    System.out.println("update sql = " + lsSQL);
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
     * Sets the Transaction Number of this record.
     * 
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setTransactionNumber(String fsValue){
        return setValue("sTransNox", fsValue);
    }
    
    /**
     * @return The Transaction Number of this record.
     */
    public String getTransactionNumber(){
        return (String) getValue("sTransNox");
    }
    
    /**
     * Sets the Entry Number of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setEntryNumber(int fnValue){
        return setValue("nEntryNox", fnValue);
    }
    
    /**
     * @return The Entry Number of this record. 
     */
    public int getEntryNumber(){
        return (int) getValue("nEntryNox");
    }
    
    /**
     * Sets the Stock ID of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setStockID(String fsValue){
        return setValue("sStockIDx", fsValue);
    }
    
    /**
     * @return The Stock ID of this record. 
     */
    public String getStockID(){
        return (String) getValue("sStockIDx");
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
    public Object getQuantity(){
        return (Object) getValue("nQuantity");
    }
    
            /**
     * Sets the Classify of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setClassify(String fsValue){
        return setValue("cClassify", fsValue);
    }
    
    /**
     * @return The Classify of this record. 
     */
    public String getClassify(){
        return (String) getValue("cClassify");
    }
    
    /**
     * Sets the Record Order of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setRecordOrder(int fnValue){
        return setValue("nRecOrder", fnValue);
    }
    
    /**
     * @return The Record Order of this record. 
     */
    public int getRecordOrder(){
        return (int) getValue("nRecOrder");
    }
    
    /**
     * Sets the quantity on hand of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setQuantityOnHand(int fnValue){
        return setValue("nQtyOnHnd", fnValue);
    }
    
    /**
     * @return The quantity on hand of this record. 
     */
    public Object getQuantityOnHand(){
        return (Object) getValue("nQtyOnHnd");
    }
                    /**
     * Sets the Reserved Order of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setReservedOrder(int fnValue){
        return setValue("nResvOrdr", fnValue);
    }
    
    /**
     * @return The Reserved Order of this record. 
     */
    public Object getReservedOrder(){
        return (Object) getValue("nResvOrdr");
    }
    
    
                        /**
     * Sets the Back Order of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setBackOrder(int fnValue){
        return setValue("nBackOrdr", fnValue);
    }
    
    /**
     * @return The Back Order of this record. 
     */
    public Object getBackOrder(){
        return (Object) getValue("nBackOrdr");
    }
    
                            /**
     * Sets the On Transit of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setOnTransit(int fnValue){
        return setValue("nOnTranst", fnValue);
    }
    
    /**
     * @return The On Transit of this record. 
     */
    public Object getOnTransit(){
        return (Object) getValue("nOnTranst");
    }
    
                                /**
     * Sets the Average Monthly Salary of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setAverageMonthlySalary(int fnValue){
        return setValue("nAvgMonSl", fnValue);
    }
    
    /**
     * @return The Average Monthly Salary of this record. 
     */
    public int getAverageMonthlySalary(){
        return (int) getValue("nAvgMonSl");
    }
    
                                    /**
     * Sets the Maximum Level of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setMaximumLevel(int fnValue){
        return setValue("nMaxLevel", fnValue);
    }
    
    /**
     * @return The Maximum Level of this record. 
     */
    public int getMaximumLevel(){
        return (int) getValue("nMaxLevel");
    }
    
    
    
    
                                        /**
     * Sets the Approved of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setApproved(int fnValue){
        return setValue("nApproved", fnValue);
    }
    
    /**
     * @return The Approved of this record. 
     */
    public int getApproved(){
        return (int) getValue("nApproved");
    }
    
    
                                            /**
     * Sets the Cancelled of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setCancelled(int fnValue){
        return setValue("nCancelld", fnValue);
    }
    
    /**
     * @return The Cancelled of this record. 
     */
    public int getCancelled(){
        return (int) getValue("nCancelld");
    }
    
    
    
                                                /**
     * Sets the Issue Quantity of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setIssueQuantity(int fnValue){
        return setValue("nIssueQty", fnValue);
    }
    
    /**
     * @return The Issue Quantity of this record. 
     */
    public int getIssueQuantity(){
        return (int) getValue("nIssueQty");
    }
    
    
     /**
     * Sets the Order Quantity of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setOrderQuantity(int fnValue){
        return setValue("nOrderQty", fnValue);
    }
    /**
     * @return The Order Quantity of this record. 
     */
    public int getOrderQuantity(){
        return (int) getValue("nOrderQty");
    }
    
         /**
     * Sets the Allocated Quantity of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setAllocatedQuantity(int fnValue){
        return setValue("nAllocQty", fnValue);
    }
    /**
     * @return The Allocated Quantity of this record. 
     */
    public int getAllocatedQuantity(){
        return (int) getValue("nAllocQty");
    }
    
             /**
     * Sets the Received of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setReceived(int fnValue){
        return setValue("nReceived", fnValue);
    }
    /**
     * @return The Received of this record. 
     */
    public int getReceived(){
        return (int) getValue("nReceived");
    }
    
    
                    /**
     * Sets the Notes of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setNotes(String fsValue){
        return setValue("sNotesxxx", fsValue);
    }
    
    /**
     * @return The Notes of this record. 
     */
    public String getNotes(){
        return (String) getValue("sNotesxxx");
    }
     
       
    /**
     * Sets the date the record was modified.
     * 
     * @param fdValue 
     * @return result as success/failed
     */
    public JSONObject setModifiedDate(Date fdValue){
        return setValue("dModified", fdValue);
    }
    
    /**
     * @return The date the record was modified.
     */
    public Date getModifiedDate(){
        return (Date) getValue("dModified");
    }
                    /**
     * Sets the inventory barrcode of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setBarcode(String fsValue){
        return setValue("xBarCodex", fsValue);
    }
    
    /**
     * @return The inventory barrcode of this record. 
     */
    public String getBarcode(){
        return (String) getValue("xBarCodex");
    }
   
                    /**
     * Sets the inventory description of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setDescription(String fsValue){
        return setValue("xDescript", fsValue);
    }
    
    /**
     * @return The inventory description of this record. 
     */
    public String getDescription(){
        return (String) getValue("xDescript");
    }
    /**
     * Sets the inventory category name of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setCategoryName(String fsValue){
        return setValue("xCategr01", fsValue);
    }
    
    /**
     * @return The inventory category name  of this record. 
     */
    public String getCategoryName(){
        return (String) getValue("xCategr01");
    }
    
    /**
     * Sets the inventory category name 2 of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setCategoryName2(String fsValue){
        return setValue("xCategr02", fsValue);
    }
    
    /**
     * @return The inventory category name 2  of this record. 
     */
    public String getCategoryName2(){
        return (String) getValue("xCategr02");
    }
    /**
     * Sets the inventory category type of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setCategoryType(String fsValue){
        return setValue("xInvTypNm", fsValue);
    }
    
    /**
     * @return The inventory category type of this record. 
     */
    public String getCategoryType(){
        return (String) getValue("xInvTypNm");
    }
    
    
    /**
     * @return The xBrandNme of this record.
     */
     
    public String getBrandName() {
         System.out.println("\nto get xBrandNme == " + (String)getValue("xBrandNme"));
        return (String) getValue("xBrandNme");
    }
    /**
     * Sets the sBrandCde of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBrandName(String fsValue) {
         System.out.println("\nto set xBrandNme == " + fsValue);
        return setValue("xBrandNme", fsValue);
    }

    /**
     * @return The sModelCde of this record.
     */
    public String getModelName() {
        return (String) getValue("xModelNme");
    }
    
    /**
     * Sets the sModelCde of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setModelName(String fsValue) {
        return setValue("xModelNme", fsValue);
    }
    
    /**
     * @return The xModelDsc of this record.
     */
    public String getModelDescription() {
        return (String) getValue("xModelDsc");
    }
    
    /**
     * Sets the xModelDsc of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setModelDescription(String fsValue) {
        return setValue("xModelDsc", fsValue);
    }
    
   

    /**
     * @return The sModelCde of this record.
     */
    public String getColorName() {
        return (String) getValue("xColorNme");
    }
    
    /**
     * Sets the sModelCde of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setColorName(String fsValue) {
        return setValue("xColorNme", fsValue);
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
    public String makeSQL(){
        return MiscUtil.makeSQL(this, "xBarCodex»xDescript»xCategr01»xCategr02»xInvTypNm»xBrandNme»xModelNme»xModelDsc»xColorNme»xMeasurNm");
    }
    
    /**
     * Gets the SQL statement for this entity.
     * 
     * @return SQL Statement
     */
    public String makeSelectSQL() {
        return MiscUtil.makeSelect(this, "xBarCodex»xDescript»xCategr01»xCategr02»xInvTypNm»xBrandNme»xModelNme»xModelDsc»xColorNme»xMeasurNm");
    }
    
    /**
     * Gets the SQL statement for this entity.
     * 
     * @return SQL Statement
     */
    public String getSQL(){
        return "SELECT" +
                            "  a.sTransNox" +
                            ", a.nEntryNox" +
                            ", a.sStockIDx" +
                            ", a.nQuantity" +
                            ", a.cClassify" +
                            ", a.nRecOrder" +
                            ", a.nQtyOnHnd" +
                            ", a.nResvOrdr" +
                            ", a.nBackOrdr" +
                            ", a.nOnTranst" +
                            ", a.nAvgMonSl" +
                            ", a.nMaxLevel" +
                            ", a.nApproved" +
                            ", a.nCancelld" +
                            ", a.nIssueQty" +
                            ", a.nOrderQty" +
                            ", a.nAllocQty" +
                            ", a.nReceived" +
                            ", a.sNotesxxx" +
                            ", a.dModified" +
                            ", b.sBarCodex xBarCodex" +
                            ", b.sDescript xDescript" +
                            ", c.sDescript xCategr01" +
                            ", d.sDescript xCategr02" +
                            ", e.sDescript xInvTypNm" +
                            ", f.sDescript xBrandNme" +
                            ", g.sModelNme xModelNme" +
                            ", g.sDescript xModelDsc" +
                            ", h.sDescript xColorNme" +
                            ", i.sMeasurNm xMeasurNm" +
                        " FROM Inv_Stock_Request_Detail a" + 
                            " LEFT JOIN Inventory b ON a.sStockIDx = b.sStockIDx" +
                            " LEFT JOIN Category c ON b.sCategCd1 = c.sCategrCd" +
                            " LEFT JOIN Category_Level2 d ON b.sCategCd2 = d.sCategrCd" +
                            " LEFT JOIN Inv_Type e ON d.sInvTypCd = e.sInvTypCd" +
                            " LEFT JOIN Brand f ON b.sBrandCde = f.sBrandCde" +
                            " LEFT JOIN Model g ON b.sModelCde = g.sModelCde" +
                            " LEFT JOIN Color h ON b.sColorCde = h.sColorCde" +
                            " LEFT JOIN Measure i ON b.sMeasurID = i.sMeasurID" ;
    }
    
    private void initialize(){
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());
            
            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);   
            poEntity.updateObject("sStockIDx", "");   
            poEntity.updateObject("nQuantity", 0);
            poEntity.updateObject("nRecOrder", 0);
            poEntity.updateObject("nQtyOnHnd", 0);
            poEntity.updateObject("nResvOrdr", 0);
            poEntity.updateObject("nBackOrdr", 0);
            poEntity.updateObject("nOnTranst", 0);
            poEntity.updateObject("nAvgMonSl", 0);
            poEntity.updateObject("nMaxLevel", 0);
            poEntity.updateObject("nApproved", 0);
            poEntity.updateObject("nCancelld", 0);
            poEntity.updateObject("nIssueQty", 0);
            poEntity.updateObject("nOrderQty", 0);
            poEntity.updateObject("nAllocQty", 0);
            poEntity.updateObject("nReceived", 0);
            poEntity.updateObject("cClassify", "");
            
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
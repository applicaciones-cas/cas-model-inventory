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
public class Model_Inv_Master implements GEntity {

    final String XML = "Model_Inv_Master.xml";

    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode

    /**
     * Entity constructor
     *
     * @param foValue - GhostRider Application Driver
     */
    public Model_Inv_Master(GRider foValue) {
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
        return "Inv_Master";
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
//        setStockID(MiscUtil.getNextCode(getTable(), "sStockIDx", false, poGRider.getConnection(), ""));
        setAcquiredDate(poGRider.getServerDate());
        setDBegInvxx(poGRider.getServerDate());
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
        lsSQL = MiscUtil.addCondition(lsSQL, "a.sStockIDx = " + SQLUtil.toSQL(fsCondition));
        System.out.println("lsSQL = " + lsSQL);
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
//                setStockID(MiscUtil.getNextCode(getTable(), "sStockIDx", false, poGRider.getConnection(), ""));

                setModifiedDate(poGRider.getServerDate());
                setModifiedBy(poGRider.getUserID());
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
                Model_Inv_Master loOldEntity = new Model_Inv_Master(poGRider);

                setModifiedDate(poGRider.getServerDate());
                setModifiedBy(poGRider.getUserID());
                //replace with the primary key column info
                JSONObject loJSON = loOldEntity.openRecord(this.getStockID());

                if ("success".equals((String) loJSON.get("result"))) {
                    //replace the condition based on the primary key column of the record
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, "sStockIDx = " + SQLUtil.toSQL(this.getStockID()),  "xBarCodex»xDescript»xWHouseNm»xLocatnNm»xSectnNme");
                     
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
     * Sets the sBranchCd of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBranchCd(String fsValue) {
        return setValue("sBranchCd", fsValue);
    }

    /**
     * @return The sBranchCd of this record.
     */
    public String getBranchCd() {
        return (String) getValue("sBranchCd");
    }
    
    /**
     * Sets the sWHouseID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setWareHouseID(String fsValue) {
        return setValue("sWHouseID", fsValue);
    }

    /**
     * @return The sWHouseID of this record.
     */
    public String getWareHouseID() {
        return (String) getValue("sWHouseID");
    }
    
    /**
     * Sets the sLocatnCd of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setLocatnCode(String fsValue) {
        return setValue("sLocatnCd", fsValue);
    }

    /**
     * @return The sLocatnCd of this record.
     */
    public String getLocatnCode() {
        return (String) getValue("sLocatnCd");
    }
    
    /**
     * Sets the nBinNumbr of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBinNumber(Number fsValue) {
        return setValue("nBinNumbr", fsValue);
    }

    /**
     * @return The nBinNumbr of this record.
     */
    public Object getBinNumber() {
        return (Object) getValue("nBinNumbr");
    }
    
    /**
     * Sets the dAcquired of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setAcquiredDate(Date  fsValue) {
        return setValue("dAcquired", fsValue);
    }

    /**
     * @return The dAcquired of this record.
     */
    public Object getcquiredDate() {
        return (Object) getValue("dAcquired");
    }
    
    /**
     * Sets the dBegInvxx of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setDBegInvxx(Date fsValue) {
        return setValue("dBegInvxx", fsValue);
    }

    /**
     * @return The dBegInvxx of this record.
     */
    public Object getDBegInvxx() {
        return (Object) getValue("dBegInvxx");
    }
    
    /**
     * Sets the nBegQtyxx of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBegQtyxx(Number fsValue) {
        return setValue("nBegQtyxx", fsValue);
    }

    /**
     * @return The dBegInvxx of this record.
     */
    public Object getBegQtyxx() {
        return (Object) getValue("nBegQtyxx");
    }
    /**
     * Sets the nQtyOnHnd of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setQtyOnHnd(Number fsValue) {
        return setValue("nQtyOnHnd", fsValue);
    }

    /**
     * @return The nQtyOnHnd of this record.
     */
    public Object getQtyOnHnd() {
        return (Object) getValue("nQtyOnHnd");
    }
    
    /**
     * Sets the nLedgerNo of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setLedgerNo(Number fsValue) {
        return setValue("nLedgerNo", fsValue);
    }

    /**
     * @return The nLedgerNo of this record.
     */
    public Object getLedgerNo() {
        return (Object) getValue("nLedgerNo");
    }
     
    /**
     * Sets the nMinLevel of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setMinLevel(Number fsValue) {
        return setValue("nMinLevel", fsValue);
    }

    /**
     * @return The nMinLevel of this record.
     */
    public Object getMinLevel() {
        return (Object) getValue("nMinLevel");
    }
     
    /**
     * Sets the nMaxLevel of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setMaxLevel(Number fsValue) {
        return setValue("nMaxLevel", fsValue);
    }

    /**
     * @return The nMaxLevel of this record.
     */
    public Object getMaxLevel() {
        return (Object) getValue("nMaxLevel");
    }
    
    /**
     * Sets the nAvgMonSl of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setAvgMonSl(Number fsValue) {
        return setValue("nAvgMonSl", fsValue);
    }

    /**
     * @return The nAvgMonSl of this record.
     */
    public Object getAvgMonSl() {
        return (Object) getValue("nAvgMonSl");
    }
    
    /**
     * Sets the nAvgCostx of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setAvgCostx(Number fsValue) {
        return setValue("nAvgCostx", fsValue);
    }

    /**
     * @return The nAvgCostx of this record.
     */
    public Object getAvgCostx() {
        return (Object) getValue("nAvgCostx");
    }
    
    
    /**
     * Sets the cClassify of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setClassify(String fsValue) {
        return setValue("cClassify", fsValue);
    }

    /**
     * @return The cClassify of this record.
     */
    public String getClassify() {
        return (String) getValue("cClassify");
    }
    
    /**
     * Sets the nBackOrdr of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBackOrdr(Number fsValue) {
        return setValue("nBackOrdr", fsValue);
    }

    /**
     * @return The nBackOrdr of this record.
     */
    public Object getBackOrdr() {
        return (Object) getValue("nBackOrdr");
    }
    
    /**
     * Sets the nResvOrdr of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setResvOrdr(Number fsValue) {
        return setValue("nResvOrdr", fsValue);
    }

    /**
     * @return The nResvOrdr of this record.
     */
    public Object getResvOrdr() {
        return (Object) getValue("nResvOrdr");
    }
    
    /**
     * Sets the nFloatQty of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setFloatQty(Number fsValue) {
        return setValue("nFloatQty", fsValue);
    }

    /**
     * @return The nFloatQty of this record.
     */
    public Object getFloatQty() {
        return (Object) getValue("nFloatQty");
    }
    
    /**
     * Sets the dLastTran of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setLastTranDate(String fsValue) {
        return setValue("dLastTran", fsValue);
    }

    /**
     * @return The dLastTran of this record.
     */
    public Object getLastTranDate() {
        return (Object) getValue("dLastTran");
    }
    
    /**
     * Sets the cPrimaryx of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setPrimary(String fsValue) {
        return setValue("cPrimaryx", fsValue);
    }

    /**
     * @return The cPrimaryx of this record.
     */
    public String getPrimary() {
        return (String) getValue("cPrimaryx");
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
     * @return The xBarCodex the record
     */
    public String getBarCodex() {
        return (String) getValue("xBarCodex");
    }

    /**
     * Sets the xBarCodex of the record was modified.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setBarCodex(String fdValue) {
        return setValue("xBarCodex", fdValue);
    }

    /**
     * @return The xDescript the record
     */
    public String getDescript() {
        return (String) getValue("xDescript");
    }

    /**
     * Sets the xDescript of the record was modified.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setDescript(String fdValue) {
        return setValue("xDescript", fdValue);
    }
    
    /**
     * @return The xWHouseNm the record
     */
    public String getWareHouseNm() {
        return (String) getValue("xWHouseNm");
    }

    /**
     * Sets the xWHouseNm of the record was modified.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setWareHouseNm(String fdValue) {
        return setValue("xWHouseNm", fdValue);
    }
    
    /**
     * @return The xLocatnNm the record
     */
    public String getLocationnName() {
        return (String) getValue("xLocatnNm");
    }

    /**
     * Sets the xLocatnNm of the record was modified.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setLocationnName(String fdValue) {
        return setValue("xLocatnNm", fdValue);
    }

    /**
     * @return The xSectnNme the record
     */
    public String getSectionName() {
        return (String) getValue("xSectnNme");
    }
    /**
     * Gets the SQL statement for this entity.
     *
     * @return SQL Statement
     */
    public String makeSQL() {
        return MiscUtil.makeSQL(this, "xBarCodex»xDescript»xWHouseNm»xLocatnNm»dLastTran»xSectnNme");
    }
    

    /**
     * Gets the SQL Select statement for this entity.
     *
     * @return SelectSQL Statement
     */
    public String makeSelectSQL() {
        return MiscUtil.makeSelect(this, "xBarCodex»xDescript»xWHouseNm»xLocatnNm»xSectnNme");
    }
    
    public String getSQL(){
        return "SELECT" +
                            "   a.sStockIDx" +
                            " , a.sBranchCd" +
                            " , a.sWHouseID" +
                            " , a.sLocatnCd" +
                            " , a.nBinNumbr" +
                            " , a.dAcquired" +
                            " , a.dBegInvxx" +
                            " , a.nBegQtyxx" +
                            " , a.nQtyOnHnd" +
                            " , a.nLedgerNo" +
                            " , a.nMinLevel" +
                            " , a.nMaxLevel" +
                            " , a.nAvgMonSl" +
                            " , a.nAvgCostx" +
                            " , a.cClassify" +
                            " , a.nBackOrdr" +
                            " , a.nResvOrdr" +
                            " , a.nFloatQty" +
                            " , a.dLastTran" +
                            " , a.cPrimaryx" +
                            " , a.cRecdStat" +
                            " , a.sModified" +
                            " , a.dModified" +
                            " , b.sBarCodex xBarCodex" +
                            " , b.sDescript xDescript" +
                            " , c.sWHouseNm xWHouseNm" +
                            " , d.sDescript xLocatnNm" +
                            " , e.sSectnNme xSectnNme" +
                        " FROM Inv_Master a"+ 
                            " LEFT JOIN Inventory b ON a.sStockIDx = b.sStockIDx" +
                            " LEFT JOIN Warehouse c ON a.sWhouseID = c.sWhouseID" +
                            " LEFT JOIN Inv_Location d ON a.sLocatnCd = d.sLocatnCd" +
                            " LEFT JOIN Section e ON e.sSectnIDx = d.sSectnIDx";
    }

    private void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);
            poEntity.updateString("cRecdStat", RecordStatus.ACTIVE);
            poEntity.updateObject("nBinNumbr", 0);
            poEntity.updateObject("nBegQtyxx", 0);
            poEntity.updateObject("nQtyOnHnd", 0);
            poEntity.updateObject("nLedgerNo", 0);
            poEntity.updateObject("nMinLevel", 0);
            poEntity.updateObject("nMaxLevel", 0);
            poEntity.updateObject("nAvgMonSl", 0.0);
            poEntity.updateObject("nAvgCostx", 0.0);
            poEntity.updateObject("nBackOrdr", 0.0);
            poEntity.updateObject("nResvOrdr", 0.0);
            poEntity.updateObject("nFloatQty", 0.0);
            
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

package org.guanzon.cas.model.inventory;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import javax.sql.rowset.CachedRowSet;
import javax.swing.JOptionPane;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.TransactionStatus;
import org.guanzon.appdriver.iface.GEntity;
import org.json.simple.JSONObject;

/**
 * @author Michael Cuison
 */
public class Model_PO_Quotation_Request_Master implements GEntity {

    final String XML = "Model_PO_Quotation_Request_Master.xml";

    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode

    /**
     * Entity constructor
     *
     * @param foValue - GhostRider Application Driver
     */
    public Model_PO_Quotation_Request_Master(GRider foValue) {
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
        return "PO_Quotation_Request_Master";
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
//            poJSON = MiscUtil.validateColumnValue(System.getProperty("sys.default.path.metadata") + XML, MiscUtil.getColumnLabel(poEntity, fnColumn), foValue);
//            if ("error".equals((String) poJSON.get("result"))) {
//                return poJSON;
//            }

            poEntity.updateObject(fnColumn, foValue);
            poEntity.updateRow();

            poJSON = new JSONObject();
            poJSON.put("result", "success");
            poJSON.put(MiscUtil.getColumnLabel(poEntity, fnColumn), getValue(fnColumn));
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

    public boolean isOpen() {
        return ((String) getValue("cTranStat")).equals("0");
    }

    public boolean isClosed() {
        return ((String) getValue("cTranStat")).equals("0");
    }

    public boolean isPosted() {
        return ((String) getValue("cTranStat")).equals("0");
    }

    public boolean isCancelled() {
        return ((String) getValue("cTranStat")).equals("0");
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
        setTransactionNo(MiscUtil.getNextCode(getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()));

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
        lsSQL = MiscUtil.addCondition(lsSQL, " sTransNox = " + SQLUtil.toSQL(fsCondition));

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
                setTransactionNo(MiscUtil.getNextCode(getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()));

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
                Model_PO_Quotation_Request_Master loOldEntity = new Model_PO_Quotation_Request_Master(poGRider);

                //replace with the primary key column info
                JSONObject loJSON = loOldEntity.openRecord(this.getTransactionNumber());

                if ("success".equals((String) loJSON.get("result"))) {
                    //replace the condition based on the primary key column of the record
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, "sTransNox = " + SQLUtil.toSQL(this.getTransactionNumber()), "xCategrNm");

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

    public JSONObject searchCategory(String fsValue) {
        JSONObject loJSON = new JSONObject();

        String lsSQL = "SELECT sCategrCd, sDescript FROM Category";
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        try {
            if (!loRS.next()) {
                loJSON.put("result", "error");
            } else {
                loJSON.put("result", "success");
                loJSON.put("sDescript", loRS.getString("sDescript"));
            }
        } catch (SQLException e) {
            loJSON.put("result", "error");
            loJSON.put("message", e.getMessage());
        } finally {
            MiscUtil.close(loRS);
        }

        return loJSON;
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
     * Sets the sBrandCde of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setTransactionNo(String fsValue) {
        return setValue("sTransNox", fsValue);
    }

    /**
     * @return The sBrandCde of this record.
     */
    public String getTransactionNumber() {
        return (String) getValue("sTransNox");
    }

    /**
     * Sets the sDescript of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBranchCd(String fsValue) {
        return setValue("sBranchCd", fsValue);
    }

    /**
     * @return The sDescript of this record.
     */
    public String getBranchCd() {
        return (String) getValue("sBranchCd");
    }

    /**
     * Sets the user encoded/updated the record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setDestination(String fsValue) {
        return setValue("sDestinat", fsValue);
    }

    /**
     * @return The user encoded/updated the record
     */
    public String getDestination() {
        return (String) getValue("sDestinat");
    }

    /**
     * Sets the date and time the record was modified.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setTransactionDate(Date fdValue) {
        return setValue("dTransact", fdValue);
    }

    /**
     * @return The date and time the record was modified.
     */
    public Date getTransactionDate() {
        return (Date) getValue("dTransact");
    }

    /**
     * Sets the user encoded/updated the record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setReferenceNumber(String fsValue) {
        return setValue("sReferNox", fsValue);
    }

    /**
     * @return The user encoded/updated the record
     */
    public String getReferenceNumber() {
        return (String) getValue("sReferNox");
    }

    /**
     * Sets the user encoded/updated the record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setRemarks(String fsValue) {
        return setValue("sRemarksx", fsValue);
    }

    /**
     * @return The user encoded/updated the record
     */
    public String getRemarks() {
        return (String) getValue("sRemarksx");
    }

    /**
     * Sets the date and time the record was modified.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setExpectedPurchaseDate(Date fdValue) {
        return setValue("dExpPurch", fdValue);
    }

    /**
     * @return The date and time the record was modified.
     */
    public Date getExpectedPurchaseDate() {
        return (Date) getValue("dExpPurch");
    }

    /**
     * Sets the user encoded/updated the record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setEntryNumber(String fsValue) {
        return setValue("nEntryNox", fsValue);
    }

    /**
     * @return The user encoded/updated the record
     */
    public int getEntryNumber() {
        return (int) getValue("nEntryNox");
    }

    /**
     * Sets the user encoded/updated the record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setInventoryTypeCode(String fsValue) {
        return setValue("sInvTypCd", fsValue);
    }

    /**
     * @return The user encoded/updated the record
     */
    public String getInventoryTypeCode() {
        return (String) getValue("sInvTypCd");
    }

    /**
     * Sets the user encoded/updated the record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setTransactionStatus(String fsValue) {
        return setValue("cTranStat", fsValue);
    }

    /**
     * @return The user encoded/updated the record
     */
    public Integer getTransactionStatus() {
        return (Integer) getValue("cTranStat");
    }

    /**
     * Sets the user encoded/updated the record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setPreparedBy(String fsValue) {
        return setValue("sPrepared", fsValue);
    }

    /**
     * @return The user encoded/updated the record
     */
    public String getPreparedBy() {
        return (String) getValue("sPrepared");
    }

    /**
     * Sets the date and time the record was modified.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setPreparedDate(Date fdValue) {
        return setValue("dPrepared", fdValue);
    }

    /**
     * @return The date and time the record was modified.
     */
    public Date getPreparedDate() {
        return (Date) getValue("dPrepared");
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

    public JSONObject setCategoryCode(String fsValue) {
        return setValue("sCategrCd", fsValue);
    }

    /**
     * @return The sCategrCd of this record.
     */
    public String getCategoryCode() {
        return (String) getValue("sCategrCd");
    }

    /**
     * @return The Category Name of this record.
     */
    public String getCategoryName() {
        return (String) getValue("xCategrNm");
    }

    /*
     * @return The Category Name of this record. 
     */
    public JSONObject setCategoryName(String fsValue) {
        return setValue("xCategrNm", fsValue);
    }

    /**
     * Gets the SQL statement for this entity.
     *
     * @return SQL Statement
     */
    public String makeSQL() {
        return MiscUtil.makeSQL(this, "xCategrNm");
    }

    /**
     * Gets the SQL Select statement for this entity.
     *
     * @return SelectSQL Statement
     */
    public String makeSelectSQL() {
        return MiscUtil.makeSelect(this, "xCategrNm");
    }

    public String getSQL() {
        String lsSQL = "SELECT"
                + "  a.sTransNox"
                + ", a.sBranchCd"
                + ", a.dTransact"
                + ", a.sDestinat"
                + ", a.sReferNox"
                + ", a.sRemarksx"
                + ", a.dExpPurch"
                + ", a.nEntryNox"
                + ", a.sCategrCd"
                + ", a.cTranStat"
                + ", a.sPrepared"
                + ", a.dPrepared"
                + ", a.sModified"
                + ", a.dModified"
                + ", b.sBranchNm xBranchNm"
                + ", c.sBranchNm xDestinat"
                + ", d.sDescript xCategrNm"
                + ", e.sdescript xInvTypNm"
                + " FROM " + System.getProperty("sys.table") + " a"
                + " LEFT JOIN Branch b ON a.sBranchCd = b.sBranchCd"
                + " LEFT JOIN Branch c ON a.sDestinat = b.sBranchCd"
                + " LEFT JOIN Category d ON a.sCategrCd = d.sCategrCd"
                + " LEFT JOIN Inv_Type e ON d.sInvTypCd = e.sInvTypCd"
                + " WHERE 0=1";

        if (!System.getProperty("store.inventory.industry").isEmpty()) {
            lsSQL = MiscUtil.addCondition(lsSQL, " sTransNox IN " + CommonUtils.getParameter(System.getProperty("store.inventory.industry")));
        }
        return lsSQL;
    }

    private void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);
            poEntity.updateString("cTranStat", RecordStatus.ACTIVE);

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

package org.guanzon.cas.inventory.models;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
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
public class Model_PO_Quotation_Request_Detail implements GEntity {

    final String XML = "Model_PO_Quotation_Request_Detail.xml";

    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode

    /**
     * Entity constructor
     *
     * @param foValue - GhostRider Application Driver
     */
    public Model_PO_Quotation_Request_Detail(GRider foValue) {
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
        return "PO_Quotation_Request_Detail";
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
        setQuantity(0);
        setUnitPrice(0.0);
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }

    /**
     * Opens a record by single condition. Not supported yet on detail model.
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

    /**
     * Opens a record.
     *
     * @param fsCondition - filter values
     * @return result as success/failed
     */
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
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            String lsSQL;
            if (pnEditMode == EditMode.ADDNEW) {
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
                Model_PO_Quotation_Request_Detail loOldEntity = new Model_PO_Quotation_Request_Detail(poGRider);

                //replace with the primary key column info  and additional primary column
                JSONObject loJSON = loOldEntity.openRecord(this.getTransactionNo(), this.getEntryNo().toString());

                if ("success".equals((String) loJSON.get("result"))) {
                    //replace the condition based on the primary key column of the record and additional primary column
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, "sTransNox = " + SQLUtil.toSQL(this.getTransactionNo())
                            + "AND nEntryNox = " + SQLUtil.toSQL(this.getEntryNo().toString()), "xCategrNm»xInvTypNm");

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
     * Description: Sets the sTransNox of this record.
     *
     * @param fsValue
     * @return True if the record assignment is successful.
     */
    public JSONObject setTransactionNo(String fsValue) {
        return setValue("sTransNox", fsValue);
    }

    /**
     * @return The sTransNox of this record.
     */
    public String getTransactionNo() {
        return (String) getValue("sTransNox");
    }

    /**
     * Description: Sets the nEntryNox of this record.
     *
     * @param fsValue
     * @return True if the record assignment is successful.
     */
    public JSONObject setEntryNo(Number fsValue) {
        return setValue("nEntryNox", fsValue);
    }

    /**
     * @return The nEntryNox of this record.
     */
    public Number getEntryNo() {
        return (Number) getValue("nEntryNox");
    }

    /**
     * Description: Sets the sStockIDx of this record.
     *
     * @param fsValue
     * @return True if the record assignment is successful.
     */
    public JSONObject setStockID(String fsValue) {
        return setValue("sStockIDx", fsValue);
    }

    /**
     * @return The nEntryNox of this record.
     */
    public String getStockID() {
        return (String) getValue("sStockIDx");
    }

    /**
     * Description: Sets the sDescript of this record.
     *
     * @param fsValue
     * @return True if the record assignment is successful.
     */
    public JSONObject setDescript(String fsValue) {
        return setValue("sDescript", fsValue);
    }

    /**
     * @return The sDescript of this record.
     */
    public String getDescript() {
        return (String) getValue("sDescript");
    }

    /**
     * Description: Sets the nQuantity of this record.
     *
     * @param fsValue
     * @return True if the record assignment is successful.
     */
    public JSONObject setQuantity(int fsValue) {
        return setValue("nQuantity", fsValue);
    }

    /**
     * @return The nQuantity of this record.
     */
    public int getQuantity() {
        return (Integer) getValue("nQuantity");
    }

    /**
     * Description: Sets the nUnitPrce of this record.
     *
     * @param fsValue
     * @return True if the record assignment is successful.
     */
    public JSONObject setUnitPrice(Number fsValue) {
        return setValue("nUnitPrce", fsValue);
    }

    /**
     * @return The nUnitPrce of this record.
     */
    public Number getUnitPrice() {
        return (Number) getValue("nUnitPrce");
    }

    /**
     * Description: Sets the dModified of this record.
     *
     * @param fsValue
     * @return True if the record assignment is successful.
     */
    public JSONObject setModifiedDate(Date fdValue) {
        return setValue("dModified", fdValue);
    }

    /**
     * @return The dModified of this record.
     */
    public Date getModifiedDate() {
        return (Date) getValue("dModified");
    }

    /**
     * Description: Sets the xCategrNm of this record.
     *
     * @param fsValue
     * @return True if the record assignment is successful.
     */
    public JSONObject setCategoryName(String fsValue) {
        return setValue("xCategrNm", fsValue);
    }

    /**
     * @return The xCategrNm of this record.
     */
    public String getCategoryName() {
        return (String) getValue("xCategrNm");
    }

    /**
     * Description: Sets the xInvTypNm of this record.
     *
     * @param fsValue
     * @return True if the record assignment is successful.
     */
    public JSONObject setInventoryName(String fsValue) {
        return setValue("xInvTypNm", fsValue);
    }

    /**
     * @return The xInvTypNm of this record.
     */
    public String getInventoryName() {
        return (String) getValue("xInvTypNm");
    }

    /**
     * Gets the SQL statement for this entity.
     *
     * @return SQL Statement
     */
    public String makeSQL() {
        return MiscUtil.makeSQL(this, "xCategrNm»xInvTypNm");
    }

    public String makeSelectSQL() {
        return MiscUtil.makeSelect(this, "xCategrNm»xInvTypNm");
    }

    public String getSQL() {
        String lsSQL;

        lsSQL = "SELECT "
                + " a.sTransNox sTransNox "
                + ",a.nEntryNox nEntryNox"
                + ",a.sStockIDx sStockIDx"
                + ",a.sDescript sDescript"
                + ",a.nQuantity nQuantity"
                + ",a.nUnitPrce nUnitPrce"
                + ",a.dModified  dModified"
                + ", c.sDescript xCategrNm "
                + ", d.sDescript xInvTypNm "
                + " FROM " + getTable() + " a"
                + " LEFT JOIN Inventory b ON a.sStockIDx = b.sStockIDx"
                + " LEFT JOIN Category_Level2 c ON b.sCategCd2 = c.sCategrCd"
                + " LEFT JOIN Inv_Type d ON c.sInvTypCd = d.sInvTypCd";
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

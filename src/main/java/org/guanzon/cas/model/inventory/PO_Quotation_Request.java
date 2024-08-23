/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.guanzon.cas.model.inventory;






import java.math.BigDecimal;
import org.guanzon.appdriver.iface.GTranDet;

import org.guanzon.cas.parameters.Category_Level3;
import org.guanzon.cas.parameters.Category_Level4;
import org.guanzon.cas.parameters.Color;
import org.guanzon.cas.parameters.Inv_Type;
import org.guanzon.cas.parameters.Measure;
import org.guanzon.cas.parameters.Model;
import org.guanzon.cas.inventory.base.Inventory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.TransactionStatus;
import org.guanzon.appdriver.iface.GTransaction;

import org.guanzon.cas.parameters.Category;

import org.json.simple.JSONObject;


/**
 *
 * @author user
 */
public class PO_Quotation_Request implements GTransaction, GTranDet {
    GRider poGRider;
    boolean pbWthParent;
    int pnEditMode;
    String psTranStatus;
    Category catest;
    String psAccountType = "0";
    Inventory psInventory;

    Model_PO_Quotation_Request_Master poModelMaster;
    ArrayList<Model_PO_Quotation_Request_Detail> poModelDetail;
    ArrayList<Model_PO_Quotation_Request_Detail> poModelDetail1;
    ArrayList<Model_PO_Quotation_Request_Master> poModelMasterA;

    JSONObject poJSON;

    public PO_Quotation_Request(GRider foGRider, boolean fbWthParent) {
        poGRider = foGRider;
        pbWthParent = fbWthParent;

        poModelMaster = new Model_PO_Quotation_Request_Master(foGRider);
        poModelDetail = new ArrayList<Model_PO_Quotation_Request_Detail>();
//        poModelDetail1 = new Model_PO_Quotation_Request_Detail;
        poModelDetail.add(new Model_PO_Quotation_Request_Detail(foGRider));
        pnEditMode = EditMode.UNKNOWN;
    }

    @Override
    public JSONObject newTransaction() {
//            poDetail = new Model_PO_Quotation_Request_Detail();
        return poModelMaster.newRecord();
        
    }
    
    public ArrayList<Model_PO_Quotation_Request_Detail> getDetail(){return poModelDetail;}
    public void setDetaila(ArrayList<Model_PO_Quotation_Request_Detail> foObj){poModelDetail = foObj;}
    
    
    public void setDetaila(int fnRow, int fnIndex, Object foValue){ 
        poModelDetail.get(fnRow).setValue(fnIndex, foValue);
    }
    
    public void setDetaila(int fnRow, String fsIndex, Object foValue){ 
        poModelDetail.get(fnRow).setValue(fsIndex, foValue);
    }
    
    public Object getDetail(int fnRow, int fnIndex){
        return poModelDetail.get(fnRow).getValue(fnIndex);
    }
    public Object getDetail(int fnRow, String fsIndex){
        return poModelDetail.get(fnRow).getValue(fsIndex);
    }
    
    
    

    @Override
    public JSONObject openTransaction(String fsValue) {

        poModelMaster.openRecord(SQLUtil.toSQL(fsValue));
        if ("error".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        OpenModelDetail(poModelMaster.getTransactionNumber());

        return poJSON;

    }

    @Override
    public JSONObject updateTransaction() {
        JSONObject loJSON = new JSONObject();

        if (poModelMaster.getEditMode() == EditMode.UPDATE) {
            loJSON.put("result", "success");
            loJSON.put("message", "Edit mode has changed to update.");
        } else {
            loJSON.put("result", "error");
            loJSON.put("message", "No record loaded to update.");
        }

        return loJSON;
    }

    @Override
    public JSONObject saveTransaction() {
        if (!pbWthParent) {
            poGRider.beginTrans();
        }

        if (getItemCount() >= 1) {
            for (int lnCtr = 0; lnCtr <= getItemCount() - 1; lnCtr++) {
                poModelDetail.get(lnCtr).setEntryNo(lnCtr + 1);
                poJSON = poModelDetail.get(lnCtr).saveRecord();

                if ("error".equals((String) poJSON.get("result"))) {

                    if (!pbWthParent) {
                        poGRider.rollbackTrans();
                    }
                    return poJSON;
                }

            }

        } else {
            poJSON.put("result", "error");
            poJSON.put("message", "Unable to Save empty Transaction.");
            return poJSON;
        }

        poJSON = poModelMaster.saveRecord();
        if ("success".equals((String) poJSON.get("result"))) {
            if (!pbWthParent) {
                poGRider.commitTrans();
            }
        } else {
            if (!pbWthParent) {
                poGRider.rollbackTrans();
                poJSON.put("result", "error");
                poJSON.put("message", "Unable to Save Transaction.");
            }
        }

        return poJSON;
    }

    @Override
    public JSONObject deleteTransaction(String fsValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JSONObject closeTransaction(String fsValue) {
        poJSON = new JSONObject();
        if (poModelMaster.getEditMode() == EditMode.READY || poModelMaster.getEditMode() == EditMode.UPDATE) {
            poJSON = poModelMaster.setTransactionStatus(TransactionStatus.STATE_CLOSED);
            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

            poJSON = poModelMaster.saveRecord();
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
        }
        return poJSON;
    }

    @Override
    public JSONObject postTransaction(String fsValue) {
        poJSON = new JSONObject();

        if (poModelMaster.getEditMode() == EditMode.READY
                || poModelMaster.getEditMode() == EditMode.UPDATE) {

            poJSON = poModelMaster.setTransactionStatus(TransactionStatus.STATE_POSTED);
            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

            poJSON = poModelMaster.saveRecord();
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
        }
        return poJSON;
    }

    @Override
    public JSONObject voidTransaction(String string) {
        poJSON = new JSONObject();

        if (poModelMaster.getEditMode() == EditMode.READY
                || poModelMaster.getEditMode() == EditMode.UPDATE) {
            poJSON = poModelMaster.setTransactionStatus(TransactionStatus.STATE_VOID);

            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

            poJSON = poModelMaster.saveRecord();
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
        }
        return poJSON;
    }

    @Override
    public JSONObject cancelTransaction(String fsTransNox) {
        poJSON = new JSONObject();

        if (poModelMaster.getEditMode() == EditMode.READY
                || poModelMaster.getEditMode() == EditMode.UPDATE) {
            poJSON = poModelMaster.setTransactionStatus(TransactionStatus.STATE_CANCELLED);

            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

            poJSON = poModelMaster.saveRecord();
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
        }
        return poJSON;
    }

    @Override
    public int getItemCount() {
        return poModelDetail.size();
    }

//    @Override
    public ArrayList<Model_PO_Quotation_Request_Detail> getDetailModel() {
        return poModelDetail;
    }

    @Override
    public JSONObject setDetail(int fnRow, int fnCol, Object foData) {
        return poModelDetail.get(fnRow).setValue(fnCol, foData);
    }

    @Override
    public JSONObject setDetail(int fnRow, String fsCol, Object foData) {
        return poModelDetail.get(fnRow).setValue(fsCol, foData);
    }

    @Override
    public JSONObject searchDetail(int i, String string, String string1, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public JSONObject searchDetail(int i, int i1, String string, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public JSONObject searchWithCondition(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public JSONObject searchTransaction(String fsColumn, String fsValue, boolean fbByCode) {
        String lsCondition = "";
        String lsFilter = "";

        if (psTranStatus.length() > 1) {
            for (int lnCtr = 0; lnCtr <= psTranStatus.length() - 1; lnCtr++) {
                lsCondition += ", " + SQLUtil.toSQL(Character.toString(psTranStatus.charAt(lnCtr)));
            }

            lsCondition = fsColumn + " IN (" + lsCondition.substring(2) + ")";
        } else {
            lsCondition = fsColumn + " = " + SQLUtil.toSQL(psTranStatus);
        }

        if (!fbByCode) {
            lsFilter = fsColumn + " LIKE " + SQLUtil.toSQL(fsValue);
        } else {
            lsFilter = fsColumn + " = " + SQLUtil.toSQL(fsValue);
        }

        String lsSQL = MiscUtil.addCondition(poModelMaster.makeSelectSQL(), " sTransNox LIKE "
                + SQLUtil.toSQL(fsValue + "%") + " AND " + lsCondition);

        poJSON = new JSONObject();

        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "Transaction No»Date»Refer No",
                "sTransNox»dTransact»sReferNox",
                "sTransNox»dTransact»sReferNox",
                fbByCode ? 0 : 1);

        if (poJSON != null) {
            return openTransaction((String) poJSON.get("sTransNox"));

        } else {
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
            return poJSON;
        }
    }

    @Override
    public JSONObject searchMaster(String string, String string1, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public JSONObject searchMaster(int i, String string, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Model_PO_Quotation_Request_Master getMasterModel() {
        return poModelMaster;
    }

    @Override
    public JSONObject setMaster(int fnCol, Object foData) {
        return poModelMaster.setValue(fnCol, foData);
    }

    @Override
    public JSONObject setMaster(String fsCol, Object foData) {
        return poModelMaster.setValue(fsCol, foData);
    }
    
    

    @Override
    public int getEditMode() {
        return pnEditMode;
    }
    
    

    

        
    public Category catest1() {
        return catest;
    }

    @Override
    public void setTransactionStatus(String fsValue) {
        psTranStatus = fsValue;
    }

    public JSONObject OpenModelDetail(String fsTransNo) {

        try {
            String lsSQL = MiscUtil.addCondition(poModelDetail.get(0).makeSQL(), "sTransNox = " + SQLUtil.toSQL(fsTransNo));
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            while (loRS.next()) {

                poModelDetail.add(new Model_PO_Quotation_Request_Detail(poGRider));
                poModelDetail.get(poModelDetail.size() - 1).openRecord(loRS.getString("sTransNox"), loRS.getString("sStockIDx"));
                if ("error".equals((String) poJSON.get("result"))) {
                    return poJSON;
                } else {
                    poJSON = new JSONObject();
                    poJSON.put("result", "error");
                    poJSON.put("message", "No record loaded to Detail.");

                }
            }

            return poJSON;

        } catch (SQLException ex) {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", ex.getMessage());

            return poJSON;
        }
    }

    public JSONObject AddModelDetail() {
        String lsModelRequired = poModelDetail.get(poModelDetail.size() - 1).getStockID();
        if (!lsModelRequired.isEmpty()) {
            poModelDetail.add(new Model_PO_Quotation_Request_Detail(poGRider));
            poModelDetail.get(poModelDetail.size() - 1).newRecord();
            poModelDetail.get(poModelDetail.size() - 1).setTransactionNo(poModelMaster.getTransactionNumber());

        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "Information");
            poJSON.put("message", "Please Fill up Required Record Fist!");

        }

        return poJSON;
    }

    public void RemoveModelDetail(int fnRow) {
        poModelDetail.remove(fnRow - 1);

    }

    
    public Object getDetailModel(int i) {
        return poModelDetail; // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    public Object getDetailModel1(int i) {
        return poModelDetail1; 
    }

    public JSONObject SearchMaster(int fnCol, String lsValue, boolean fbByCode){
        String lsHeader = "";
        String lsColName = "";
        String lsColCrit = "";
        String lsSQL = "";
        JSONObject loJSON;
        String fsValue = (lsValue) == null?"":lsValue;
//        if (fsValue.equals("") && fbByCode) return null;

        switch(fnCol){
            case 5: //Inventory-Barcode Search (Currently Testing)

                Inventory loInventory = new Inventory(poGRider, true);
                loInventory.setRecordStatus(psTranStatus);
                loJSON = loInventory.searchRecord(fsValue, fbByCode);

                if (loJSON != null){

//                    setMaster(1, (String) loInventory.getMaster(1));
//                    setMaster("xCategNm1", (String)loCategory.getMaster("sDescript"));
                    loJSON.put("sBarCodex", loInventory.setMaster("sBarCodex", (String) loInventory.getMaster("sBarCodex")));
                    loJSON.put("sDescript", loInventory.setMaster("sDescript", (String) loInventory.getMaster("sDescript")));
                    loJSON.put("xMeasurNm", loInventory.setMaster("xMeasurNm", (String) loInventory.getMaster("xMeasurNm")));
                    loJSON.put("xColorNme", loInventory.setMaster("xColorNme", (String) loInventory.getMaster("xColorNme")));
                    loJSON.put("xModelNme", loInventory.setMaster("xModelNme", (String) loInventory.getMaster("xModelNme")));
                    loJSON.put("sStockIDx", loInventory.setMaster("sStockIDx", (String) loInventory.getMaster("sStockIDx")));
                    loJSON.put("nUnitPrce", loInventory.setMaster("nUnitPrce", (BigDecimal) loInventory.getMaster("nUnitPrce")));
//                    return setMaster(1, (String)loInventory.getMaster(2));
                      return loJSON;
                    
                } else {
                    
                    loJSON = new JSONObject();
                    loJSON.put("result", "error");
                    loJSON.put("message", "No record found.");
                    return loJSON;
                }
            case 6: //sCategCd1

                Category loCategory = new Category(poGRider, true);
                loCategory.setRecordStatus(psTranStatus);
                loJSON = loCategory.searchRecord(fsValue, fbByCode);

                if (loJSON != null){

                    setMaster(fnCol, (String) loCategory.getMaster("sCategrCd"));
//                    setMaster("xCategNm1", (String)loCategory.getMaster("sDescript"));

                    return setMaster("sCategrCd", (String)loCategory.getMaster("sDescript"));
                    
                } else {
                    
                    loJSON = new JSONObject();
                    loJSON.put("result", "error");
                    loJSON.put("message", "No record found.");
                    return loJSON;
                }
                
                
            case 7: //sCategCd2

                lsSQL = "SELECT" +
                            "  a.sCategrCd" +
                            ", a.sDescript" +
                            ", a.sInvTypCd" +
                            ", a.sMainCatx" +
                            ", a.cClassify" +
                            ", a.cRecdStat" +
                            ", a.sModified" +
                            ", a.dModified" +
                            ", b.sDescript xInvTypNm" +
                            ", c.sDescript xMainCatx" +
                        " FROM Category_Level2 a" +
                            " LEFT JOIN Inv_Type b ON a.sInvTypCd = b.sInvTypCd" +
                            " LEFT JOIN Category c ON a.sMainCatx = c.sCategrCd";
                String lsCondition = "";

                if (psTranStatus.length() > 1) {
                    for (int lnCtr = 0; lnCtr <= psTranStatus.length() - 1; lnCtr++) {
                        lsCondition += ", " + SQLUtil.toSQL(Character.toString(psTranStatus.charAt(lnCtr)));
                    }

                    lsCondition = "a.cRecdStat IN (" + lsCondition.substring(2) + ")";
                } else {
                    lsCondition = "a.cRecdStat = " + SQLUtil.toSQL(psTranStatus);
                }

                if (fbByCode)
                    lsSQL = MiscUtil.addCondition(lsSQL, "a.sBarCodex = " + SQLUtil.toSQL(fsValue));
                else
                    lsSQL = MiscUtil.addCondition(lsSQL, "a.sDescript LIKE " + SQLUtil.toSQL(fsValue + "%"));


//                if(!poModelMaster.getCategCd1().isEmpty()){
//                    lsSQL = MiscUtil.addCondition(lsSQL, "a.sMainCatx = " + SQLUtil.toSQL(poModelMaster.getCategCd1()));
//                }

                lsSQL = MiscUtil.addCondition(lsSQL, lsCondition);
                System.out.println(lsSQL);
                loJSON = ShowDialogFX.Search(
                                poGRider,
                                lsSQL,
                                fsValue,
                                "Code»Name",
                                "sCategrCd»sDescript",
                                "a.sCategrCd»a.sDescript",
                                fbByCode ? 0 : 1);

                if (loJSON != null) {
                    setMaster(fnCol, (String) loJSON.get("sCategrCd"));
                    setMaster("sInvTypCd", (String) loJSON.get("sInvTypCd"));
                    setMaster("sInvTypCd", (String) loJSON.get("sInvTypCd"));
                    setMaster("xInvTypNm", (String) loJSON.get("xInvTypNm"));

                    setMaster(6, (String) loJSON.get("sMainCatx"));
//                    setMaster("xCategNm1", (String)loCategory.getMaster("sDescript"));
                    setMaster("xCategNm1", (String)loJSON.get("xMainCatx"));
                    setMaster("xMainCatx", (String)loJSON.get("sMainCatx"));
//                    System.out.println("sInvTypCd = " + setMaster("sInvTypCd", (String) loJSON.get("sCategrCd")));
                    return setMaster("xCategNm2", (String) loJSON.get("sDescript"));

                }else {
                    loJSON = new JSONObject();
                    loJSON.put("result", "error");
                    loJSON.put("message", "No record selected.");
                    return loJSON;
                }

//                Category_Level2 loCategory2 = new Category_Level2(poGRider, true);
//                loCategory2.setRecordStatus(psTranStatus);
//                loJSON = loCategory2.searchRecord(fsValue, fbByCode);
//
//                if (loJSON != null){
//                    setMaster(fnCol, (String) loCategory2.getMaster("sCategrCd"));
//                    setMaster("sInvTypCd", (String) loCategory2.getMaster("sCategrCd"));
//
//                    setMaster(6, (String) loCategory2.getMaster("sMainCatx"));
////                    setMaster("xCategNm1", (String)loCategory.getMaster("sDescript"));
//                    setMaster("xCategNm1", (String)loCategory2.getMaster("xMainCatx"));
//                    System.out.println("sInvTypCd = " + setMaster("sInvTypCd", (String) loCategory2.getMaster("sCategrCd")));
//                    return setMaster("xCategNm2", (String) loCategory2.getMaster("sDescript"));
//                } else {
//                    loJSON.put("result", "error");
//                    loJSON.put("message", "No record found.");
//                    return loJSON;
//                }
            case 8: //sCategCd3
                Category_Level3 loCategory3 = new Category_Level3(poGRider, true);
                loCategory3.setRecordStatus(psTranStatus);
                loJSON = loCategory3.searchRecord(fsValue, fbByCode);

                if (loJSON != null){
                    setMaster(fnCol, (String) loCategory3.getMaster("sCategrCd"));
                    return setMaster("xCategNm3", (String) loCategory3.getMaster("sDescript"));
                } else {
                    loJSON = new JSONObject();
                    loJSON.put("result", "error");
                    loJSON.put("message", "No record found.");
                    return loJSON;
                }
            case 9: //sCategCd4
                Category_Level4 loCategory4 = new Category_Level4(poGRider, true);
                loCategory4.setRecordStatus(psTranStatus);
                loJSON = loCategory4.searchRecord(fsValue, fbByCode);

                if (loJSON != null){
                    setMaster("sCategCd4", (String) loCategory4.getMaster("sCategrCd"));
                    return setMaster("xCategNm4", (String) loCategory4.getMaster("sDescript"));
                } else {
                    loJSON = new JSONObject();
                    loJSON.put("result", "error");
                    loJSON.put("message", "No record found.");
                    return loJSON;
                }
            case 11: //sModelCde
                Model loModel = new Model(poGRider, false);
                loModel.setRecordStatus(psTranStatus);
                loJSON = loModel.searchRecord(fsValue, fbByCode);

                if (loJSON != null){
                    setMaster(fnCol, (String) loModel.getMaster("sModelCde"));
                    return setMaster("xModelNme", (String) loModel.getMaster("sModelNme"));
                } else {
                    loJSON = new JSONObject();
                    loJSON.put("result", "error");
                    loJSON.put("message", "No record found.");
                    return loJSON;
                }
            case 12: //sColorCde
                Color loColor = new Color(poGRider, false);
                loColor.setRecordStatus(psTranStatus);
                loJSON = loColor.searchRecord(fsValue, fbByCode);

                if (loJSON != null){
                    setMaster(fnCol, (String) loColor.getMaster("sColorCde"));
                    return setMaster("xColorNme", (String) loColor.getMaster("sDescript"));
                } else {
                    loJSON = new JSONObject();
                    loJSON.put("result", "error");
                    loJSON.put("message", "No record found.");
                    return loJSON;
                }
//            case 13: //sInvTypCd
//                Inv_Type loInvType = new Inv_Type(poGRider, false);
//
//                loJSON = loInvType.searchRecord(fsValue, fbByCode);
//
//                if (loJSON != null){
//                    setMaster(fnCol, (String) loJSON.get("sInvTypCd"));
//                    return (String) loJSON.get("sDescript");
//                } else {
//                    setMaster(fnCol, "");
//                    return "";
//                }
            case 13: //sMeasurID
                Measure loMeasure = new Measure(poGRider, false);
                loMeasure.setRecordStatus(psTranStatus);
                loJSON = loMeasure.searchRecord(fsValue, fbByCode);

                if (loJSON != null){
                    setMaster(fnCol, (String) loMeasure.getMaster("sMeasurID"));
                    return setMaster("xMeasurNm", (String) loMeasure.getMaster("sMeasurNm"));
                }  else {
                    loJSON = new JSONObject();
                    loJSON.put("result", "error");
                    loJSON.put("message", "No record found.");
                    return loJSON;
                }
            default:
                return null;
        }
    }
    
    public JSONObject SearchMaster(String fsCol, String fsValue, boolean fbByCode){
        return SearchMaster(poModelMaster.getColumn(fsCol), fsValue, fbByCode);
    }
    

}


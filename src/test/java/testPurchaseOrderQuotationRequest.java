
import java.math.BigDecimal;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.TransactionStatus;
import org.guanzon.cas.model.inventory.PurchaseOrderQuotationRequest;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author Maynard
 */


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class testPurchaseOrderQuotationRequest {
 
    static GRider instance;
    static PurchaseOrderQuotationRequest record;

    @BeforeClass
    public static void setUpClass() {
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/");

        instance = MiscUtil.Connect();
        record = new PurchaseOrderQuotationRequest(instance, false);
    }

    @Test
    public void testProgramFlow() {
        JSONObject loJSON;

        loJSON = record.newTransaction();
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }    
        
        
        //set master information
        loJSON = record.getMasterModel().setBranchCode("M00124000001");
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        loJSON = record.getMasterModel().setTransaction(SQLUtil.toDate("2024-04-25",SQLUtil.FORMAT_SHORT_DATE));
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        loJSON = record.getMasterModel().setDestination("M00124000001");
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        loJSON = record.getMasterModel().setReferenceNumber("M00124000001");
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        loJSON = record.getMasterModel().setRemarks("This is the remarks information");
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        loJSON = record.getMasterModel().setExpiredPurchase(SQLUtil.toDate("2024-04-25",SQLUtil.FORMAT_SHORT_DATE));
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        loJSON = record.getMasterModel().setEntryNumber(180000);
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        loJSON = record.getMasterModel().setCategoryCode("M001");
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        loJSON = record.getMasterModel().setTransactionStatus("1");
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        loJSON = record.getMasterModel().setPrepared("M00124000001");
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        loJSON = record.getMasterModel().setPreparedDate(SQLUtil.toDate("2024-04-25",SQLUtil.FORMAT_SHORT_DATE));
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }

    
      
        loJSON = record.getMasterModel().setModified("M00124000001");
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
     
        loJSON = record.getMasterModel().setModifiedDate(SQLUtil.toDate("2024-04-25",SQLUtil.FORMAT_SHORT_DATE));
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        
        
        
        
        //set detail information
        
        loJSON = record.getDetailModel().get(record.getDetailModel().size()-1).setStockID("M00124000001");
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        loJSON = record.getDetailModel().get(record.getDetailModel().size()-1).setDescription("This is Detail 02");
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        loJSON = record.getDetailModel().get(record.getDetailModel().size()-1).setQuantity(123456);
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        loJSON = record.getDetailModel().get(record.getDetailModel().size()-1).setUnitPrice(BigDecimal.valueOf(1999.99));
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        loJSON = record.getDetailModel().get(record.getDetailModel().size()-1).setModified(SQLUtil.toDate("2024-04-25",SQLUtil.FORMAT_SHORT_DATE));
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
       
        
        
        
        
        
        
        //set detail 2 information
        record.AddModelDetail();
        
        
        loJSON = record.getDetailModel().get(record.getDetailModel().size()-1).setStockID("M00124000001");
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        loJSON = record.getDetailModel().get(record.getDetailModel().size()-1).setDescription("This is Detail 02");
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        loJSON = record.getDetailModel().get(record.getDetailModel().size()-1).setQuantity(123456);
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        loJSON = record.getDetailModel().get(record.getDetailModel().size()-1).setUnitPrice(BigDecimal.valueOf(1999.99));
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        loJSON = record.getDetailModel().get(record.getDetailModel().size()-1).setModified(SQLUtil.toDate("2024-04-25",SQLUtil.FORMAT_SHORT_DATE));
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
        
        
        
        loJSON = record.saveTransaction();
        if ("error".equals((String) loJSON.get("result"))) {
            Assert.fail((String) loJSON.get("message"));
        }
    }

    @AfterClass
    public static void tearDownClass() {
        record = null;
        instance = null;
    }
}

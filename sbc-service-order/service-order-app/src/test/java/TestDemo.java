//import com.wanmi.sbc.order.OrderServiceApplication;
//import com.wanmi.sbc.order.bean.dto.PurchaseQueryDTO;
//import com.wanmi.sbc.order.purchase.PurchaseService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = OrderServiceApplication.class)
//public class TestDemo {
//
//    @Resource
//    private PurchaseService purchaseService;
//
//    @Test
//    public void TestDemo() {
//        Map<String,Long> data =  purchaseService.getSkuPurchaseNum(new PurchaseQueryDTO(){{
//            List<String> ids = new ArrayList<>();
//            ids.add("2c9fb80f7439bb20017448f208a40056");
//            ids.add("2c9fb80f7439bb200174705448270063");
//            setGoodsInfoIds(ids);
//            setCustomerId("2c9fb80f7439b9dc017470e247850017");
//        }});
//        System.out.println(data);
//    }
//
//}
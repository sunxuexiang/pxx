
import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.marketing.api.request.coupon.CouponInfoGetRecordRequest;
import com.wanmi.sbc.order.bean.dto.PurchaseQueryDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = OrderServiceApplication.class)
public class TestDemo {


    @Test
    public void TestDemo() {
    }

    @Test
    public void decodeExportExcelParam() {
        String sc = "eyJjdXN0b21lckFjY291bnQiOiIxNTExMTIzNzUwNyIsImNvdXBvbk5hbWUiOiLlgJLorqHml7bov5DokKXliLgiLCJ0b2tlbiI6ImV5SmhiR2NpT2lKSVV6STFOaUo5LmV5SmxiblpEYjJSbElqb2lkR1Z6ZERFaUxDSmpiMjF3WVc1NVZIbHdaU0k2TUN3aWMzbHpkR1Z0UTI5a1pTSTZJbVF5WTFOMGIzSmxJaXdpYVhBaU9pSXhNVEV1TWpNdU1UTTVMakl6TnlJc0luSnZiR1ZPWVcxbElqb2lJaXdpWlcxd2JHOTVaV1ZKWkNJNklqSmpPREE0TURneE5XTmtNMkUzTkdFd01UVmpaRE5oWlRnMk9EVXdNREF4SWl3aWRtRnNkV1ZmWVdSa1pXUmZjMlZ5ZG1salpYTWlPaUo3WENKMllYTmZhV1Z3WDNObGRIUnBibWRjSWpwY0ltUnBjMkZpYkdWY0luMGlMQ0psZUhBaU9qRTJNelkzTVRBeE5Ea3NJa1Z0Y0d4dmVXVmxUbUZ0WlNJNkluTjVjM1JsYlNJc0luVnpaWEpKWkNJNklqQXdNREF3TUNJc0luQnNZWFJtYjNKdElqb2lVRXhCVkVaUFVrMGlMQ0p5WldGc1JXMXdiRzk1WldWT1lXMWxJam9pYzNsemRHVnRJbjAucEpLR0xMakM5N2pRMm81YXZwb0luQjJHSUxNMjM0aHJkelo0dUdQeG1VRSIsImNvdXBvbklkIjoiMmM5ZmI4MGY3Y2JmMzFkNDAxN2NlM2E0ZjcyZjAwMmMiLCJwYWdlTnVtIjowLCJwYWdlU2l6ZSI6MTAwMH0=";
        String decrypted = new String(Base64.getUrlDecoder().decode(sc.getBytes()));
        CouponInfoGetRecordRequest request = JSON.parseObject(decrypted, CouponInfoGetRecordRequest.class);
        System.out.println(request);
    }

}
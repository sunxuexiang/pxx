package com.wanmi.sbc.marketing.util.marketing;

import com.wanmi.sbc.marketing.common.model.root.Marketing;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class MarketingPurchaseNumUtil {

    static final int MINUTES_PER_HOUR = 60;
    static final int SECONDS_PER_MINUTE = 60;
    static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;

    /**
     * 获取限购的商品信息
     * @param marketing
     * @return
     */
    public static List<PurchaseNumDTO> getPurchaseNum(Marketing marketing){
        List<PurchaseNumDTO> purchaseNumDTOS = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(marketing.getMarketingScopeList())){
            marketing.getMarketingScopeList().forEach(marketingScope -> {
                if(Objects.nonNull(marketingScope.getPurchaseNum())){
                    PurchaseNumDTO dto = new PurchaseNumDTO();
                    dto.setMarketingId(marketingScope.getMarketingId());
                    dto.setGoodsInfoId(marketingScope.getScopeId());
                    dto.setPurchaseNum(marketingScope.getPurchaseNum());
                    purchaseNumDTOS.add(dto);
                }
            });
        }
        return purchaseNumDTOS;
    }


    public static Period getPeriod(LocalDateTime dob, LocalDateTime now) {
        return Period.between(dob.toLocalDate(), now.toLocalDate());
    }

    public static long[] getTime(LocalDateTime dob, LocalDateTime now) {
        LocalDateTime today = LocalDateTime.of(now.getYear(),
                now.getMonthValue(), now.getDayOfMonth(), dob.getHour(), dob.getMinute(), dob.getSecond());
        Duration duration = Duration.between(today, now);

        long seconds = duration.getSeconds();

        long hours = seconds / SECONDS_PER_HOUR;
        long minutes = ((seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE);
        long secs = (seconds % SECONDS_PER_MINUTE);

        return new long[]{hours, minutes, secs};
    }

    public static void main(String[] args) {
        LocalDateTime toDateTime = LocalDateTime.of(2014, 9, 9, 19, 46, 45);
        LocalDateTime fromDateTime = LocalDateTime.of(1984, 12, 16, 7, 45, 55);

        Period period = getPeriod(fromDateTime, toDateTime);
        long time[] = getTime(fromDateTime, toDateTime);

        System.out.println(period.getYears() + " years " +
                period.getMonths() + " months " +
                period.getDays() + " days " +
                time[0] + " hours " +
                time[1] + " minutes " +
                time[2] + " seconds.");


    }
}

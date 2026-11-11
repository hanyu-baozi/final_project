package edu.crm.domain.customer;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerCenterGateway {
    record Holding(String productName, BigDecimal value) { }
    record CustomerData(String idNumber, String name, String accountStatus, String memberLevel,
                        BigDecimal availableBalance, List<Holding> holdings) { }
    CustomerData findByIdNumber(String idNumber);
}

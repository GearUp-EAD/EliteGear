package team.elite_gear;

import team.elite_gear.rest.ProductResourceTest;
import team.elite_gear.rest.PaymentResourceTest;
import team.elite_gear.rest.OrderResourceTest;
import team.elite_gear.rest.OrderItemResourceTest;
import team.elite_gear.rest.CategoryResourceTest;
import team.elite_gear.rest.CustomerResourceTest;
import team.elite_gear.rest.ShippingResourceTest;
import team.elite_gear.service.CategoryServiceTest;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@Suite
@SelectClasses({
        // Controller Tests
        PaymentResourceTest.class,
        ProductResourceTest.class,
        CategoryResourceTest.class,
        CustomerResourceTest.class,
        OrderItemResourceTest.class,
        OrderResourceTest.class,
        ShippingResourceTest.class,

        // Service Tests
        CategoryServiceTest.class,
        CustomerResourceTest.class,
        OrderItemResourceTest.class,
        OrderResourceTest.class,
        PaymentResourceTest.class,
        ProductResourceTest.class,        // Controller Tests




})
@SpringBootTest
public class EliteGearApplicationTests {
}
package org.aadi.bookstore.orders.web.controllers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.List;
import org.aadi.bookstore.orders.AbstractIT;
import org.aadi.bookstore.orders.domain.models.OrderSummary;
import org.aadi.bookstore.orders.testdata.TestDataFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@Sql("/test-orders.sql")
class OrderControllerTests extends AbstractIT {

    @Nested
    class CreateOrderTests {
        @Test
        void shouldCreateOrderSuccessfully() {
            mockGetProductByCode("P100", "The Hunger Games", new BigDecimal("34.0"));
            var payload =
                    """
                        {
                            "customer" : {
                                "name": "Aadi",
                                "email": "aadi@mail.com",
                                "phone": "9999999999"
                            },
                            "deliveryAddress" : {
                                "addressLine1": "Gachibowli",
                                "addressLine2": "Grove Villas Township",
                                "city": "Hyderabad",
                                "state": "Telangana",
                                "zipCode": "500032",
                                "country": "India"
                            },
                            "items": [
                                {
                                    "code": "P100",
                                    "name": "The Hunger Games",
                                    "price": 34.0,
                                    "quantity": 2
                                }
                            ]
                        }
                    """;
            given().contentType(ContentType.JSON)
                    .body(payload)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("orderNumber", notNullValue());
        }

        @Test
        void shouldReturnBadRequestWhenMandatoryDataIsMissing() {
            var payload = TestDataFactory.createOrderRequestWithInvalidCustomer();
            given().contentType(ContentType.JSON)
                    .body(payload)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class GetOrdersTests {
        @Test
        void shouldGetOrdersSuccessfully() {
            List<OrderSummary> orderSummaries = given().when()
                    .get("/api/orders")
                    .then()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(new TypeRef<>() {});

            assertThat(orderSummaries).hasSize(2);
        }
    }

    @Nested
    class GetOrderByOrderNumberTests {
        String orderNumber = "order-123";

        @Test
        void shouldGetOrderSuccessfully() {
            given().when()
                    .get("/api/orders/{orderNumber}", orderNumber)
                    .then()
                    .statusCode(200)
                    .body("orderNumber", is(orderNumber))
                    .body("items.size()", is(2));
        }
    }
}

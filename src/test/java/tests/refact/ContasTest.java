package tests.refact;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.BeforeClass;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ContasTest {

    @BeforeClass
    public static void login(){
        Map<String, String> login = new HashMap<>();
        login.put("email", "ricardoveiga.ti@gmail.com");
        login.put("senha", "123456");

        Response response  =
                given()
                    .body(login)
                .when()
                    .post("/signin");
        response
                .then()
                    .statusCode(200)
                    .body("token", is(notNullValue()))

        ;
        String TOKEN = response.jsonPath().getString("token");
        System.out.print("TOKEN => " + TOKEN); //apenas didático

        RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
        RestAssured.get("/rese").then().statusCode(200);
    }
}

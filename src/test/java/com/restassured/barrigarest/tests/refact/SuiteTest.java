package com.restassured.barrigarest.tests.refact;

import com.restassured.barrigarest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Suite.class)
@Suite.SuiteClasses({ // respeita a ordem que for definida neste ponto
        ContasTest.class,
        MovimentacaoTest.class,
        SaldoTest.class,
        AuthTest.class//tem que ser por último porque tira o token.json dos testes

})
public class SuiteTest extends BaseTest {
    @BeforeClass
    public static void login(){
        System.out.println("Before de Conta");
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
                .statusCode(200).log().all()
                //.body("token.json", is(notNullValue()))
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("token.json"))

        ;
        String TOKEN = response.jsonPath().getString("token");
        //System.out.print("TOKEN => " + TOKEN); //apenas didático

        RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
        RestAssured.get("/reset").then().statusCode(200);
    }
}

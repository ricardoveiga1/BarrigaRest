package com.restassured.barrigarest.tests.refact;

import com.restassured.barrigarest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class AuthTest extends BaseTest {

    @Test
    public void naoDeveAcessarApiSemToken(){  // tive que mudar para 11, pois o teste remove o TOKEN

        FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
        req.removeHeader("Authorization"); //removendo header para não pegar o header statico do metodo login

        given()
                .when()
                .get("/contas")
                .then()
                .statusCode(401)
        ;
    }
}
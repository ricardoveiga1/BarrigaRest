package com.restassured.barrigarest.core;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;

public class BaseTest implements Constantes {

    @BeforeClass
    public static void setup() {
        System.out.println("Before Base");
        //System.out.print("Passou aqui BaseTest");
        RestAssured.baseURI = Constantes.APP_BASE_URL;
        RestAssured.port = Constantes.APP_PORT;
        RestAssured.basePath = Constantes.APP_BASE_PATH;

        //para trabalhar semprecom contentType e json, sem precisar informar em toda requisição
        RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
        reqBuilder.setContentType(Constantes.APP_CONTENT_TYPE);
        RestAssured.requestSpecification = reqBuilder.build();

        ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
        resBuilder.expectResponseTime(Matchers.lessThan(Constantes.MAX_TIMEOUT));
        RestAssured.responseSpecification = resBuilder.build();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();//logar apenas os erros
    }


}

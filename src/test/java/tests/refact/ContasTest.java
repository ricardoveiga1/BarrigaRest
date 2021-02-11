package tests.refact;

import core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ContasTest extends BaseTest {

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
                    .statusCode(200)
                    .body("token", is(notNullValue()))

        ;
        String TOKEN = response.jsonPath().getString("token");
        System.out.print("TOKEN => " + TOKEN); //apenas didático

        RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
        RestAssured.get("/reset").then().statusCode(200);
    }

    @Test
    public void deveIncluirContaComSucesso() {
        System.out.println("Incluir");
        given()
                .body("{ \"nome\": \"Conta inserida\" }")
                .when()
                .post("/contas")
                .then()
                .statusCode(201)
        ;
    }

    @Test
    public void deveAlterarContaComSucesso(){
        System.out.println("Alterar");
        Integer CONTA_ID = getIdContaPeloNome("Conta para alterar"); //usando método da query para selecionar a Conta para alterar

        given()
                .body("{ \"nome\": \"Conta alterada\" }")
                .pathParam("id", CONTA_ID)
                .when()
                .put("/contas/{id}")
                .then()
                .statusCode(200)
                .body("nome", is("Conta alterada"))
        ;
    }

    public Integer getIdContaPeloNome(String nome){
        return RestAssured.get("/contas?nome=" +nome).then().extract().path("id[0]"); //usando restassured para fazer uma query em contas e pegar o primeiro id da lista dentro do array[0]
    }

    @Test
    public void t04_naoDeveInserirContaComMesmoNome(){

        given()
                //.header("Authorization", "JWT " + token)
                .body("{ \"nome\": \"Conta mesmo nome\" }")
                .when()
                .post("/contas")
                .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }



}

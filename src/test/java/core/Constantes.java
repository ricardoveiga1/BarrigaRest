package core;

import io.restassured.http.ContentType;

public interface Constantes {

    String APP_BASE_URL = "https://barrigarest.wcaquino.me";
    Integer APP_PORT = 443;  // http -> 80
    String APP_BASE_PATH = ""; // pro caso de precisar alterra o path v1/cadastro/

    ContentType APP_CONTENT_TYPE = ContentType.JSON;

    Long MAX_TIMEOUT = 5000L;

}

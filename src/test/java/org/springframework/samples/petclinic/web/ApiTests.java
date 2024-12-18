package org.springframework.samples.petclinic.web;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiTests {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void testHealthCheckEndpoint() {
        given()
            .when()
            .get("/health")
            .then()
            .statusCode(200)
            .body("database", equalTo("UP"))
            .body("externalAPIs", equalTo("UP"))
            .body("otherComponents", equalTo("UP"));
    }

    @Test
    public void testGetOwnerById() {
        Response response = given()
            .when()
            .get("/owners/1")
            .then()
            .statusCode(200)
            .extract()
            .response();

        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");

        assert firstName.equals("George");
        assert lastName.equals("Franklin");
    }

    @Test
    public void testGetPetById() {
        Response response = given()
            .when()
            .get("/owners/1/pets/1")
            .then()
            .statusCode(200)
            .extract()
            .response();

        String petName = response.jsonPath().getString("name");

        assert petName.equals("Leo");
    }

    @Test
    public void testGetVets() {
        given()
            .when()
            .get("/vets")
            .then()
            .statusCode(200)
            .body("[0].firstName", equalTo("James"))
            .body("[0].lastName", equalTo("Carter"))
            .body("[1].firstName", equalTo("Helen"))
            .body("[1].lastName", equalTo("Leary"));
    }

    @Test
    public void testGetVisitsByPetId() {
        given()
            .when()
            .get("/owners/1/pets/1/visits")
            .then()
            .statusCode(200)
            .body("visits[0].description", equalTo("rabies shot"))
            .body("visits[1].description", equalTo("neutered"));
    }
}

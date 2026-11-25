package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import org.json.JSONObject;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

public class MovieControllerRA {

	private static final String ADMIN_USERNAME = "maria@gmail.com";
	private static final String CLIENT_USERNAME = "alex@gmail.com";
	private static final String PASSWORD = "123456";

	private static String adminToken;
	private static String clientToken;

	@BeforeAll
	public static void setUp() throws JSONException {
		baseURI = "http://localhost:8080";
		adminToken = TokenUtil.obtainAccessToken(ADMIN_USERNAME, PASSWORD);
		clientToken = TokenUtil.obtainAccessToken(CLIENT_USERNAME, PASSWORD);
	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		given()
			.when()
				.get("/movies")
			.then()
				.statusCode(200)
				.body("content", notNullValue())
				.body("content.size()", greaterThan(0))
				.body("content[0].title", not(emptyOrNullString()));
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {		
		given()
			.queryParam("title", "The Witcher")
			.when()
				.get("/movies")
			.then()
				.statusCode(200)
				.body("content", notNullValue())
				.body("content.size()", equalTo(1))
				.body("content[0].title", equalTo("The Witcher"));
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {		
		given()
			.when()
				.get("/movies/1")
			.then()
				.statusCode(200)
				.body("id", equalTo(1))
				.body("title", equalTo("The Witcher"));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {	
		given()
			.when()
				.get("/movies/999999")
			.then()
				.statusCode(404)
				.body("status", equalTo(404))
				.body("error", equalTo("Recurso não encontrado"));
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {
		JSONObject body = new JSONObject();
		body.put("title", "");
		body.put("score", 0.0);
		body.put("count", 0);
		body.put("image", "https://example.com/movie.jpg");

		given()
			.header("Authorization", "Bearer " + adminToken)
			.contentType("application/json")
			.body(body.toString())
			.when()
				.post("/movies")
			.then()
				.statusCode(422)
				.body("status", equalTo(422))
				.body("errors.fieldName", hasItem("title"))
				.body("errors.message", hasItem(not(emptyOrNullString())));
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		JSONObject body = new JSONObject();
		body.put("title", "Test Movie");
		body.put("score", 0.0);
		body.put("count", 0);
		body.put("image", "https://example.com/movie.jpg");

		given()
			.header("Authorization", "Bearer " + clientToken)
			.contentType("application/json")
			.body(body.toString())
			.when()
				.post("/movies")
			.then()
				.statusCode(403);
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		JSONObject body = new JSONObject();
		body.put("title", "Test Movie");
		body.put("score", 0.0);
		body.put("count", 0);
		body.put("image", "https://example.com/movie.jpg");

		given()
			.header("Authorization", "Bearer invalid-token")
			.contentType("application/json")
			.body(body.toString())
			.when()
				.post("/movies")
			.then()
				.statusCode(401);
	}
}

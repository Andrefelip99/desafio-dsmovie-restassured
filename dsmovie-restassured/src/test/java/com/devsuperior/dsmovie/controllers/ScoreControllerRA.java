package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import org.json.JSONObject;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

public class ScoreControllerRA {

	private static final String CLIENT_USERNAME = "alex@gmail.com";
	private static final String PASSWORD = "123456";

	private static String clientToken;

	@BeforeAll
	public static void setUp() throws JSONException {
		baseURI = "http://localhost:8080";
		clientToken = TokenUtil.obtainAccessToken(CLIENT_USERNAME, PASSWORD);
	}
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {		
		JSONObject body = new JSONObject();
		body.put("movieId", 999999);
		body.put("score", 4.0);

		given()
			.header("Authorization", "Bearer " + clientToken)
			.contentType("application/json")
			.body(body.toString())
			.when()
				.put("/scores")
			.then()
				.statusCode(404)
				.body("status", equalTo(404))
				.body("error", equalTo("Recurso não encontrado"));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		JSONObject body = new JSONObject();
		body.put("score", 4.0);

		given()
			.header("Authorization", "Bearer " + clientToken)
			.contentType("application/json")
			.body(body.toString())
			.when()
				.put("/scores")
			.then()
				.statusCode(422)
				.body("status", equalTo(422))
				.body("errors.fieldName", hasItem("movieId"))
				.body("errors.message", hasItem("Campo requerido"));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {		
		JSONObject body = new JSONObject();
		body.put("movieId", 1);
		body.put("score", -1.0);

		given()
			.header("Authorization", "Bearer " + clientToken)
			.contentType("application/json")
			.body(body.toString())
			.when()
				.put("/scores")
			.then()
				.statusCode(422)
				.body("status", equalTo(422))
				.body("errors.fieldName", hasItem("score"))
				.body("errors.message", hasItem("Valor mínimo 0"));
	}
}

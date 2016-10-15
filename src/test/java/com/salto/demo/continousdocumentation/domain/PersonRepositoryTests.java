package com.salto.demo.continousdocumentation.domain;

import static com.jcabi.matchers.RegexMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonRepositoryTests {

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
				.apply(documentationConfiguration(this.restDocumentation)).alwaysDo(print()).build();
	}

	@Test
	public void listPersons() throws Exception {
		this.mockMvc.perform(get("/people").accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$._embedded.people", hasSize(1)))
				.andExpect(jsonPath("$._embedded.people[0].lastName", equalTo("Vandekerckhove")))
				.andExpect(jsonPath("$._embedded.people[0].firstName", equalTo("Quentin")))
				.andDo(document("index"));
	}
	
	@Test
	public void getPerson() throws Exception {
		this.mockMvc.perform(get("/people/{id}", 1).accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.lastName", equalTo("Vandekerckhove")))
				.andExpect(jsonPath("$.firstName", equalTo("Quentin")))
				.andDo(document("get"));
	}

	@Test
	public void createPerson() throws Exception {
		this.mockMvc
				.perform(post("/people").accept(MediaType.APPLICATION_JSON_UTF8)
						.content("{ \"firstName\": \"Lola\", \"lastName\": \"Vandekerckhove\"}"))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(header().string("Location", matchesPattern(".*\\/people\\/\\d")))
				.andExpect(jsonPath("$.lastName", equalTo("Vandekerckhove")))
				.andExpect(jsonPath("$.firstName", equalTo("Lola")))
				.andDo(document("create"));
	}
	
	@Test
	@DirtiesContext
	public void deletePerson() throws Exception {
		this.mockMvc
				.perform(delete("/people/{id}", 1))
				.andExpect(status().isNoContent())
				.andDo(document("delete"));
	}
}

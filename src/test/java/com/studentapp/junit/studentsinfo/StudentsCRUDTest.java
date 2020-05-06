package com.studentapp.junit.studentsinfo;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import com.studentapp.model.StudentClass;
import com.studentapp.testbase.TestBase;
import com.studentapp.utils.TestUtils;

import io.restassured.http.ContentType;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.Title;

@RunWith(SerenityRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StudentsCRUDTest extends TestBase {
	
	static String firstName = "SMOKEUSER"+TestUtils.getRandomValue();
	static String lastName = "SMOKEUSER"+TestUtils.getRandomValue();
	static String programme = "Computer Science";
	static String email = TestUtils.getRandomValue()+"xyz@gmail.com";
	static int studentId;
	
	@Title("This Test will create a New Student")
	@Test
	public void test001() {
		ArrayList<String> courses = new ArrayList<String>();
		courses.add("JAVA");
		courses.add("C++");
		
		StudentClass student = new StudentClass();
		student.setFirstName(firstName);
		student.setLastName(lastName);
		student.setEmail(email);
		student.setProgramme(programme);
		student.setCourses(courses);
		
		SerenityRest.rest().given()
		.contentType(ContentType.JSON)
		.log()
		.all()
		.when()
		.body(student)
		.post()
		.then()
		.log()
		.all()
		.statusCode(201);
	}
	
	@Title("veirfy if the student is addded to the application successfully")
	@Test
	public void test002() {
		
		String p1 = "findAll{it.firstName=='";
		String p2 = "'}.get(0)";	
		
		HashMap<String,Object> value = SerenityRest.rest().given()
		.when()
		.get("/list")
		.then()
		.log()
		.all()
		.statusCode(200)
		.extract()
		.path(p1+firstName+p2);
		
		System.out.println("The value is:"+value);
		assertThat(value,hasValue(firstName));
		studentId = (int) value.get("id");
	}
	
	@Title("Update the user information and verify the updated information")
	@Test
	public void test003() {
		ArrayList<String> courses = new ArrayList<String>();
		courses.add("JAVA");
		courses.add("C++");
		
		firstName = firstName+"_updated";
		
		StudentClass student = new StudentClass();
		student.setFirstName(firstName);
		student.setLastName(lastName);
		student.setEmail(email);
		student.setProgramme(programme);
		student.setCourses(courses);
		
		SerenityRest.rest().given()
		.contentType(ContentType.JSON)
		.log()
		.all()
		.when()
		.body(student)
		.put("/"+studentId)
		.then()
		.log()
		.all();
		
		String p1 = "findAll{it.firstName=='";
		String p2 = "'}.get(0)";	
		
		HashMap<String,Object> value = SerenityRest.rest().given()
		.when()
		.get("/list")
		.then()
		.log()
		.all()
		.statusCode(200)
		.extract()
		.path(p1+firstName+p2);
		
		System.out.println("The value is:"+value);
		assertThat(value,hasValue(firstName));
	}
	
	@Title("Delete the student and verify if the student is deleted")
	@Test
	public void test004() {
		SerenityRest.rest().given()
		.when()
		.delete("/"+studentId);
		
		SerenityRest.rest().given()
		.when()
		.get("/"+studentId)
		.then()
		.log()
		.all()
		.statusCode(404);
	}
}

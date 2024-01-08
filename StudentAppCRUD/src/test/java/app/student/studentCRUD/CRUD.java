package app.student.studentCRUD;

import app.student.model.StudentPojo;
import app.student.path.EndPoints;
import app.student.utils.TestUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CRUD {

    RequestSpecification requestSpecification;
    Response response;
    ValidatableResponse validatableResponse;

    int studentId;
    static String firstName = "Codebuster" + TestUtils.getRandomValue();
    static String lastName = "Training" + TestUtils.getRandomValue();
    static String email = TestUtils.getRandomValue() + "codebuster@gmail.com";
    static String programme = "Study";

    @Test(priority = 1)
    public void getAllStudentsInfo() {

        response = RestAssured.given().log().all()
                .when()
                .get("http://localhost:8080/student/list");
        response.then().log().all()
                .statusCode(200);

    }

    @Test(priority = 2)
    public void createStudentInfo() {

        List<String> courses = new ArrayList<>();
        courses.add("Automation");
        courses.add("Manual");

        StudentPojo studentPojo = new StudentPojo();
        studentPojo.setFirstName(firstName);
        studentPojo.setLastName(lastName);
        studentPojo.setEmail(email);
        studentPojo.setProgramme(programme);
        studentPojo.setCourses(courses);

        response = given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .body(studentPojo)
                .post("http://localhost:8080/student");
        response.then().log().all().statusCode(201)
                .body("msg", equalTo("Student added"));

    }

    @Test(priority = 3)
    public void getAllStudentAndExtractIdOfPostData() {

        HashMap<String, Object> studentDetails = given().log().all()
                .when()
                .get(EndPoints.GET_ALL_STUDENTS)
                .then()
                .extract()
                .path("findAll{it.firstName =='" + firstName + "'}.get(0)");
        System.out.println(studentDetails);
        studentId = (int) studentDetails.get("id");
        System.out.println(studentId);
    }


    @Test(priority = 4)
    public void getSingleStudentInfo() {

        response = given().log().all()
                .pathParams("id", studentId)
                .get("http://localhost:8080/student/{id}");
        response.then().log().all()
                .statusCode(200);
//                .body("firstname", equalTo(firstName))
//                .body("lastname", equalTo(lastName));

    }

    @Test(priority = 5)
    public void verifyNewStudentInfoDeletedByID() {

        response = given().log().all()
                .pathParam("id", studentId)
                .when()
                .delete("http://localhost:8080/student/{id}");
        response.then().log().all()
                .statusCode(204);
    }

    @Test(priority = 6)
    public void verifyStudentInfoDeleted() {

        response = given().log().all()
                .pathParam("id", studentId)
                .when()
                .get("http://localhost:8080/student/{id}");
        response.then().statusCode(404);
    }

}




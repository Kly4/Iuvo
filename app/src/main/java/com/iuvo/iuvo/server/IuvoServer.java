package com.iuvo.iuvo.server;

import com.iuvo.iuvo.schemas.Course;
import com.iuvo.iuvo.schemas.Event;

import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Instantiated in AsyncServer
 */
public interface IuvoServer {
    /**
     * @return A list of all schools in the database
     */
    @GET("/schools")
    String[] getSchools();

    /**
     * @param school
     * @return A list of course info (namely a the subject and course code)
     */
    @GET("/courses/{school}")
    CourseInfo[] getCourseList(@Path("school") String school);

    /**
     * @param school
     * @param subject
     * @param code
     * @return Study sessions pertaining to the course
     */
    @GET("/events/{school}/{subject}/{code}")
    Event[] getEventList(@Path("school") String school,
                         @Path("subject") String subject,
                         @Path("code") String code);

    /**
     * This URL is for getting full course details (Instructor, Course title, etc.) from
     * its basic information.
     *
     * @param school
     * @param subject
     * @param code
     * @return Full Course object in JSON
     */
    @GET("/courses/{school}/{subject}/{code}")
    Course getCourse(@Path("school") String school,
                     @Path("subject") String subject,
                     @Path("code") String code);

//            @POST("/events/new")
//            void createEvent(@Body Event event, Callback<Event> cb);

}

package hac.controllers;

import hac.entity.Course;
import hac.entity.Student;
import hac.repository.CourseRepository;
import hac.repository.StudentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class StudentController {
    private Student student;

    @Autowired
    private StudentRepository studentRepository;
    private CourseRepository courseRepository;

    @Autowired
    public void setStudentRepository(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Autowired
    public void setCourseRepository(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    //-----------------------------------------admin-----------------------------------------------

    /**
     * Get all students enrolled in a specific course (admin view).
     *
     * @param model    The model object to be populated with data.
     * @param courseId The ID of the course.
     * @return The view for displaying all students in the course.
     */
    @GetMapping(path = "/admin/course/{courseId}/students")
    public String getAllStudents(Model model, @PathVariable(value = "courseId") int courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            model.addAttribute("students", course.getStudents());
            model.addAttribute("course", course);
        }
        return "students-page";
    }

    //-----------------------------------admin finish-------------------------------------------------------------

    /**
     * Get the student page.
     *
     * @param session   The HttpSession object.
     * @param model     The model object to be populated with data.
     * @param principal The Principal object representing the currently authenticated user.
     * @return The view for the student page.
     */
    @GetMapping("/student")
    public String getStudentPage(HttpSession session, Model model, Principal principal) {
        if (principal.getName() == null) {
            return "redirect:/login";
        } else {
            if (this.student == null) {
                this.student = studentRepository.findStudentsByStudentEmail(principal.getName());
            }
        }

        model.addAttribute("email", principal.getName());
        return "student-page";
    }

    /**
     * Get the list of courses for a specific student.
     *
     * @param model     The model object to be populated with data.
     * @param principal The Principal object representing the currently authenticated user.
     * @return The view for the student courses page.
     */
    @GetMapping(path = "/student/courses")
    public String coursesListOfSpecificStudent(Model model, Principal principal) {
        if (principal.getName() == null) {
            return "redirect:/login";
        } else {
            model.addAttribute("courses", getCoursesByStudentEmail(principal.getName()));
            model.addAttribute("email", principal.getName());
            return "student-courses";
        }
    }

    /**
     * Get the list of courses for a specific student based on their email.
     *
     * @param email The email of the student.
     * @return The list of courses for the student.
     */
    public List<Course> getCoursesByStudentEmail(String email) {
        List<Course> courses = new ArrayList<>();

        for (Course course : courseRepository.findAll()) {
            List<Student> students = course.getStudents();
            for (Student student : students) {
                if (student.getStudentEmail().equals(email)) {
                    courses.add(course);
                    break;
                }
            }
        }
        return courses;
    }

    /**
     * Get the list of all courses (admin view).
     *
     * @param model          The model object to be populated with data.
     * @param authentication The Authentication object representing the current user's authentication.
     * @param principal      The Principal object representing the currently authenticated user.
     * @return The view for displaying all courses.
     */
    @GetMapping(path = "/all-courses")
    public String getAllCourses(Model model, Authentication authentication, Principal principal) {
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

            model.addAttribute("email", principal.getName());
            if (isAdmin) {
                model.addAttribute("courses", courseRepository.findAll());
            } else {
                model.addAttribute("courses", getUnCoursesByStudentEmail(principal.getName()));
            }
            return "courses-page";
        }

        return "redirect:/login";
    }

    /**
     * Get information about a specific course.
     *
     * @param model     The model object to be populated with data.
     * @param id        The ID of the course.
     * @param principal The Principal object representing the currently authenticated user.
     * @return The view for displaying the course information.
     */
    @GetMapping(path = "/courses/{id}")
    public String getSpecificCourseInfo(Model model, @PathVariable(value = "id") int id, Principal principal) {
        Optional<Course> c = courseRepository.findById(id);
        if (c.isPresent()) {
            Course course = c.get();
            model.addAttribute("course", course);
            if (principal.getName() != null) {
                model.addAttribute("email", principal.getName());
            }
            return "course-page";
        }
        return "error";
    }

    /**
     * Get the list of uncourses for a specific student based on their email.
     *
     * @param email The email of the student.
     * @return The list of uncourses for the student.
     */
    public List<Course> getUnCoursesByStudentEmail(String email) {
        List<Course> courses = new ArrayList<>();

        for (Course course : courseRepository.findAll()) {
            List<Student> students = course.getStudents();
            boolean found = false;
            for (Student student : students) {
                if (student.getStudentEmail().equals(email)) {
                    found = true;
                    break;
                }
            }
            if (!found)
                courses.add(course);
        }
        return courses;
    }

    /**
     * Add a student to a specific course.
     *
     * @param model     The model object to be populated with data.
     * @param id        The ID of the course.
     * @param principal The Principal object representing the currently authenticated user.
     * @return The view for adding the student to the course.
     */
    @GetMapping(path = "/student/courses/{id}/add")
    public String getAddStudentPage(Model model, @PathVariable(value = "id") int id, Principal principal) {
        if (principal.getName() != null) {
            Course course = courseRepository.findById(id).orElse(null);
            Student student = studentRepository.findStudentsByStudentEmail(principal.getName());

            if (course != null && student != null) {
                course.addStudent(student);
                courseRepository.save(course);

                return "redirect:/student/courses";
            }
            System.out.println("1");
        }
        System.out.println("1");
        return "redirect:/login";
    }
}

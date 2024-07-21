package hac.controllers;

import hac.entity.Course;
import hac.repository.CourseRepository;
import hac.repository.StudentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;
    private StudentRepository studentRepository;

    @Autowired
    public void courseRepository(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Autowired
    public void studentRepository(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * This method initializes the data by deleting the course with ID 1 and adding some sample courses.
     */
    @PostConstruct
    public void initData() {
        courseRepository.deleteCourseWithIdOne();
        Course course1 = new Course( "Introduction to Computer Science","Dr. Yoram Biberman","10204011","In this course, we will get to know the basics of programming, and the procedural approach as expressed in C/C++ languages. In addition, as an introductory course to Computer Science as a scientific discipline, we get exposed to issues of it, like: What is an algorithm? How is its effectiveness evaluated? How to write computer programs properly? How does the operating system manage a program’s allocated memory? We touch on all of these issues as we walk the programming path: each question will be presented in the context of programs to which it is relevant");
        Course course2 = new Course("Digital Systems","Dr. Simcha Rozen","10203011","How is data stored on a computer? How does a computer execute basic operations? In this course we will\n" +
                "get to know the fundamental building blocks of computerized systems. We will learn how data is presented\n" +
                "in binary systems and will learn about the basic logic gates that allow us to process information of any kind:\n" +
                "numbers, letters, music, pictures etc.\n");
        Course course3=new Course("Discrete Mathematics","Dr. Eran London","10202011","The course begins with the fundamentals of the language of mathematics. It presents the rules of the game\n" +
                "including basic concepts for mathematical studies in general and in particular for theoretical computer\n" +
                "science.");
        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);
    }

    //--------------------------------------------------------------admin----------------------------------------------

    /**
     * Retrieves the course information for editing.
     *
     * @param model The model object to be populated with data.
     * @param id    The ID of the course to be edited.
     * @return The view for editing the course.
     */
    @GetMapping(path = "/admin/course-edit/{id}")
    public String editCourse(Model model, @PathVariable(value = "id") int id) {
        model.addAttribute("course", courseRepository.findById(id));
        return "course-adding";
    }

    /**
     * Saves the course information after adding or editing a course.
     *
     * @param course         The course object to be saved.
     * @param bindingResult  The binding result for validation.
     * @param redirectAttrs  The redirect attributes for flash messages.
     * @return The redirection URL after saving the course.
     */
    @PostMapping(path = "/admin/add-course")
    public String saveCourse(@Validated Course course, BindingResult bindingResult, RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            return "course-adding";
        } else {
            for (int i = 0; i < courseRepository.findAll().size(); i++) {
                if (courseRepository.findAll().get(i).getCode().equals(course.getCode())) {
                    course.setStudents(courseRepository.findAll().get(i).getStudents());
                }
            }

            courseRepository.save(course);
            redirectAttrs.addFlashAttribute("message", "A new course has been added successfully.");
        }
        return "redirect:/all-courses";
    }

    /**
     * Deletes a course with the specified ID.
     *
     * @param id     The ID of the course to be deleted.
     * @param model  The model object to be populated with data.
     * @return The redirection URL after deleting the course.
     */
    @GetMapping(path = "/admin/course-delete/{id}")
    public String deleteCourse(@PathVariable(value = "id") int id, RedirectAttributes model) {
        try {
            courseRepository.deleteById(id);

            // Get the remaining courses from the repository
            List<Course> remainingCourses = courseRepository.findAll();

            // Update the new IDs for the remaining courses
            for (int i = 0; i < remainingCourses.size(); i++) {
                Course course = remainingCourses.get(i);
                course.setId(i + 1);
                courseRepository.save(course);
            }

            model.addFlashAttribute("message", "The course has been deleted successfully.");
        } catch (Exception e) {
            e.getMessage();
        }
        return "redirect:/all-courses";
    }

    /**
     * Deletes a student from a specific course.
     *
     * @param id          The ID of the course.
     * @param studentId   The ID of the student to be deleted.
     * @param model       The model object to be populated with data.
     * @return The redirection URL after deleting the student from the course.
     */
    @GetMapping(path = "/admin/course-delete/{id}/{studentId}")
    public String deleteStudentFromCourse(@PathVariable(value = "id") int id, @PathVariable(value = "studentId") int studentId, RedirectAttributes model) {
        try {
            Course c = courseRepository.findById(id).orElse(null);
            if (c != null) {
                c.deleteStudent(studentId);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        model.addFlashAttribute("message", "One student has been successfully removed/deleted.");
        return "redirect:/admin/course/" + id + "/students";
    }

    /**
     * Displays the form for adding a new course.
     *
     * @param model The model object to be populated with data.
     * @return The view for adding a new course.
     */
    @GetMapping(path = "admin/addNewcourses")
    public String addCourse(Model model) {
        model.addAttribute("course", new Course());
        return "course-adding";
    }

    /**
     * Retrieves all students and displays them in the admin dashboard.
     *
     * @param model The model object to be populated with data.
     * @return The view for the admin dashboard.
     */
    @GetMapping(path = "admin/all-students")
    public String getDashboard(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("course", null);
        return "students-page";
    }

    /**
     * Retrieves the number of courses and students and displays them in the admin page.
     *
     * @param model The model object to be populated with data.
     * @return The view for the admin page.
     */
    @GetMapping(path = "/admin")
    public String getAdminPage(Model model) {
        model.addAttribute("courses", courseRepository.findAll().size());
        model.addAttribute("students", studentRepository.findAll().size());
        return "admin-page";
    }
}

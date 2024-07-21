package hac.entity;


import hac.entity.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "course")
public class Course {
    @Id
    @Column(name = "course_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull(message = "Please enter course name")
    @NotEmpty(message = "Please enter course name")
    @Column(name = "name", length = 100)
    private String name;
    @NotNull(message = "Please enter professor name")
    @NotEmpty(message = "Please enter professor name")
    @Column(name = "professor", length = 100)
    private String professor;

    @NotNull(message = "Please enter course code")
    @Size(min = 8, max = 8, message = "Code name should be 8 characters")
    @Column(length = 8)
    private String code;

    @NotNull(message = "Please enter description")
    @NotEmpty(message = "Please enter description")
    @Column(length = 100000)
    private String description;




    @ManyToMany
    private List<Student> students;

    public Course() {
    }

    public Course(String name) {
        this.name = name;
        this.students = new ArrayList<>();
    }


    public Course(String name,String professor,String code,String description)
    {
        this.name=name;
        this.professor=professor;
        this.code=code;
        this.description=description;
        this.students=new ArrayList<>();
    }
    public void deleteStudent(int id) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId() == id) {
                students.remove(i);
                break;
            }
        }
    }
    public void setStudents(List<Student>s){
        this.students=s;
    }
    public List<Student> getStudents(){
        return students;
    }
    public void addStudent(Student s){
        if(students.contains(s))
            return;
        this.students.add(s);
    }

    public String getProfessor(){
        return professor;
    }
    public void setProfessor(String professors){
        this.professor=professors;
    }
    // Getter and setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}

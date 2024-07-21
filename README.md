

# Course Registration System


## Description
The Course Registration System is a web application built using Spring Boot that allows administrators to create courses with a maximum number of participants and manage course registrations. Users can register for available courses, and administrators have the ability to browse, edit, and delete courses, as well as remove participants.

### Functionality

**Admin functionality:**
* Create new courses with a maximum number of participants.
* Edit existing courses.
* Delete courses that are no longer needed.
* Browse all courses and view the list of participants for each course.
* Remove specific students from specific courses.

**User functionality:**
* Browse available courses.
* Register for one or multiple courses.

## Installation

1. **Clone the repository:**
   ```sh
   git clone https://github.com/lubabanairoukh/Course-Registration-System.git
   cd Course-Registration-System
   ```

2. **Set up the database:**
   - Open phpMyAdmin.
   - Create a new database named `ex5`.

3. **Configure the application:**
   - Update the `application.properties` file with your database connection details if necessary.

4. **Run the application:**
   ```sh
   ./mvnw spring-boot:run
   ```

5. **Access the application:**
   - Open your browser and navigate to `http://localhost:8080`.

## Useful Information


### Student Credentials:
There is a file with 25 students. Their email is the username, and the password for each student is provided in the file.

## Additional Notes
- The admin can create, edit, and delete courses, as well as manage participants.
- Users can register for available courses without needing to create an account.
- Ensure the MySQL server is running and accessible.


## Dependencies
- Spring Boot
- Thymeleaf
- MySQL
- JPA





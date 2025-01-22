# BookMania User Microservice

This repository contains the **User Microservice**, developed as part of the **Software Engineering Methods** course at TU Delft. The User Microservice provides user management functionalities and integrates with other microservices to create a collaborative BookMania system.

## Features

- **User Management**:
  - Create, update, deactivate, and delete user accounts.
  - Manage user profiles with attributes such as bio, location, favorite genres, and favorite books.
  - Role-based access control (User, Author, Admin).

- **Integration with Other Microservices**:
  - Interaction with the Bookshelf microservice for favorite books and genres.
  - Support for analytics and logs tracking user activity.

- **Security**:
  - Proxy design pattern to protect sensitive data, such as user passwords.
  - Compliance with privacy regulations for account and data management.

- **Scalability**:
  - Chain of Responsibility design pattern for modular analytics processing.
  - Extensible architecture enabling easy integration of additional features.

## Running the Microservice

You can run the microservice locally or deploy it using Docker. Follow the steps below to set up the environment:

### Prerequisites

- Java 15
- Gradle
- Spring Boot
- Docker (optional, for containerized deployment)

### Local Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/bookmania-user-microservice.git
   cd bookmania-user-microservice
   ```

2. Build the project:
   ```bash
   ./gradlew build
   ```

3. Run the application:
   ```bash
   ./gradlew bootRun
   ```

4. Access the API documentation:
   Open the `openapi.yaml` file for a comprehensive list of endpoints.

## API Endpoints

The User Microservice API allows users to perform account and profile management operations. Key endpoints include:

- **Account Management**:
  - `POST /account/create`: Create a new user account.
  - `PUT /account/modify`: Update account details.
  - `DELETE /account/delete`: Delete user accounts.

- **Profile Management**:
  - `GET /profile/view`: View a user's profile.
  - `PUT /profile/modify`: Update profile details.
  - `PUT /profile/favoriteGenre/{genre_id}`: Add a genre to favorites.
  - `PUT /profile/favoriteBook/{book_id}`: Add a book to favorites.

- **Admin Features**:
  - `DELETE /admin/delete/{user_id}`: Delete user accounts as an admin.
  - `PUT /admin/ban/{user_id}`: Ban a user.

For a detailed list of endpoints and their parameters, refer to the `openapi.yaml` file.

## Technologies Used

- **Backend**: Java, Spring Boot
- **Build Tool**: Gradle
- **Testing**: JUnit, PITest (Mutation Testing)
- **Version Control**: Git
- **Integration**: REST API

## Design Patterns

- **Proxy Pattern**: Secures sensitive user data by providing proxy objects for user information.
- **Chain of Responsibility Pattern**: Modularizes analytics processing, ensuring scalability and extensibility.

## Project Contributors

This project was developed collaboratively by the following team members:
- Maria Ruxanda Tudor
- Ivar van Loon
- Matei Stănescu
- Maria Cristescu
- Wishaal Kanhai

## License

This project is licensed under the TU Delft gitlab: https://gitlab.tudelft.nl

## Contact

For inquiries or issues, please contact mariaruxandatudor@gmail.com

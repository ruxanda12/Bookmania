package nl.tudelft.sem.template.user.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * UserWithoutPassword
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-01-22T18:09:18.751+01:00[Europe/Berlin]")
public class UserWithoutPassword {

  private UUID userId;

  private String email;

  /**
   * Default constructor
   * @deprecated Use {@link UserWithoutPassword#UserWithoutPassword(String)}
   */
  @Deprecated
  public UserWithoutPassword() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UserWithoutPassword(String email) {
    this.email = email;
  }

  public UserWithoutPassword userId(UUID userId) {
    this.userId = userId;
    return this;
  }

  /**
   * the ID of the user
   * @return userId
  */
  @Valid 
  @Schema(name = "user_id", description = "the ID of the user", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("user_id")
  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public UserWithoutPassword email(String email) {
    this.email = email;
    return this;
  }

  /**
   * email of user
   * @return email
  */
  @NotNull @Pattern(regexp = "\\w+@\\w+\\.\\w{2,}") @javax.validation.constraints.Email
  @Schema(name = "email", description = "email of user", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserWithoutPassword userWithoutPassword = (UserWithoutPassword) o;
    return Objects.equals(this.userId, userWithoutPassword.userId) &&
        Objects.equals(this.email, userWithoutPassword.email);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, email);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserWithoutPassword {\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}


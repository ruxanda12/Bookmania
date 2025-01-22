package nl.tudelft.sem.template.user.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * User data regarding system analytics
 */

@Schema(name = "UserAnalytics", description = "User data regarding system analytics")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-01-22T18:09:18.751+01:00[Europe/Berlin]")
public class UserAnalytics {

  private Integer userCount;

  private Integer activeUsers;

  private Integer authorCount;

  @Valid
  private List<String> mostFollowedUsers;

  @Valid
  private List<String> userLog;

  private String mostPopularBook;

  @Valid
  private List<String> mostPopularGenres;

  public UserAnalytics userCount(Integer userCount) {
    this.userCount = userCount;
    return this;
  }

  /**
   * Get userCount
   * @return userCount
  */
  
  @Schema(name = "user-count", example = "58", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("user-count")
  public Integer getUserCount() {
    return userCount;
  }

  public void setUserCount(Integer userCount) {
    this.userCount = userCount;
  }

  public UserAnalytics activeUsers(Integer activeUsers) {
    this.activeUsers = activeUsers;
    return this;
  }

  /**
   * Get activeUsers
   * @return activeUsers
  */
  
  @Schema(name = "active-users", example = "52", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("active-users")
  public Integer getActiveUsers() {
    return activeUsers;
  }

  public void setActiveUsers(Integer activeUsers) {
    this.activeUsers = activeUsers;
  }

  public UserAnalytics authorCount(Integer authorCount) {
    this.authorCount = authorCount;
    return this;
  }

  /**
   * Get authorCount
   * @return authorCount
  */
  
  @Schema(name = "author-count", example = "12", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("author-count")
  public Integer getAuthorCount() {
    return authorCount;
  }

  public void setAuthorCount(Integer authorCount) {
    this.authorCount = authorCount;
  }

  public UserAnalytics mostFollowedUsers(List<String> mostFollowedUsers) {
    this.mostFollowedUsers = mostFollowedUsers;
    return this;
  }

  public UserAnalytics addMostFollowedUsersItem(String mostFollowedUsersItem) {
    if (this.mostFollowedUsers == null) {
      this.mostFollowedUsers = new ArrayList<>();
    }
    this.mostFollowedUsers.add(mostFollowedUsersItem);
    return this;
  }

  /**
   * top 3 followed users by id's
   * @return mostFollowedUsers
  */
  
  @Schema(name = "most-followed-users", example = "[\"user4@email.com\",\"tudelft@tudelft.nl\",\"email@user.py\"]", description = "top 3 followed users by id's", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("most-followed-users")
  public List<String> getMostFollowedUsers() {
    return mostFollowedUsers;
  }

  public void setMostFollowedUsers(List<String> mostFollowedUsers) {
    this.mostFollowedUsers = mostFollowedUsers;
  }

  public UserAnalytics userLog(List<String> userLog) {
    this.userLog = userLog;
    return this;
  }

  public UserAnalytics addUserLogItem(String userLogItem) {
    if (this.userLog == null) {
      this.userLog = new ArrayList<>();
    }
    this.userLog.add(userLogItem);
    return this;
  }

  /**
   * last 'x' user activities, x can be chosen accordingly.
   * @return userLog
  */
  
  @Schema(name = "user-log", example = "[\"user1 deactivated their account\",\"user42 got banned by admin388\",\"user21 became an author\"]", description = "last 'x' user activities, x can be chosen accordingly.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("user-log")
  public List<String> getUserLog() {
    return userLog;
  }

  public void setUserLog(List<String> userLog) {
    this.userLog = userLog;
  }

  public UserAnalytics mostPopularBook(String mostPopularBook) {
    this.mostPopularBook = mostPopularBook;
    return this;
  }

  /**
   * id of most popular book
   * @return mostPopularBook
  */
  
  @Schema(name = "most-popular-book", description = "id of most popular book", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("most-popular-book")
  public String getMostPopularBook() {
    return mostPopularBook;
  }

  public void setMostPopularBook(String mostPopularBook) {
    this.mostPopularBook = mostPopularBook;
  }

  public UserAnalytics mostPopularGenres(List<String> mostPopularGenres) {
    this.mostPopularGenres = mostPopularGenres;
    return this;
  }

  public UserAnalytics addMostPopularGenresItem(String mostPopularGenresItem) {
    if (this.mostPopularGenres == null) {
      this.mostPopularGenres = new ArrayList<>();
    }
    this.mostPopularGenres.add(mostPopularGenresItem);
    return this;
  }

  /**
   * array of id's of top 3 favorite genres
   * @return mostPopularGenres
  */
  
  @Schema(name = "most-popular-genres", description = "array of id's of top 3 favorite genres", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("most-popular-genres")
  public List<String> getMostPopularGenres() {
    return mostPopularGenres;
  }

  public void setMostPopularGenres(List<String> mostPopularGenres) {
    this.mostPopularGenres = mostPopularGenres;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserAnalytics userAnalytics = (UserAnalytics) o;
    return Objects.equals(this.userCount, userAnalytics.userCount) &&
        Objects.equals(this.activeUsers, userAnalytics.activeUsers) &&
        Objects.equals(this.authorCount, userAnalytics.authorCount) &&
        Objects.equals(this.mostFollowedUsers, userAnalytics.mostFollowedUsers) &&
        Objects.equals(this.userLog, userAnalytics.userLog) &&
        Objects.equals(this.mostPopularBook, userAnalytics.mostPopularBook) &&
        Objects.equals(this.mostPopularGenres, userAnalytics.mostPopularGenres);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userCount, activeUsers, authorCount, mostFollowedUsers, userLog, mostPopularBook, mostPopularGenres);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserAnalytics {\n");
    sb.append("    userCount: ").append(toIndentedString(userCount)).append("\n");
    sb.append("    activeUsers: ").append(toIndentedString(activeUsers)).append("\n");
    sb.append("    authorCount: ").append(toIndentedString(authorCount)).append("\n");
    sb.append("    mostFollowedUsers: ").append(toIndentedString(mostFollowedUsers)).append("\n");
    sb.append("    userLog: ").append(toIndentedString(userLog)).append("\n");
    sb.append("    mostPopularBook: ").append(toIndentedString(mostPopularBook)).append("\n");
    sb.append("    mostPopularGenres: ").append(toIndentedString(mostPopularGenres)).append("\n");
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


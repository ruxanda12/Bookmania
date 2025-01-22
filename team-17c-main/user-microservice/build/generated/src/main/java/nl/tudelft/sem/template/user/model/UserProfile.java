package nl.tudelft.sem.template.user.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * UserProfile
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-01-22T18:09:18.751+01:00[Europe/Berlin]")
@javax.persistence.Entity
public class UserProfile {

  @javax.persistence.Id @javax.persistence.GeneratedValue
  private UUID userId;

  private String username;

  private String name;

  /**
   * Privacy ID:  * `0` - Public Profile  * `1` - Private Profile 
   */
  public enum PrivacyEnum {
    PUBLIC(0),
    
    PRIVATE(1);

    private Integer value;

    PrivacyEnum(Integer value) {
      this.value = value;
    }

    @JsonValue
    public Integer getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static PrivacyEnum fromValue(Integer value) {
      for (PrivacyEnum b : PrivacyEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private PrivacyEnum privacy;

  /**
   * Role ID:  * `0` - User Role  * `1` - Author Role  * `2` - Admin Role 
   */
  public enum RoleEnum {
    USER(0),
    
    AUTHOR(1),
    
    ADMIN(2);

    private Integer value;

    RoleEnum(Integer value) {
      this.value = value;
    }

    @JsonValue
    public Integer getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static RoleEnum fromValue(Integer value) {
      for (RoleEnum b : RoleEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private RoleEnum role;

  /**
   * State ID:  * `0` - Active  * `1` - Inactive  * `2` - Banned 
   */
  public enum StateEnum {
    ACTIVE(0),
    
    INACTIVE(1),
    
    BANNED(2);

    private Integer value;

    StateEnum(Integer value) {
      this.value = value;
    }

    @JsonValue
    public Integer getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StateEnum fromValue(Integer value) {
      for (StateEnum b : StateEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private StateEnum state = StateEnum.ACTIVE;

  private String bio;

  private String avatar;

  private String location;

  @javax.persistence.ElementCollection
  @Valid
  private List<String> favoriteGenres;

  private String favoriteBook;

  @javax.persistence.ElementCollection
  @Valid
  private List<UUID> followers;

  @javax.persistence.ElementCollection
  @Valid
  private List<UUID> following;

  @javax.persistence.ElementCollection
  @Valid
  private List<UUID> friends;

  @javax.persistence.ElementCollection
  @Valid
  private List<UUID> bookshelves;

  /**
   * Default constructor
   * @deprecated Use {@link UserProfile#UserProfile(String)}
   */
  @Deprecated
  public UserProfile() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UserProfile(String username) {
    this.username = username;
  }

  public UserProfile userId(UUID userId) {
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

  public UserProfile username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Get username
   * @return username
  */
  @NotNull @Pattern(regexp = "^\\w{2,20}$") 
  @Schema(name = "username", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("username")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public UserProfile name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UserProfile privacy(PrivacyEnum privacy) {
    this.privacy = privacy;
    return this;
  }

  /**
   * Privacy ID:  * `0` - Public Profile  * `1` - Private Profile 
   * @return privacy
  */
  
  @Schema(name = "privacy", description = "Privacy ID:  * `0` - Public Profile  * `1` - Private Profile ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("privacy")
  public PrivacyEnum getPrivacy() {
    return privacy;
  }

  public void setPrivacy(PrivacyEnum privacy) {
    this.privacy = privacy;
  }

  public UserProfile role(RoleEnum role) {
    this.role = role;
    return this;
  }

  /**
   * Role ID:  * `0` - User Role  * `1` - Author Role  * `2` - Admin Role 
   * @return role
  */
  
  @Schema(name = "role", description = "Role ID:  * `0` - User Role  * `1` - Author Role  * `2` - Admin Role ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("role")
  public RoleEnum getRole() {
    return role;
  }

  public void setRole(RoleEnum role) {
    this.role = role;
  }

  public UserProfile state(StateEnum state) {
    this.state = state;
    return this;
  }

  /**
   * State ID:  * `0` - Active  * `1` - Inactive  * `2` - Banned 
   * @return state
  */
  
  @Schema(name = "state", description = "State ID:  * `0` - Active  * `1` - Inactive  * `2` - Banned ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("state")
  public StateEnum getState() {
    return state;
  }

  public void setState(StateEnum state) {
    this.state = state;
  }

  public UserProfile bio(String bio) {
    this.bio = bio;
    return this;
  }

  /**
   * Get bio
   * @return bio
  */
  
  @Schema(name = "bio", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bio")
  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public UserProfile avatar(String avatar) {
    this.avatar = avatar;
    return this;
  }

  /**
   * The profile picture
   * @return avatar
  */
  
  @Schema(name = "avatar", description = "The profile picture", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("avatar")
  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public UserProfile location(String location) {
    this.location = location;
    return this;
  }

  /**
   * Get location
   * @return location
  */
  
  @Schema(name = "location", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("location")
  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public UserProfile favoriteGenres(List<String> favoriteGenres) {
    this.favoriteGenres = favoriteGenres;
    return this;
  }

  public UserProfile addFavoriteGenresItem(String favoriteGenresItem) {
    if (this.favoriteGenres == null) {
      this.favoriteGenres = new ArrayList<>();
    }
    this.favoriteGenres.add(favoriteGenresItem);
    return this;
  }

  /**
   * array of ids of favorite genres
   * @return favoriteGenres
  */
  
  @Schema(name = "favoriteGenres", description = "array of ids of favorite genres", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("favoriteGenres")
  public List<String> getFavoriteGenres() {
    return favoriteGenres;
  }

  public void setFavoriteGenres(List<String> favoriteGenres) {
    this.favoriteGenres = favoriteGenres;
  }

  public UserProfile favoriteBook(String favoriteBook) {
    this.favoriteBook = favoriteBook;
    return this;
  }

  /**
   * id of favorite book
   * @return favoriteBook
  */
  
  @Schema(name = "favoriteBook", description = "id of favorite book", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("favoriteBook")
  public String getFavoriteBook() {
    return favoriteBook;
  }

  public void setFavoriteBook(String favoriteBook) {
    this.favoriteBook = favoriteBook;
  }

  public UserProfile followers(List<UUID> followers) {
    this.followers = followers;
    return this;
  }

  public UserProfile addFollowersItem(UUID followersItem) {
    if (this.followers == null) {
      this.followers = new ArrayList<>();
    }
    this.followers.add(followersItem);
    return this;
  }

  /**
   * UserProfiles of followers
   * @return followers
  */
  @Valid 
  @Schema(name = "followers", description = "UserProfiles of followers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("followers")
  public List<UUID> getFollowers() {
    return followers;
  }

  public void setFollowers(List<UUID> followers) {
    this.followers = followers;
  }

  public UserProfile following(List<UUID> following) {
    this.following = following;
    return this;
  }

  public UserProfile addFollowingItem(UUID followingItem) {
    if (this.following == null) {
      this.following = new ArrayList<>();
    }
    this.following.add(followingItem);
    return this;
  }

  /**
   * UserProfiles of the \"followed\"
   * @return following
  */
  @Valid 
  @Schema(name = "following", description = "UserProfiles of the \"followed\"", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("following")
  public List<UUID> getFollowing() {
    return following;
  }

  public void setFollowing(List<UUID> following) {
    this.following = following;
  }

  public UserProfile friends(List<UUID> friends) {
    this.friends = friends;
    return this;
  }

  public UserProfile addFriendsItem(UUID friendsItem) {
    if (this.friends == null) {
      this.friends = new ArrayList<>();
    }
    this.friends.add(friendsItem);
    return this;
  }

  /**
   * UserProfiles of friends, friends = users who follow each other back
   * @return friends
  */
  @Valid 
  @Schema(name = "friends", description = "UserProfiles of friends, friends = users who follow each other back", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("friends")
  public List<UUID> getFriends() {
    return friends;
  }

  public void setFriends(List<UUID> friends) {
    this.friends = friends;
  }

  public UserProfile bookshelves(List<UUID> bookshelves) {
    this.bookshelves = bookshelves;
    return this;
  }

  public UserProfile addBookshelvesItem(UUID bookshelvesItem) {
    if (this.bookshelves == null) {
      this.bookshelves = new ArrayList<>();
    }
    this.bookshelves.add(bookshelvesItem);
    return this;
  }

  /**
   * array of id's of the bookshelves
   * @return bookshelves
  */
  @Valid 
  @Schema(name = "bookshelves", description = "array of id's of the bookshelves", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookshelves")
  public List<UUID> getBookshelves() {
    return bookshelves;
  }

  public void setBookshelves(List<UUID> bookshelves) {
    this.bookshelves = bookshelves;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserProfile userProfile = (UserProfile) o;
    return Objects.equals(this.userId, userProfile.userId) &&
        Objects.equals(this.username, userProfile.username) &&
        Objects.equals(this.name, userProfile.name) &&
        Objects.equals(this.privacy, userProfile.privacy) &&
        Objects.equals(this.role, userProfile.role) &&
        Objects.equals(this.state, userProfile.state) &&
        Objects.equals(this.bio, userProfile.bio) &&
        Objects.equals(this.avatar, userProfile.avatar) &&
        Objects.equals(this.location, userProfile.location) &&
        Objects.equals(this.favoriteGenres, userProfile.favoriteGenres) &&
        Objects.equals(this.favoriteBook, userProfile.favoriteBook) &&
        Objects.equals(this.followers, userProfile.followers) &&
        Objects.equals(this.following, userProfile.following) &&
        Objects.equals(this.friends, userProfile.friends) &&
        Objects.equals(this.bookshelves, userProfile.bookshelves);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, username, name, privacy, role, state, bio, avatar, location, favoriteGenres, favoriteBook, followers, following, friends, bookshelves);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserProfile {\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    privacy: ").append(toIndentedString(privacy)).append("\n");
    sb.append("    role: ").append(toIndentedString(role)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    bio: ").append(toIndentedString(bio)).append("\n");
    sb.append("    avatar: ").append(toIndentedString(avatar)).append("\n");
    sb.append("    location: ").append(toIndentedString(location)).append("\n");
    sb.append("    favoriteGenres: ").append(toIndentedString(favoriteGenres)).append("\n");
    sb.append("    favoriteBook: ").append(toIndentedString(favoriteBook)).append("\n");
    sb.append("    followers: ").append(toIndentedString(followers)).append("\n");
    sb.append("    following: ").append(toIndentedString(following)).append("\n");
    sb.append("    friends: ").append(toIndentedString(friends)).append("\n");
    sb.append("    bookshelves: ").append(toIndentedString(bookshelves)).append("\n");
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


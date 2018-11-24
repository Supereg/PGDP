import java.util.Objects;

/**
 * The class {@code Author} represents an author of a {@link Document} or a
 * {@link Review}.
 *
 * This class ensures that neither of first name, last name, residence and email
 * is <code>null</code>. There is no <code>setter</code> for the birthday of the
 * author, since this field can never ever change.
 *
 * @see Document
 * @see Review
 * @see Date
 *
 */
public class Author {

    private String firstName;
    private String lastName;

    private Date birthday;

    private String residence;
    private String email;

    /**
     * Constructs an author with the given values.
     *
     * @param firstName the author's first name
     * @param lastName  the author's last name
     * @param birthday  the author's birthday
     * @param residence the author's residence
     * @param email     the author's email address
     */
    public Author(String firstName, String lastName, Date birthday, String residence, String email) {
        this.setFirstName(firstName);
        this.setLastName(lastName);

        this.setBirthday(birthday);

        this.setResidence(residence);
        this.setEmail(email);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getResidence() {
        return residence;
    }

    public String getContactInformation() {
        return firstName + " " + lastName + Terminal.NEWLINE +
                email + Terminal.NEWLINE +
                residence;
    }

    /**
     * Sets the first name of the author.
     *
     * If the specified first name is <code>null</code>, then the first name is set
     * to an empty {@link String}.
     *
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) {
        if (firstName == null) {
            this.firstName = "";
        } else {
            this.firstName = firstName;
        }
    }

    /**
     * Sets the last name of the author.
     *
     * If the specified last name is <code>null</code>, then the last name is set to
     * an empty {@link String}.
     *
     * @param lastName the new last name
     */
    public void setLastName(String lastName) {
        if (lastName == null) {
            this.lastName = "";
        } else {
            this.lastName = lastName;
        }
    }

    /**
     * Sets the birthday of the author.
     *
     * If the specified birthday is <code>null</code>, then the birthday is set
     * to the 1st of January, 1999.
     *
     * @param birthday the new birthday
     */
    public void setBirthday(Date birthday) {
        if(birthday == null)
            this.birthday = new Date(1, 1, 1999);
        else
            this.birthday = birthday;
    }

    /**
     * Sets the residence of the author.
     *
     * If the specified residence is <code>null</code>, then the residence is set to
     * an empty {@link String}.
     *
     * @param residence the new residence
     */
    public void setResidence(String residence) {
        if (residence == null) {
            this.residence = "";
        } else {
            this.residence = residence;
        }
    }

    /**
     * Sets the email address of the author.
     *
     * If the specified email address is <code>null</code> or does not contain an @-symbol,
     * then the email address is set to invalid@peguins.com.
     *
     * @param email the new email address
     */
    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            this.email = "invalid@penguins.com";
        } else {
            this.email = email;
        }
    }

    public int getAgeAt(Date today) {
        return birthday.getAgeInYearsAt(today);
    }

    public boolean equals(Author author) {
        if (this == author) return true;
        return Objects.equals(firstName, author.firstName) &&
                Objects.equals(lastName, author.lastName) &&
                Objects.equals(birthday, author.birthday) &&
                Objects.equals(residence, author.residence) &&
                Objects.equals(email, author.email);
    }

    @Override
    public String toString() {
        return "Author{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthday=" + birthday +
                ", email='" + email + '\'' +
                '}';
    }
}

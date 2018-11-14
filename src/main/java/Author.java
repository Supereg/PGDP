/**
 * The class {@code Author} represents an author of a {@see Document} or a
 * {@see Review}.
 * 
 * @author Florian Kelbert
 *
 */
public class Author {

    private String firstName;
    private String lastName;

    private Date birthday;

    private String residence;
    private String email;

    public Author(String firstName, String lastName, Date birthday, String residence, String email) {
        this.firstName = firstName;
        this.lastName = lastName;

        this.birthday = birthday;

        this.residence = residence;
        this.email = email;
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAgeAt(Date today) {
        return birthday.getAgeInYearsAt(today);
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

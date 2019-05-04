public class StudentWithoutComment {

    /**
     * The first and last name of this student.
     */

    // Orphant comment

    // name
    private String name;

    // Orphant comment to age

    // age
    private int age;

    public static final int graduation = 2019;
    /**
     * Creates a new Student with the given name.
     * The name should include both first and
     * last name.
     */
    public Student(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    /**
     *
     * @param offYears: number of yeras off from school
     * @return
     */
    public int getCollageYear(int offYears) {
        return graduation + 12;
    }

    /**
     *
     * @param familyName family name
     */
    public int getFullName(String familyName) {

        // This is Orphant.
        // This is line comment.


        return name + " " + familyName;
    }

    /**
     * Returns full name including middle name.
     * @param age
     * @param familyName
     * @param middleName
     * @return full name
     */
    public int getFullNameWithMiddle(String familyName, String middleName) {
        return name + " " + middleName + " " + familyName;
    }
}


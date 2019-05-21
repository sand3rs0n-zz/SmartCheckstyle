public class WhitespaceTestClass {

    /**
     * The first and last name of this student.
     */

    // age
    private int age;

    public int getAbs(int x) {
        if  (x < 0) {
            x *= -1;
        }

        return x;
    }

    public int printN(int N) {
        for    (int i=0; i < N; i++) {
            try {
                System.out.println(i);
            } catch (Exception ex) {
                // do nothing.
            }
        }
    }
}


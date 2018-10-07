package exception;

/**
 * Created by jodiakyulas on 30/9/18.
 */
public class APIQueriedWrongly extends RuntimeException {

    public APIQueriedWrongly() {
        System.out.println("The api has been queried wrongly.");
    }
}

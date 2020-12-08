package watchDog.bean.result;

/**
* Description: The code of the result.
* @author MatthewXu
* @date Nov 26, 2019
*/
public enum ResultCode {

	SUCCESS(200),
    FAIL(400),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500);

    private final int status;

    ResultCode(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}

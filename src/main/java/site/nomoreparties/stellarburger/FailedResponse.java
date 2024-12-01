package site.nomoreparties.stellarburger;

public class FailedResponse {
    private String message;
    private boolean success;

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "FailedResponse{" +
                "message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}

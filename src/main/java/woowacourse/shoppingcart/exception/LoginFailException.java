package woowacourse.shoppingcart.exception;

public class LoginFailException extends RuntimeException {

    public LoginFailException() {
        super("ID 또는 비밀번호가 올바르지 않습니다.");
    }
}

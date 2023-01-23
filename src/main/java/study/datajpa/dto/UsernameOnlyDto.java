package study.datajpa.dto;

public class UsernameOnlyDto {
    private final String username;
    // 생성자가 중요 // 파라미터 이름이 프로퍼티와 똑같아야 한다.
    public UsernameOnlyDto(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }
}


package study.datajpa.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter // 세터 없애고 생성자와 메소드를 활용
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 스펙상 기본생성자가 있어야함.
public class Member {
    @Id
    @GeneratedValue
    private Long id;
    private String username;


    // setter 말고 생성자로 만들자
    public Member(String username) {
        this.username = username;
    }

    // 이름 바꾸기 메소드
    public void changeUserName(String username) {
        this.username = username;
    }
}

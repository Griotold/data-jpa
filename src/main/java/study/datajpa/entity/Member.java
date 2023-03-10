package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter // 세터 없애고 생성자와 메소드를 활용 // 하지만 공부를 위해 세터를 열어둔다.
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 스펙상 기본생성자가 있어야함.
@ToString(of = {"id", "username", "age"}) // team을 걸면 무한루프에 빠짐
@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = :username"
)
@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
public class Member extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    // setter 말고 생성자로 만들자
    // 1번 생성자 : username만 지정
    public Member(String username) {
        this(username, 0); // 2번 생성자 호출
    }

    // 2번 생성자 : username, age 지정
    public Member(String username, int age){
        this(username, age, null); // 3번 생성자 호출
    }

    // 3번 생성자 : username, age, team 지정
    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null){
            changeTeam(team); // team이 null이어도 무시하기
        }

    }

    // 이름 바꾸기 메소드
    public void changeUserName(String username) {
        this.username = username;
    }

    // 연관관계 편의 메소드
    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);

    }
}

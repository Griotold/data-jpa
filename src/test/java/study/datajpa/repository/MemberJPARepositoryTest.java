package study.datajpa.repository;



import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest // 스프링부트 2.2이상 + junit5 조합이면 이거만 있으면 됨.
@Transactional
@Rollback(value = false)
class MemberJPARepositoryTest {

    @Autowired
    MemberJPARepository memberJPARepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberJPARepository.save(member);

        Member findMember = memberJPARepository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD()throws Exception {
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJPARepository.save(member1);
        memberJPARepository.save(member2);
        // when
        // 1. 단건 조회 검증
        Member findMember1 = memberJPARepository.findById(member1.getId()).get();
        Member findMember2 = memberJPARepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 2. 리스트 조회
        List<Member> all = memberJPARepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 3. 카운트 메소드
        long count = memberJPARepository.count();
        assertThat(count).isEqualTo(2);

        // 4. 삭제 메소드
        memberJPARepository.delete(member1);
        long countAfterDelete = memberJPARepository.count();
        assertThat(countAfterDelete).isEqualTo(1);

        // then
    }
    @Test
    public void 이름_나이() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        Member m3 = new Member("AAA", 30);
        memberJPARepository.save(m1);
        memberJPARepository.save(m2);
        memberJPARepository.save(m3);

       // when
        List<Member> result = memberJPARepository.findByusernameAndAgeGreaterThan("AAA", 15);
        // then
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.get(1).getAge()).isEqualTo(30);
        assertThat(result.size()).isEqualTo(2);
    }
}
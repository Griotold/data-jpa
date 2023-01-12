package study.datajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Optional<Member> findMember = memberRepository.findById(savedMember.getId());
        Member findedMember = findMember.get();

        assertThat(findedMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findedMember.getId()).isEqualTo(member.getId());
        assertThat(findedMember).isEqualTo(member);
    }

    // MemberJPARepository에서 한 거 그대로 복붙해서 잘 되는지 확인
    @Test 
    public void basicCRUD() throws Exception {
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);
        // when
        // 1. 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 2. 리스트 조회
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 3. 카운트 메소드
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 4. 삭제 메소드
        memberRepository.delete(member1);
        long countAfterDelete = memberRepository.count();
        assertThat(countAfterDelete).isEqualTo(1);

        // then
    }

    @Test
    public void 이름_나이_스프링_데이터_JPA() throws Exception {
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        Member m3 = new Member("AAA", 30);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);

        // when
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        // then
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.get(1).getAge()).isEqualTo(30);
        assertThat(result.size()).isEqualTo(2);
    }
}
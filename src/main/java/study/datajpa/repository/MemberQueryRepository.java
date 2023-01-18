package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

// 사용자 정의 인터페이스에 모든 로직을 넣을 필요가 없다.
@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final EntityManager em;

    // 아주 복잡한 JPQL이라 가정!
    public List<Member> findAllMembers() {
        return em.createQuery("selelct m from Member m")
                .getResultList();
    }
}

package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
@Getter
@MappedSuperclass // 진짜 상속은 아니고 속성만 물려준다.
public class JpaBaseEntity {
    @Column(updatable = false) // insertable = true(default)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    
    @PrePersist // 영속화 하기 전에
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now; // this 생략 : IDE가 보라색으로 색칠해주잖아
        updatedDate = now; // 여기는 null 아닌가? 데이터를 넣어주면 쿼리할 떄 편하다
    }

    @PreUpdate // 업데이트 전에
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }

}

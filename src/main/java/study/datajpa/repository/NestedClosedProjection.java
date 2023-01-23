package study.datajpa.repository;

public interface NestedClosedProjection {
    String getUsername(); // 얘는 select절 최적화
    TeamInfo getTeam(); // 얘는 team 엔티티를 다 가져옴

    // 중첩 인터페이스
    interface TeamInfo {
        String getName();
    }
}


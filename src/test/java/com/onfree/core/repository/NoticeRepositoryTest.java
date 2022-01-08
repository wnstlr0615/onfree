package com.onfree.core.repository;

import com.onfree.core.entity.Notice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


@DataJpaTest
@ActiveProfiles("test")
class NoticeRepositoryTest {
    @Autowired
    NoticeRepository noticeRepository;

    @BeforeEach
    public void init(){
        final List<Notice> noticeList = getNoticeList();
        noticeRepository.saveAll(noticeList);
    }

    private List<Notice> getNoticeList() {
        List<Notice> notices = new ArrayList<>();
        for (int i = 1; i <= 10; i++) { // 그냥 게시글 10개
            notices.add(
                    createNotice(i, false, false)
            );
        }

        for (int i = 11; i <= 20; i++) { // 상단 고정 게시글 10개
            notices.add(
                    createNotice(i, true, false)
            );
        }

        for (int i = 21; i <= 30; i++) { //상단 고정 게시글이면서 삭제 처리된 게시글 10개
            notices.add(
                    createNotice(i, true, true)
            );
        }

        for (int i = 31; i <= 40; i++) { // 삭제 처리도니 게시글 10개
            notices.add(
                    createNotice(i, false, true)
            );
        }
        return notices;
    }

    private Notice createNotice(int n, boolean top, boolean disabled) {
        return Notice.builder()
                .title("제목"+n)
                .content("내용"+n)
                .top(top)
                .view(n+10)
                .disabled(disabled)
                .build();
    }


    @Test
    @DisplayName("[페이징] 페이징 테스트1 - page = 0  size = 15")
    public void givenPageable_whenFindAllByDisabledIsFalseOrderByTopDescNoticeIdAsc_thenSortNoticeList() throws Exception{
        //given
        final PageRequest pageRequest = PageRequest.of(0, 15);

        //when
        final Page<Notice> noticePage = noticeRepository.findAllByDisabledIsFalseOrderByTopDescNoticeIdAsc(
                pageRequest
        );

        //then
        assertAll(
                () -> assertThat(noticePage.getTotalElements())
                        .isEqualTo(20).as("삭제 처리되지 않은 게시글 갯수"),
                () -> assertThat(noticePage.getNumberOfElements())
                        .isEqualTo(15).as("삭제 처리되지 않은 게시글 중 0 페이지 게시글 갯수"),
                () -> assertThat(noticePage.get().filter(Notice::isTop).count())
                        .isEqualTo(10).as("top이 true인 게시글 갯수"),
                () -> assertThat(noticePage.get().noneMatch(Notice::isDisabled))
                        .isTrue().as("삭제처리된 게시글이 존재 하지 않는다.")
        );
    }


    @Test
    @DisplayName("[페이징] 페이징 테스트2- page = 1  size = 15")
    public void givenPageIs1AndSizeIs15_whenFindAllByDisabledIsFalseOrderByTopDescNoticeIdAsc_thenSortNoticeList() throws Exception{
        //given
        final PageRequest pageRequest = PageRequest.of(1, 15);

        //when
        final Page<Notice> noticePage = noticeRepository.findAllByDisabledIsFalseOrderByTopDescNoticeIdAsc(
                pageRequest
        );

        noticePage.get().forEach(System.out::println);
        //then

        assertAll(
                () -> assertThat(noticePage.getTotalElements())
                        .isEqualTo(20).as("삭제 처리되지 않은 게시글 갯수"),
                () -> assertThat(noticePage.getNumberOfElements())
                        .isEqualTo(5).as("삭제 처리되지 않은 게시글 중 1 페이지 게시글 갯수"),
                () -> assertThat(noticePage.get().filter(Notice::isTop).count())
                        .isEqualTo(0).as("top이 true인 게시글 갯수"),
                () -> assertThat(noticePage.get().noneMatch(Notice::isDisabled))
                        .isTrue().as("삭제처리된 게시글이 존재 하지 않는다.")
        );
    }

    @Test
    @DisplayName("[페이징] 페이징 테스트3 - page = 0  size = 13")
    public void givenPageIs0AndSizeIs13_whenFindAllByDisabledIsFalseOrderByTopDescNoticeIdAsc_thenSortNoticeList() throws Exception{
        //given
        final PageRequest pageRequest = PageRequest.of(0, 13);

        //when
        final Page<Notice> noticePage = noticeRepository.findAllByDisabledIsFalseOrderByTopDescNoticeIdAsc(
                pageRequest
        );

        //then

        assertAll(
                () -> assertThat(noticePage.getTotalElements())
                        .isEqualTo(20).as("삭제 처리되지 않은 게시글 갯수"),
                () -> assertThat(noticePage.getNumberOfElements())
                        .isEqualTo(13).as("삭제 처리되지 않은 게시글 중 0 페이지 게시글 갯수"),
                () -> assertThat(noticePage.get().filter(Notice::isTop).count())
                        .isEqualTo(10).as("top이 true인 게시글 갯수"),
                () -> assertThat(noticePage.get().noneMatch(Notice::isDisabled))
                        .isTrue().as("삭제처리된 게시글이 존재 하지 않는다.")
        );
    }

    @Test
    @DisplayName("[페이징] 페이징 테스트4 - page = 1  size = 13")
    public void givenPageIs1AndSizeIs13_whenFindAllByDisabledIsFalseOrderByTopDescNoticeIdAsc_thenSortNoticeList() throws Exception{
        //given
        final PageRequest pageRequest = PageRequest.of(1, 13);

        //when
        final Page<Notice> noticePage = noticeRepository.findAllByDisabledIsFalseOrderByTopDescNoticeIdAsc(
                pageRequest
        );

        //then
        assertAll(
                () -> assertThat(noticePage.getTotalElements())
                        .isEqualTo(20).as("삭제 처리되지 않은 게시글 갯수"),
                () -> assertThat(noticePage.getNumberOfElements())
                        .isEqualTo(7).as("삭제 처리되지 않은 게시글 중 0 페이지 게시글 갯수"),
                () -> assertThat(noticePage.get().filter(Notice::isTop).count())
                        .isEqualTo(0).as("top이 true인 게시글 갯수"),
                () -> assertThat(noticePage.get().noneMatch(Notice::isDisabled))
                        .isTrue().as("삭제처리된 게시글이 존재 하지 않는다.")
        );
    }


}
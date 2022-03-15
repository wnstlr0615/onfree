package com.onfree.core.service.requestapply;

import com.onfree.common.error.code.UserErrorCode;
import com.onfree.common.error.exception.UserException;
import com.onfree.core.entity.requestapply.DirectRequestApply;
import com.onfree.core.entity.requestapply.RequestApplyStatus;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.entity.user.User;
import com.onfree.core.repository.ArtistUserRepository;
import com.onfree.core.repository.DirectRequestApplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectRequestApplyService {
    private final DirectRequestApplyRepository directRequestApplyRepository;
    private final ArtistUserRepository artistUserRepository;

    @Transactional
    public void addDirectRequestApply(User user, Long artistUserId) {
        ArtistUser artistUser = getArtistUser(artistUserId); // 작가유저 조회
        validateRequestApplicable(user,artistUser); // 의뢰 가능 여부 확인하기
        saveDirectRequestApply( // DirectRequestApply Entity 저장
                createDirectRequestApply(user, artistUser) // DirectRequestApply Entity 생성
        );
    }

    private void validateRequestApplicable(User user, ArtistUser artistUser) {
        //List<DirectRequestApply> directRequestApplies = directRequestApplyRepository.findAllByClientUserAndArtistUser(user, artistUser);
        //TODO 작가유저와 사용자에 따른 요청관계 파악 후 의뢰 가능 여부 설정하기
    }

    private void saveDirectRequestApply(DirectRequestApply directRequestApply) {
        directRequestApplyRepository.save(directRequestApply);
    }

    private DirectRequestApply createDirectRequestApply(User user, ArtistUser artistUser) {
        return DirectRequestApply.createDirectRequestApply(user, artistUser, RequestApplyStatus.REQUEST_APPLY_CRATED);
    }

    private ArtistUser getArtistUser(Long artistUserId) {
        return artistUserRepository.findByUserIdAndDeletedIsFalse(artistUserId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USERID));
    }
}

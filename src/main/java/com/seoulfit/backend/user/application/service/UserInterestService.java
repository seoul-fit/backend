package com.seoulfit.backend.user.application.service;

import com.seoulfit.backend.user.application.port.in.ManageUserInterestUseCase;
import com.seoulfit.backend.user.application.port.in.dto.UpdateUserInterestCommand;
import com.seoulfit.backend.user.application.port.in.dto.UserInterestResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserInterestService implements ManageUserInterestUseCase {

    @Override
    public UserInterestResult getUserInterests(Long userId) {
        return null;
    }

    @Override
    public UserInterestResult updateUserInterests(UpdateUserInterestCommand command) {
        return null;
    }
}

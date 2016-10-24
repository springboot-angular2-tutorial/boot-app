package com.myapp.service;

import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.repository.RelatedUserCustomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelationshipServiceImpl implements RelationshipService {

    private final RelatedUserCustomRepository relatedUserCustomRepository;
    private final SecurityContextService securityContextService;

    public RelationshipServiceImpl(RelatedUserCustomRepository relatedUserCustomRepository, SecurityContextService securityContextService) {
        this.relatedUserCustomRepository = relatedUserCustomRepository;
        this.securityContextService = securityContextService;
    }

    @Override
    public List<RelatedUserDTO> findFollowings(User user, PageParams pageParams) {
        final User currentUser = securityContextService.currentUser();
        return relatedUserCustomRepository.findFollowings(user, currentUser, pageParams);
    }

    @Override
    public List<RelatedUserDTO> findFollowers(User user, PageParams pageParams) {
        final User currentUser = securityContextService.currentUser();
        return relatedUserCustomRepository.findFollowers(user, currentUser, pageParams);
    }

}

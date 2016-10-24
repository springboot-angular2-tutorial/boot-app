package com.myapp.service;

import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.repository.RelationshipCustomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelationshipServiceImpl implements RelationshipService {

    private final RelationshipCustomRepository relationshipCustomRepository;
    private final SecurityContextService securityContextService;

    public RelationshipServiceImpl(RelationshipCustomRepository relationshipCustomRepository, SecurityContextService securityContextService) {
        this.relationshipCustomRepository = relationshipCustomRepository;
        this.securityContextService = securityContextService;
    }

    @Override
    public List<RelatedUserDTO> findFollowings(User user, PageParams pageParams) {
        final User currentUser = securityContextService.currentUser();
        return relationshipCustomRepository.findFollowings(user, currentUser, pageParams);
    }

    @Override
    public List<RelatedUserDTO> findFollowers(User user, PageParams pageParams) {
        final User currentUser = securityContextService.currentUser();
        return relationshipCustomRepository.findFollowers(user, currentUser, pageParams);
    }

}

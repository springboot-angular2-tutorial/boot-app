package com.myapp.service;

import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.repository.RelatedUserCustomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        return relatedUserCustomRepository.findFollowings(user, currentUser, pageParams)
                .map(toDTO(currentUser))
                .collect(Collectors.toList());
    }

    @Override
    public List<RelatedUserDTO> findFollowers(User user, PageParams pageParams) {
        final User currentUser = securityContextService.currentUser();
        return relatedUserCustomRepository.findFollowers(user, currentUser, pageParams)
                .map(toDTO(currentUser))
                .collect(Collectors.toList());
    }

    private Function<RelatedUserCustomRepository.Row, RelatedUserDTO> toDTO(User currentUser) {
        return r -> {
            final Boolean isMyself = Optional.ofNullable(currentUser)
                    .map(u -> currentUser.equals(r.getUser()))
                    .orElse(null);
            return RelatedUserDTO.newInstance(
                    r.getUser(),
                    r.getRelationship(),
                    r.getUserStats(),
                    isMyself
            );
        };
    }

}

package com.myapp.service;

import com.myapp.domain.Relationship;
import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.repository.RelatedUserCustomRepository;
import com.myapp.repository.RelationshipRepository;
import com.myapp.repository.UserRepository;
import com.myapp.service.exceptions.RelationshipNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RelationshipServiceImpl implements RelationshipService {

    private final RelationshipRepository relationshipRepository;
    private final RelatedUserCustomRepository relatedUserCustomRepository;
    private final UserRepository userRepository;
    private final SecurityContextService securityContextService;

    public RelationshipServiceImpl(RelationshipRepository relationshipRepository,
                                   RelatedUserCustomRepository relatedUserCustomRepository,
                                   UserRepository userRepository,
                                   SecurityContextService securityContextService) {
        this.relationshipRepository = relationshipRepository;
        this.relatedUserCustomRepository = relatedUserCustomRepository;
        this.userRepository = userRepository;
        this.securityContextService = securityContextService;
    }

    @Override
    public List<RelatedUserDTO> findFollowings(Long userId, PageParams pageParams) {
        final User user = userRepository.findOne(userId);
        final List<RelatedUserCustomRepository.Row> rows = relatedUserCustomRepository.findFollowings(user, pageParams);

        return rowsToRelatedUsers(rows);
    }

    @Override
    public List<RelatedUserDTO> findFollowers(Long userId, PageParams pageParams) {
        final User user = userRepository.findOne(userId);
        final List<RelatedUserCustomRepository.Row> rows = relatedUserCustomRepository.findFollowers(user, pageParams);

        return rowsToRelatedUsers(rows);
    }

    @Override
    public void follow(Long userId) {
        final User user = userRepository.findOne(userId);
        final User currentUser = securityContextService.currentUser();
        final Relationship relationship = new Relationship(currentUser, user);
        // TODO unless followed

        relationshipRepository.save(relationship);
    }

    @Override
    public void unfollow(Long userId) throws RelationshipNotFoundException {
        final User followed = userRepository.findOne(userId);
        final User currentUser = securityContextService.currentUser();
        final Relationship relationship = relationshipRepository
                .findOneByFollowerAndFollowed(currentUser, followed)
                .orElseThrow(RelationshipNotFoundException::new);
        relationshipRepository.delete(relationship);
    }

    private List<RelatedUserDTO> rowsToRelatedUsers(List<RelatedUserCustomRepository.Row> rows) {
        final User currentUser = securityContextService.currentUser();

        final List<User> relatedUsers = rows.stream()
                .map(RelatedUserCustomRepository.Row::getUser)
                .collect(Collectors.toList());

        final List<User> followedByMe = relationshipRepository
                .findAllByFollowerAndFollowedIn(currentUser, relatedUsers)
                .map(Relationship::getFollowed)
                .collect(Collectors.toList());

        return rows.stream().map(row -> {
            final Boolean isFollowedByMe = Optional.ofNullable(currentUser)
                    .map(u -> followedByMe.contains(row.getUser()))
                    .orElse(null) ;
            final Boolean isMyself = Optional.ofNullable(currentUser)
                    .map(u -> currentUser.equals(row.getUser()))
                    .orElse(null);
            return RelatedUserDTO.builder2(row.getUser(), row.getRelationship(), row.getUserStats())
                    .isMyself(isMyself)
                    .isFollowedByMe(isFollowedByMe)
                    .build();
        }).collect(Collectors.toList());
    }

}

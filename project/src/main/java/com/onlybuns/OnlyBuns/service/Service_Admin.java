package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Account;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import com.onlybuns.OnlyBuns.repository.Repository_Follow;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@Transactional // TODO: change this to only be on top of the functions that actually need it (create things / have lazy fetching)
public class Service_Admin {

    final int PAGE_SIZE = 6;
    @Autowired
    private Repository_Account repository_account;

    @Autowired
    private Repository_Follow repository_follow;

    @Autowired
    private Service_Account service_Account;

    @Autowired
    private Repository_Follow repository_Follow;

    // /admin/accounts
    public Page<DTO_Get_Account> getPaginatedAccounts(
            HttpSession session,
            String firstName,
            String lastName,
            String userName,
            String email,
            String address,
            Integer minPostCount,
            Integer maxPostCount,
            String sortOption,
            int page) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            throw new SecurityException("Unauthorized access");
        }

        List<Account> filteredAccounts = repository_account.findAllAccountsByAttributesLike(
                firstName, lastName, userName, email, address,
                minPostCount, maxPostCount);

        List<DTO_Get_Account> allDtos = filteredAccounts.stream()
                .map(account -> {
                    int followersCount = repository_follow.countFollowers(account);
                    return new DTO_Get_Account(account, followersCount);
                }).collect(Collectors.toList());

        if (sortOption != null) {
            Comparator<DTO_Get_Account> comparator = getComparator(sortOption);

            if (comparator != null) {
                allDtos.sort(comparator);
            }
        }

        int totalElements = allDtos.size();
        int fromIndex = Math.min(page * PAGE_SIZE, totalElements);
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalElements);

        List<DTO_Get_Account> pageContent = allDtos.subList(fromIndex, toIndex);

        return new PageImpl<>(
                pageContent,
                PageRequest.of(page, PAGE_SIZE),
                totalElements
        );
    }
    private Comparator<DTO_Get_Account> getComparator(String sortOption) {
        return switch (sortOption) {
            case "follow_count,asc" -> Comparator.comparingInt(DTO_Get_Account::getFollowersCount);
            case "follow_count,desc" -> Comparator.comparingInt(DTO_Get_Account::getFollowersCount).reversed();
            case "email,asc" -> Comparator.comparing(DTO_Get_Account::getEmail);
            case "email,desc" -> Comparator.comparing(DTO_Get_Account::getEmail).reversed();
            default -> null;
        };
    }

    public ResponseEntity<List<DTO_Get_Account>> get_api_admin_accounts(
            HttpSession session,
            String firstName,
            String lastName,
            String userName,
            String email,
            String address,
            Integer minPostCount,
            Integer maxPostCount,
            int pageNum) {

        Page<DTO_Get_Account> page = getPaginatedAccounts(
                session, firstName, lastName, userName, email, address,
                minPostCount, maxPostCount, null, pageNum);

        return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }

    private Sort parseSortOption(String sortOption) {
        if (sortOption == null) {
            return Sort.unsorted();
        }

        return switch (sortOption) {
            case "follow_count,asc" -> Sort.by(Sort.Direction.ASC, "followersCount");
            case "follow_count,desc" -> Sort.by(Sort.Direction.DESC, "followersCount");
            case "email,asc" -> Sort.by(Sort.Direction.ASC, "email");
            case "email,desc" -> Sort.by(Sort.Direction.DESC, "email");
            default -> Sort.unsorted();
        };
    }
}

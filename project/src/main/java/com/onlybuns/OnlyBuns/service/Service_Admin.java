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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional // TODO: change this to only be on top of the functions that actually need it (create things / have lazy fetching)
public class Service_Admin {

    final int PAGE_SIZE = 6;
    @Autowired
    private Repository_Account repository_account;

    @Autowired
    private Repository_Follow repository_follow;

    // /admin/accounts
    public ResponseEntity<List<DTO_Get_Account>> get_api_admin_accounts(HttpSession session, String firstName, String lastName, String userName, String email, String address, Integer minPostCount, Integer maxPostCount, int pageNum) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        Pageable pageable = PageRequest.of(pageNum, PAGE_SIZE);

        Page<Account> accountsPage = repository_account.findAllAccountsByAttributesLike(firstName, lastName, userName, email, address, minPostCount, maxPostCount, pageable);
        List<DTO_Get_Account> accountDTOS = accountsPage.stream()
                .map(a -> new DTO_Get_Account(a, repository_follow))
                .collect(Collectors.toList());

        return new ResponseEntity<>(accountDTOS, HttpStatus.OK);
    }
    public List<Account> getSortedAccounts(HttpSession session, String sortOption) {
        Sort sort = switch (sortOption) {
            case "follow_count,asc" -> Sort.by(Sort.Direction.ASC, "followCount");
            case "follow_count,desc" -> Sort.by(Sort.Direction.DESC, "followCount");
            case "email,asc" -> Sort.by(Sort.Direction.ASC, "email");
            case "email,desc" -> Sort.by(Sort.Direction.DESC, "email");
            default -> Sort.unsorted();
        };

        return repository_account.findAll(sort);
    }
    public List<DTO_Get_Account> getFilteredAndSortedAccounts(HttpSession session, String firstName, String lastName, String userName, String email, String address, Integer minPostCount, Integer maxPostCount, String sortOption) {
        List<DTO_Get_Account> accounts = get_api_admin_accounts(session, firstName, lastName, userName, email, address, minPostCount, maxPostCount, 0).getBody();

        if (accounts == null || accounts.isEmpty()) {
            return accounts;
        }
        if (sortOption == null) {
            return accounts;
        }

        Comparator<DTO_Get_Account> comparator = null;

        switch (sortOption) {
            case "follow_count,asc":
//                comparator = Comparator.comparingInt(DTO_Get_Account::getFollowCount);
                comparator = Comparator.comparing(DTO_Get_Account::getEmail);
                break;
            case "follow_count,desc":
//                comparator = Comparator.comparingInt(DTO_Get_Account::getFollowCount).reversed();
                comparator = Comparator.comparing(DTO_Get_Account::getEmail);
                break;
            case "email,asc":
                comparator = Comparator.comparing(DTO_Get_Account::getEmail);
                break;
            case "email,desc":
                comparator = Comparator.comparing(DTO_Get_Account::getEmail).reversed();
                break;
            default:
                return accounts;
        }

        accounts.sort(comparator);

        return accounts;
    }

}

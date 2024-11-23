package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Account;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional // TODO: change this to only be on top of the functions that actually need it (create things / have lazy fetching)
public class Service_Admin {

    @Autowired
    private Repository_Account repository_account;

    // /admin/accounts
    public ResponseEntity<List<DTO_Get_Account>> get_api_admin_accounts(HttpSession session, String firstName, String lastName, String userName, String email, String address, Integer minPostCount, Integer maxPostCount) {
        return new ResponseEntity<>(get_api_admin_accounts_raw(session, firstName, lastName, userName, email, address, minPostCount, maxPostCount), HttpStatus.OK);
    }
    public List<DTO_Get_Account> get_api_admin_accounts_raw(HttpSession session, String firstName, String lastName, String userName, String email, String address, Integer minPostCount, Integer maxPostCount) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return null;
        }

        List<Account> accounts = repository_account.findAccountsByAttributesLike(firstName, lastName, userName, email, address, minPostCount, maxPostCount);
        List<DTO_Get_Account> accountDTOS = new ArrayList<>();
        for (Account account : accounts) {
            accountDTOS.add(new DTO_Get_Account(account));
        }
        return accountDTOS;
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
        List<DTO_Get_Account> accounts = get_api_admin_accounts_raw(session, firstName, lastName, userName, email, address, minPostCount, maxPostCount);

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

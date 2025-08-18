package in.vijay.bank.repository;

import in.vijay.bank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,String> {

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId OR t.relatedAccount.id = :accountId")
    List<Transaction> findByAccountIdOrRelatedAccountId(@Param("accountId") String accountId, @Param("accountId") String accountId2);


    @Query("SELECT t FROM Transaction t WHERE t.account.accountNumber = :accountNumber OR t.relatedAccount.accountNumber = :accountNumber")
    List<Transaction> findByAccountNumberOrRelatedAccountNumber(@Param("accountNumber") String accountNumber);
}

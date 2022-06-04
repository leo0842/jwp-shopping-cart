package woowacourse.shoppingcart.application;

import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woowacourse.shoppingcart.dao.CustomerDao;
import woowacourse.shoppingcart.domain.Account;
import woowacourse.shoppingcart.domain.Address;
import woowacourse.shoppingcart.domain.Customer;
import woowacourse.shoppingcart.domain.Nickname;
import woowacourse.shoppingcart.domain.PhoneNumber;
import woowacourse.shoppingcart.dto.CustomerResponse;
import woowacourse.shoppingcart.dto.DeleteCustomerRequest;
import woowacourse.shoppingcart.dto.PhoneNumberFormat;
import woowacourse.shoppingcart.dto.SignupRequest;
import woowacourse.shoppingcart.dto.UpdateCustomerRequest;
import woowacourse.shoppingcart.exception.CustomerNotFoundException;
import woowacourse.shoppingcart.exception.DuplicatedAccountException;
import woowacourse.shoppingcart.exception.WrongPasswordException;

@Service
@Transactional
public class CustomerService {

    private final CustomerDao customerDao;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerDao customerDao, PasswordEncoder passwordEncoder) {
        this.customerDao = customerDao;
        this.passwordEncoder = passwordEncoder;
    }

    public CustomerResponse create(SignupRequest signupRequest) {
        final Customer customer = toCustomer(signupRequest);

        if (customerDao.findByAccount(customer.getAccount()).isPresent()) {
            throw new DuplicatedAccountException();
        }
        final Customer savedCustomer = customerDao.save(customer);
        return CustomerResponse.of(savedCustomer);
    }

    private Customer toCustomer(SignupRequest signupRequest) {
        PhoneNumberFormat phoneNumberFormat = signupRequest.getPhoneNumber();
        return new Customer(
                new Account(signupRequest.getAccount()),
                new Nickname(signupRequest.getNickname()),
                passwordEncoder.encode(signupRequest.getPassword()),
                new Address(signupRequest.getAddress()),
                new PhoneNumber(phoneNumberFormat.appendNumbers()));
    }

    @Transactional(readOnly = true)
    public CustomerResponse getById(long customerId) {
        Customer customer = customerDao.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        return CustomerResponse.of(customer);
    }

    public int update(long customerId, UpdateCustomerRequest updateCustomerRequest) {
        return customerDao.update(
                customerId,
                updateCustomerRequest.getNickname(),
                updateCustomerRequest.getAddress(),
                updateCustomerRequest.getPhoneNumber().appendNumbers());
    }

    public int delete(long id, DeleteCustomerRequest deleteCustomerRequest) {
        final Customer customer = customerDao.findById(id).orElseThrow(CustomerNotFoundException::new);
        if (!passwordEncoder.matches(deleteCustomerRequest.getPassword(), customer.getPassword())) {
            throw new WrongPasswordException();
        }
        return customerDao.deleteById(id);
    }
}

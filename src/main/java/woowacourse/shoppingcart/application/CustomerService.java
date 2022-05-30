package woowacourse.shoppingcart.application;

import org.springframework.stereotype.Service;
import woowacourse.shoppingcart.dao.CustomerDao;
import woowacourse.shoppingcart.domain.Customer;
import woowacourse.shoppingcart.dto.CustomerDto;
import woowacourse.shoppingcart.dto.SignupRequest;
import woowacourse.shoppingcart.exception.DuplicatedAccountException;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public CustomerDto createCustomer(SignupRequest signupRequest) {
        final Customer customer = signupRequest.toEntity();
        if (customerDao.findByAccount(customer.getAccount()).isPresent()) {
            throw new DuplicatedAccountException();
        }
        final Customer save = customerDao.save(customer);
        return new CustomerDto(save.getId());
    }
}

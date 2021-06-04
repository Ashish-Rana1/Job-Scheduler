package com.ashish.jobscheduler.step;

import com.ashish.jobscheduler.dao.Employee;
import org.springframework.batch.item.ItemProcessor;

public class EmployeeItemProcessor implements ItemProcessor<Employee, Employee> {

    @Override
    public Employee process(Employee item) throws Exception {
        String firstName = item.getFirstName().toUpperCase();
        String lastName = item.getLastName().toUpperCase();

        Employee empProcess = new Employee(firstName, lastName);
        return empProcess;
    }
}





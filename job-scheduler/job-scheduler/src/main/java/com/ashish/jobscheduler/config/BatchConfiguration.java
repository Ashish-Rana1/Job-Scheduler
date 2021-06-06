package com.ashish.jobscheduler.config;

import com.ashish.jobscheduler.dao.Employee;
import com.ashish.jobscheduler.listener.JobCompletionNotificationListener;
import com.ashish.jobscheduler.step.EmployeeItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.util.function.Function;

    @Configuration
    @EnableBatchProcessing
    public class BatchConfiguration {

        @Autowired
        public JobBuilderFactory jobBuilderFactory;

        @Autowired
        public StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<Employee> reader() throws MalformedURLException {
        return new FlatFileItemReaderBuilder<Employee>()
                .name("employeeItemReader")
                        .resource(new ClassPathResource("data.csv"))
                        .delimited()
                        .names(new String[]{"firstName", "lastName"})
                        .fieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {{
                            setTargetType(Employee.class);
                        }})
                        .build();
    }

    @Bean
    public EmployeeItemProcessor processor() {
        return new EmployeeItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Employee> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Employee>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO employee (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(dataSource)
                .build();
    }
    @Bean
    public Job employeeJob(JobCompletionNotificationListener listener, Step employeeStep) {
        return jobBuilderFactory.get("employeeJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(employeeStep)
                .end()
                .build();
    }

    @Bean
    public Step employeeStep(JdbcBatchItemWriter<Employee> writer) throws MalformedURLException {
        return stepBuilderFactory.get("employeeStep")
                .<Employee, Employee> chunk(3)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }


}

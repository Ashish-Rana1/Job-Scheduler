package com.ashish.jobscheduler.config;

import com.ashish.jobscheduler.dao.Employee;
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
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.util.function.Function;

@Configuration
    @EnableBatchProcessing
    public class BatchConfiguration {

        @Autowired
        public JobBuilderFactory jobBuilderFactory;

        @Autowired
        public StepBuilderFactory stepBuilderFactory;
    private Object EmployeeItemReader;

    @Bean
    public ItemReader<? extends Person> reader() {
        return new FlatFileItemReaderBuilder<Employee>()
                        .resource(new ClassPathResource(“data.csv”))
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
                .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(dataSource)
                .build();
    }
    @Bean
    public Job employeeJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("employeeJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step employeeStep(JdbcBatchItemWriter<Person> writer) {
        return stepBuilderFactory.get("employeeStep")
                .<Person, Person> chunk(10)
                .reader(reader())
                .processor((Function<? super Person, ? extends Person>) processor())
                .writer(writer)
                .build();
    }


}

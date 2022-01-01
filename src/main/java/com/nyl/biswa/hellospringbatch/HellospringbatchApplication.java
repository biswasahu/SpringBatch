package com.nyl.biswa.hellospringbatch;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Date;

@EnableBatchProcessing
@SpringBootApplication
public class HellospringbatchApplication {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Step step(){
	return this.stepBuilderFactory.get("step1").tasklet(new Tasklet() {
		@Override
		public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
			System.out.println("Hello SpringBatch");
			String name = (String)chunkContext.getStepContext().getJobParameters().get("name");
			System.out.println(name);
			return RepeatStatus.FINISHED;
		}
	}).build();

	}


	public class ParameterValidator implements JobParametersValidator{
		@Override
		public void validate(JobParameters parameters) throws JobParametersInvalidException
		{
			String fileName  = parameters.getString("fileName");
		}

	}

	public class DailyJobTimestamper implements JobParametersIncrementer{
	@Override
	public JobParameters getNext(JobParameters parameters){

		return new JobParametersBuilder(parameters).addDate("currentDate", new Date()).toJobParameters();
	}

	}

/*	@Bean
	public CompositeJobParametersValidator validator(){
		CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
		DefaultJobParametersValidator defaultJobParametersValidator =
				new DefaultJobParametersValidator(new String[]{"fileName"},
						                          new String[]{"name", "currentDate"});
		defaultJobParametersValidator.afterPropertiesSet();
		validator.setValidators(Arrays.asList(new ParameterValidator(), defaultJobParametersValidator));

		return validator;
	}*/

	@Bean
	public Job job(){

		return this.jobBuilderFactory.get("job").start(step()).incrementer(new DailyJobTimestamper()).build();
	}

	public static void main(String[] args) {
		SpringApplication.run(HellospringbatchApplication.class, args);
	}

}

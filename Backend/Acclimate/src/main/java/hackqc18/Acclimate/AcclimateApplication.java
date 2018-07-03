package hackqc18.Acclimate;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@SpringBootApplication
@EnableScheduling
public class AcclimateApplication {

    public static void main(String[] args) {
	// StaticParser staticParser = new StaticParser();

//        try {
//            // Grab the Scheduler instance from the Factory
//            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
//
//            // and start it off
//            scheduler.start();
//
//            // define the job and tie it to our HelloJob class
//            JobDetail job = newJob(QuartzTest.class)
//                    .withIdentity("job1", "group1")
//                    .build();
//
//            // Trigger the job to run now, and then repeat every 40 seconds
//            Trigger trigger = newTrigger()
//                    .withIdentity("trigger1", "group1")
//                    .startNow()
//                    .withSchedule(simpleSchedule()
//                            .withIntervalInSeconds(10)
//                            .repeatForever())
//                    .build();
//
//            // Tell quartz to schedule the job using our trigger
//            scheduler.scheduleJob(job, trigger);
//
//            for(int i =0 ;i < 5000; ++i) Thread.sleep(10000000);
//
//            scheduler.shutdown();
//
//        } catch (SchedulerException se) {
//            se.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

		SpringApplication.run(AcclimateApplication.class, args);
    }
}

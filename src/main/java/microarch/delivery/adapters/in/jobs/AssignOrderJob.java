package microarch.delivery.adapters.in.jobs;

import microarch.delivery.core.application.commands.AssignOrderCommand;
import microarch.delivery.core.application.commands.AssignOrderCommandHandler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

public class AssignOrderJob implements Job {

    @Autowired
    private AssignOrderCommandHandler assignOrderCommandHandler;

    @Override
    public void execute(JobExecutionContext context) {
        var command = AssignOrderCommand.create().getValue();
        assignOrderCommandHandler.handle(command);
    }
}

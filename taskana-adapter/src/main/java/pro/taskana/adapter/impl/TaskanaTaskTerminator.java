package pro.taskana.adapter.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import pro.taskana.adapter.exceptions.TaskTerminationFailedException;
import pro.taskana.adapter.manager.AdapterManager;
import pro.taskana.adapter.systemconnector.api.ReferencedTask;
import pro.taskana.adapter.systemconnector.api.SystemConnector;
import pro.taskana.adapter.taskanaconnector.api.TaskanaConnector;

/**
 * terminates taskana tasks if the associated task in the external system was finished.
 *
 * @author bbr
 */
@Component
public class TaskanaTaskTerminator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaTaskTerminator.class);

    @Value("${taskanaAdapter.total.transaction.lifetime.in.seconds:120}")
    private int maximumTotalTransactionLifetime;

    @Autowired
    AdapterManager adapterManager;

    public void retrieveFinishedReferencedTasksAndTerminateCorrespondingTaskanaTasks() {

        synchronized (AdapterManager.class) {
            if (!adapterManager.isInitialized()) {
                adapterManager.init();
                return;
            }
        }

        synchronized (TaskanaTaskTerminator.class) {

            if (!adapterManager.isInitialized()) {
                return;
            }

            LOGGER.debug(
                "----------retrieveFinishedReferencedTasksAndTerminateCorrespondingTaskanaTasks started----------------------------");

            try {

                for (SystemConnector systemConnector : (adapterManager.getSystemConnectors().values())) {
                    retrieveTerminatedReferencedTasksAndTerminateCorrespondingTaskanaTasks(systemConnector);
                }
            } catch (Exception e) {
                LOGGER.warn("caught exception {} ", e);
            }
        }
    }

    public void retrieveTerminatedReferencedTasksAndTerminateCorrespondingTaskanaTasks(
        SystemConnector systemConnector) {
        LOGGER
            .trace("TaskanaTaskTerminator.retrieveFinishedReferencedTasksAndTerminateCorrespondingTaskanaTasks ENTRY ");

        List<ReferencedTask> tasksSuccessfullyTerminatedInTaskana = new ArrayList<>();

        try {
            List<ReferencedTask> taskanaTasksToTerminate = systemConnector.retrieveTerminatedTasks();

            for (ReferencedTask referencedTask : taskanaTasksToTerminate) {
                try {
                    terminateTaskanaTask(referencedTask);
                    tasksSuccessfullyTerminatedInTaskana.add(referencedTask);
                } catch (TaskTerminationFailedException ex) {
                    LOGGER.error("attempted to terminate task with external Id {} and caught {}",
                        referencedTask.getId(), ex);
                }
            }
            systemConnector.taskanaTasksHaveBeenCompletedForTerminatedReferencedTasks(taskanaTasksToTerminate);

        } finally {
            LOGGER.trace(
                "TaskanaTaskTerminator.retrieveFinishedReferencedTasksAndTerminateCorrespondingTaskanaTasks EXIT ");
        }
    }

    private void terminateTaskanaTask(ReferencedTask referencedTask) throws TaskTerminationFailedException {
        LOGGER.trace("TaskanaTaskTerminator.terminateTaskanaTask ENTRY ");
        TaskanaConnector taskanaConnector = adapterManager.getTaskanaConnector();
        taskanaConnector.terminateTaskanaTask(referencedTask);

        LOGGER.trace("TaskanaTaskTerminator.terminateTaskanaTask EXIT ");
    }
}

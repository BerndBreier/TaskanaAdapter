package pro.taskana.adapter.scheduler;

/**
 * Distinguish the various agents running in scheduler.
 * @author bbr
 *
 */
public enum AgentType {
    START_TASKANA_TASKS("START_TASKANA_TASK"),
    HANDLE_FINISHED_TASKANA_TASKS("HANDLE_FINISHED_TASKANA_TASKS"),
    CLEANUP_TASKANA_TABLES("CLEANUP_TASKANA_TABLES"),
    HANDLE_FINISHED_GENERAL_TASKS("HANDLE_FINISHED_GENERAL_TASKS");

    private String name;
    AgentType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

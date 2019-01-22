package pro.taskana.adapter.taskanaconnector.spi;

import java.util.List;

import pro.taskana.adapter.taskanaconnector.api.TaskanaConnector;

/**
 * The interface, a Provider for TaskanaSystemConnectors must implement.
 * @author bbr
 *
 */
public interface TaskanaConnectorProvider {
    List<TaskanaConnector> create();
}

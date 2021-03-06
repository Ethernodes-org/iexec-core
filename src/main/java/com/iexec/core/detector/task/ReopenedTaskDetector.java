package com.iexec.core.detector.task;

import com.iexec.common.chain.ChainTask;
import com.iexec.common.chain.ChainTaskStatus;
import com.iexec.core.chain.IexecHubService;
import com.iexec.core.detector.Detector;
import com.iexec.core.task.Task;
import com.iexec.core.task.TaskExecutorEngine;
import com.iexec.core.task.TaskService;
import com.iexec.core.task.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ReopenedTaskDetector implements Detector {

    private TaskService taskService;
    private TaskExecutorEngine taskExecutorEngine;
    private IexecHubService iexecHubService;

    public ReopenedTaskDetector(TaskService taskService,
                                TaskExecutorEngine taskExecutorEngine,
                                IexecHubService iexecHubService) {
        this.taskService = taskService;
        this.taskExecutorEngine = taskExecutorEngine;
        this.iexecHubService = iexecHubService;
    }

    /**
     * Detector to detect tasks that are reopening but are not reopened yet.
     */
    @Scheduled(fixedRateString = "${detector.task.finalized.unnotified.period}")
    @Override
    public void detect() {
        log.debug("Trying to detect reopened tasks");
        for (Task task : taskService.findByCurrentStatus(TaskStatus.REOPENING)) {
            Optional<ChainTask> oChainTask = iexecHubService.getChainTask(task.getChainTaskId());
            if (!oChainTask.isPresent()) {
                continue;
            }

            ChainTask chainTask = oChainTask.get();
            if (chainTask.getStatus().equals(ChainTaskStatus.ACTIVE)) {
                log.info("Detected confirmed missing update (task) [is:{}, should:{}, taskId:{}]",
                        TaskStatus.REOPENING, TaskStatus.REOPENED, task.getChainTaskId());
                taskExecutorEngine.updateTask(task.getChainTaskId());
            }
        }
    }
}


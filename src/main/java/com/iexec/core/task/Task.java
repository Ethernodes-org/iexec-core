package com.iexec.core.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iexec.common.dapp.DappType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    private String id;

    @Version
    private Long version;

    private String chainTaskId;
    private DappType dappType;
    private String dappName;
    private String commandLine;
    private TaskStatus currentStatus;
    private List<TaskStatusChange> dateStatusList;
    private int trust;
    private String uploadingWorkerWalletAddress;
    private String consensus;

    public Task(String dappName, String commandLine, int trust) {
        this.dappType = DappType.DOCKER;
        this.dappName = dappName;
        this.commandLine = commandLine;
        this.trust = trust;
        this.dateStatusList = new ArrayList<>();
        this.dateStatusList.add(new TaskStatusChange(TaskStatus.CREATED));
        this.currentStatus = TaskStatus.CREATED;
    }

    public Task(String dappName, String commandLine, int trust, String chainTaskId) {
        this(dappName, commandLine, trust);
        this.chainTaskId = chainTaskId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setChainTaskId(String chainTaskId) {
        this.chainTaskId = chainTaskId;
    }

    public void setDateStatusList(List<TaskStatusChange> dateStatusList) {
        this.dateStatusList = dateStatusList;
    }

    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    public void setTrust(int trust) {
        this.trust = trust;
    }

    public void setUploadingWorkerWalletAddress(String uploadingWorkerWalletAddress) {
        this.uploadingWorkerWalletAddress = uploadingWorkerWalletAddress;
    }

    public void setCurrentStatus(TaskStatus status) {
        this.currentStatus = status;
    }

    public void changeStatus(TaskStatus status) {
        setCurrentStatus(status);
        this.getDateStatusList().add(new TaskStatusChange(status));
    }

    @JsonIgnore
    public TaskStatusChange getLatestStatusChange() {
        return this.getDateStatusList().get(this.getDateStatusList().size() - 1);
    }

    public void setConsensus(String consensus) {
        this.consensus = consensus;
    }
}

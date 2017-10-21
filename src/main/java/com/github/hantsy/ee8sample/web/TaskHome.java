package com.github.hantsy.ee8sample.web;

import com.github.hantsy.ee8sample.domain.Task;
import com.github.hantsy.ee8sample.domain.TaskNotFoundException;
import com.github.hantsy.ee8sample.domain.TaskRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import java.util.logging.Logger;
import javax.faces.view.ViewScoped;

/**
 *
 * @author hantsy
 *
 */
@Named("taskHome")
@ViewScoped()
public class TaskHome implements Serializable {

    //@Inject
    private static final Logger log = Logger.getLogger(TaskHome.class.getName());

    @Inject
    private TaskRepository taskRepository;

    private List<TaskDetails> todotasks = new ArrayList<>();

    private List<TaskDetails> doingtasks = new ArrayList<>();

    private List<TaskDetails> donetasks = new ArrayList<>();

    public List<TaskDetails> getTodotasks() {
        return todotasks;
    }

    public List<TaskDetails> getDoingtasks() {
        return doingtasks;
    }

    public List<TaskDetails> getDonetasks() {
        return donetasks;
    }

    public void init() {
        log.log(Level.INFO, "initalizing TaskHome...");
        retrieveAllTasks();
    }

    private void retrieveAllTasks() {
        log.log(Level.INFO, "retriveing all tasks...");
        this.todotasks = findTasksByStatus(Task.Status.TODO);
        this.doingtasks = findTasksByStatus(Task.Status.DOING);
        this.donetasks = findTasksByStatus(Task.Status.DONE);
    }

    private List<TaskDetails> findTasksByStatus(Task.Status status) {
        List<TaskDetails> taskList = new ArrayList<>();
        List<Task> tasks = taskRepository.findByStatus(status);

        tasks.stream().map((task) -> {
            TaskDetails details = new TaskDetails();
            details.setId(task.getId());
            details.setName(task.getName());
            details.setDescription(task.getDescription());
            details.setCreatedDate(task.getCreatedDate());
            details.setLastModifiedDate(task.getLastModifiedDate());
            return details;
        }).forEach((details) -> {
            taskList.add(details);
        });

        return taskList;
    }

    public void deleteTask(Long id) {

        log.log(Level.INFO, "delete task of id@{0}", id);

        Task task = taskRepository.findById(id);

        if (task == null) {
            throw new TaskNotFoundException(id);
        }

        taskRepository.deleteById(id);

        // retrieve all tasks
        retrieveAllTasks();

        FacesMessage deleteInfo = new FacesMessage(FacesMessage.SEVERITY_WARN, "Task is deleted!", "Task is deleted!");
        FacesContext.getCurrentInstance().addMessage(null, deleteInfo);
    }

    public void markTaskDoing(Long id) {
        log.log(Level.INFO, "changing task DONG @{0}", id);

        Task task = taskRepository.findById(id);

        if (task == null) {
            throw new TaskNotFoundException(id);
        }

        task.setStatus(Task.Status.DOING);

        taskRepository.update(task);

        // retrieve all tasks
        retrieveAllTasks();
    }

    public void markTaskDone(Long id) {
        log.log(Level.INFO, "changing task DONE @{0}", id);

        Task task = taskRepository.findById(id);

        if (task == null) {
            throw new TaskNotFoundException(id);
        }

        task.setStatus(Task.Status.DONE);

        taskRepository.update(task);

        // retrieve all tasks
        retrieveAllTasks();

    }

}

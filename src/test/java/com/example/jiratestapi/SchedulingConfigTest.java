package com.example.jiratestapi;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.Batch.BatchRepository;
import com.example.jiratestapi.BatchTicket.BatchTicket;
import com.example.jiratestapi.BatchTicket.BatchTicketRepository;
import com.example.jiratestapi.Configs.SchedulingConfig;
import com.example.jiratestapi.Jira.JiraController;
import com.example.jiratestapi.Projects.Project;
import com.example.jiratestapi.Projects.ProjectRepository;
import com.example.jiratestapi.Task.Task;
import com.example.jiratestapi.Task.TaskRepository;
import com.example.jiratestapi.users.User;
import com.example.jiratestapi.users.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SchedulingConfigTest {

    @Mock
    private JiraController ticketController;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private BatchTicketRepository batchTicketRepository;

    @InjectMocks
    private SchedulingConfig schedulingConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testSyncTicketsNightly_NoTasks() throws Exception {
        when(ticketController.syncTickets()).thenReturn(Collections.emptyList());

        schedulingConfig.syncTicketsNightly();

        verify(taskRepository, never()).deleteAll(any());
        verify(taskRepository, never()).save(any());
        verify(batchTicketRepository, never()).saveAll(any());
    }

    @Test
    void testSyncTicketsNightly_WithTasks() throws Exception {
        // Initialize the Batch object with default values for relevant fields
        Batch batch = new Batch();
        batch.setId(1L);
        batch.setTicketsCreated(0);
        batch.setTicketsUpdated(0);
        batch.setTicketsUnchanged(0);

        // Initialize the BatchTicket and Task objects
        BatchTicket batchTicket = new BatchTicket();
        batchTicket.setBatch(batch);
        batchTicket.setJiraId("123");
        batchTicket.setAssigneeName("John Doe");
        batchTicket.setSummary("Test Summary");

        Task task = new Task();
        task.setJiraId("123");

        // Stubbing necessary repository methods
        when(ticketController.syncTickets()).thenReturn(Arrays.asList(batchTicket));
        when(batchRepository.findById(anyLong())).thenReturn(Optional.of(batch));
        when(taskRepository.findAll()).thenReturn(Collections.singletonList(task));
        when(projectRepository.findByJiraKey(any())).thenReturn(Optional.of(new Project()));
//        lenient().when(userRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(anyString(), anyString())).thenReturn(new User());
        when(userRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(anyString(), anyString())).thenReturn(new User());



        schedulingConfig.syncTicketsNightly();

        verify(taskRepository, times(1)).deleteAll(any());
        verify(taskRepository, times(1)).save(any());
        verify(batchTicketRepository, never()).saveAll(any());
        verify(userRepository, times(1)).findByFirstNameIgnoreCaseAndLastNameIgnoreCase("John", "Doe");
    }

}

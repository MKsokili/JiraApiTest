package com.example.jiratestapi;

import com.example.jiratestapi.Batch.Batch;
import com.example.jiratestapi.Batch.BatchRepository;
import com.example.jiratestapi.BatchTicket.BatchTicket;
import com.example.jiratestapi.Jira.JiraController;
import com.example.jiratestapi.Jira.JiraService;
import com.example.jiratestapi.Projects.Project;
import com.example.jiratestapi.Projects.ProjectRepository;
import com.example.jiratestapi.Task.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(MockitoExtension.class)
public class JiraControllerTest {

    @Mock
    private JiraService jiraService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private BatchRepository batchRepository;

    @InjectMocks
    private JiraController jiraController;

    private MockMvc mockMvc;

    @Test
    public void testSyncTickets() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(jiraController).build();

        // Prepare test data
        BatchTicket ticket1 = new BatchTicket();
        ticket1.setProjectKey("TEST_KEY");
        ticket1.setSummary("Ticket 1");

        BatchTicket ticket2 = new BatchTicket();
        ticket2.setProjectKey("TEST_KEY");
        ticket2.setSummary("Ticket 2");

        List<BatchTicket> batchTickets = Arrays.asList(ticket1, ticket2);

        Project project = new Project();
        project.setId(1L);
        project.setJiraKey("TEST_KEY");

        List<Project> projects = Arrays.asList(project);

        List<BatchTicket> ticketsList = new ArrayList<>();

        // Mocking service and repository methods
        when(jiraService.fetchTickets()).thenReturn(batchTickets);
        when(projectRepository.findAll()).thenReturn(projects);
        when(taskRepository.findAllByProjectIdAndStatusNot(1L , "Archived")).thenReturn(new ArrayList<>());

        // Perform the request and assert the response
        mockMvc.perform(post("/sync"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].summary").value("Ticket 1"))
                .andExpect(jsonPath("$[1].summary").value("Ticket 2"));

        // Verify interactions
        verify(batchRepository).save(org.mockito.Mockito.any(Batch.class));
        verify(projectRepository).save(org.mockito.Mockito.any(Project.class));
    }
}

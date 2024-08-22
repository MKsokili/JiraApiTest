//package com.example.jiratestapi;
//
//import com.example.jiratestapi.Batch.Batch;
//import com.example.jiratestapi.Batch.BatchController;
//import com.example.jiratestapi.Batch.BatchRepository;
//import com.example.jiratestapi.Projects.Project;
//import com.example.jiratestapi.Projects.ProjectRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@ExtendWith(MockitoExtension.class)
//public class BatchControllerTest {
//
//    @Mock
//    private ProjectRepository projectRepository;
//
//    @Mock
//    private BatchRepository batchRepository;
//
//    @InjectMocks
//    private BatchController batchController;
//
//    private MockMvc mockMvc;
//
//
//    @Test
//    public void testGetProjectBatch() throws Exception {
//        mockMvc = MockMvcBuilders.standaloneSetup(batchController).build();
//
//        Project project = new Project();  // Assuming Project class has a default constructor
//        project.setJiraKey("TEST_KEY");   // Set the expected key
//
//        Batch batch = new Batch(1L, LocalDate.of(2023, 8, 1), true, 5, 10, 15, 20, 50, null, project);
//
//        // Mocking repository responses
//        when(projectRepository.findByJiraKey("TEST_KEY")).thenReturn(project);
//        when(batchRepository.findByProjectAndStartedDate(project, LocalDate.of(2023, 8, 1))).thenReturn(Optional.of(batch));
//
//        // Perform the request and assert the response
//        mockMvc.perform(get("/batch/TEST_KEY/2023-08-01"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(batch.getId()))
//                .andExpect(jsonPath("$.startedDate[0]").value(batch.getStartedDate().getYear()))
//                .andExpect(jsonPath("$.startedDate[1]").value(batch.getStartedDate().getMonthValue()))
//                .andExpect(jsonPath("$.startedDate[2]").value(batch.getStartedDate().getDayOfMonth()))
//                .andExpect(jsonPath("$.isCompleted").value(batch.getIsCompleted()))
//                .andExpect(jsonPath("$.ticketsDeleted").value(batch.getTicketsDeleted()))
//                .andExpect(jsonPath("$.ticketsUpdated").value(batch.getTicketsUpdated()))
//                .andExpect(jsonPath("$.ticketsCreated").value(batch.getTicketsCreated()))
//                .andExpect(jsonPath("$.ticketsUnchanged").value(batch.getTicketsUnchanged()))
//                .andExpect(jsonPath("$.totalTicketsSync").value(batch.getTotalTicketsSync()));
//    }
//
////    @Test
////    public void testGetProjectBatchNotFound() throws Exception {
////        mockMvc = MockMvcBuilders.standaloneSetup(batchController).build();
////
////        when(projectRepository.findByJiraKey("TEST_KEY")).thenReturn(null);
////
////        mockMvc.perform(get("/batch/TEST_KEY/2023-08-01"))
////                .andExpect(status().isNotFound());
////    }
//
//    @Test
//    public void testGetProjectBatch_BatchNotFound() {
//        // Arrange
//        String projectKey = "TEST_PROJECT";
//        String date = "2024-08-09";
//        LocalDate batchDate = LocalDate.parse(date);
//
//        when(projectRepository.findByJiraKey(projectKey)).thenReturn(new Project());
//        when(batchRepository.findByProjectAndStartedDate(any(Project.class), any(LocalDate.class)))
//                .thenReturn(Optional.empty());
//
//        // Act
//        Batch result = batchController.getProjectBatch(projectKey, date);
//
//        // Assert
//        assertNull(result, "Expected null when no batch is found for the given project and date");
//    }
//
//    @Test
//    public void testGetProjectBatches() throws Exception {
//        mockMvc = MockMvcBuilders.standaloneSetup(batchController).build();
//
//        Batch batch1 = new Batch(1L, LocalDate.of(2023, 8, 1), true, 5, 10, 15, 20, 50, null, null);
//        Batch batch2 = new Batch(2L, LocalDate.of(2023, 8, 2), false, 3, 6, 9, 12, 30, null, null);
//        List<Batch> batches = Arrays.asList(batch1, batch2);
//
//        when(batchRepository.findAll()).thenReturn(batches);
//
//        mockMvc.perform(get("/batch/all"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].id").value(batch1.getId()))
//                .andExpect(jsonPath("$[0].startedDate[0]").value(batch1.getStartedDate().getYear()))
//                .andExpect(jsonPath("$[0].startedDate[1]").value(batch1.getStartedDate().getMonthValue()))
//                .andExpect(jsonPath("$[0].startedDate[2]").value(batch1.getStartedDate().getDayOfMonth()))
//                .andExpect(jsonPath("$[0].isCompleted").value(batch1.getIsCompleted()))
//                .andExpect(jsonPath("$[0].ticketsDeleted").value(batch1.getTicketsDeleted()))
//                .andExpect(jsonPath("$[0].ticketsUpdated").value(batch1.getTicketsUpdated()))
//                .andExpect(jsonPath("$[0].ticketsCreated").value(batch1.getTicketsCreated()))
//                .andExpect(jsonPath("$[0].ticketsUnchanged").value(batch1.getTicketsUnchanged()))
//                .andExpect(jsonPath("$[0].totalTicketsSync").value(batch1.getTotalTicketsSync()))
//                .andExpect(jsonPath("$[1].id").value(batch2.getId()))
//                .andExpect(jsonPath("$[1].startedDate[0]").value(batch2.getStartedDate().getYear()))
//                .andExpect(jsonPath("$[1].startedDate[1]").value(batch2.getStartedDate().getMonthValue()))
//                .andExpect(jsonPath("$[1].startedDate[2]").value(batch2.getStartedDate().getDayOfMonth()))
//                .andExpect(jsonPath("$[1].isCompleted").value(batch2.getIsCompleted()))
//                .andExpect(jsonPath("$[1].ticketsDeleted").value(batch2.getTicketsDeleted()))
//                .andExpect(jsonPath("$[1].ticketsUpdated").value(batch2.getTicketsUpdated()))
//                .andExpect(jsonPath("$[1].ticketsCreated").value(batch2.getTicketsCreated()))
//                .andExpect(jsonPath("$[1].ticketsUnchanged").value(batch2.getTicketsUnchanged()))
//                .andExpect(jsonPath("$[1].totalTicketsSync").value(batch2.getTotalTicketsSync()));
//    }
//}